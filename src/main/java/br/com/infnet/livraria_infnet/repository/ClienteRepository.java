package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    List<Cliente> findAll();

    Optional<Cliente> findByCpf(String cpf);
}
