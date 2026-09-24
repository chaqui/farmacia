package com.inventario.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.inventario.services.DevolucionVentaService;
import com.inventario.services.TicketPdfService;
import com.inventario.services.VentaService;
import com.inventario.services.VentaStateService;

@ExtendWith(MockitoExtension.class)
class VentaControllerRangoTest {

    @Mock
    private VentaService ventaService;

    @Mock
    private VentaStateService ventaStateService;

    @Mock
    private TicketPdfService ticketPdfService;

    @Mock
    private DevolucionVentaService devolucionVentaService;

    private VentaController controller;

    @BeforeEach
    void configurar() {
        controller = new VentaController(
                ventaService, ventaStateService, ticketPdfService, devolucionVentaService);
    }

    @Test
    void obtenerVentas_ConRangoValido_DelegaAlServicio() {
        LocalDate desde = LocalDate.of(2026, 9, 24);
        LocalDate hasta = LocalDate.of(2026, 9, 24);
        when(ventaService.obtenerVentas(desde, hasta)).thenReturn(List.of());

        var resultado = controller.obtenerVentas(desde, hasta);

        assertEquals(0, resultado.size());
        verify(ventaService).obtenerVentas(desde, hasta);
    }

    @Test
    void obtenerVentas_SinRango_MantieneCompatibilidad() {
        when(ventaService.obtenerVentas()).thenReturn(List.of());

        controller.obtenerVentas(null, null);

        verify(ventaService).obtenerVentas();
    }

    @Test
    void obtenerVentas_ConUnaFechaFaltante_RespondeBadRequest() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.obtenerVentas(LocalDate.of(2026, 9, 24), null));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(ventaService);
    }

    @Test
    void obtenerVentas_ConRangoInvertido_RespondeBadRequest() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.obtenerVentas(
                        LocalDate.of(2026, 9, 30), LocalDate.of(2026, 9, 1)));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(ventaService);
    }
}
