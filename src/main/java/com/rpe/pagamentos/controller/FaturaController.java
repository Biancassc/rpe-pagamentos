package com.rpe.pagamentos.controller;

import com.rpe.pagamentos.model.Fatura;
import com.rpe.pagamentos.service.FaturaService;
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
@RequestMapping("/faturas")
@Tag(name = "Faturas", description = "Gerenciamento de Faturas e Pagamentos na API")
public class FaturaController {

    private final FaturaService faturaService;
    private final ClienteService clienteService; 

    @Autowired
    public FaturaController(FaturaService faturaService, ClienteService clienteService) {
        this.faturaService = faturaService;
        this.clienteService = clienteService;
    }

    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Lista todas as faturas de um cliente", description = "Retorna uma lista de faturas associadas a um cliente específico pelo seu ID.")
    public ResponseEntity<List<Fatura>> listarFaturasPorCliente(@PathVariable Long clienteId) {
        List<Fatura> faturas = faturaService.listarFaturasPorCliente(clienteId);
        if (faturas.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.ok(faturas);
    }

    
    @PutMapping("/{id}/pagamento")
    @Operation(summary = "Registra o pagamento de uma fatura", description = "Atualiza o status de uma fatura para 'Paga' e registra a data do pagamento.")
    public ResponseEntity<Fatura> registrarPagamento(@PathVariable Long id) {
        try {
            Fatura faturaPaga = faturaService.registrarPagamento(id);
            return ResponseEntity.ok(faturaPaga);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    
    @GetMapping("/atrasadas")
    @Operation(summary = "Lista faturas em atraso", description = "Retorna uma lista de todas as faturas com status 'Atrasada'.")
    public ResponseEntity<List<Fatura>> listarFaturasAtrasadas() {
        List<Fatura> faturasAtrasadas = faturaService.listarFaturasAtrasadas();
        return ResponseEntity.ok(faturasAtrasadas);
    }

    
    @PostMapping
    @Operation(summary = "Cria uma nova fatura", description = "Cria um novo registro de fatura associado a um cliente existente.")
    public ResponseEntity<Fatura> criarFatura(@RequestBody Fatura fatura) {
        try {
            Fatura novaFatura = faturaService.criarFatura(fatura);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaFatura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    
    @PutMapping("/verificar-bloqueio-clientes")
    @Operation(summary = "Verifica e bloqueia clientes com faturas atrasadas", description = "Gatilha manualmente a regra de negócio para bloquear clientes com faturas atrasadas há mais de 3 dias. (Para teste/demonstração)")
    public ResponseEntity<String> verificarEBloquearClientesAtrasados() {
        clienteService.verificarEBloquearClientesAtrasados();
        return ResponseEntity.ok("Verificação e bloqueio de clientes atrasados acionada com sucesso.");
    }
}