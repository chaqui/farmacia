# Guía: Impresión de Tickets en Impresora Térmica

## Overview

Esta guía te proporciona los pasos para imprimir tickets de venta generados por tu aplicación en una impresora térmica (80mm) desde el navegador.

## Pasos para Imprimir en Impresora Térmica

### 1. **Descargar el PDF del Ticket**

Primero, obtén el PDF del ticket de venta desde el backend:

```javascript
// Función para descargar el ticket en PDF
async function descargarTicket(ventaId) {
    try {
        const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
        
        if (!response.ok) {
            throw new Error('Error al descargar el ticket');
        }
        
        // Obtener el PDF como blob
        const blob = await response.blob();
        
        // Crear una URL temporal para el blob
        const blobUrl = window.URL.createObjectURL(blob);
        
        // Crear un enlace temporal para descargar
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = `ticket-venta-${ventaId}.pdf`;
        
        // Disparar la descarga
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Liberar la URL
        window.URL.revokeObjectURL(blobUrl);
        
    } catch (error) {
        console.error('Error:', error);
        alert('No se pudo descargar el ticket');
    }
}
```

### 2. **Abrir en Visor de PDF y Imprimir**

Opcionalmente, abre el PDF en una ventana nueva antes de imprimir:

```javascript
// Función para visualizar y imprimir el ticket
async function abrirYImprimirTicket(ventaId) {
    try {
        const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
        
        if (!response.ok) {
            throw new Error('Error al descargar el ticket');
        }
        
        const blob = await response.blob();
        const blobUrl = window.URL.createObjectURL(blob);
        
        // Abrir en una nueva ventana
        const ventana = window.open(blobUrl, '_blank');
        
        // Esperar a que se cargue y luego mostrar diálogo de impresión
        ventana.onload = () => {
            ventana.print();
        };
        
    } catch (error) {
        console.error('Error:', error);
        alert('No se pudo abrir el ticket para imprimir');
    }
}
```

### 3. **Método Directo: Imprimir Directamente sin Descargar**

```javascript
// Función para imprimir directamente sin descargar
async function imprimirTicketDirecto(ventaId) {
    try {
        const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
        
        if (!response.ok) {
            throw new Error('Error al descargar el ticket');
        }
        
        const blob = await response.blob();
        const blobUrl = window.URL.createObjectURL(blob);
        
        // Crear un iframe para imprimir
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = blobUrl;
        
        document.body.appendChild(iframe);
        
        // Esperar a que se cargue el iframe
        iframe.onload = () => {
            iframe.contentWindow.print();
            
            // Limpiar después de imprimir
            setTimeout(() => {
                document.body.removeChild(iframe);
                window.URL.revokeObjectURL(blobUrl);
            }, 1000);
        };
        
    } catch (error) {
        console.error('Error:', error);
        alert('No se pudo imprimir el ticket');
    }
}
```

### 4. **Configurar la Impresora Térmica en el Sistema Operativo**

#### En Windows:
1. Ve a **Configuración > Dispositivos > Impresoras y escáneres**
2. Haz clic en **Agregar una impresora**
3. Busca tu impresora térmica en la lista
4. Una vez instalada, establécela como **impresora predeterminada** (opcional)
5. Haz clic derecho en la impresora y selecciona **Preferencias** para configurar:
   - Tamaño de papel: **80mm x 297mm** (o personalizado)
   - Márgenes: **Mínimos (5-10mm)**
   - Orientación: **Vertical (Portrait)**

#### En macOS:
1. Ve a **Sistema > Impresoras y Escáneres**
2. Haz clic en **+** para agregar una impresora
3. Selecciona tu impresora térmica
4. Establece los valores predeterminados:
   - Tamaño: **Custom (80mm x 297mm)**
   - Márgenes: **Mínimos**

#### En Linux:
```bash
# Usar CUPS (Common Unix Printing System)
sudo apt install cups
sudo systemctl start cups

# Luego visita http://localhost:631 para configurar
```

## 5. **Integración en la Interfaz de Usuario**

Ejemplo de HTML para los botones:

```html
<!-- Botón en tu vista de detalles de venta -->
<div id="acciones-venta">
    <button onclick="descargarTicket(${ventaId})" class="btn btn-primary">
        <i class="icon-download"></i> Descargar Ticket
    </button>
    
    <button onclick="abrirYImprimirTicket(${ventaId})" class="btn btn-success">
        <i class="icon-print"></i> Ver e Imprimir
    </button>
    
    <button onclick="imprimirTicketDirecto(${ventaId})" class="btn btn-info">
        <i class="icon-printer"></i> Imprimir Directo
    </button>
</div>
```

## 6. **Consideraciones Especiales para Impresora Térmica**

### Configuración Recomendada en Chrome/Edge:

Cuando se abre el diálogo de impresión:

1. **Destino**: Selecciona tu impresora térmica
2. **Más configuración** > **Tamaño personalizado**:
   - Ancho: **80mm** (≈3.15 pulgadas)
   - Alto: **297mm** (≈11.7 pulgadas)
3. **Márgenes**: Selecciona "Ninguno"
4. **Escala**: 100% (no escalar)
5. **Fondo**: Desactiva si no necesitas colores de fondo

### CSS Media Query para Impresión:

