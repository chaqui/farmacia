## Guía Práctica: Autorización de Límite de Crédito

### Escenario: Crear cliente "Tienda Mi Barrio" con límite de crédito 5000

---

## 1️⃣ CREAR CLIENTE CON SOLICITUD DE AUTORIZACIÓN

**Request:**
```http
POST http://localhost:8080/api/clientes?usuario=gerente@sistema.com
Content-Type: application/json

{
  "nombre": "Tienda Mi Barrio",
  "tipoCliente": 2,
  "limiteCredito": 5000.0
}
```

**Response (201):**
```json
{
  "id": 15,
  "nombre": "Tienda Mi Barrio",
  "tipoCliente": 2,
  "limiteCredito": 0,
  "saldoCredito": 0
}
```

✅ **Resultado**: 
- Cliente creado pero con `limiteCredito = 0`
- Se envió solicitud de autorización al sistema

---

## 2️⃣ VERIFICAR SOLICITUD PENDIENTE

**Request:**
```http
GET http://localhost:8080/api/autorizaciones-limite-credito/pendientes
```

**Response (200):**
```json
[
  {
    "id": 87,
    "clienteId": 15,
    "clienteNombre": "Tienda Mi Barrio",
    "limiteCredito": 5000.0,
    "estado": "PENDIENTE",
    "fechaSolicitud": "2026-04-06T10:30:45.123456",
    "fechaAutorizacion": null,
    "razonRechazo": null,
    "solicitadoPor": "gerente@sistema.com",
    "autorizadoPor": null
  }
]
```

---

## 3️⃣ OPCIÓN A: APROBAR LA SOLICITUD

**Request:**
```http
POST http://localhost:8080/api/autorizaciones-limite-credito/87/aprobar
Content-Type: application/json

{
  "autorizadoPor": "admin@sistema.com"
}
```

**Response (200):**
```json
{
  "id": 87,
  "clienteId": 15,
  "clienteNombre": "Tienda Mi Barrio",
  "limiteCredito": 5000.0,
  "estado": "APROBADO",
  "fechaSolicitud": "2026-04-06T10:30:45.123456",
  "fechaAutorizacion": "2026-04-06T10:35:20.654321",
  "razonRechazo": null,
  "solicitadoPor": "gerente@sistema.com",
  "autorizadoPor": "admin@sistema.com"
}
```

✅ **Resultado**: 
- El cliente ahora tiene `limiteCredito = 5000.0` activo

---

## 3️⃣ OPCIÓN B: RECHAZAR LA SOLICITUD

**Request:**
```http
POST http://localhost:8080/api/autorizaciones-limite-credito/87/rechazar
Content-Type: application/json

{
  "razonRechazo": "Documentación incompleta - Falta RUC",
  "autorizadoPor": "admin@sistema.com"
}
```

**Response (200):**
```json
{
  "id": 87,
  "clienteId": 15,
  "clienteNombre": "Tienda Mi Barrio",
  "limiteCredito": 5000.0,
  "estado": "RECHAZADO",
  "fechaSolicitud": "2026-04-06T10:30:45.123456",
  "fechaAutorizacion": "2026-04-06T10:40:10.456789",
  "razonRechazo": "Documentación incompleta - Falta RUC",
  "solicitadoPor": "gerente@sistema.com",
  "autorizadoPor": "admin@sistema.com"
}
```

⚠️ **Resultado**: 
- El cliente mantiene `limiteCredito = 0`
- Se registró el motivo del rechazo
- El cliente puede solicitar nuevamente después de corregir

---

## 4️⃣ VERIFICAR CLIENTE DESPUÉS DE APROBACIÓN

**Request:**
```http
GET http://localhost:8080/api/clientes/15
```

**Response (200):**
```json
{
  "id": 15,
  "nombre": "Tienda Mi Barrio",
  "tipoCliente": 2,
  "limiteCredito": 5000.0,
  "saldoCredito": 0
}
```

✅ El límite de crédito está **ACTIVO**

