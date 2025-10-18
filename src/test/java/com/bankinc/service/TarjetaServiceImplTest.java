package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Tarjeta;
import com.bankinc.repository.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TarjetaServiceImplTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private TarjetaServiceImpl tarjetaService;

    private final String idProducto = "123456";
    private final String primerNombre = "Juan";
    private final String segundoNombre = "Perez";

    @BeforeEach
    void setUp() {
    }


    @Test
    void generarTarjeta_success_whenNoExisting() {
        when(tarjetaRepository.findByIdProductoAndNombreTitular(eq(idProducto), anyString()))
                .thenReturn(Optional.empty());
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Tarjeta> resp = tarjetaService.generarTarjeta(idProducto, primerNombre, segundoNombre);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData()).isNotNull();
        Tarjeta t = resp.getData();
        assertThat(t.getId()).hasSize(16);
        assertThat(t.getIdProducto()).isEqualTo(idProducto);
        assertThat(t.getNombreTitular()).isEqualTo(primerNombre + " " + segundoNombre);
        assertThat(t.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(tarjetaRepository, times(1)).save(any(Tarjeta.class));
    }

    @Test
    void generarTarjeta_fails_ifAlreadyExists() {
        Tarjeta existing = Tarjeta.builder().id("1234560000000001").idProducto(idProducto)
                .nombreTitular(primerNombre + " " + segundoNombre).build();

        when(tarjetaRepository.findByIdProductoAndNombreTitular(eq(idProducto), anyString()))
                .thenReturn(Optional.of(existing));

        ApiResponse<Tarjeta> resp = tarjetaService.generarTarjeta(idProducto, primerNombre, segundoNombre);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("Tarjeta ya existe con el idProducto y titular");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void recargar_aumenta_balance() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000002")
                .balance(new BigDecimal("10.00"))
                .bloqueada(false)
                .activa(true)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Tarjeta> resp = tarjetaService.recargarTarjeta(tarjeta.getId(), new BigDecimal("15.50"));

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getBalance()).isEqualByComparingTo(new BigDecimal("25.50"));
        verify(tarjetaRepository).save(any());
    }

    @Test
    void recargar_fails_whenNotFound() {

        String tarjetaId = "nonexistent";
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        ApiResponse<Tarjeta> resp = tarjetaService.recargarTarjeta(tarjetaId, new BigDecimal("10.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("Tarjeta no encontrada");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void recarga_fails_whenAmountNegative() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000002")
                .balance(new BigDecimal("10.00"))
                .bloqueada(false)
                .activa(true)
                .build();
        String tarjetaId = "1234560000000002";
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        ApiResponse<Tarjeta> resp = tarjetaService.recargarTarjeta(tarjetaId, new BigDecimal("-5.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("El monto debe ser mayor a cero");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void recarga_fails_whenCardBlocked() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000008")
                .bloqueada(true)
                .activa(true)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.recargarTarjeta(tarjeta.getId(), new BigDecimal("10.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta está bloqueada");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void recarga_fails_whenCardInactive() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000009")
                .bloqueada(false)
                .activa(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.recargarTarjeta(tarjeta.getId(), new BigDecimal("10.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta no está activa");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void activarTarjeta_success_whenInactive() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000003")
                .activa(false)
                .bloqueada(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Tarjeta> resp = tarjetaService.activarTarjeta(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getActiva()).isTrue();
        assertThat(resp.getData().getBloqueada()).isFalse();
        verify(tarjetaRepository).save(any());
    }

    @Test
    void activarTarjeta_fails_whenAlreadyActive() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000004")
                .activa(true)
                .bloqueada(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.activarTarjeta(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta ya está activa");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void activarTarjeta_fails_whenNotFound() {
        String tarjetaId = "nonexistent";
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        ApiResponse<Tarjeta> resp = tarjetaService.activarTarjeta(tarjetaId);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("Tarjeta no encontrada");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void bloquearTarjeta_success_whenActive() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000005")
                .activa(true)
                .bloqueada(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Tarjeta> resp = tarjetaService.bloquearTarjeta(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getActiva()).isFalse();
        assertThat(resp.getData().getBloqueada()).isTrue();
        verify(tarjetaRepository).save(any());
    }

    @Test
    void bloquearTarjeta_fails_whenAlreadyBlocked() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000006")
                .activa(false)
                .bloqueada(true)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.bloquearTarjeta(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta ya está bloqueada");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void bloquearTarjeta_fails_whenNotFound() {
        String tarjetaId = "nonexistent";
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        ApiResponse<Tarjeta> resp = tarjetaService.bloquearTarjeta(tarjetaId);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("Tarjeta no encontrada");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void obtenerBalance_success_whenFound() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000010")
                .balance(new BigDecimal("50.00"))
                .activa(true)
                .bloqueada(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.obtenerBalance(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getBalance()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void obtenerBalance_fails_whenNotFound() {
        String tarjetaId = "nonexistent";
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        ApiResponse<Tarjeta> resp = tarjetaService.obtenerBalance(tarjetaId);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("Tarjeta no encontrada");
    }

    @Test
    void obtenerBalance_fails_whenCardBlocked() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000011")
                .bloqueada(true)
                .activa(true)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.obtenerBalance(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta está bloqueada");
    }

    @Test
    void obtenerBalance_fails_whenCardInactive() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id("1234560000000012")
                .bloqueada(false)
                .activa(false)
                .build();

        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Tarjeta> resp = tarjetaService.obtenerBalance(tarjeta.getId());

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        assertThat(resp.getMessage()).containsIgnoringCase("La tarjeta no está activa");
    }

}