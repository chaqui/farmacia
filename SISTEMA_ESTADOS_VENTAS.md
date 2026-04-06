# Sistema de Estados de Ventas

## Descripción General

El sistema de ventas implementa un flujo de 4 estados para controlar el ciclo de vida de una venta y la gestión de inventario:

1. **CREADA** ✓ Bloqueados los productos
2. **VERIFICADA** - Verificación de detalles de venta
3. **AUTORIZADA** - Generación de créditos y venta confirmada
4. **CANCELADA** - Se liberan los productos

---

## Estados Detallados

### 1. CREADA
**Cuando se crea una venta:**
- La venta se registra con estado inicial `CREADA`
- Se asocian los detalles (productos y cantidades) a la venta
- **Los productos se BLOQUEAN**: Se reduce inmediatamente la cantidad disponible en el inventario
- Los productos no pueden ser vendidos a otros clientes mientras esté en este estado

**Endpoint:**
```
POST /ventas
Body: {
  "fecha": "2024-01-15",
  "cliente": "Juan Perez",
  "detalles": [
    {
      "lote": "LOTE001",
      "cantidad": 5
    }
  ]
}
```

---

### 2. VERIFICADA
**Transición: CREADA → VERIFICADA (o directamente a AUTORIZADA si es al contado)**

Cuando se verifica una venta, se especifica si es a **CRÉDITO** o al **CONTADO**:
- Parámetro: `esCredito` (true = crédito, false = contado)
- Si es **a crédito** (esCredito=true):
  - Se valida que el cliente tenga límite de crédito disponible
  - El montoCredito = total de la venta
  - Cambiar estado a VERIFICADA (requiere autorización manual)
- Si es **al contado** (esCredito=false):
  - El montoCredito = 0
  - El montoContado = total de la venta (es la diferencia)
  - Se **AUTORIZA automáticamente** (salta el estado VERIFICADA)
- **Los productos permanecen bloqueados**

**Endpoint:**
```
POST /ventas/estados/{ventaId}/verificar?esCredito=true
POST /ventas/estados/{ventaId}/verificar?esCredito=false
```

**Parámetro:**
- `esCredito`: `true` si es venta a crédito, `false` si es al contado

**Validaciones:**
- Venta debe estar en estado CREADA
- Venta debe tener al menos un detalle
- Si es crédito (esCredito=true): validar límite de crédito del cliente

**Comportamiento especial:**
- Si esCredito=false: la venta se autoriza automáticamente sin pasar por VERIFICADA
- Si esCredito=true: la venta queda en VERIFICADA hasta ser autorizada manualmente

**Ejemplos:**

Venta a crédito (requiere autorización manual):
```
POST /ventas/estados/1/verificar?esCredito=true
→ Estado: CREADA → VERIFICADA
→ montoCredito = 8000
→ Requiere: POST /ventas/estados/1/autorizar
```

Venta al contado (autoriza automáticamente):
```
POST /ventas/estados/1/verificar?esCredito=false
→ Estado: CREADA → VERIFICADA → AUTORIZADA (automático)
→ montoCredito = 0
→ montoContado = 8000 (diferencia)
→ La venta está lista, sin necesidad de autorización
```

---

### 3. AUTORIZADA
**Transición: VERIFICADA → AUTORIZADA**

Cuando se autoriza una venta:
- La venta se confirma como finalizada
- Si es **venta a CRÉDITO**:
  - Se crea automáticamente un registro de crédito asociado a la venta
  - El monto del crédito = total de la venta
  - Se registra contra el límite de crédito del cliente
- Si es **venta AL CONTADO**:
  - Solo se confirma la venta
  - No se genera crédito
- **Los productos quedan definitivamente descontados del inventario** (se hizo en CREADA)

**Endpoint:**
```
POST /ventas/estados/{ventaId}/autorizar
```

**Validaciones:**
- Venta debe estar en estado VERIFICADA
- Si es crédito: se genera automáticamente

