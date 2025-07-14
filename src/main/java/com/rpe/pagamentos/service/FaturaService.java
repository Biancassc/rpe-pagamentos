package com.rpe.pagamentos.service;

import com.rpe.pagamentos.model.Fatura;
import com.rpe.pagamentos.model.Cliente;
import com.rpe.pagamentos.repository.FaturaRepository;
import com.rpe.pagamentos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FaturaService {

    private final FaturaRepository faturaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public FaturaService(FaturaRepository faturaRepository, ClienteRepository clienteRepository) {
        this.faturaRepository = faturaRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Fatura> listarFaturasPorCliente(Long clienteId) {
        return faturaRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Fatura registrarPagamento(Long faturaId) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada com ID: " + faturaId));

        if ('B' == fatura.getStatus() || 'A' == fatura.getStatus()) {
            fatura.setStatus('P');
            fatura.setDataPagamento(LocalDate.now());
            return faturaRepository.save(fatura);
        } else {
            throw new RuntimeException("Fatura ID: " + faturaId + " já está paga ou não pode ser processada.");
        }
    }

    public List<Fatura> listarFaturasAtrasadas() {
        return faturaRepository.findByStatus('A');
    }

    @Transactional
    public Fatura criarFatura(Fatura fatura) {
        if (fatura.getCliente() == null || fatura.getCliente().getId() == null) {
            throw new IllegalArgumentException("Fatura deve estar associada a um cliente existente.");
        }

        clienteRepository.findById(fatura.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente da fatura não encontrado com ID: " + fatura.getCliente().getId()));

        if (fatura.getStatus() == null) {
            fatura.setStatus('B');
        }

        return faturaRepository.save(fatura);
    }

    @Transactional
    public Fatura verificarEAtualizarStatusAtraso(Long faturaId) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada com ID: " + faturaId));

        if ('B' == fatura.getStatus() && fatura.getDataVencimento().isBefore(LocalDate.now())) {
            fatura.setStatus('A');
            return faturaRepository.save(fatura);
        }

        return fatura;
    }
}

