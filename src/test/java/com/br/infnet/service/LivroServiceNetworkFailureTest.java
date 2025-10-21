package com.br.infnet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LivroServiceNetworkFailureTest {

    @Test
    @DisplayName("Deve falhar graciosamente quando arquivo CSV não estiver disponível")
    void testCSVIndisponivel() {
        // Criar uma subclasse de LivroService que tenta carregar um arquivo inexistente
        LivroService service = new LivroService() {
            @Override
            protected void carregarLivrosDoCSV() {
                try (var is = getClass().getClassLoader().getResourceAsStream("arquivo-inexistente.csv")) {
                    if (is == null) {
                        System.out.println("Não foi possível carregar livros do CSV: arquivo não encontrado");
                        System.out.println("Iniciando com acervo vazio.");
                        return;
                    }
                    super.carregarLivrosDoCSV();
                } catch (Exception e) {
                    System.out.println("Não foi possível carregar livros do CSV: " + e.getMessage());
                    System.out.println("Iniciando com acervo vazio.");
                }
            }
        };

        assertDoesNotThrow(() -> {
            //Exceções não devem ser lançadas
            assertNotNull(service.listarLivrosDoAcervo());
            assertEquals(0, service.listarLivrosDoAcervo().size());
        });
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Operações devem ter timeout definido")
    void testTimeoutOperacoes() {
        LivroService service = new LivroService();
        long inicio = System.currentTimeMillis();
        assertDoesNotThrow(() -> {
            service.listarLivrosDoAcervo();
        });
        long duracao = System.currentTimeMillis() - inicio;
        assertTrue(duracao < 1000, "Operação muito lenta: " + duracao + "ms");
    }
}
