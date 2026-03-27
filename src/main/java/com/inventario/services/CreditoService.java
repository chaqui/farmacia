package com.inventario.services;

import com.inventario.dto.CreditoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Cliente;
import com.inventario.models.Credito;
import com.inventario.models.Venta;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.CreditoRepository;
import com.inventario.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CreditoService {

    private final CreditoRepository creditoRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public CreditoService(CreditoRepository creditoRepository, ClienteRepository clienteRepository, VentaRepository ventaRepository) {
        this.creditoRepository = creditoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Credito> listAll() {
        return creditoRepository.findAll();
    }

    public Optional<Credito> getById(Integer id) {
        return creditoRepository.findById(id);
    }

    @Transactional
    public Credito create(CreditoDto.Post dto) throws HttpException {
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElseThrow(() -> new HttpException("Cliente no encontrado", 400));

        Venta venta = null;
        if (dto.getVentaId() != null) {
            venta = ventaRepository.findById(dto.getVentaId()).orElseThrow(() -> new HttpException("Venta no encontrada", 400));
            if (dto.getMonto() > venta.getTotal()) {
                throw new HttpException("El monto del crédito no puede ser mayor al total de la venta", 400);
            }
        }

        // verificar límite de crédito
        if (cliente.getLimiteCredito() != null) {
            java.util.List<Credito> existentes = creditoRepository.findByClienteId(cliente.getId());
            float totalExistente = 0f;
            for (Credito c : existentes) totalExistente += c.getMonto();
            if (totalExistente + dto.getMonto() > cliente.getLimiteCredito()) {
                throw new HttpException("Límite de crédito excedido para el cliente", 400);
            }
        }

        Credito credito = new Credito(dto.getMonto(), dto.getFecha(), cliente, venta);
        return creditoRepository.save(credito);
    }

    @Transactional
    public Credito update(Integer id, CreditoDto.Post dto) throws HttpException {
        Credito existente = creditoRepository.findById(id).orElseThrow(() -> new HttpException("Crédito no encontrado", 404));

        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElseThrow(() -> new HttpException("Cliente no encontrado", 400));

        Venta venta = null;
        if (dto.getVentaId() != null) {
            venta = ventaRepository.findById(dto.getVentaId()).orElseThrow(() -> new HttpException("Venta no encontrada", 400));
            if (dto.getMonto() > venta.getTotal()) {
                throw new HttpException("El monto del crédito no puede ser mayor al total de la venta", 400);
            }
        }

        // verificar límite de crédito (restar el monto existente antes de sumar el nuevo)
        if (cliente.getLimiteCredito() != null) {
            java.util.List<Credito> existentes = creditoRepository.findByClienteId(cliente.getId());
            float totalExistente = 0f;
            for (Credito c : existentes) {
                if (!c.getId().equals(id)) totalExistente += c.getMonto();
            }
            if (totalExistente + dto.getMonto() > cliente.getLimiteCredito()) {
                throw new HttpException("Límite de crédito excedido para el cliente", 400);
            }
        }

        existente.setMonto(dto.getMonto());
        existente.setFecha(dto.getFecha());
        existente.setCliente(cliente);
        existente.setVenta(venta);

        return creditoRepository.save(existente);
    }

    @Transactional
    public void delete(Integer id) throws HttpException {
        Credito existente = creditoRepository.findById(id).orElseThrow(() -> new HttpException("Crédito no encontrado", 404));
        creditoRepository.delete(existente);
    }

}
