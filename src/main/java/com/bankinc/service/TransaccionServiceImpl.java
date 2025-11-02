package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Tarjeta;
import com.bankinc.entity.Transaccion;
import com.bankinc.repository.TarjetaRepository;
import com.bankinc.repository.Transaccionrepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransaccionServiceImpl implements TransaccionService {

    private final Transaccionrepository transaccionrepository;
    private final TarjetaRepository tarjetaRepository;

    public TransaccionServiceImpl(Transaccionrepository transaccionrepository, TarjetaRepository tarjetaRepository) {
        this.transaccionrepository = transaccionrepository;
        this.tarjetaRepository = tarjetaRepository;
    }


    @Override
    public ApiResponse<Transaccion> crearTransaccion(String idTarjeta, BigDecimal precio) {
        Optional<Tarjeta> tarjeta = tarjetaRepository.findById(idTarjeta);
        if (tarjeta.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        } else if (tarjeta.get().getBloqueada()) {
            return new ApiResponse<>("ERROR", "Tarjeta bloqueada", null);
        } else if (!tarjeta.get().getActiva()) {
            return new ApiResponse<>("ERROR", "Tarjeta no activa", null);
        } else if (validarFechaExpiracion(tarjeta.get().getFechaExpiracion())) {
            return new ApiResponse<>("ERROR", "Tarjeta expirada", null);
        }else{
            if (tarjeta.get().getBalance().compareTo(precio) < 0) {
                return new ApiResponse<>("ERROR", "Fondos insuficientes", null);
            }
            Tarjeta tarjetaActual = tarjeta.get();
            tarjetaActual.setBalance(tarjetaActual.getBalance().subtract(precio));
            tarjetaRepository.save(tarjetaActual);
            Transaccion transaccion = new Transaccion();
            transaccion.setIdTarjeta(idTarjeta);
            transaccion.setMonto(precio);
            transaccion.setFechaTransaccion(LocalDateTime.now());
            transaccion.setAnulada(false);
            transaccionrepository.save(transaccion);
            return new ApiResponse<>("SUCCESS", "Transaccion realizada correctamente", transaccion);

        }


    }

    public boolean validarFechaExpiracion(String vencimiento){

        String[] parts = vencimiento.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        LocalDate hoy = LocalDate.now();
        int mesActual = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        if (year < anioActual) {
            return true;
        } else if (year == anioActual && month < mesActual) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public ApiResponse<Transaccion> obtenerTransaccion(Long id) {
        Optional<Transaccion> transaccion = transaccionrepository.findById(id);
        if (transaccion.isEmpty()) {
            return new ApiResponse<>("ERROR", "Transaccion no encontrada", null);
        } else {
            return new ApiResponse<>("SUCCESS", "Transaccion encontrada", transaccion.get());
        }
    }

    @Override
    public ApiResponse<Transaccion> anularTransaccion(String idTarjeta, Long idTransaccion) {
        Optional<Tarjeta> tarjeta = tarjetaRepository.findById(idTarjeta);
        if (tarjeta.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        }else{
            if(tarjeta.get().getBloqueada()){
                return new ApiResponse<>("ERROR", "Tarjeta bloqueada", null);
            }
            Optional<Transaccion> transaccion = transaccionrepository.findByIdAndIdTarjeta(idTransaccion, idTarjeta);
            if (transaccion.isEmpty()) {
                return new ApiResponse<>("ERROR", "Transaccion no encontrada para la tarjeta proporcionada", null);
            } else if (transaccion.get().getAnulada()) {
                return new ApiResponse<>("ERROR", "Transaccion ya anulada", null);
            } else if (validarCaducidadTransaccion(transaccion.get().getFechaTransaccion())) {
                return new ApiResponse<>("ERROR", "Transaccion caducada para anular", null);
            }else{
                Transaccion trx = transaccion.get();
                trx.setAnulada(true);
                transaccionrepository.save(trx);
                Tarjeta tarjetaActual = tarjeta.get();
                tarjetaActual.setBalance(tarjetaActual.getBalance().add(trx.getMonto()));
                tarjetaRepository.save(tarjetaActual);
                return new ApiResponse<>("SUCCESS", "Transaccion anulada correctamente", trx);
            }
        }


    }

    @Override
    public ApiResponse<Iterable<Transaccion>> obtenerTransaccionesPorTarjeta(String cardId) {
        Optional<Tarjeta> tarjeta = tarjetaRepository.findById(cardId);
        if (tarjeta.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        } else {
            Iterable<Transaccion> transacciones = transaccionrepository.findAllByIdTarjeta(cardId);
            if (!transacciones.iterator().hasNext()) {
                return new ApiResponse<>("ERROR", "No se encontraron transacciones para esta tarjeta", null);
            }
            return new ApiResponse<>("SUCCESS", "Transacciones encontradas", transacciones);
        }
    }

    public boolean validarCaducidadTransaccion(LocalDateTime fechaTransaccion){
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = fechaTransaccion.plusHours(24);
        return ahora.isAfter(fechaLimite);

    }
}
