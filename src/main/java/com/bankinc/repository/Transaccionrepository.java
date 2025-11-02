package com.bankinc.repository;

import com.bankinc.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface Transaccionrepository extends JpaRepository<Transaccion, Long> {

    Optional<Transaccion> findByIdAndIdTarjeta(Long id, String cardId);

    Iterable<Transaccion> findAllByIdTarjeta(String cardId);
}
