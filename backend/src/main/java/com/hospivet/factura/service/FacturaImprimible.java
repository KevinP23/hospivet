package com.hospivet.factura.service;

import com.hospivet.factura.dto.DetalleResponse;
import com.hospivet.factura.dto.FacturaResponse;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** R7.4.2: formato de impresión de la factura (detalle y total a pagar), en una página. */
class FacturaImprimible implements Printable {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FacturaResponse factura;

    FacturaImprimible(FacturaResponse factura) {
        this.factura = factura;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        int ancho = (int) pageFormat.getImageableWidth();
        int y = 20;

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.drawString("HOSPIVET · Sistema de Gestión", 0, y);
        y += 22;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g.drawString("Factura " + factura.numero(), 0, y);
        y += 18;
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g.drawString("Cliente: " + factura.cliente(), 0, y);
        y += 14;
        g.drawString("Fecha de emisión: " + factura.fechaHora().format(FECHA), 0, y);
        y += 24;

        int[] columnas = {0, (int) (ancho * 0.50), (int) (ancho * 0.65), (int) (ancho * 0.83)};
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        g.drawString("Descripción", columnas[0], y);
        g.drawString("Cantidad", columnas[1], y);
        g.drawString("Precio unitario", columnas[2], y);
        g.drawString("Subtotal", columnas[3], y);
        y += 6;
        g.drawLine(0, y, ancho, y);
        y += 14;

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        for (DetalleResponse linea : factura.detalle()) {
            g.drawString(linea.descripcion(), columnas[0], y);
            g.drawString(String.valueOf(linea.cantidad()), columnas[1], y);
            g.drawString(bolivianos(linea.precioUnitario()), columnas[2], y);
            g.drawString(bolivianos(linea.subtotal()), columnas[3], y);
            y += 16;
        }
        g.drawLine(0, y - 8, ancho, y - 8);
        y += 10;
        g.drawString("Subtotal: " + bolivianos(factura.subtotal()), 0, y);
        y += 14;
        g.drawString("Descuento: " + bolivianos(factura.descuento()), 0, y);
        y += 18;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g.drawString("Total a pagar: " + bolivianos(factura.total()), 0, y);
        return PAGE_EXISTS;
    }

    static String bolivianos(BigDecimal monto) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-BO"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return "Bs " + formato.format(monto);
    }
}
