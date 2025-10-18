package com.bankinc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Transaccion {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_tarjeta", length = 16)
    private String idTarjeta;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;

    @Column(name = "anulada")
    private Boolean anulada;


}
