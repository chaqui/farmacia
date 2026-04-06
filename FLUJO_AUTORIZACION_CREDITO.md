# Flujo de Autorización de Límite de Crédito

## Descripción General

Este sistema implementa un flujo de **autorización en dos pasos** para el límite de crédito de los clientes. Cuando se crea un cliente o se actualiza su límite de crédito, el sistema:

1. **Solicita autorización** - Crea una solicitud pendiente de revisión
2. **Bloquea el límite** - El límite no se activa hasta ser autorizado
3. **Requiere aprobación** - Un administrador debe aprobar o rechazar la solicitud
4. **Activa el límite** - Una vez aprobado, el límite se actualiza en el cliente

---

## Entidades Principales

### 1. **Cliente**
```
- id: Integer
- nombre: String
- tipoCliente: Integer
- limiteCredito: Float (se actualiza solo cuando es autorizado)
- creditos: List<Credito>
```

### 2. **AutorizacionLimiteCredito** (Nueva)
```
- id: Integer
- cliente: Cliente (referencia)
- limiteCredito: Float (límite solicitado)
- estado: String (PENDIENTE, APROBADO, RECHAZADO)
- fechaSolicitud: LocalDateTime
- fechaAutorizacion: LocalDateTime
- razonRechazo: String (solo si fue rechazado)
- solicitadoPor: String (quién solicita)
- autorizadoPor: String (quién autoriza)
```

---

## Flujo de Creación de Cliente

```
1. POST /api/clientes
   {
     "nombre": "Empresa XYZ",
     "tipoCliente": 1,
     "limiteCredito": 5000.0
   }

2. Sistema:
   ↓
   ✓ Crea el Cliente con limiteCredito = 0
   ✓ Crea AutorizacionLimiteCredito con estado = PENDIENTE
   ↓
   Respuesta: 
   {
     "id": 1,
     "nombre": "Empresa XYZ",
     "tipoCliente": 1,
     "limiteCredito": 0,      ← Bloqueado hasta autorización
     "saldoCredito": 0
   }
```

---

## Flujo de Actualización de Cliente

```
1. PUT /api/clientes/{id}
   {
     "nombre": "Empresa XYZ Actualizada",
     "limiteCredito": 8000.0   ← Cambio de límite
   }

2. Sistema (si el límite cambió):
   ↓
   ✓ Verifica si hay solicitud pendiente
   ✓ Si existe pendiente → Lanza error (409)
   ✓ Si no → Crea nueva AutorizacionLimiteCredito
   ✓ Mantiene el límite anterior en Cliente
   ↓
   Respuesta: Cliente con límite anterior
```

---

## Flujo de Autorización

### Paso 1: Listar Solicitudes Pendientes
```
GET /api/autorizaciones-limite-credito/pendientes

Respuesta:
[
  {
    "id": 1,
    "clienteId": 1,
    "clienteNombre": "Empresa XYZ",
    "limiteCredito": 5000.0,
    "estado": "PENDIENTE",
    "fechaSolicitud": "2026-04-06T10:30:00",
    "fechaAutorizacion": null,
    "razonRechazo": null,
    "solicitadoPor": "usuario@sistema",
    "autorizadoPor": null
  }
]
```

### Paso 2: Aprobar Solicitud
```
POST /api/autorizaciones-limite-credito/{id}/aprobar
{
  "autorizadoPor": "admin@sistema"
}

Sistema:
↓
✓ Cambia estado a APROBADO
✓ Actualiza limiteCredito en Cliente a 5000.0
✓ Registra fechaAutorizacion y autorizadoPor
↓
Respuesta:
{
  "id": 1,
  "estado": "APROBADO",
  "fechaAutorizacion": "2026-04-06T11:00:00",
  "autorizadoPor": "admin@sistema"
}
```

### Paso 3: O Rechazar Solicitud
```
POST /api/autorizaciones-limite-credito/{id}/rechazar
{
  "razonRechazo": "No cumplen requisitos de validación de crédito",
  "autorizadoPor": "admin@sistema"
}

Sistema:
↓
✓ Cambia estado a RECHAZADO
✓ No modifica limiteCredito en Cliente
✓ Registra razonRechazo, fechaAutorizacion y autorizadoPor
↓
Cliente puede volver a solicitar autorización después
```

---

## Endpoints Disponibles

### **Clientes**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| POST | `/api/clientes?usuario=...` | Crear cliente (solicita autorización) |
| PUT | `/api/clientes/{id}?usuario=...` | Actualizar cliente |
| PUT | `/api/clientes/{id}/limite?nuevoLimite=...&usuario=...` | Cambiar límite de crédito |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

