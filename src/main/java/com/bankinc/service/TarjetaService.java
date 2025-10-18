package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Tarjeta;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface TarjetaService {

    ApiResponse<Tarjeta> generarTarjeta(String idProducto, String primerNombre, String segundoNombre);
    ApiResponse<Tarjeta> activarTarjeta(String tarjetaId);
    ApiResponse<Tarjeta> bloquearTarjeta(String tarjetaId);
    ApiResponse<Tarjeta> recargarTarjeta(String tarjetaId, BigDecimal monto);
    ApiResponse<Tarjeta> obtenerBalance(String tarjetaId);
}
