package com.bankinc.service;

import com.bankinc.dto.ApiResponse;
import com.bankinc.entity.Tarjeta;
import com.bankinc.repository.TarjetaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }


    @Override
    public ApiResponse<Tarjeta> generarTarjeta(String idProducto, String primerNombre, String segundoNombre) {
        if(idProducto.length() != 6) {
            return new ApiResponse<>("ERROR", "El idProducto debe tener exactamente 6 digitos", null);
        }
        Optional<Tarjeta> existingTarjeta = tarjetaRepository.findByIdProductoAndNombreTitular(idProducto, primerNombre + " " + segundoNombre);
        if (existingTarjeta.isPresent()) {
            return new ApiResponse<>("ERROR", "Tarjeta ya existe con el idProducto y titular", null);
        }
        String numeroTarjeta = generarNumeroTarjeta(idProducto);
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(numeroTarjeta);
        tarjeta.setIdProducto(idProducto);
        tarjeta.setNombreTitular(primerNombre + " " + segundoNombre);
        tarjeta.setFechaExpiracion(calcularFechaExpiracion());
        tarjeta.setActiva(false);
        tarjeta.setBloqueada(false);
        tarjeta.setBalance(new BigDecimal(0));
        tarjetaRepository.save(tarjeta);
        return new ApiResponse<>("SUCCESS", "Tarjeta Generada Correctamente", tarjeta);

    }

    public String calcularFechaExpiracion() {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaExpiracion = fechaActual.plusYears(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        return fechaExpiracion.format(formatter);
    }

    public String generarNumeroTarjeta(String idProduct){
        if (idProduct == null || idProduct.length() != 6) throw new IllegalArgumentException("productId must be 6 digits");
        StringBuilder sb = new StringBuilder(idProduct);
        Random random = new Random();
        while (sb.length() < 16) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public ApiResponse<Tarjeta> activarTarjeta(String tarjetaId) {

        Optional<Tarjeta> tarjetaOpt = tarjetaRepository.findById(tarjetaId);
        if (tarjetaOpt.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        }
        Tarjeta tarjeta = tarjetaOpt.get();
        if (tarjeta.getActiva()) {
            return new ApiResponse<>("ERROR", "La tarjeta ya está activa", null);
        }
        tarjeta.setActiva(true);
        tarjeta.setBloqueada(false);
        tarjetaRepository.save(tarjeta);
        return new ApiResponse<>("SUCCESS", "Tarjeta activada correctamente", tarjeta);

    }

    @Override
    public ApiResponse<Tarjeta> bloquearTarjeta(String tarjetaId) {
        Optional<Tarjeta> tarjetaOpt = tarjetaRepository.findById(tarjetaId);
        if (tarjetaOpt.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        }
        Tarjeta tarjeta = tarjetaOpt.get();
        if (tarjeta.getBloqueada()) {
            return new ApiResponse<>("ERROR", "La tarjeta ya está bloqueada", null);
        }
        tarjeta.setActiva(false);
        tarjeta.setBloqueada(true);
        tarjetaRepository.save(tarjeta);
        return new ApiResponse<>("SUCCESS", "Tarjeta bloqueada correctamente", tarjeta);
    }

    @Override
    public ApiResponse<Tarjeta> recargarTarjeta(String tarjetaId, BigDecimal monto) {
        Optional<Tarjeta> tarjetaOpt = tarjetaRepository.findById(tarjetaId);
        if (tarjetaOpt.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        }else if(monto.compareTo(BigDecimal.ZERO) <= 0){
            return new ApiResponse<>("ERROR", "El monto debe ser mayor a cero", null);
        } else if (tarjetaOpt.get().getBloqueada()) {
            return new ApiResponse<>("ERROR", "La tarjeta está bloqueada", null);
        } else if (!tarjetaOpt.get().getActiva()) {
            return new ApiResponse<>("ERROR", "La tarjeta no está activa", null);
        } else {
            Tarjeta tarjeta = tarjetaOpt.get();
            tarjeta.setBalance(tarjeta.getBalance().add(monto));
            tarjetaRepository.save(tarjeta);
            return new ApiResponse<>("SUCCESS", "Tarjeta recargada correctamente", tarjeta);

        }
    }

    @Override
    public ApiResponse<Tarjeta> obtenerBalance(String tarjetaId) {
        Optional<Tarjeta> tarjetaOpt = tarjetaRepository.findById(tarjetaId);
        if (tarjetaOpt.isEmpty()) {
            return new ApiResponse<>("ERROR", "Tarjeta no encontrada", null);
        } else if (tarjetaOpt.get().getBloqueada()) {
            return new ApiResponse<>("ERROR", "La tarjeta está bloqueada", null);
        } else if (!tarjetaOpt.get().getActiva()) {
            return new ApiResponse<>("ERROR", "La tarjeta no está activa", null);
        } else {
            Tarjeta tarjeta = tarjetaOpt.get();
            return new ApiResponse<>("SUCCESS", "Balance obtenido correctamente", tarjeta);
        }
    }
}
