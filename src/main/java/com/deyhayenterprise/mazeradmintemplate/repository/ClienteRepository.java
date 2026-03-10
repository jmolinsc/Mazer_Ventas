package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Cliente> findByEmailIgnoreCase(String email);
}
