package com.bankinc.repository;

import com.bankinc.entity.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, String> {
    Optional<Tarjeta> findByIdProductoAndNombreTitular(String idProducto, String s);
}
