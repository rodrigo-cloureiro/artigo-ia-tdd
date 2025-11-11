package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteControllerImpl implements ClienteController {
    private final ClienteService clienteService;

    @GetMapping("/")
    public List<Cliente> listar() {
        return clienteService.listar();
    }
}
