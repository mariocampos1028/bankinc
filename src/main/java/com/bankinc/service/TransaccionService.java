package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Transaccion;

import java.math.BigDecimal;

public interface TransaccionService {

    ApiResponse<Transaccion> crearTransaccion(String idTarjeta, BigDecimal precio);
    ApiResponse<Transaccion> obtenerTransaccion(Long id);
    ApiResponse<Transaccion> anularTransaccion(String idTarjeta, Long idTransaccion);
}
