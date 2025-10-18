package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Tarjeta;
import com.bankinc.entity.Transaccion;
import com.bankinc.repository.TarjetaRepository;
import com.bankinc.repository.Transaccionrepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TransaccionServiceImplTest {

    @Mock
    private Transaccionrepository transaccionRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private TransaccionServiceImpl transaccionService;

    private Tarjeta tarjeta;

    @BeforeEach
    void setUp() {
        tarjeta = Tarjeta.builder()
                .id("1234560000000003")
                .balance(new BigDecimal("100.00"))
                .activa(true)
                .bloqueada(false)
                .fechaExpiracion("12/2028")
                .build();
    }

    @Test
    void crearTransaccion_success_reduces_balance_and_creates_transaction() {
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> {
            Transaccion t = i.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("30.00"));

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getMonto()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(tarjeta.getBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
        verify(transaccionRepository).save(any());
        verify(tarjetaRepository).save(any());
    }

    @Test
    void crearTransaccion_fails_when_insufficient_balance() {
        tarjeta.setBalance(new BigDecimal("10.00"));
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("50.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void crearTransaccion_fails_when_card_blocked() {
        tarjeta.setBloqueada(true);
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("50.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void crearTransaccion_fails_when_card_inactive() {
        tarjeta.setActiva(false);
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("50.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void crearTransaccion_fails_when_card_expired() {
        tarjeta.setFechaExpiracion("01/2020");
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("50.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void crearTransaccion_fails_when_card_not_found() {
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.empty());

        ApiResponse<Transaccion> resp = transaccionService.crearTransaccion(tarjeta.getId(), new BigDecimal("50.00"));

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void anular_within_24h_restores_balance_and_marks_annulled() {
        Transaccion tx = Transaccion.builder()
                .id(100L)
                .idTarjeta(tarjeta.getId())
                .monto(new BigDecimal("20.00"))
                .fechaTransaccion(LocalDateTime.now().minusHours(2))
                .anulada(false)
                .build();

        when(transaccionRepository.findByIdAndIdTarjeta(tx.getId(), tarjeta.getId())).thenReturn(Optional.of(tx));
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArgument(0));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(i -> i.getArgument(0));

        ApiResponse<Transaccion> result = transaccionService.anularTransaccion(tarjeta.getId(), tx.getId());

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getData().getAnulada()).isTrue();
        assertThat(tarjeta.getBalance()).isEqualByComparingTo(new BigDecimal("120.00"));
        verify(transaccionRepository).save(any());
        verify(tarjetaRepository).save(any());
    }

    @Test
    void anular_fails_if_more_than_24h() {
        Transaccion tx = Transaccion.builder()
                .id(101L)
                .idTarjeta(tarjeta.getId())
                .monto(new BigDecimal("20.00"))
                .fechaTransaccion(LocalDateTime.now().minusDays(2))
                .anulada(false)
                .build();

        when(transaccionRepository.findByIdAndIdTarjeta(tx.getId(), tarjeta.getId())).thenReturn(Optional.of(tx));
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        ApiResponse<Transaccion> resp = transaccionService.anularTransaccion(tarjeta.getId(), tx.getId());

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void anularTransaccion_fails_when_transaction_not_found() {
        when(transaccionRepository.findByIdAndIdTarjeta(999L, tarjeta.getId())).thenReturn(Optional.empty());
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));

        ApiResponse<Transaccion> resp = transaccionService.anularTransaccion(tarjeta.getId(), 999L);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    void anularTransaccion_fails_when_card_not_found() {
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.empty());

        ApiResponse<Transaccion> resp = transaccionService.anularTransaccion(tarjeta.getId(), 1L);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void anularTransaccion_fails_when_already_active(){
        tarjeta.setActiva(true);
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        ApiResponse<Transaccion> resp = transaccionService.anularTransaccion(tarjeta.getId(), 1L);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void anularTransaccion_fails_when_already_anulada(){

        when(transaccionRepository.findByIdAndIdTarjeta(1L, tarjeta.getId())).thenReturn(Optional.of(
                Transaccion.builder()
                        .id(1L)
                        .idTarjeta(tarjeta.getId())
                        .monto(new BigDecimal("20.00"))
                        .fechaTransaccion(LocalDateTime.now().minusHours(2))
                        .anulada(true)
                        .build()
        ));
        when(tarjetaRepository.findById(tarjeta.getId())).thenReturn(Optional.of(tarjeta));
        ApiResponse<Transaccion> resp = transaccionService.anularTransaccion(tarjeta.getId(), 1L);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void validarFechaExpiracion_returns_true_for_expired_date() {
        boolean result = transaccionService.validarFechaExpiracion("01/2020");
        assertTrue(result);
    }
    @Test
    void validarFechaExpiracion_returns_false_for_valid_date() {
        boolean result = transaccionService.validarFechaExpiracion("12/2028");
        assertFalse(result);
    }

    @Test
    void validarFechaExpiracion_returns_true_month_minus_actual_month() {
        boolean result = transaccionService.validarFechaExpiracion("01/2025");
        assertTrue(result);
    }

    @Test
    void obtenerTransaccion_success_when_found() {
        Transaccion tx = Transaccion.builder()
                .id(200L)
                .idTarjeta(tarjeta.getId())
                .monto(new BigDecimal("50.00"))
                .fechaTransaccion(LocalDateTime.now())
                .anulada(false)
                .build();

        when(transaccionRepository.findById(tx.getId())).thenReturn(Optional.of(tx));

        ApiResponse<Transaccion> resp = transaccionService.obtenerTransaccion(tx.getId());

        assertThat(resp.getStatus()).isEqualTo("SUCCESS");
        assertThat(resp.getData().getId()).isEqualTo(tx.getId());
    }

    @Test
    void obtenerTransaccion_fails_when_not_found() {
        when(transaccionRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse<Transaccion> resp = transaccionService.obtenerTransaccion(999L);

        assertThat(resp.getStatus()).isEqualTo("ERROR");
    }

}