---

## 5️⃣ VER HISTORIAL COMPLETO DEL CLIENTE

**Request:**
```http
GET http://localhost:8080/api/autorizaciones-limite-credito/cliente/15
```

**Response (200) - Historial:**
```json
[
  {
    "id": 87,
    "clienteId": 15,
    "clienteNombre": "Tienda Mi Barrio",
    "limiteCredito": 5000.0,
    "estado": "APROBADO",
    "fechaSolicitud": "2026-04-06T10:30:45.123456",
    "fechaAutorizacion": "2026-04-06T10:35:20.654321",
    "razonRechazo": null,
    "solicitadoPor": "gerente@sistema.com",
    "autorizadoPor": "admin@sistema.com"
  }
]
```

---

## 6️⃣ ACTUALIZAR CLIENTE Y AUMENTAR LÍMITE

El cliente solicita aumentar su límite a 10000.

**Request:**
```http
PUT http://localhost:8080/api/clientes/15?usuario=gerente@sistema.com
Content-Type: application/json

{
  "nombre": "Tienda Mi Barrio",
  "tipoCliente": 2,
  "limiteCredito": 10000.0
}
```

**Response (200):**
```json
{
  "id": 15,
  "nombre": "Tienda Mi Barrio",
  "tipoCliente": 2,
  "limiteCredito": 5000.0,
  "saldoCredito": 0
}
```

⚠️ **Nota**: El límite NO cambió a 10000 aún, sigue siendo 5000

✅ Se creó **nueva solicitud de autorización** para 10000

**Request para ver la nueva solicitud:**
```http
GET http://localhost:8080/api/autorizaciones-limite-credito/pendientes
```

**Response:**
```json
[
  {
    "id": 88,
    "clienteId": 15,
    "clienteNombre": "Tienda Mi Barrio",
    "limiteCredito": 10000.0,
    "estado": "PENDIENTE",
    "fechaSolicitud": "2026-04-06T11:00:00.123456",
    "solicitadoPor": "gerente@sistema.com"
  }
]
```

---

## ⚠️ CASOS DE ERROR

### Error 1: Intentar cambiar límite cuando hay solicitud pendiente
```http
PUT /api/clientes/15?usuario=gerente@sistema.com
{
  "limiteCredito": 15000.0
}
```

**Response (409 Conflict):**
```json
{
  "mensaje": "Existe una solicitud de autorización pendiente para este cliente",
  "status": 409
}
```

### Error 2: Procesar autorización dos veces
```http
POST /api/autorizaciones-limite-credito/87/aprobar
```

Si ya fue aprobada/rechazada:

**Response (409 Conflict):**
```json
{
  "mensaje": "La solicitud ya ha sido procesada",
  "status": 409
}
```

### Error 3: Cliente no encontrado
```http
GET /api/clientes/999
```

**Response (404 Not Found):**
```json
{
  "mensaje": "Cliente no encontrado",
  "status": 404
}
```

---

## 📊 RESUMEN DEL FLUJO

```
┌─────────────────────┐
│ Crear Cliente       │
│ limiteCredito: 5000 │
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────────────┐
│ Auto-crear Solicitud Pendiente   │
│ Estado: PENDIENTE                │
└──────────┬───────────────────────┘
           │
      ┌────┴────┐
      │          │
      ▼          ▼
  APROBAR   RECHAZAR
      │          │
      ▼          ▼
┌─────────┐  ┌──────────┐
│ APROBADO│  │RECHAZADO │
│límite=  │  │límite=   │
│ 5000    │  │ 0 (orig) │
└─────────┘  └──────────┘
```

---

## 🔐 USUARIOS Y PERMISOS

Recomendación de roles:

| Rol | Permiso |
|-----|---------|
| **Gerente/Vendedor** | Crear clientes, Solicitar aumento de límite |
| **Administrador** | Aprobar/Rechazar solicitudes |
| **Reportes** | Ver historial y pendientes |

Implementado a través del parámetro `usuario` en los requests.

