# API Endpoints - Sistema de Ventas Mejorado

## Tabla de Contenidos
- [Códigos de Venta](#códigos-de-venta)
- [Devoluciones](#devoluciones)
- [Tickets PDF](#tickets-pdf)
- [Cancelación de Ventas](#cancelación-de-ventas)

---

## Códigos de Venta

El sistema ahora genera automáticamente un código único para cada venta con el formato: `VTA-YYYYMMDD-XXXXX`

**Ejemplo**: `VTA-20260423-71234`

### Estructura:
- **VTA**: Prefijo fijo
- **YYYYMMDD**: Fecha de la venta (2026-04-23)
- **XXXXX**: Número aleatorio de 5 dígitos

El código se genera automáticamente al crear la venta y se devuelve en la respuesta.

---

## Devoluciones

### Crear una Devolución (Se procesa automáticamente)

**POST** `/devoluciones` o **POST** `/ventas/{ventaId}/devolucion`

La devolución se procesa automáticamente al crearla. No es necesario llamar a un endpoint separado de procesamiento.

```json
{
    "ventaId": 1,
    "fecha": "2026-04-23",
    "motivo": "Producto defectuoso",
    "detalles": [
        {
            "detalleVentaId": 5,
            "cantidad": 2,
            "razonDevolucion": "Cliente reportó defectos de fabricación"
        },
        {
            "detalleVentaId": 6,
            "cantidad": 1,
            "razonDevolucion": "Equivocación en el pedido"
        }
    ]
}
```

**Respuesta (201 Created)**:
```json
{
    "id": 1,
    "codigoDevolucion": "DEV-20260423-a7f3c2",
    "fecha": "2026-04-23",
    "ventaId": 1,
    "nombreCliente": "Juan Pérez",
    "motivo": "Producto defectuoso",
    "total": 150.50,
    "procesada": true
}
```

### Obtener Todas las Devoluciones

**GET** `/devoluciones`

**Respuesta**:
```json
[
    {
        "id": 1,
        "codigoDevolucion": "DEV-20260423-a7f3c2",
        "fecha": "2026-04-23",
        "ventaId": 1,
        "nombreCliente": "Juan Pérez",
        "motivo": "Producto defectuoso",
        "total": 150.50,
        "procesada": true
    }
]
```

### Obtener Devolución por ID

**GET** `/devoluciones/{id}`

### Cancelar una Devolución

Elimina una devolución

**DELETE** `/devoluciones/{id}`

**Respuesta**: 204 No Content

---

## Devoluciones desde Venta Controller

Todos estos endpoints están disponibles bajo `/ventas` para una mejor organización:

### Crear una Devolución desde Venta

**POST** `/ventas/{ventaId}/devolucion`

La devolución se procesa automáticamente al crearla (ver [Crear una Devolución](#crear-una-devolución-se-procesa-automáticamente) para detalles del body).

### Obtener Devoluciones de una Venta Específica

**GET** `/ventas/{ventaId}/devoluciones`

**Respuesta**:
```json
[
    {
        "id": 1,
        "codigoDevolucion": "DEV-20260423-a7f3c2",
        "fecha": "2026-04-23",
        "ventaId": 1,
        "nombreCliente": "Juan Pérez",
        "motivo": "Producto defectuoso",
        "total": 150.50,
        "procesada": true
    }
]
```

---

## Tickets PDF

### Descargar Ticket de Venta

Descarga el PDF del ticket optimizado para impresora térmica (80mm)

**GET** `/ventas/{ventaId}/descargar-ticket`

**Headers de Respuesta**:
- `Content-Type`: `application/pdf`
- `Content-Disposition`: `attachment; filename="ticket-venta-{ventaId}.pdf"`

**Ejemplo JavaScript**:
```javascript
async function descargarTicket(ventaId) {
    const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ticket-${ventaId}.pdf`;
    a.click();
}
```

---

## Cancelación de Ventas

El sistema ya permitía cancelación de ventas. Usa el endpoint existente:

**DELETE** `/ventas/{ventaId}/cancelar`

### Estados de Venta

- **CREADA**: Venta acabada de crear
- **VERIFICADA**: Venta verificada (pendiente de autorización)
- **AUTORIZADA**: Venta completada
- **CANCELADA**: Venta cancelada

---

## Modelos de Datos

### Venta
```java
{
    id: Integer,
    codigoVenta: String,           // Generado automáticamente
    fecha: LocalDate,
    nombreCliente: String,
    estado: EstadoVenta,           // CREADA, VERIFICADA, AUTORIZADA, CANCELADA
    esCredito: Boolean,
    montoCredito: Float,
    cliente: Cliente,               // Nullable
    sucursal: Sucursal,            // Nullable
    detalleVentas: List<DetalleVenta>,
    devoluciones: List<DevolucionVenta>,
    creditos: List<Credito>
}
```

### DevolucionVenta
```java
{
    id: Integer,
    codigoDevolucion: String,      // Generado automáticamente (DEV-...)
    fecha: LocalDate,
    motivo: String,
    venta: Venta,
    detalles: List<DetalleDevolucionVenta>,
    procesada: Boolean
}
```

### DetalleDevolucionVenta
```java
{
    id: Integer,
    cantidad: Long,
    devolucion: DevolucionVenta,
    detalleVentaOriginal: DetalleVenta,
    razonDevolucion: String
}
```

---

## Códigos de Error

| Código | Mensaje | Causa |
|--------|---------|-------|
| 400 | Cantidad a devolver excede vendida | Intentaste devolver más de lo que se vendió |
| 400 | Detalle no pertenece a venta | El detalle pertenece a otra venta |
| 400 | La devolución ya fue procesada | Intentaste procesar una devolución ya procesada |
| 400 | No se puede cancelar devueltas procesadas | La devolución ya fue procesada |
| 404 | Venta no encontrada | El ID de venta no existe |
| 404 | Devolución no encontrada | El ID de devolución no existe |
| 500 | Error al generar PDF | Error interno al crear el PDF |

---

## Flujo Típico de Venta con Devolución

```
1. Crear venta (POST /ventas)
   ↓ Genera automáticamente: codigoVenta = "VTA-20260423-71234"
   
2. Verificar venta (POST /ventas/{ventaId}/verificar)
   
3. Autorizar venta (POST /ventas/{ventaId}/autorizar)
   ↓ Venta completada
   
4. [Opcional] Descargar ticket (GET /ventas/{ventaId}/descargar-ticket)
   ↓ Se abre diálogo de impresión
   
5. [Si hay error] Crear devolución (POST /ventas/{ventaId}/devolucion)
   ↓ Genera automáticamente: codigoDevolucion = "DEV-20260423-a7f3c2"
   ↓ Se procesa automáticamente: procesada = true
   ↓ Se envía notificación de devolución creada
   
6. [Opcional] Cancelar devolución (DELETE /devoluciones/{devolucionId})
   ↓ Si necesitas revertir una devolución
```

---

## Formato del Ticket PDF

El PDF está optimizado para impresoras térmicas de 80mm:

```
════════════════════════════════════
        TICKET DE VENTA
════════════════════════════════════
Código: VTA-20260423-71234
Fecha: 23/04/2026
Cliente: Juan Pérez

┌──────────────────────────────────┐
│ Item │ Cant. │  P.U.  │  Total  │
├──────────────────────────────────┤
│  1   │  2    │ 50.00  │ 100.00  │
│  2   │  1    │ 75.50  │  75.50  │
├──────────────────────────────────┤
│         TOTAL: $175.50           │
└──────────────────────────────────┘

Estado: Autorizada

¡Gracias por su compra!
════════════════════════════════════
```

---

## Testing con cURL

### Crear una devolución:
```bash
curl -X POST http://localhost:8080/devoluciones \
  -H "Content-Type: application/json" \
  -d '{
    "ventaId": 1,
    "fecha": "2026-04-23",
    "motivo": "Producto defectuoso",
    "detalles": [
      {
        "detalleVentaId": 5,
        "cantidad": 2,
        "razonDevolucion": "Defecto de fabricación"
      }
    ]
  }'
```

### Descargar ticket:
```bash
curl -X GET http://localhost:8080/ventas/1/descargar-ticket \
  -H "Accept: application/pdf" \
  -o ticket.pdf
```

### Crear devolución directamente desde venta (procesada automáticamente):
```bash
curl -X POST http://localhost:8080/ventas/1/devolucion \
  -H "Content-Type: application/json" \
  -d '{
    "fecha": "2026-04-23",
    "motivo": "Producto defectuoso",
    "detalles": [
      {
        "detalleVentaId": 5,
        "cantidad": 2,
        "razonDevolucion": "Defecto de fabricación"
      }
    ]
  }'
```

### Obtener devoluciones de una venta:
```bash
curl -X GET http://localhost:8080/ventas/1/devoluciones
```

---

## Notas Importantes

1. **Códigos Únicos**: Tanto `codigoVenta` como `codigoDevolucion` son únicos en la base de datos
2. **Devoluciones Automáticas**: La devolución se procesa automáticamente al crearla:
   - Se marca como procesada = true
   - Se envía notificación de devolución
   - No requiere un paso de procesamiento posterior
3. **Devoluciones Parciales**: Puedes devolver solo algunos items o cantidades parciales
4. **Validaciones Automáticas**: El sistema valida que:
   - La venta exista
   - El detalle pertenezca a la venta
   - La cantidad no exceda la vendida
5. **PDF Optimizado**: Diseñado específicamente para impresoras térmicas con márgenes mínimos
6. **Estado CANCELADA**: Cuando una venta se cancela, se libera el inventario automáticamente
7. **Endpoints organizados por contexto**:
   - Endpoints generales: `/devoluciones/*`
   - Endpoints desde venta: `/ventas/{ventaId}/devolucion*`

