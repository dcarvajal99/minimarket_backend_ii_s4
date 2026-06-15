package com.minimarket.service;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de ventas. Las dependencias (ProductoRepository,
 * VentaRepository y UsuarioService) se simulan con Mockito para probar la logica
 * de ventas de forma aislada: validacion de stock, calculo de total y relaciones
 * entre Venta, Usuario y Producto.
 */
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Usuario usuario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");

        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Agua Mineral 1L");
        producto.setPrecio(990.0);
        producto.setStock(50);
    }

    /** Crea un detalle de venta para el producto base con la cantidad indicada. */
    private DetalleVenta detalle(int cantidad, double precio) {
        DetalleVenta d = new DetalleVenta();
        d.setProducto(producto);
        d.setCantidad(cantidad);
        d.setPrecio(precio);
        return d;
    }

    @Test
    @DisplayName("calcularTotal suma correctamente precio por cantidad de cada detalle")
    void calcularTotal_variosDetalles_sumaCorrecta() {
        Venta venta = new Venta();
        venta.setDetalles(List.of(detalle(2, 990.0), detalle(3, 1500.0)));

        double total = ventaService.calcularTotal(venta);

        // 2*990 + 3*1500 = 1980 + 4500 = 6480
        assertEquals(6480.0, total, 0.001);
    }

    @Test
    @DisplayName("calcularTotal retorna 0 cuando la venta no tiene detalles")
    void calcularTotal_sinDetalles_retornaCero() {
        Venta venta = new Venta();
        assertEquals(0.0, ventaService.calcularTotal(venta), 0.001);
    }

    @Test
    @DisplayName("hayStockSuficiente retorna true cuando el stock alcanza (mock del repositorio)")
    void hayStockSuficiente_stockAlcanza_retornaTrue() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto)); // stock=50

        assertTrue(ventaService.hayStockSuficiente(10L, 30));
        verify(productoRepository).findById(10L);
    }

    @Test
    @DisplayName("hayStockSuficiente retorna false cuando la cantidad supera el stock")
    void hayStockSuficiente_stockInsuficiente_retornaFalse() {
        producto.setStock(5);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertFalse(ventaService.hayStockSuficiente(10L, 20));
    }

    @Test
    @DisplayName("registrarVenta guarda la venta cuando el usuario es valido y hay stock")
    void registrarVenta_usuarioValidoYStock_persiste() {
        Venta venta = new Venta();
        venta.setUsuario(usuario);                     // relacion Venta -> Usuario
        venta.setDetalles(List.of(detalle(2, 990.0))); // relacion Venta -> Producto

        when(usuarioService.datosCompletos(usuario)).thenReturn(true);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto)); // stock=50
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta guardada = ventaService.registrarVenta(venta);

        assertNotNull(guardada);
        assertEquals(usuario, guardada.getUsuario()); // se mantiene la relacion con el usuario
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    @DisplayName("registrarVenta lanza excepcion y NO guarda si no hay stock suficiente")
    void registrarVenta_sinStock_lanzaExcepcion() {
        producto.setStock(1);
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setDetalles(List.of(detalle(10, 990.0))); // pide 10, hay 1

        when(usuarioService.datosCompletos(usuario)).thenReturn(true);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(venta));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("registrarVenta lanza excepcion cuando el usuario es invalido o incompleto")
    void registrarVenta_usuarioInvalido_lanzaExcepcion() {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setDetalles(List.of(detalle(1, 990.0)));

        when(usuarioService.datosCompletos(usuario)).thenReturn(false); // usuario incompleto

        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(venta));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("findById retorna la venta cuando existe")
    void findById_existente_retornaVenta() {
        Venta venta = new Venta();
        venta.setId(99L);
        when(ventaRepository.findById(99L)).thenReturn(Optional.of(venta));

        assertEquals(99L, ventaService.findById(99L).getId());
        verify(ventaRepository).findById(99L);
    }

    @Test
    @DisplayName("findById retorna null cuando la venta no existe")
    void findById_inexistente_retornaNull() {
        when(ventaRepository.findById(404L)).thenReturn(Optional.empty());
        assertNull(ventaService.findById(404L));
    }

    @Test
    @DisplayName("findByUsuarioId retorna las ventas asociadas al usuario (relacion Venta-Usuario)")
    void findByUsuarioId_retornaVentasDelUsuario() {
        Venta v = new Venta();
        v.setUsuario(usuario);
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(v));

        List<Venta> ventas = ventaService.findByUsuarioId(1L);

        assertEquals(1, ventas.size());
        assertEquals(usuario, ventas.get(0).getUsuario());
        verify(ventaRepository).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("findAll retorna todas las ventas")
    void findAll_retornaLista() {
        when(ventaRepository.findAll()).thenReturn(List.of(new Venta(), new Venta()));
        assertEquals(2, ventaService.findAll().size());
    }
}
