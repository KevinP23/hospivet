package com.hospivet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
        "com.hospivet.auth.mapper",
        "com.hospivet.veterinario.mapper",
        "com.hospivet.cliente.mapper",
        "com.hospivet.mascota.mapper",
        "com.hospivet.inventario.mapper",
        "com.hospivet.servicio.mapper",
        "com.hospivet.cita.mapper"
})
public class HospivetApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospivetApplication.class, args);
    }
}

