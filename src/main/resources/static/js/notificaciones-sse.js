/**
 * Cliente SSE (Server-Sent Events) para recibir notificaciones en tiempo real
 * Alternativa a WebSocket - Solo servidor → cliente (unidireccional)
 * 
 * Ventajas:
 * - Más simple que WebSocket
 * - HTTP estándar (sin protocolo especial)
 * - Reconexión automática integrada
 * - Menos overhead
 * - Ideal para notificaciones
 */
class NotificacionesSse {
    constructor(tipo = 'general') {
        // tipo: 'general', 'credito', 'compra'
        this.tipo = tipo;
        this.eventSource = null;
        this.url = this.construirUrl();
        this.handlers = {
            onConnect: null,
            onDisconnect: null,
            onNotificacion: null,
            onError: null
        };
        this.reconectarIntento = 0;
        this.maxReconexiones = Infinity; // SSE reconecta automáticamente
    }

    /**
     * Construye la URL del endpoint SSE según el tipo
     */
    construirUrl() {
        const protocolo = window.location.protocol === 'https:' ? 'https' : 'http';
        const host = window.location.host;

        let endpoint = '/api/notificaciones/stream';
        if (this.tipo === 'credito') {
            endpoint = '/api/notificaciones/stream/credito';
        } else if (this.tipo === 'compra') {
            endpoint = '/api/notificaciones/stream/compra';
        }

        return `${protocolo}://${host}${endpoint}`;
    }

    /**
     * Conecta al servidor SSE
     */
    conectar() {
        try {
            console.log(`Conectando a SSE: ${this.url}`);
            
            this.eventSource = new EventSource(this.url);

            // Evento abierto (conexión establecida)
            this.eventSource.onopen = (event) => this.onConexionAbierta(event);

            // Evento de mensaje (notificación)
            this.eventSource.onmessage = (event) => this.procesarMensaje(event);

            // Evento de error
            this.eventSource.onerror = (event) => this.onError(event);

            // Escuchar eventos con nombre específico
            this.eventSource.addEventListener('notificacion', (event) => {
                this.procesarMensaje(event);
            }, false);

        } catch (error) {
            console.error('Error al conectar SSE:', error);
            if (this.handlers.onError) {
                this.handlers.onError(error);
            }
        }
    }

    /**
     * Se ejecuta cuando la conexión se abre
     */
    onConexionAbierta(event) {
        console.log('✓ Conectado a SSE - Estado:', this.eventSource.readyState);
        this.reconectarIntento = 0;

        if (this.handlers.onConnect) {
            this.handlers.onConnect();
        }
    }

    /**
     * Procesa los mensajes recibidos del servidor
     */
    procesarMensaje(event) {
        try {
            const notificacion = JSON.parse(event.data);
            console.log('Notificación recibida:', notificacion);

            // Ejecutar el handler personalizado
            if (this.handlers.onNotificacion) {
                this.handlers.onNotificacion(notificacion);
            }

            // Procesar según el tipo
            this.procesarPorTipo(notificacion);

        } catch (error) {
            console.error('Error al procesar mensaje SSE:', error);
        }
    }

    /**
     * Procesa la notificación según su tipo
     */
    procesarPorTipo(notificacion) {
        switch (notificacion.tipo) {
            case 'CONEXION_ESTABLECIDA':
                console.log('✓ Confirmación de conexión SSE recibida');
                break;

            case 'CREDITO_SOLICITADO':
                this.mostrarNotificacion('Nueva Solicitud', notificacion.mensaje, 'warning');
                this.reproducirSonido('notificacion');
                break;

            case 'CREDITO_APROBADO':
                this.mostrarNotificacion('Crédito Aprobado', notificacion.mensaje, 'success');
                this.reproducirSonido('exito');
                break;

            case 'CREDITO_RECHAZADO':
                this.mostrarNotificacion('Crédito Rechazado', notificacion.mensaje, 'error');
                break;

            case 'COMPRA_PENDIENTE_VERIFICACION':
                this.mostrarNotificacion('Compra en Espera', notificacion.mensaje, 'warning');
                this.reproducirSonido('notificacion');
                break;

            case 'COMPRA_AUTORIZADA':
                this.mostrarNotificacion('Compra Autorizada', notificacion.mensaje, 'success');
                this.reproducirSonido('exito');
                break;

            case 'COMPRA_RECHAZADA':
                this.mostrarNotificacion('Compra Rechazada', notificacion.mensaje, 'error');
                break;

            case 'COMPRA_VERIFICADA_APROBADA':
                this.mostrarNotificacion('Verificación Completada', 'Compra aprobada', 'success');
                break;

            case 'COMPRA_VERIFICADA_RECHAZADA':
                this.mostrarNotificacion('Verificación Completada', 'Compra rechazada', 'warning');
                break;

            default:
                this.mostrarNotificacion(notificacion.titulo, notificacion.mensaje, 'info');
        }
    }

