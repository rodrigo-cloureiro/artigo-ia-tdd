package com.br.infnet.selenium.tests;

import com.br.infnet.selenium.pages.BuscaPage;
import com.br.infnet.selenium.base.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class BuscaTest extends TestBase {

    private BuscaPage buscaPage;

    @BeforeEach
    public void setUp() {
        super.setUp();
        driver.get(BASE_URL + "/buscar");
        buscaPage = new BuscaPage(driver);
    }

    @Test
    @DisplayName("Deve exibir página de busca corretamente")
    public void testExibirPaginaBusca() {
        assertTrue(buscaPage.validarCamposObrigatorios());
        assertTrue(buscaPage.isFormularioLimpo());
    }

    @Test
    @DisplayName("Deve realizar busca por título com sucesso")
    public void testBuscarPorTitulo() {
        buscaPage.realizarBusca("titulo", "O Pequeno Príncipe");

        assertTrue(buscaPage.isResultadosDisplayed());
        assertTrue(buscaPage.contemLivroComTitulo("O Pequeno Príncipe"));
        assertTrue(buscaPage.getQuantidadeResultados() > 0);
    }

    @Test
    @DisplayName("Deve realizar busca por autor com sucesso")
    public void testBuscarPorAutor() {
        buscaPage.realizarBusca("autor", "Eça de Queirós");

        assertTrue(buscaPage.isResultadosDisplayed());
        assertTrue(buscaPage.contemLivroComAutor("Eça de Queirós"));
        assertTrue(buscaPage.getQuantidadeResultados() > 0);
    }

    @Test
    @DisplayName("Deve realizar busca por ID com sucesso")
    public void testBuscarPorId() {
        buscaPage.realizarBusca("id", "8");

        assertTrue(buscaPage.isResultadosDisplayed());
        assertTrue(buscaPage.contemLivroComId("8"));
        assertEquals(1, buscaPage.getQuantidadeResultados());
    }

    @ParameterizedTest
    @ValueSource(strings = {"titulo", "autor", "id"})
    @DisplayName("Deve testar busca com diferentes tipos")
    public void testBuscarComDiferentesTipos(String tipo) {
        String termo = switch (tipo) {
            case "titulo" -> "Vidas Secas";
            case "autor" -> "Graciliano Ramos";
            case "id" -> "15";
            default -> "teste";
        };

        buscaPage.realizarBusca(tipo, termo);
        assertEquals(tipo, buscaPage.getTipoSelecionado());
    }

    @ParameterizedTest
    @CsvSource({
            "titulo, teste123, Nenhum livro encontrado",
            "autor, autorInexistente, Nenhum livro encontrado",
    })
    @DisplayName("Deve exibir mensagem quando não encontrar resultados")
    public void testBuscaSemResultados(String tipo, String termo, String mensagemEsperada) {
        buscaPage.realizarBusca(tipo, termo);
        assertTrue(buscaPage.isMensagemNenhumResultado());
        assertEquals(0, buscaPage.getQuantidadeResultados());
    }

    @Test
    @DisplayName("Deve exibir erro ao buscar por ID inválido")
    public void testBuscarPorIdInvalido() {
        buscaPage.realizarBusca("id", "abc");
        WebElement mensagemErro = driver.findElement(By.className("error-details"));
        assertTrue(mensagemErro.getText().contains("ID deve ser um número válido"), "ID deve ser um número válido");
    }

    @Test
    @DisplayName("Deve manter valores no formulário após busca")
    public void testManterValoresFormulario() {
        String tipo = "titulo";
        String termo = "A Hora da Estrela";

        buscaPage.realizarBusca(tipo, termo);

        assertEquals(tipo, buscaPage.getTipoSelecionado());
        assertEquals(termo, buscaPage.getTermoDigitado());
    }

    @Test
    @DisplayName("Deve voltar para lista de livros")
    public void testVoltarParaListaLivros() {
        buscaPage.clickVoltar();
        assertTrue(driver.getCurrentUrl().contains("/livros"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Deve validar termo vazio ou com espaços")
    public void testTermoVazio(String termo) {
        buscaPage.selecionarTipoBusca("titulo");
        buscaPage.digitarTermo(termo);
        buscaPage.clickBuscar();

        // O formulário deve permanecer na mesma página devido à validação HTML
        assertTrue(driver.getCurrentUrl().contains("/buscar"));
    }

    @Test
    @DisplayName("Deve testar busca parcial por título")
    public void testBuscaParcialTitulo() {
        buscaPage.realizarBusca("titulo", "Hora");

        if (buscaPage.isResultadosDisplayed()) {
            assertTrue(buscaPage.contemLivroComTitulo("Hora"));
        }
    }

    @Test
    @DisplayName("Deve testar busca case insensitive")
    public void testBuscaCaseInsensitive() {
        buscaPage.realizarBusca("titulo", "hora da estrela");

        if (buscaPage.isResultadosDisplayed()) {
            assertTrue(buscaPage.contemLivroComTitulo("A Hora da Estrela"));
        }
    }

    @Test
    @DisplayName("Deve verificar estrutura da tabela de resultados")
    public void testEstruturaTabela() {
        buscaPage.realizarBusca("autor", "Machado de Assis");

        if (buscaPage.isResultadosDisplayed()) {
            assertFalse(buscaPage.getIdsResultados().isEmpty());
            assertFalse(buscaPage.getTitulosResultados().isEmpty());
            assertFalse(buscaPage.getAutoresResultados().isEmpty());
            assertFalse(buscaPage.getStatusResultados().isEmpty());
        }
    }
}
