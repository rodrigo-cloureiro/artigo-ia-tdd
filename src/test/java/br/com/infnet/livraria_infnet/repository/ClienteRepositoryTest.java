package br.com.infnet.livraria_infnet.repository;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRepositoryTest {
    private ClienteRepository clienteRepository;

    @BeforeEach
    public void setUp() {
        clienteRepository = new ClienteRepositoryImpl();
    }

    @AfterEach
    public void tearDown() {
        if (clienteRepository != null) {
            clienteRepository = null;
        }
    }

    @Test
    public void deveRetornarListaDeClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        assertEquals(5, clientes.size());
    }
}
