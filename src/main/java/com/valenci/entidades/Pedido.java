package com.valenci.entidades;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private int idPedido;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "total_pedido")
    private BigDecimal totalPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estadoPedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
}
