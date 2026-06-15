package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.UsuarioService;
import com.minimarket.service.VentaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    // Inyeccion por constructor: facilita el uso de @InjectMocks en las pruebas.
    public VentaServiceImpl(VentaRepository ventaRepository,
                            ProductoRepository productoRepository,
                            UsuarioService usuarioService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public double calcularTotal(Venta venta) {
        if (venta == null || venta.getDetalles() == null) {
            return 0.0;
        }
        double total = 0.0;
        for (DetalleVenta detalle : venta.getDetalles()) {
            total += detalle.getPrecio() * detalle.getCantidad();
        }
        return total;
    }

    @Override
    public boolean hayStockSuficiente(Long productoId, int cantidadSolicitada) {
        // Consulta el producto en el repositorio (dependencia simulada en pruebas).
        Optional<Producto> producto = productoRepository.findById(productoId);
        return producto.isPresent() && producto.get().getStock() >= cantidadSolicitada;
    }

    @Override
    public Venta registrarVenta(Venta venta) {
        // 1) El usuario asociado debe ser valido y tener sus datos completos.
        if (venta.getUsuario() == null
                || !usuarioService.datosCompletos(venta.getUsuario())) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario valido y completo");
        }
        // 2) Cada detalle debe tener stock suficiente.
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Long productoId = detalle.getProducto().getId();
                if (!hayStockSuficiente(productoId, detalle.getCantidad())) {
                    throw new IllegalArgumentException(
                            "Stock insuficiente para el producto con id " + productoId);
                }
            }
        }
        return ventaRepository.save(venta);
    }
}