Aunque el PDF ya está optimizado, puedes agregar estilos adicionales en tu HTML:

```css
@media print {
    body {
        width: 80mm;
        margin: 0;
        padding: 0;
    }
    
    .no-imprimir {
        display: none !important;
    }
    
    .ticket-container {
        page-break-after: avoid;
    }
}
```

## 7. **Troubleshooting**

### El ticket se corta en la impresora:
- Ajusta los márgenes a cero en las preferencias de impresión
- Verifica que el tamaño de papel sea **80mm x 297mm**
- Asegúrate de que el PDF no está escalado

### La impresora no aparece en la lista:
- Verifica que los drivers estén instalados correctamente
- Reinicia el servicio de impresión:
  ```bash
  # Windows
  net stop spooler
  net start spooler
  
  # macOS
  sudo launchctl restart org.cups.cupsd
  
  # Linux
  sudo systemctl restart cups
  ```

### El texto se ve muy pequeño:
- El PDF está optimizado para 80mm
- Si necesitas texto más grande, modifica el tamaño de fuente en `TicketPdfService.java`
- Actual: **8-10pt**, puedes cambiar a **9-12pt**

## 8. **Ejemplo Completo en React**

```jsx
import React from 'react';

export function TicketVenta({ ventaId }) {
    const [cargando, setCargando] = React.useState(false);
    const [error, setError] = React.useState(null);

    const imprimirTicket = async () => {
        setCargando(true);
        setError(null);
        
        try {
            const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
            
            if (!response.ok) {
                throw new Error('Error al descargar el ticket');
            }
            
            const blob = await response.blob();
            const blobUrl = window.URL.createObjectURL(blob);
            
            const iframe = document.createElement('iframe');
            iframe.style.display = 'none';
            iframe.src = blobUrl;
            
            document.body.appendChild(iframe);
            
            iframe.onload = () => {
                iframe.contentWindow.print();
                setTimeout(() => {
                    document.body.removeChild(iframe);
                    window.URL.revokeObjectURL(blobUrl);
                }, 1000);
            };
            
        } catch (err) {
            setError(err.message);
            console.error(err);
        } finally {
            setCargando(false);
        }
    };

    return (
        <div>
            <button 
                onClick={imprimirTicket} 
                disabled={cargando}
                className="btn btn-primary"
            >
                {cargando ? 'Cargando...' : 'Imprimir Ticket'}
            </button>
            {error && <div className="alert alert-danger">{error}</div>}
        </div>
    );
}
```

## 9. **Ejemplo Completo en Vue.js**

```vue
<template>
    <div class="ticket-container">
        <button 
            @click="imprimirTicket" 
            :disabled="cargando"
            class="btn btn-primary"
        >
            {{ cargando ? 'Cargando...' : 'Imprimir Ticket' }}
        </button>
        <div v-if="error" class="alert alert-danger">{{ error }}</div>
    </div>
</template>

<script>
export default {
    props: ['ventaId'],
    data() {
        return {
            cargando: false,
            error: null
        };
    },
    methods: {
        async imprimirTicket() {
            this.cargando = true;
            this.error = null;
            
            try {
                const response = await fetch(`/ventas/${this.ventaId}/descargar-ticket`);
                
                if (!response.ok) {
                    throw new Error('Error al descargar el ticket');
                }
                
                const blob = await response.blob();
                const blobUrl = window.URL.createObjectURL(blob);
                
                const iframe = document.createElement('iframe');
                iframe.style.display = 'none';
                iframe.src = blobUrl;
                
                document.body.appendChild(iframe);
                
                iframe.onload = () => {
                    iframe.contentWindow.print();
                    setTimeout(() => {
                        document.body.removeChild(iframe);
                        window.URL.revokeObjectURL(blobUrl);
                    }, 1000);
                };
                
            } catch (err) {
                this.error = err.message;
                console.error(err);
            } finally {
                this.cargando = false;
            }
        }
    }
};
</script>
```

## 10. **APIs de Impresora Térmica (Alternativa Avanzada)**

Si necesitas más control, considera usar la **Thermal Printer API** (experimental):

```javascript
async function imprimirConPrinterAPI(ventaId) {
    try {
        // Verificar disponibilidad de la API
        if (!navigator.permissions || !navigator.permissions.query) {
            console.warn('Printer API no disponible, usando método fallback');
            imprimirTicketDirecto(ventaId);
            return;
        }
        
        // Obtener el PDF
        const response = await fetch(`/ventas/${ventaId}/descargar-ticket`);
        const blob = await response.blob();
        
        // Usar setTimeout para asegurar compatibilidad
        const blobUrl = window.URL.createObjectURL(blob);
        window.open(blobUrl, '_blank');
        
    } catch (error) {
        console.error('Error:', error);
    }
}
```

## Resumen

| Método | Ventajas | Desventajas |
|--------|----------|-------------|
| **Descargar** | Simple, permite guardar archivo | Requiere 2 clicks del usuario |
| **Ver e Imprimir** | Usuario ve antes de imprimir | Requiere más pasos |
| **Imprimir Directo** | Una sola acción | Abre diálogo de impresión |
| **Print API** | Control total | Experimental, poco soporte |

**Recomendación**: Usa **"Imprimir Directo"** para flujos rápidos (caja) e **"Imprimir"** para cuando necesites vista previa.
