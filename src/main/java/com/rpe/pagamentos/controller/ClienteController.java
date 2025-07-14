package com.rpe.pagamentos.controller;

import com.rpe.pagamentos.model.Cliente;
import com.rpe.pagamentos.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de Clientes na API de Pagamentos")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista completa de todos os clientes cadastrados.")
    public ResponseEntity<List<Cliente>> listarTodosClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente", description = "Cria um novo registro de cliente no sistema.")
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
        Cliente novoCliente = clienteService.cadastrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta cliente por ID", description = "Retorna os detalhes de um cliente específico pelo seu ID.")
    public ResponseEntity<Cliente> consultarClientePorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.consultarClientePorId(id);
        return cliente.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza ou bloqueia um cliente", description = "Atualiza os dados de um cliente existente ou altera seu status de bloqueio. Se o status for 'B', o limite de crédito será zerado.")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        try {
            Cliente cliente = clienteService.atualizarCliente(id, clienteAtualizado);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/bloqueados")
    @Operation(summary = "Lista clientes bloqueados", description = "Retorna uma lista de todos os clientes com status 'Bloqueado'.")
    public ResponseEntity<List<Cliente>> listarClientesBloqueados() {
        List<Cliente> clientesBloqueados = clienteService.listarClientesBloqueados();
        return ResponseEntity.ok(clientesBloqueados);
    }
}
