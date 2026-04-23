package com.inventario.services;

import com.inventario.exception.HttpException;
import com.inventario.models.Credito;
import com.inventario.models.PagoCredito;
import com.inventario.repository.CreditoRepository;
import com.inventario.repository.PagoCreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagoCreditoService {

    private final PagoCreditoRepository pagoCreditoRepository;
    private final CreditoRepository creditoRepository;

    public PagoCreditoService(PagoCreditoRepository pagoCreditoRepository, CreditoRepository creditoRepository) {
        this.pagoCreditoRepository = pagoCreditoRepository;
        this.creditoRepository = creditoRepository;
    }

    @Transactional
    public PagoCredito registrarPago(Integer creditoId, Float monto) throws HttpException {
        if (monto == null || monto <= 0f) throw new HttpException("Monto de pago inválido", 400);
        Credito credito = creditoRepository.findById(creditoId).orElseThrow(() -> new HttpException("Crédito no encontrado", 404));

        float saldo = credito.getSaldoPendiente();
        if (monto > saldo) throw new HttpException("El monto supera el saldo pendiente", 400);

        PagoCredito pago = new PagoCredito(credito, monto, LocalDate.now());
        PagoCredito saved = pagoCreditoRepository.save(pago);

        // refresh credito pagos list (optional, to keep in-memory consistent)
        credito.getPagos().add(saved);
        creditoRepository.save(credito);

        return saved;
    }

    public List<PagoCredito> obtenerPagosPorCredito(Integer creditoId) throws HttpException {
        Credito credito = creditoRepository.findById(creditoId)
                .orElseThrow(() -> new HttpException("Crédito no encontrado", 404));
        return credito.getPagos();
    }

}
