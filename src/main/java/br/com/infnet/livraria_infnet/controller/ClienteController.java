package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteController {
    List<Cliente> listar();

    Optional<Cliente> buscarPorCpf(String cpf);
}
