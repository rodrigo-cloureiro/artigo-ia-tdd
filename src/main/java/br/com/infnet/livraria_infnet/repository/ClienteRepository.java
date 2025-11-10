package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Cliente;

import java.util.List;

public interface ClienteRepository {
    List<Cliente> findAll();
}
