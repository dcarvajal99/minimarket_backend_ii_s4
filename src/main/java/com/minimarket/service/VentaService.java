package com.minimarket.service;

import com.minimarket.entity.Venta;

import java.util.List;

public interface VentaService {
    List<Venta> findAll();
    Venta findById(Long id);
    Venta save(Venta venta);
    List<Venta> findByUsuarioId(Long usuarioId);

    /**
     * Calcula el total de una venta sumando precio * cantidad de cada detalle.
     */
    double calcularTotal(Venta venta);

    /**
     * Verifica si hay stock suficiente del producto indicado, consultando el
     * repositorio de productos (dependencia simulada en las pruebas con Mockito).
     */
    boolean hayStockSuficiente(Long productoId, int cantidadSolicitada);

    /**
     * Registra la venta validando que el usuario sea valido y que exista stock
     * suficiente para todos sus detalles.
     * @throws IllegalArgumentException si el usuario es invalido o no hay stock.
     */
    Venta registrarVenta(Venta venta);
}
