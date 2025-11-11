package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Cliente;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    private final List<Cliente> clientes;

    public ClienteRepositoryImpl() {
        this.clientes = initClientes();
    }

    @Override
    public List<Cliente> findAll() {
        return Collections.unmodifiableList(clientes);
    }

    @Override
    public Optional<Cliente> findByCpf(String cpf) {
        return clientes.stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }

    private List<Cliente> initClientes() {
        List<Cliente> temp = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = getClass().getResourceAsStream("/data/clientes.json")) {
            temp = mapper.readValue(is, new TypeReference<>() {
            });
            System.out.println("🧑‍🧑‍🧒 " + temp.size() + " clientes carregados com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Ocorreu um erro ao carregar os clientes!");
        }

        return temp;
    }
}
