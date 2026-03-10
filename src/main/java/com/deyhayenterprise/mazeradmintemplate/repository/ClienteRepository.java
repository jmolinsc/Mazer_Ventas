package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Cliente> findAllByActivoTrueOrderByIdDesc();

    Optional<Cliente> findByIdAndActivoTrue(Long id);

    Optional<Cliente> findByEmailIgnoreCase(String email);
}
