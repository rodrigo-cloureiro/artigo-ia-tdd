package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listar();

    Optional<Cliente> buscarPorCpf(String cpf);
}
