package com.inventario.services;

import org.springframework.stereotype.Service;

import com.inventario.dto.ClienteDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Cliente;
import com.inventario.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AutorizacionLimiteCreditoService autorizacionService;

    public ClienteService(ClienteRepository clienteRepository,
                         AutorizacionLimiteCreditoService autorizacionService) {
        this.clienteRepository = clienteRepository;
        this.autorizacionService = autorizacionService;
    }

    /**
     * Obtener todos los clientes
     */
    public List<Cliente> obtenerTodos() {
        return this.clienteRepository.findAll();
    }

    /**
     * Obtener cliente por ID
     */
    public Cliente obtenerClientePorId(Integer id) throws HttpException {
        return this.clienteRepository.findById(id)
                .orElseThrow(() -> new HttpException("Cliente no encontrado", 404));
    }

    /**
     * Obtener clientes cuyo nombre contiene la cadena (case-insensitive)
     */
    public List<Cliente> obtenerClientesPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        return this.clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Crear cliente
     * Si se proporciona limiteCredito, se crea una solicitud de autorización
     */
    @Transactional
    public Cliente crearCliente(ClienteDto.Post dto) throws HttpException {
        Cliente cliente = new Cliente(dto);
        Cliente clienteGuardado = this.clienteRepository.save(cliente);
        
        // Si se proporciona un límite de crédito, solicitar autorización
        if (cliente.getLimiteCredito() != null && cliente.getLimiteCredito() > 0) {
            autorizacionService.solicitarAutorizacion(
                    clienteGuardado.getId(), 
                    cliente.getLimiteCredito());
            
            // Limpiar el límite hasta que sea autorizado
            clienteGuardado.setLimiteCredito(0f);
            clienteGuardado = this.clienteRepository.save(clienteGuardado);
        }
        
        return clienteGuardado;
    }

    /**
     * Actualizar cliente
     * Si se actualiza el límite de crédito, se crea una solicitud de autorización
     */
    @Transactional
    public Cliente actualizarCliente(Integer id, ClienteDto.Put dto) throws HttpException {
        Cliente cliente = obtenerClientePorId(id);
        Float limiteAnterior = cliente.getLimiteCredito();
        cliente.setTipoCliente(dto.getTipoCliente());
        
        // Si el límite de crédito cambió, solicitar nueva autorización
        if (dto.getLimiteCredito() != null && 
            !dto.getLimiteCredito().equals(limiteAnterior)) {
            
            if (dto.getLimiteCredito() > 0) {
                autorizacionService.solicitarAutorizacion(
                        id, 
                        dto.getLimiteCredito());
                
                // No actualizar el límite hasta que sea autorizado
                cliente.setLimiteCredito(limiteAnterior);
            } else {
                cliente.setLimiteCredito(0f);
            }
        } else {
            cliente.setLimiteCredito(dto.getLimiteCredito());
        }
        
        return this.clienteRepository.save(cliente);
    }

    /**
     * Eliminar cliente
     */
    public void eliminarCliente(Integer id) throws HttpException {
        Cliente cliente = obtenerClientePorId(id);
        this.clienteRepository.delete(cliente);
    }

}
