package com.rpe.pagamentos.repository;

import com.rpe.pagamentos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
      List<Cliente> findByStatusBloqueio(Character statusBloqueio);
}
