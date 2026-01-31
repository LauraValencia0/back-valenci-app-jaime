package com.valenci.entidades;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private int idFactura;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "fecha_factura")
    private LocalDateTime fechaFactura;

    @Column(name = "total_factura")
    private BigDecimal totalFactura;

    @Column(name = "iva")
    private BigDecimal iva;
}
