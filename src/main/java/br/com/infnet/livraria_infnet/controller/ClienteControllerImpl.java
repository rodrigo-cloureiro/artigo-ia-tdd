package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteControllerImpl implements ClienteController {
    private final ClienteService clienteService;

    @Override
    @GetMapping("")
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @Override
    @GetMapping("/{cpf}")
    public Optional<Cliente> buscarPorCpf(@PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf);
    }
}
