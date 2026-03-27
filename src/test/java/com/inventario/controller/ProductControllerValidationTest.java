package com.inventario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
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
public class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_missingName_returnsValidationError() throws Exception {
        // create provider
        Map<String, Object> prov = new HashMap<>();
        prov.put("nombre", "ProvX");
        prov.put("direccion", "Dir");
        prov.put("telefono", "1");
        prov.put("email", "a@b.com");
        prov.put("contacto", "cx");
        mockMvc.perform(post("/proveedores").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov))).andExpect(status().isOk());

        MvcResult resProv = mockMvc.perform(get("/proveedores").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JsonNode provList = objectMapper.readTree(resProv.getResponse().getContentAsString());
        Long proveedorId = provList.get(0).get("id").asLong();

        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", "");
        producto.put("descripcion", "desc");

        MvcResult res = mockMvc.perform(post("/proveedores/" + proveedorId + "/productos")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest()).andReturn();

        JsonNode body = objectMapper.readTree(res.getResponse().getContentAsString());
        assertThat(body.get("message").asText()).isEqualTo("Validation failed");
        assertThat(body.get("errors").isArray()).isTrue();
    }

}