---

### 4. CANCELADA
**Transición: CREADA/VERIFICADA → CANCELADA**

Cuando se cancela una venta:
- La venta pasa a estado `CANCELADA`
- Se **liberan todos los productos bloqueados** al eliminar los DetalleVenta
- Los productos vuelven a estar disponibles en el inventario
- **NO se puede cancelar una venta AUTORIZADA**

**Endpoint:**
```
POST /ventas/estados/{ventaId}/cancelar
```

**Validaciones:**
- Venta debe estar en estado CREADA o VERIFICADA
- NO se puede cancelar una venta AUTORIZADA

**Efecto:**
- Cambiar estado a CANCELADA
- Eliminar todos los DetalleVenta (liberan los productos)
- Los productos vuelven al inventario disponible como si la venta nunca hubiera ocurrido

---

## Flujo Completo de Ejemplo

### Escenario: Venta a Crédito

**Paso 1: Crear Venta (Estado: CREADA)**
```
POST /ventas
{
  "fecha": "2024-01-15",
  "cliente": "Juan Perez",
  "detalles": [
    {"lote": "LOTE001", "cantidad": 10}
  ]
}
Respuesta: ventaId = 1, total = 8000

Efecto: 
- Venta #1 creada en estado CREADA
- Lote LOTE001: Cantidad reducida en 10 unidades (BLOQUEADAS)
```

**Paso 2: Verificar Venta (Estado: VERIFICADA) - Es crédito**
```
POST /ventas/estados/1/verificar?esCredito=true

Validaciones:
- Venta 1 está en CREADA ✓
- Tiene detalles ✓
- Cliente "Juan Perez" tiene crédito disponible para 8000 ✓

Efecto:
- Venta #1 cambia a estado VERIFICADA
- montoCredito = 8000
- montoContado = 0 (diferencia)
- Productos siguen bloqueados
```

**Paso 3: Autorizar Venta (Estado: AUTORIZADA)**
```
POST /ventas/estados/1/autorizar

Validaciones:
- Venta 1 está en VERIFICADA ✓
- Es crédito (esCredito=true) ✓

Efecto:
- Venta #1 cambia a estado AUTORIZADA
- Se crea automáticamente registro de Crédito:
  - Cliente: Juan Perez
  - Monto: 8000 (total de la venta)
  - Venta asociada: #1
- Productos definitivamente descontados del inventario
```

---

## Flujo Alterno: Venta al Contado (autorización automática)

**Paso 1: Crear Venta (Estado: CREADA)**
- Igual que el ejemplo anterior

**Paso 2: Verificar Venta (Estado: AUTORIZADA) - Es contado, se autoriza automáticamente**
```
POST /ventas/estados/1/verificar?esCredito=false

Validaciones:
- Venta 1 está en CREADA ✓
- Tiene detalles ✓
- No requiere validación de crédito

Efecto:
- Venta #1 cambia a estado CREADA → VERIFICADA → AUTORIZADA (automático)
- esCredito = false
- montoCredito = 0
- montoContado = 8000 (resta automática)
- NO se crea crédito (esCredito=false)
- ✓ Venta completada en un paso

Resultado: La venta está autorizada, sin necesidad de paso adicional
```

---

## Comparativa de Flujos

### Venta a Crédito: 3 pasos
```
1. POST /ventas → CREADA
2. POST /ventas/estados/{id}/verificar?esCredito=true → VERIFICADA
3. POST /ventas/estados/{id}/autorizar → AUTORIZADA (crea crédito)
```

### Venta al Contado: 1 paso (autorización automática)
```
1. POST /ventas → CREADA
2. POST /ventas/estados/{id}/verificar?esCredito=false → AUTORIZADA (automático, sin crédito)

Sin necesidad de paso 3
```

---

## Flujo de Cancelación

