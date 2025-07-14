package com.rpe.pagamentos.repository;

import com.rpe.pagamentos.model.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate; 
@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Long> {
    List<Fatura> findByClienteId(Long clienteId);
    List<Fatura> findByStatus(Character status);
      List<Fatura> findByStatusAndDataVencimentoBefore(Character status, LocalDate dataVencimento);
}
