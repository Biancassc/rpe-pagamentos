package com.rpe.pagamentos.service;

import com.rpe.pagamentos.model.Cliente;
import com.rpe.pagamentos.model.Fatura;
import com.rpe.pagamentos.repository.ClienteRepository;
import com.rpe.pagamentos.repository.FaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FaturaRepository faturaRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, FaturaRepository faturaRepository) {
        this.clienteRepository = clienteRepository;
        this.faturaRepository = faturaRepository;
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) {
        if (cliente.getStatusBloqueio() == null) {
            cliente.setStatusBloqueio('A');
        }
        if (cliente.getLimiteCredito() == null) {
            cliente.setLimiteCredito(java.math.BigDecimal.valueOf(1000.00));
        }
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> consultarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setStatusBloqueio(clienteAtualizado.getStatusBloqueio());
        clienteExistente.setLimiteCredito(clienteAtualizado.getLimiteCredito());

        if ('B' == clienteAtualizado.getStatusBloqueio()) {
            clienteExistente.setLimiteCredito(java.math.BigDecimal.ZERO);
        }

        return clienteRepository.save(clienteExistente);
    }

    public List<Cliente> listarClientesBloqueados() {
        return clienteRepository.findByStatusBloqueio('B');
    }

    @Transactional
    public void verificarEBloquearClientesAtrasados() {
        LocalDate tresDiasAtras = LocalDate.now().minusDays(3);
        List<Fatura> faturasAtrasadasHaMaisDe3Dias = faturaRepository
                .findByStatusAndDataVencimentoBefore(Character.valueOf('A'), tresDiasAtras);

        for (Fatura fatura : faturasAtrasadasHaMaisDe3Dias) {
            Cliente cliente = fatura.getCliente();
            if ('A' == cliente.getStatusBloqueio()) {
                cliente.setStatusBloqueio('B');
                cliente.setLimiteCredito(java.math.BigDecimal.ZERO);
                clienteRepository.save(cliente);
                System.out.println("Cliente " + cliente.getNome() + " (ID: " + cliente.getId() + ") bloqueado por fatura atrasada.");
            }
        }
    }
}

