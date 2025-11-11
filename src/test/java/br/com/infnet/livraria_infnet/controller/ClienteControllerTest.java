package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.model.Endereco;
import br.com.infnet.livraria_infnet.service.ClienteService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {
    private Cliente cliente;
    @Mock
    private ClienteService clienteService;
    private ClienteController clienteController;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente(
                "Lívia Pires",
                "livia.pires@al.infnet.edu.br",
                "42084537703",
                "21999963251",
                new Endereco(
                        "Estrada Dom Pedro Augusto",
                        "539",
                        "ATP 503",
                        "Alto da Boa Vista",
                        "Rio de Janeiro",
                        "RJ",
                        "20531250"
                ),
                Date.from(LocalDate.of(2000, 5, 5)
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant()),
                true
        );
        clienteController = new ClienteControllerImpl(clienteService);
    }

    @AfterEach
    public void tearDown() {
        if (clienteController != null) {
            clienteController = null;
        }
        reset(clienteService);
    }

    @Test
    public void deveRetornarClientesAtivosComSucesso() {
        when(clienteService.listar()).thenReturn(List.of(cliente));
        List<Cliente> clientes = clienteController.listar();
        assertEquals(1, clientes.size());
    }

    @Test
    public void deveRetornarClientePorCpf() {
        when(clienteService.buscarPorCpf("42084537703")).thenReturn(Optional.ofNullable(cliente));
        Optional<Cliente> clienteOptional = clienteController.buscarPorCpf("42084537703");
        assertTrue(clienteOptional.isPresent());
        assertEquals("livia.pires@al.infnet.edu.br", clienteOptional.get().getEmail());
    }
}
