package com.inventario.services;

import com.inventario.exception.HttpException;
import com.inventario.models.AutorizacionLimiteCredito;
import com.inventario.models.Cliente;
import com.inventario.repository.AutorizacionLimiteCreditoRepository;
import com.inventario.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutorizacionLimiteCreditoService {

    private final AutorizacionLimiteCreditoRepository autorizacionRepository;
    private final ClienteRepository clienteRepository;

    public AutorizacionLimiteCreditoService(
            AutorizacionLimiteCreditoRepository autorizacionRepository,
            ClienteRepository clienteRepository) {
        this.autorizacionRepository = autorizacionRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Solicitar autorización de límite de crédito
     * Se crea una solicitud pendiente de autorización
     */
    @Transactional
    public AutorizacionLimiteCredito solicitarAutorizacion(
            Integer clienteId, Float nuevoLimite) throws HttpException {
        
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new HttpException("Cliente no encontrado", 404));

        // Verificar si hay solicitud pendiente
        var solicitudPendiente = autorizacionRepository
                .findFirstByClienteAndEstadoOrderByFechaSolicitudDesc(cliente, "PENDIENTE");
        
        if (solicitudPendiente.isPresent()) {
            throw new HttpException("Existe una solicitud de autorización pendiente para este cliente", 409);
        }

        AutorizacionLimiteCredito autorizacion = new AutorizacionLimiteCredito(cliente, nuevoLimite);
        
        return autorizacionRepository.save(autorizacion);
    }

    /**
     * Obtener todas las solicitudes pendientes de autorización
     */
    public List<AutorizacionLimiteCredito> obtenerSolicitudesPendientes() {
        return autorizacionRepository.findByEstado("PENDIENTE");
    }

    /**
     * Obtener historial de autorizaciones de un cliente
     */
    public List<AutorizacionLimiteCredito> obtenerHistorialCliente(Integer clienteId) throws HttpException {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new HttpException("Cliente no encontrado", 404));
        
        return autorizacionRepository.findByClienteId(clienteId);
    }

    /**
     * Aprobar solicitud de límite de crédito
     */
    @Transactional
    public AutorizacionLimiteCredito aprobarAutorizacion(
            Integer autorizacionId) throws HttpException {
        
        AutorizacionLimiteCredito autorizacion = autorizacionRepository.findById(autorizacionId)
                .orElseThrow(() -> new HttpException("Autorización no encontrada", 404));

        if (!autorizacion.estaPendiente()) {
            throw new HttpException("La solicitud ya ha sido procesada", 409);
        }

        autorizacion.aprobar();
        
        // Actualizar el límite de crédito del cliente
        Cliente cliente = autorizacion.getCliente();
        cliente.setLimiteCredito(autorizacion.getLimiteCredito());
        clienteRepository.save(cliente);

        return autorizacionRepository.save(autorizacion);
    }

    /**
     * Rechazar solicitud de límite de crédito
     */
    @Transactional
    public AutorizacionLimiteCredito rechazarAutorizacion(
            Integer autorizacionId, String razonRechazo) throws HttpException {
        
        AutorizacionLimiteCredito autorizacion = autorizacionRepository.findById(autorizacionId)
                .orElseThrow(() -> new HttpException("Autorización no encontrada", 404));

        if (!autorizacion.estaPendiente()) {
            throw new HttpException("La solicitud ya ha sido procesada", 409);
        }

        autorizacion.rechazar(razonRechazo);
        
        return autorizacionRepository.save(autorizacion);
    }

    /**
     * Obtener autorización por ID
     */
    public AutorizacionLimiteCredito obtenerPorId(Integer id) throws HttpException {
        return autorizacionRepository.findById(id)
                .orElseThrow(() -> new HttpException("Autorización no encontrada", 404));
    }

    /**
     * Verificar si un cliente tiene límite de crédito autorizado
     */
    public boolean tieneLimiteAutorizado(Integer clienteId) {
        var ultimaAutorizacion = autorizacionRepository
                .findByClienteId(clienteId).stream()
                .filter(AutorizacionLimiteCredito::estaAprobado)
                .findFirst();
        
        return ultimaAutorizacion.isPresent();
    }

}
