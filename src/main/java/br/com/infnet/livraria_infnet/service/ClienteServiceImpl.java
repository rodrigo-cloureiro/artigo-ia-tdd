package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listar() {
        return clienteRepository.findAll()
                .stream()
                .filter(Cliente::isAtivo)
                .toList();
    }
}
