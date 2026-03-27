package com.inventario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class CompraVentaValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearCompra_missingProveedor_returnsBadRequest() throws Exception {
        Map<String, Object> lote = new HashMap<>();
        lote.put("fechaVencimiento", LocalDate.now().plusDays(10).toString());
        lote.put("precio", 10.0);
        lote.put("precioVenta", 15.0);
        lote.put("lote", "L-002");
        lote.put("idProducto", 99999);
        lote.put("cantidad", 1);

        Map<String, Object> compra = new HashMap<>();
        // no idProveedor
        compra.put("fecha", LocalDate.now().toString());
        compra.put("lotes", List.of(lote));

        mockMvc.perform(post("/compras").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compra))).andExpect(status().isBadRequest());
    }

    @Test
    void crearVenta_missingCliente_returnsBadRequest() throws Exception {
        Map<String, Object> detalle = new HashMap<>();
        detalle.put("idProducto", 1);
        detalle.put("cantidad", 1);
        detalle.put("lote", "L-XXX");

        Map<String, Object> venta = new HashMap<>();
        // missing cliente
        venta.put("fecha", LocalDate.now().toString());
        venta.put("detalles", List.of(detalle));

        mockMvc.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta))).andExpect(status().isBadRequest());
    }

    @Test
    void listCompras_and_listVentas_returnArrays() throws Exception {
        MvcResult resC = mockMvc.perform(get("/compras").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();
        JsonNode compras = objectMapper.readTree(resC.getResponse().getContentAsString());
        assertThat(compras.isArray()).isTrue();

        MvcResult resV = mockMvc.perform(get("/ventas").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();
        JsonNode ventas = objectMapper.readTree(resV.getResponse().getContentAsString());
        assertThat(ventas.isArray()).isTrue();
    }

}
