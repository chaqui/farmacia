package com.inventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.models.Venta;
import com.inventario.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
class VentaServiceRangoTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private SucursalLoteService sucursalLoteService;

    @Mock
    private LoteService loteService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void obtenerVentasPorRango_ConsultaRepositorioYMapeaDtos() {
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);
        Venta venta = new Venta();
        venta.setId(25);
        venta.setFecha(LocalDate.of(2026, 9, 24));
        venta.setNombreCliente("Cliente de prueba");

        when(ventaRepository.findByFechaBetweenOrderByFechaDescIdDesc(desde, hasta))
                .thenReturn(List.of(venta));

        var resultado = ventaService.obtenerVentas(desde, hasta);

        assertEquals(1, resultado.size());
        assertEquals(25, resultado.get(0).getId());
        assertEquals("Cliente de prueba", resultado.get(0).getCliente());
        assertEquals(LocalDate.of(2026, 9, 24), resultado.get(0).getFecha());
        verify(ventaRepository).findByFechaBetweenOrderByFechaDescIdDesc(desde, hasta);
    }

    @Test
    void obtenerVentasDeSucursalPorRango_UsaSucursalYFechas() {
        LocalDate desde = LocalDate.of(2026, 1, 1);
        LocalDate hasta = LocalDate.of(2026, 12, 31);

        when(ventaRepository.findBySucursalIdAndFechaBetweenOrderByFechaDescIdDesc(7L, desde, hasta))
                .thenReturn(List.of());

        var resultado = ventaService.obtenerVentas(7L, desde, hasta);

        assertEquals(0, resultado.size());
        verify(ventaRepository)
                .findBySucursalIdAndFechaBetweenOrderByFechaDescIdDesc(7L, desde, hasta);
    }
}
