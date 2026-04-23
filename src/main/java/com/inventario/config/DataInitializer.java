package com.inventario.config;

import com.inventario.models.Categoria;
import com.inventario.models.Compra;
import com.inventario.models.DetalleCompra;
import com.inventario.models.Fotografia;
import com.inventario.models.Lote;
import com.inventario.models.Producto;
import com.inventario.models.Proveedor;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.FotografiaRepository;
import com.inventario.repository.LoteRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.CompraRepository;
import com.inventario.repository.DetalleCompraRepository;
import com.inventario.repository.ProveedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final FotografiaRepository fotografiaRepository;

    public DataInitializer(CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository,
                           ProductoRepository productoRepository,
                           LoteRepository loteRepository,
                           CompraRepository compraRepository,
                           DetalleCompraRepository detalleCompraRepository,
                           FotografiaRepository fotografiaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.fotografiaRepository = fotografiaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only initialize if there are few products
        if (productoRepository.count() >= 100) return;

        Categoria mecanica = categoriaRepository.findAll().stream()
                .filter(c -> "Mecánica".equalsIgnoreCase(c.getNombre()))
                .findFirst().orElse(null);

        if (mecanica == null) {
            mecanica = new Categoria("Mecánica");
            mecanica.setDescripcion("Herramientas y repuestos de taller mecánico");
            mecanica = categoriaRepository.save(mecanica);
        }

        Proveedor proveedor = proveedorRepository.findAll().stream()
                .filter(p -> "Proveedor Mecánica".equalsIgnoreCase(p.getNombre()))
                .findFirst().orElse(null);

        if (proveedor == null) {
            proveedor = new Proveedor();
            proveedor.setNombre("Proveedor Mecánica");
            proveedor.setDireccion("Calle Taller 123");
            proveedor.setTelefono("+54 9 11 5555 0000");
            proveedor.setEmail("ventas@proveedormecanica.example");
            proveedor.setContacto("Juan Pérez");
            proveedor = proveedorRepository.save(proveedor);
        }

        Random rnd = new Random(42);
        for (int i = 1; i <= 100; i++) {
            Producto p = new Producto();
            p.setNombre("Herramienta Mecánica " + i);
            p.setDescripcion(generateDescripcion(i));
            p.setProveedor(proveedor);
            p.agregarCategoria(mecanica);
            // sample location fields moved to Producto
            p.setEstanteria("A" + (i % 10));
            p.setNivel((i % 5) + 1);
            p.setBodega("Bodega Central");
            productoRepository.save(p);

            // create a lote
            Lote lote = new Lote();
            lote.setLote("MEC-" + String.format("%03d", i));
            float precio = 100 + rnd.nextInt(900) + rnd.nextFloat();
            lote.setPrecio(precio);
            lote.setPrecioVenta(precio * (1 + (0.2f + rnd.nextFloat() * 0.3f))); // 20-50% markup
            lote.setPrecioDescuento(lote.getPrecioVenta() * 0.9f); // 10% discount as default
            lote.setFechaVencimiento(LocalDate.now().plusYears(1));
            lote.setProducto(p);
            // location now stored on producto
            loteRepository.save(lote);

            // create an initial compra + detalleCompra to give the lote an initial stock
            try {
                Compra compra = new Compra();
                compra.setProveedor(proveedor);
                compra.setFecha(Date.from(java.time.Instant.now()));
                compra = compraRepository.save(compra);

                long cantidadInicial = 5 + rnd.nextInt(46); // 5..50 unidades
                DetalleCompra detalle = new DetalleCompra(compra, lote, cantidadInicial);
                detalleCompraRepository.save(detalle);
            } catch (Exception ignore) {}

            // add a simple fotografia record if repository exists
            try {
                Fotografia f = new Fotografia();
                f.setProducto(p);
                fotografiaRepository.save(f);
            } catch (Exception ignore) {}
        }

        // relate some products: for each product, add next two as relacionados
        var all = productoRepository.findAll();
        int size = all.size();
        for (int i = 0; i < size; i++) {
            Producto a = all.get(i);
            if (i + 1 < size) a.agregarRelacionado(all.get((i + 1) % size));
            if (i + 2 < size) a.agregarRelacionado(all.get((i + 2) % size));
            productoRepository.save(a);
        }
    }

    private String generateDescripcion(int i) {
        switch (i % 10) {
            case 0: return "Llave ajustable de acero cromo vanadio, ideal para tuercas y tornillos";
            case 1: return "Juego de destornilladores con mango antideslizante, varias medidas";
            case 2: return "Juego de llaves de vaso para mecánica ligera, incluye estuche";
            case 3: return "Alicates universales con corte integrado y mango aislado";
            case 4: return "Martillo de bola con mango ergonómico y cabeza templada";
            case 5: return "Cinta métrica de 5m con carcasa resistente y cierre seguro";
            case 6: return "Pinzas de precisión para trabajos finos y eléctricos";
            case 7: return "Extractor de tornillos y pernos dañados, juego profesional";
            case 8: return "Llave dinamométrica ajustable con alta precisión";
            default: return "Kit de herramientas básicas para taller, resistente y durable";
        }
    }
}