    /**
     * Se ejecuta cuando la conexión se cierra o hay error
     */
    onError(event) {
        if (this.eventSource.readyState === EventSource.CLOSED) {
            console.log('✗ Desconectado de SSE');
            
            if (this.handlers.onDisconnect) {
                this.handlers.onDisconnect();
            }
        } else {
            console.error('Error en SSE:', event);
            if (this.handlers.onError) {
                this.handlers.onError(event);
            }
        }
    }

    /**
     * Muestra una notificación visual en el navegador
     */
    mostrarNotificacion(titulo, mensaje, tipo = 'info') {
        // Usar la API de notificaciones del navegador si está disponible
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(titulo, {
                body: mensaje,
                icon: this.obtenerIcono(tipo),
                tag: 'notificacion-sse',
                requireInteraction: tipo === 'warning' || tipo === 'error'
            });
        }

        // También mostrar en la página si hay un contenedor
        this.mostrarEnPagina(titulo, mensaje, tipo);
    }

    /**
     * Muestra la notificación en un div de la página
     */
    mostrarEnPagina(titulo, mensaje, tipo) {
        const contenedor = document.getElementById('notificaciones-contenedor');
        if (!contenedor) return;

        const alerta = document.createElement('div');
        alerta.className = `alert alert-${tipo} alert-dismissible fade show`;
        alerta.role = 'alert';
        alerta.innerHTML = `
            <strong>${titulo}</strong>
            <p>${mensaje}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        contenedor.appendChild(alerta);

        // Remover después de 5 segundos
        setTimeout(() => alerta.remove(), 5000);
    }

    /**
     * Obtiene el icono según el tipo de notificación
     */
    obtenerIcono(tipo) {
        const iconos = {
            'success': '✓',
            'error': '✕',
            'warning': '⚠',
            'info': 'ℹ'
        };
        return iconos[tipo] || iconos['info'];
    }

    /**
     * Reproduce un sonido de notificación
     */
    reproducirSonido(tipo = 'notificacion') {
        try {
            const audioContext = new (window.AudioContext || window.webkitAudioContext)();
            const oscilador = audioContext.createOscillator();
            const ganancia = audioContext.createGain();

            oscilador.connect(ganancia);
            ganancia.connect(audioContext.destination);

            if (tipo === 'notificacion') {
                oscilador.frequency.value = 800;
                ganancia.gain.setValueAtTime(0.3, audioContext.currentTime);
                ganancia.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
                oscilador.start(audioContext.currentTime);
                oscilador.stop(audioContext.currentTime + 0.5);
            } else if (tipo === 'exito') {
                oscilador.frequency.value = 1000;
                ganancia.gain.setValueAtTime(0.3, audioContext.currentTime);
                ganancia.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
                oscilador.start(audioContext.currentTime);
                oscilador.stop(audioContext.currentTime + 0.3);
            }
        } catch (e) {
            console.warn('No se pudo reproducir sonido de notificación:', e);
        }
    }

    /**
     * Desconecta de SSE
     */
    desconectar() {
        if (this.eventSource) {
            this.eventSource.close();
            console.log('SSE cerrado');
        }
    }

    /**
     * Verifica si está conectado
     */
    estaConectado() {
        return this.eventSource && this.eventSource.readyState === EventSource.OPEN;
    }

    /**
     * Retorna el estado de la conexión
     */
    getEstadoConexion() {
        if (!this.eventSource) return 'NO_CONECTADO';
        switch (this.eventSource.readyState) {
            case EventSource.CONNECTING:
                return 'CONECTANDO';
            case EventSource.OPEN:
                return 'CONECTADO';
            case EventSource.CLOSED:
                return 'CERRADO';
            default:
                return 'DESCONOCIDO';
        }
    }

    /**
     * Registra un handler para eventos
     */
    on(evento, callback) {
        const nombreHandler = `on${evento.charAt(0).toUpperCase() + evento.slice(1)}`;
        if (this.handlers.hasOwnProperty(nombreHandler)) {
            this.handlers[nombreHandler] = callback;
        }
    }
}

// Ejemplo de uso:
/*
// Crear cliente para notificaciones de crédito
const notificacionesCredito = new NotificacionesSse('credito');

// Registrar handlers
notificacionesCredito.on('connect', () => {
    console.log('Conectado a SSE de Crédito');
});

notificacionesCredito.on('notificacion', (notif) => {
    console.log('Nueva notificación:', notif);
    if (notif.requiereAccion) {
        // Actualizar la UI para mostrar la solicitud pendiente
    }
});

notificacionesCredito.on('disconnect', () => {
    console.log('Desconectado de SSE');
});

notificacionesCredito.on('error', (error) => {
    console.error('Error:', error);
});

// Conectar
notificacionesCredito.conectar();

// Verificar estado
console.log('Estado conexión:', notificacionesCredito.getEstadoConexion());
console.log('¿Está conectado?:', notificacionesCredito.estaConectado());

// Posteriormente, desconectar
// notificacionesCredito.desconectar();
*/
