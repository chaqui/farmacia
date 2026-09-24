package com.inventario.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.inventario.services.SucursalService;

@ExtendWith(MockitoExtension.class)
class SucursalControllerRangoTest {

    @Mock
    private SucursalService sucursalService;

    @InjectMocks
    private SucursalController controller;

    @Test
    void obtenerVentas_ConRangoValido_IncluyeSucursal() {
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);
        when(sucursalService.obtenerVentas(3L, desde, hasta)).thenReturn(List.of());

        var resultado = controller.obtenerVentas(3L, desde, hasta);

        assertEquals(0, resultado.size());
        verify(sucursalService).obtenerVentas(3L, desde, hasta);
    }

    @Test
    void obtenerVentas_ConRangoInvertido_RespondeBadRequest() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.obtenerVentas(
                        3L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 9, 1)));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(sucursalService);
    }
}