### Escenario 1: Cancelar en estado CREADA
```
POST /ventas/estados/1/cancelar

Validaciones:
- Venta 1 está en CREADA ✓

Efecto:
- Venta #1 cambia a estado CANCELADA
- Se eliminan todos los DetalleVenta
- Lote LOTE001: Cantidad se recupera (+10 unidades)
- Los productos vuelven a estar disponibles
```

### Escenario 2: Cancelar en estado VERIFICADA
```
POST /ventas/estados/2/cancelar

Validaciones:
- Venta 2 está en VERIFICADA ✓

Efecto:
- Venta #2 cambia a estado CANCELADA
- Se eliminan todos los DetalleVenta
- Productos se liberan (recuperan cantidad)
```

### Escenario 3: No se puede cancelar AUTORIZADA
```
POST /ventas/estados/3/cancelar

Validaciones:
- Venta 3 está en AUTORIZADA ✗

Respuesta: "No se puede cancelar una venta en estado AUTORIZADA. 
            Solo se pueden cancelar ventas en estado CREADA o VERIFICADA" (400)
```

---

## Endpoints de Consulta

### Obtener estado de una venta
```
GET /ventas/estados/{ventaId}

Respuesta: "CREADA", "VERIFICADA", "AUTORIZADA" o "CANCELADA"
```

### Obtener detalles completos de una venta
```
GET /ventas/estados/{ventaId}/detalle

Respuesta: Objeto Venta con:
- id
- fecha
- clienteNombre
- estado (CREADA, VERIFICADA, AUTORIZADA, CANCELADA)
- montoCredito (monto que va a crédito)
- montoContado (monto que va al contado)
- detalleVentas (lista de productos y cantidades)
- total (suma de montoCredito + montoContado)
```

### Obtener todas las ventas de un estado específico
```
GET /ventas/estados/por-estado?estado=CREADA

Parámetro:
- estado: CREADA, VERIFICADA, AUTORIZADA o CANCELADA

Respuesta: Lista de ventas en ese estado
[
  {
    "id": 1,
    "fecha": "2024-01-15",
    "cliente": "Juan Perez",
    "estado": "CREADA",
    "total": 5000.00,
    "detalles": [...]
  }
]
```

### Obtener todas las ventas
```
GET /ventas/estados/todas

Respuesta: Lista de todas las ventas con su estado actual
[
  {"id": 1, "estado": "CREADA", ...},
  {"id": 2, "estado": "VERIFICADA", ...},
  {"id": 3, "estado": "AUTORIZADA", ...},
  {"id": 4, "estado": "CANCELADA", ...}
]
```

---

## Gestión de Inventario por Estado

| Acción | CREADA | VERIFICADA | AUTORIZADA | CANCELADA |
|--------|--------|-----------|-----------|-----------|
| ¿Bloqueados los productos? | ✓ SÍ | ✓ SÍ | ✓ SÍ | ✗ NO |
| ¿Descontados del inventario? | ✓ SÍ | ✓ SÍ | ✓ SÍ | ✗ NO |
| ¿Se puede cancelar? | ✓ SÍ | ✓ SÍ | ✗ NO | ✗ NO |
| ¿Se puede modificar? | ? | ? | ✗ NO | ✗ NO |
| ¿Crédito creado? | ✗ NO | ✗ NO | ✓ (si esCredito=true) | ✗ NO |
| ¿esCredito especificado? | ✗ NO | ✓ SÍ | ✓ SÍ | ✓ SÍ |
| ¿montoCredito calculado? | ✗ NO | ✓ (total si true, 0 si false) | ✓ SÍ | ✓ SÍ |

---

## Manejo de Errores

### Error: Venta no encontrada (404)
```
GET /ventas/estados/999/
Respuesta: "Venta no encontrada" (404)
```

### Error: Transición de estado inválida
```
POST /ventas/estados/1/verificar?esCredito=true
Si venta #1 ya está en VERIFICADA:
Respuesta: "La venta debe estar en estado CREADA para ser verificada" (400)
```

