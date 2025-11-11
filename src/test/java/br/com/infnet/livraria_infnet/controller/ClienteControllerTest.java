package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Cliente;
import br.com.infnet.livraria_infnet.service.ClienteService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
}
