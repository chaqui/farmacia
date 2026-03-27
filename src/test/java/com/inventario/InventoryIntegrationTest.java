package com.inventario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFlow_addProduct_search_createCompra_list_createVenta_list() throws Exception {
        // 1) Create provider
        Map<String, Object> prov = new HashMap<>();
        prov.put("nombre", "Proveedor Test");
        prov.put("direccion", "Calle 1");
        prov.put("telefono", "123");
        prov.put("email", "prov@test.com");
        prov.put("contacto", "Juan");

        mockMvc.perform(post("/proveedores").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov))).andExpect(status().isOk());

        // get provider id
        MvcResult resProv = mockMvc.perform(get("/proveedores").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JsonNode provList = objectMapper.readTree(resProv.getResponse().getContentAsString());
        assertThat(provList.isArray()).isTrue();
        Long proveedorId = provList.get(0).get("id").asLong();

        // 2) Create product under provider
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", "Producto A");
        producto.put("descripcion", "Descripcion A");
        producto.put("categoriaNombres", List.of("Cat1","Cat2"));

        mockMvc.perform(post("/proveedores/" + proveedorId + "/productos")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        // get products for provider
        MvcResult resProd = mockMvc.perform(get("/proveedores/" + proveedorId + "/productos")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        JsonNode prods = objectMapper.readTree(resProd.getResponse().getContentAsString());
        assertThat(prods.isArray()).isTrue();
        Long productoId = prods.get(0).get("id").asLong();

        // search product by name
        MvcResult resSearch = mockMvc.perform(get("/productos/buscar").param("q", "Producto A")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        JsonNode searchRes = objectMapper.readTree(resSearch.getResponse().getContentAsString());
        assertThat(searchRes.size()).isGreaterThanOrEqualTo(1);

        // 3) Create compra with lote for product
        Map<String, Object> lote = new HashMap<>();
        lote.put("fechaVencimiento", LocalDate.now().plusDays(30).toString());
        lote.put("precio", 10.0);
        lote.put("precioVenta", 15.0);
        lote.put("lote", "L-001");
        lote.put("idProducto", productoId);
        lote.put("cantidad", 100);

        Map<String, Object> compra = new HashMap<>();
        compra.put("idProveedor", proveedorId);
        compra.put("fecha", LocalDate.now().toString());
        compra.put("lotes", List.of(lote));

        mockMvc.perform(post("/compras").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compra))).andExpect(status().isCreated());

        // list compras
        MvcResult resCompras = mockMvc.perform(get("/compras").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JsonNode compras = objectMapper.readTree(resCompras.getResponse().getContentAsString());
        assertThat(compras.isArray()).isTrue();
        assertThat(compras.size()).isGreaterThanOrEqualTo(1);

        // 4) Create venta using lote L-001
        Map<String, Object> detalle = new HashMap<>();
        detalle.put("idProducto", productoId.intValue());
        detalle.put("cantidad", 2);
        detalle.put("lote", "L-001");

        Map<String, Object> venta = new HashMap<>();
        venta.put("cliente", "Cliente Test");
        venta.put("fecha", LocalDate.now().toString());
        venta.put("detalles", List.of(detalle));

        mockMvc.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta))).andExpect(status().isCreated());

        // list ventas
        MvcResult resVentas = mockMvc.perform(get("/ventas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JsonNode ventas = objectMapper.readTree(resVentas.getResponse().getContentAsString());
        assertThat(ventas.isArray()).isTrue();
        assertThat(ventas.size()).isGreaterThanOrEqualTo(1);
    }

}
