package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cleanupCpf(cpf));
    }

    private String cleanupCpf(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }
}