### **Autorizaciones**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/autorizaciones-limite-credito/pendientes` | Listar solicitudes pendientes |
| GET | `/api/autorizaciones-limite-credito/{id}` | Obtener detalles de autorización |
| GET | `/api/autorizaciones-limite-credito/cliente/{clienteId}` | Historial de autorizaciones |
| POST | `/api/autorizaciones-limite-credito/{id}/aprobar` | Aprobar solicitud |
| POST | `/api/autorizaciones-limite-credito/{id}/rechazar` | Rechazar solicitud |

---

## Ejemplo Completo

### 1. Crear Cliente (Solicita Autorización)
```http
POST /api/clientes?usuario=gerente@empresa.com
Content-Type: application/json

{
  "nombre": "Supermercado ABC",
  "tipoCliente": 1,
  "limiteCredito": 10000.0
}
```

**Respuesta (201)**:
```json
{
  "id": 42,
  "nombre": "Supermercado ABC",
  "tipoCliente": 1,
  "limiteCredito": 0,
  "saldoCredito": 0
}
```
→ Se creó una `AutorizacionLimiteCredito` pendiente

### 2. Ver Solicitudes Pendientes
```http
GET /api/autorizaciones-limite-credito/pendientes
```

**Respuesta**:
```json
[
  {
    "id": 100,
    "clienteId": 42,
    "clienteNombre": "Supermercado ABC",
    "limiteCredito": 10000.0,
    "estado": "PENDIENTE",
    "fechaSolicitud": "2026-04-06T14:30:00",
    "solicitadoPor": "gerente@empresa.com"
  }
]
```

### 3. Aprobar la Solicitud
```http
POST /api/autorizaciones-limite-credito/100/aprobar
Content-Type: application/json

{
  "autorizadoPor": "admin@empresa.com"
}
```

**Resultado**: 
- El `Cliente` (ID 42) ahora tiene `limiteCredito = 10000.0`
- La `AutorizacionLimiteCredito` está `APROBADO`

### 4. Verificar Cliente Actualizado
```http
GET /api/clientes/42
```

**Respuesta**:
```json
{
  "id": 42,
  "nombre": "Supermercado ABC",
  "tipoCliente": 1,
  "limiteCredito": 10000.0,
  "saldoCredito": 0
}
```

---

## Validaciones Implementadas

✅ **No se pueden crear solicitudes duplicadas**
   - Si hay una solicitud PENDIENTE, se rechaza crear otra

✅ **No se puede procesar dos veces**
   - Una solicitud APROBADA o RECHAZADA no se puede procesar de nuevo

✅ **Límite se activa solo cuando es autorizado**
   - El cliente se crea con `limiteCredito = 0`
   - Se actualiza a `limiteCredito` solicitado solo cuando se aprueba

✅ **Historial completo**
   - Se registra quién solicita, quién autoriza, fechas y motivos de rechazo

---

## Casos de Uso

### Caso 1: Nuevo Cliente con Crédito
**Requisito**: El gerente de ventas quiere crear un cliente con límite de crédito inicial.

1. Crea cliente con `limiteCredito: 5000`
2. Sistema crea solicitud de autorización
3. Administrador revisa y aprueba
4. Cliente puede usar el crédito

### Caso 2: Aumentar Límite Existente
**Requisito**: El cliente quiere aumentar su límite de crédito de 5000 a 15000.

1. Actualiza cliente con `limiteCredito: 15000`
2. Sistema crea nueva solicitud de autorización
3. Administrador revisa y aprueba/rechaza
4. Si aprueba: límite se actualiza a 15000

### Caso 3: Rechazar Solicitud
**Requisito**: El administrador rechaza la solicitud por falta de documentos.

1. Envía `POST /rechazar` con razón: "Documentos incompletos"
2. Cliente recibe notificación (implementar)
3. Puede reintentrar después de corregir documentos

---

## Implementación de Auditoría (Opcional)

Para mayor control, se pueden agregar más campos:

```java
@Column
private String motivo; // Motivo de la solicitud

@Column
private String rubroCredito; // Para qué usará el crédito

@Column
private LocalDateTime fechaProximoIntento; // Para reintentos programados
```

---

## Próximas Mejoras

- [ ] Notificaciones por email al aprobar/rechazar
- [ ] Descuentos automáticos según el monto
- [ ] Límites por tipo de cliente predefinidos
- [ ] Renovación periódica de autorizaciones
- [ ] Dashboard de pendientes
- [ ] Integración con reportes de crédito
