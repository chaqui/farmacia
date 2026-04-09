package com.inventario.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.dto.ProductoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Categoria;
import com.inventario.models.Fotografia;
import com.inventario.models.Producto;
import com.inventario.models.Proveedor;
import com.inventario.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoService productoService;

    @Test
    public void crearProducto_Valido_GuardaYAsociaCategoriasFotografiasRelacionados() {
        ProductoDto.Post dto = new ProductoDto.Post();
        dto.setNombre("TestProd");
        dto.setDescripcion("Desc");
        dto.setFotografias(List.of("http://img/1"));
        dto.setRelacionadosIds(List.of(2L));
        dto.setCategoriaIds(List.of(10L));
        dto.setCategoriaNombres(List.of("NuevaCat"));

        Proveedor proveedor = new Proveedor();
        proveedor.setId(5L);
        proveedor.setNombre("Prov");

        Producto relacionado = new Producto();
        relacionado.setId(2L);
        relacionado.setNombre("Relacionado");

        Categoria catExisting = new Categoria();
        catExisting.setId(10L);
        catExisting.setNombre("Existente");

        Categoria catNew = new Categoria();
        catNew.setNombre("NuevaCat");

        when(productoRepository.findById(2L)).thenReturn(Optional.of(relacionado));
        when(categoriaService.obtenerCategoria(10L)).thenReturn(catExisting);
        when(categoriaService.crearSiNoExiste("NuevaCat")).thenReturn(catNew);

        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            if (p.getId() == null)
                p.setId(1L);
            return p;
        });

        try {
            productoService.crearProducto(dto, proveedor);
        } catch (HttpException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository, atLeastOnce()).save(captor.capture());

        List<Producto> savedList = captor.getAllValues();
        assertFalse(savedList.isEmpty());
        Producto saved = savedList.get(savedList.size() - 1);

        assertEquals("TestProd", saved.getNombre());
        assertEquals(proveedor, saved.getProveedor());
        assertTrue(saved.getFotografias().stream().anyMatch(f -> "http://img/1".equals(((Fotografia) f).getUrl())));
        assertTrue(saved.getRelacionados().stream().anyMatch(p -> p.getId().equals(2L)));
        assertTrue(saved.getCategorias().stream().anyMatch(c -> "Existente".equals(c.getNombre())));
        assertTrue(saved.getCategorias().stream().anyMatch(c -> "NuevaCat".equals(c.getNombre())));
    }

    @Test
    public void obtenerProducto_NoExiste_LanzaHttpException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HttpException.class, () -> productoService.obtenerProducto(99L));
    }

    @Test
    public void buscarProductosPorNombre_RetornaDtos() {
        Producto p = new Producto();
        p.setId(7L);
        p.setNombre("MyProd");
        when(productoRepository.findByNombreContainingIgnoreCase("My")).thenReturn(List.of(p));

        var results = productoService.buscarProductosPorNombre("My");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("MyProd", results.get(0).getNombre());
        assertEquals(7L, results.get(0).getId());
    }

}
