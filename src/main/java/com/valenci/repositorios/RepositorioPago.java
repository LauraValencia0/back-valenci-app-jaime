package com.valenci.repositorios;

import com.valenci.entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioPago extends JpaRepository<Pago, Integer> {

    List<Pago> findByPedidoIdPedido(int idPedido);
}