### Error: Crédito insuficiente
```
POST /ventas/estados/1/verificar?esCredito=true
Si cliente no tiene límite de crédito disponible:
Respuesta: "Crédito insuficiente. Disponible: 5000, Solicitado: 8000" (400)
```

### Error: No se puede cancelar
```
POST /ventas/estados/3/cancelar
Si la venta está AUTORIZADA:
Respuesta: "No se puede cancelar una venta en estado AUTORIZADA. 
            Solo se pueden cancelar ventas en estado CREADA o VERIFICADA" (400)
```

---

### Estructura de Datos

### Modelo Venta
```java
@Entity
public class Venta {
    Integer id;
    LocalDate fecha;
    String clienteNombre;
    EstadoVenta estado;           // CREADA, VERIFICADA, AUTORIZADA, CANCELADA
    Boolean esCredito;            // true = a crédito, false = al contado
    Float montoCredito;           // Si esCredito=true: total de venta
                                  // Si esCredito=false: 0
    Sucursal sucursal;
    List<DetalleVenta> detalleVentas;
    List<Credito> creditos;
    
    Float getTotal() {            // Total de productos vendidos
        return suma de subtotales;
    }
    
    Float getMontoContado() {     // Diferencia (se puede calcular)
        return getTotal() - (montoCredito != null ? montoCredito : 0);
    }
}
```

### Enum EstadoVenta
```java
public enum EstadoVenta {
    CREADA("Creada"),
    VERIFICADA("Verificada"),
    AUTORIZADA("Autorizada"),
    CANCELADA("Cancelada");
}
```

---

## Notas Importantes

1. **Bloqueo de Productos**: Se realiza al crear la venta (estado CREADA)
2. **Verificación Simplificada**: Solo requiere esCredito (true/false)
3. **Cálculo Automático de Montos**: 
   - Si esCredito=true: montoCredito = total venta
   - Si esCredito=false: montoCredito = 0 (montoContado = total venta)
4. **Autorización Automática para Contado**: Ventas al contado se autorizan automáticamente
5. **Crédito Automático**: Se genera automáticamente al autorizar si esCredito=true
6. **Validación de Crédito**: Solo se valida el límite cuando esCredito=true
7. **Estados Irreversibles**: Una vez autorizada, la venta no puede modificarse
8. **Liberación de Productos**: Al cancelar, se eliminan los DetalleVenta y se recupera la cantidad en inventario

---

## Casos de Uso

### Use Case 1: Venta al contado (autorización automática)
1. Crear venta
2. Verificar con esCredito=false
3. **Venta se autoriza automáticamente** - sin paso adicional
4. Venta completada

### Use Case 2: Venta totalmente a crédito
1. Crear venta
2. Verificar con esCredito=true (valida límite)
3. Si el cliente tiene crédito, autorizar manualmente
4. Venta completada y crédito generado automáticamente

### Use Case 3: Cliente decide rechazar la venta
1. Crear venta
2. Verificar como crédito
3. Cliente rechaza → CANCELAR
4. Productos se liberan
5. Reintentar con diferentes términos o como contado

### Use Case 4: Rectificación antes de verificar
1. Crear venta (productos bloqueados)
2. Revisión de los detalles
3. Si hay discrepancias, cancelar
4. Productos se liberan
5. Crear nueva venta con detalles correctos

### Use Case 5: Comparativa - Velocidad de venta
**Contado (rápido)**: 2 pasos
```
POST /ventas → CREADA
POST /ventas/estados/{id}/verificar?esCredito=false → AUTORIZADA (automático)
```

**Crédito (requiere aprobación)**: 3 pasos
```
POST /ventas → CREADA
POST /ventas/estados/{id}/verificar?esCredito=true → VERIFICADA
POST /ventas/estados/{id}/autorizar → AUTORIZADA (con crédito)
```


