package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.model.Endereco;
import br.com.infnet.livraria_infnet.repository.ClienteRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {
    private Cliente cliente;
    @Mock
    private ClienteRepository clienteRepository;
    private ClienteService clienteService;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente(
                "Vitor Amadeu",
                "vitor.asouza@prof.infnet.edu.br",
                "90030848741",
                "21985982636",
                new Endereco(
                        "Rua Jhon Lenon",
                        "380",
                        "APT 910",
                        "Campo Grande",
                        "Rio de Janeiro",
                        "RJ",
                        ""
                ),
                Date.from(LocalDate.of(1980, 5, 15)
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant()),
                true
        );
        clienteService = new ClienteServiceImpl(clienteRepository);
    }

    @AfterEach
    public void tearDown() {
        if (cliente != null) {
            cliente = null;
        }
        reset(clienteRepository);
    }

    @Test
    public void deveRetornarListaDeClientesAtivos() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        List<Cliente> clientes = clienteService.listar();

        assertEquals(1, clientes.size());
        assertEquals("Vitor Amadeu", clientes.getFirst().getNome());
    }
}
