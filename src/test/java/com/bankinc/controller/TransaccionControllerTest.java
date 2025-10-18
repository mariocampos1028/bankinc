package com.bankinc.controller;

import com.bankinc.dto.AnularTransaccionRequest;
import com.bankinc.dto.ApiResponse;
import com.bankinc.dto.TransaccionRequest;
import com.bankinc.entity.Transaccion;
import com.bankinc.service.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionControllerTest {

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    private Transaccion transaccion;

    @BeforeEach
    void setUp() {
        transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setIdTarjeta("1234567890123456");

    }


    @Test
    void realizarTransaccion_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Transaccion> response = new ApiResponse<>("SUCCESS", "Transacción creada", transaccion);
        when(transaccionService.crearTransaccion(anyString(), any(BigDecimal.class)))
                .thenReturn(response);

        TransaccionRequest request = new TransaccionRequest();
        request.setCardId("1234567890123456");
        request.setPrice(new BigDecimal("50.00"));

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.realizarTransaccion(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        verify(transaccionService).crearTransaccion(anyString(), any(BigDecimal.class));
    }

    @Test
    void realizarTransaccion_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Transaccion> response = new ApiResponse<>("ERROR", "Saldo insuficiente", null);
        when(transaccionService.crearTransaccion(anyString(), any(BigDecimal.class)))
                .thenReturn(response);

        TransaccionRequest request = new TransaccionRequest();
        request.setCardId("1234567890123456");
        request.setPrice(new BigDecimal("9999.99"));

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.realizarTransaccion(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getMessage()).isEqualTo("Saldo insuficiente");
    }

    // --- TEST: Obtener transacción (OK) ---
    @Test
    void obtenerTransaccion_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Transaccion> response = new ApiResponse<>("SUCCESS", "Transacción encontrada", transaccion);
        when(transaccionService.obtenerTransaccion(anyLong())).thenReturn(response);

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.obtenerTransaccion(1L);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getData()).isEqualTo(transaccion);
        verify(transaccionService).obtenerTransaccion(1L);
    }

    // --- TEST: Obtener transacción (ERROR) ---
    @Test
    void obtenerTransaccion_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Transaccion> response = new ApiResponse<>("ERROR", "Transacción no encontrada", null);
        when(transaccionService.obtenerTransaccion(anyLong())).thenReturn(response);

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.obtenerTransaccion(99L);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getMessage()).isEqualTo("Transacción no encontrada");
    }

    // --- TEST: Anular transacción (OK) ---
    @Test
    void anularTransaccion_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Transaccion> response = new ApiResponse<>("SUCCESS", "Transacción anulada", transaccion);
        when(transaccionService.anularTransaccion(anyString(), anyLong())).thenReturn(response);

        AnularTransaccionRequest request = new AnularTransaccionRequest();
        request.setCardId("1234567890123456");
        request.setTransactionId(1L);

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.anularTransaccion(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getMessage()).isEqualTo("Transacción anulada");
        verify(transaccionService).anularTransaccion(anyString(), anyLong());
    }

    // --- TEST: Anular transacción (ERROR) ---
    @Test
    void anularTransaccion_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Transaccion> response = new ApiResponse<>("ERROR", "Fuera de tiempo", null);
        when(transaccionService.anularTransaccion(anyString(), anyLong())).thenReturn(response);

        AnularTransaccionRequest request = new AnularTransaccionRequest();
        request.setCardId("1234567890123456");
        request.setTransactionId(1L);

        ResponseEntity<ApiResponse<Transaccion>> result = transaccionController.anularTransaccion(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getMessage()).isEqualTo("Fuera de tiempo");
    }



}