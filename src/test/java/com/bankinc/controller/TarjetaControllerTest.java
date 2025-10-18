package com.bankinc.controller;

import com.bankinc.dto.ActivarTarjetaRequest;
import com.bankinc.dto.ApiResponse;
import com.bankinc.dto.BalanceRequest;
import com.bankinc.entity.Tarjeta;
import com.bankinc.service.TarjetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TarjetaControllerTest {

    @Mock
    private TarjetaService tarjetaService;

    @InjectMocks
    private TarjetaController tarjetaController;

    private Tarjeta tarjeta;

    @BeforeEach
    void setUp() {
        tarjeta = new Tarjeta();
        tarjeta.setId("1234567890123456");
        tarjeta.setIdProducto("123456");
        tarjeta.setNombreTitular("Juan Pérez");
    }


    @Test
    void generarTarjeta_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("SUCCESS", "Generada", tarjeta);
        when(tarjetaService.generarTarjeta(anyString(), anyString(), anyString()))
                .thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.generarTarjeta("123456", "Juan", "Pérez");

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        verify(tarjetaService).generarTarjeta(anyString(), anyString(), anyString());
    }

    @Test
    void generarTarjeta_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("ERROR", "Ya existe", null);
        when(tarjetaService.generarTarjeta("123456", "Juan", "Pérez")).thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.generarTarjeta("123456", "Juan", "Pérez");

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getStatus()).isEqualTo("ERROR");
    }

    @Test
    void activarTarjeta_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("SUCCESS", "Activada", tarjeta);
        when(tarjetaService.activarTarjeta("1234567890123456")).thenReturn(response);

        ActivarTarjetaRequest request = new ActivarTarjetaRequest();
        request.setCardId("1234567890123456");

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.activarTarjeta(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        verify(tarjetaService).activarTarjeta("1234567890123456");
    }

    @Test
    void activarTarjeta_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("ERROR", "No encontrada", null);
        when(tarjetaService.activarTarjeta("1234567890123456")).thenReturn(response);

        ActivarTarjetaRequest request = new ActivarTarjetaRequest();
        request.setCardId("1234567890123456");

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.activarTarjeta(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getStatus()).isEqualTo("ERROR");
    }

    @Test
    void bloquearTarjeta_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("SUCCESS", "Bloqueada", tarjeta);
        when(tarjetaService.bloquearTarjeta("1234567890123456")).thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.bloquearTarjeta("1234567890123456");

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        verify(tarjetaService).bloquearTarjeta("1234567890123456");
    }

    @Test
    void bloquearTarjeta_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("ERROR", "No encontrada", null);
        when(tarjetaService.bloquearTarjeta("1234567890123456")).thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.bloquearTarjeta("1234567890123456");

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getStatus()).isEqualTo("ERROR");
    }

    @Test
    void cargarTarjeta_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("SUCCESS", "Recargada", tarjeta);
        when(tarjetaService.recargarTarjeta("1234567890123456", new BigDecimal("100.00"))).thenReturn(response);

        BalanceRequest request = new BalanceRequest();
        request.setCardId("1234567890123456");
        request.setBalance(new BigDecimal("100.00"));

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.cargarTarjeta(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getMessage()).isEqualTo("Recargada");
        verify(tarjetaService).recargarTarjeta("1234567890123456", new BigDecimal("100.00"));
    }

    @Test
    void cargarTarjeta_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("ERROR", "No encontrada", null);
        when(tarjetaService.recargarTarjeta("1234567890123456", new BigDecimal("100.00"))).thenReturn(response);

        BalanceRequest request = new BalanceRequest();
        request.setCardId("1234567890123456");
        request.setBalance(new BigDecimal("100.00"));

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.cargarTarjeta(request);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getStatus()).isEqualTo("ERROR");
    }

    @Test
    void obtenerBalance_shouldReturnOk_whenServiceReturnsSuccess() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("SUCCESS", "Balance consultado", tarjeta);
        when(tarjetaService.obtenerBalance("1234567890123456")).thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.obtenerBalance("1234567890123456");

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getData()).isEqualTo(tarjeta);
        verify(tarjetaService).obtenerBalance("1234567890123456");
    }

    @Test
    void obtenerBalance_shouldReturnBadRequest_whenServiceReturnsError() {
        ApiResponse<Tarjeta> response = new ApiResponse<>("ERROR", "No encontrada", null);
        when(tarjetaService.obtenerBalance("1234567890123456")).thenReturn(response);

        ResponseEntity<ApiResponse<Tarjeta>> result = tarjetaController.obtenerBalance("1234567890123456");

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getStatus()).isEqualTo("ERROR");
    }

}