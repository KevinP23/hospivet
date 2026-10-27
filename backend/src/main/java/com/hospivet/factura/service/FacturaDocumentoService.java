package com.hospivet.factura.service;

import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.factura.dto.DetalleResponse;
import com.hospivet.factura.dto.FacturaResponse;
import com.hospivet.factura.dto.ImpresorasResponse;
import com.hospivet.factura.model.Factura;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Imprimir (SD-20) y enviar (SD-21) la factura desde el equipo donde está instalado el sistema (D-16). */
@Service
public class FacturaDocumentoService {

    private static final Logger log = LoggerFactory.getLogger(FacturaDocumentoService.class);
    private static final String ERROR_IMPRESION = "Error de impresión, verifique la impresora";
    private static final String SIN_CORREO = "El cliente no tiene correo registrado";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FacturaService facturaService;
    private final ClienteMapper clienteMapper;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;

    public FacturaDocumentoService(FacturaService facturaService, ClienteMapper clienteMapper,
                                   ObjectProvider<JavaMailSender> mailSender,
                                   @Value("${hospivet.mail.remitente:}") String remitente) {
        this.facturaService = facturaService;
        this.clienteMapper = clienteMapper;
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    /** N-08 B: impresoras instaladas en el equipo. */
    public ImpresorasResponse impresoras() {
        List<String> nombres = Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .map(PrintService::getName).sorted().toList();
        PrintService predeterminada = PrintServiceLookup.lookupDefaultPrintService();
        return new ImpresorasResponse(nombres, predeterminada == null ? null : predeterminada.getName());
    }

    /** SD-20 imprimir(idFactura): genera el formato y lo envía a la impresora elegida. */
    public void imprimir(Long idFactura, String impresora) {
        FacturaResponse factura = facturaService.consultarDetalle(idFactura);
        PrintService servicio = Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .filter(candidata -> candidata.getName().equals(impresora))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ERROR_IMPRESION));
        try {
            PrinterJob trabajo = PrinterJob.getPrinterJob();
            trabajo.setPrintService(servicio);
            trabajo.setJobName("Factura " + factura.numero());
            trabajo.setPrintable(new FacturaImprimible(factura));
            trabajo.print();
        } catch (PrinterException | RuntimeException exception) {
            log.warn("No se pudo imprimir la factura {} en {}", factura.numero(), impresora, exception);
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ERROR_IMPRESION);
        }
    }

    /** SD-21 enviar(idFactura, medio): solo correo electrónico; verifica antes el correo del cliente. */
    public void enviar(Long idFactura, String medio) {
        if (!"CORREO".equalsIgnoreCase(medio)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Datos inválidos", Map.of("medio", "Correo electrónico"));
        }
        FacturaResponse factura = facturaService.consultarDetalle(idFactura);
        if (!StringUtils.hasText(factura.correoCliente())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, SIN_CORREO, Map.of("correo", SIN_CORREO));
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || !StringUtils.hasText(remitente)) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "No fue posible enviar la factura");
        }
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(factura.correoCliente());
            helper.setSubject("Factura " + factura.numero() + " — HOSPIVET");
            helper.setText(cuerpo(factura), true);
            sender.send(mensaje);
        } catch (MessagingException | MailException exception) {
            log.warn("No se pudo enviar la factura {}", factura.numero(), exception);
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "No fue posible enviar la factura");
        }
    }

    /** N-10: Actualizar correo guarda el correo en el registro del cliente de la factura. */
    @Transactional
    public FacturaResponse actualizarCorreo(Long idFactura, String correo) {
        Factura factura = facturaService.requireFactura(idFactura);
        Cliente cliente = clienteMapper.selectById(factura.getIdCliente());
        cliente.setCorreo(correo.trim());
        clienteMapper.updateById(cliente);
        return facturaService.toResponse(factura, cliente, facturaService.detalleDe(idFactura));
    }

    private static String cuerpo(FacturaResponse factura) {
        StringBuilder filas = new StringBuilder();
        for (DetalleResponse linea : factura.detalle()) {
            filas.append("<tr><td>").append(HtmlUtils.htmlEscape(linea.descripcion()))
                    .append("</td><td>").append(linea.cantidad())
                    .append("</td><td>").append(FacturaImprimible.bolivianos(linea.precioUnitario()))
                    .append("</td><td>").append(FacturaImprimible.bolivianos(linea.subtotal()))
                    .append("</td></tr>");
        }
        return "<h2>HOSPIVET · Sistema de Gestión</h2>"
                + "<p><strong>Factura " + factura.numero() + "</strong><br>"
                + "Cliente: " + HtmlUtils.htmlEscape(factura.cliente()) + "<br>"
                + "Fecha de emisión: " + factura.fechaHora().format(FECHA) + "</p>"
                + "<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">"
                + "<tr><th>Descripción</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr>"
                + filas
                + "</table>"
                + "<p>Subtotal: " + FacturaImprimible.bolivianos(factura.subtotal()) + "<br>"
                + "Descuento: " + FacturaImprimible.bolivianos(factura.descuento()) + "<br>"
                + "<strong>Total: " + FacturaImprimible.bolivianos(factura.total()) + "</strong></p>";
    }
}
