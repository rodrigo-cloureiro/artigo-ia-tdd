package br.com.biblioteca.dao;

import br.com.biblioteca.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Interface de Contrato de Acesso a Dados (DAO) para a entidade Book.
 * Define as operações de persistência (CRUD).
 */
public interface BookDAO {

    /**
     * Salva um novo livro ou atualiza um existente.
     * Se book.id() for nulo, cria um novo livro com um novo ID.
     * Se book.id() não for nulo, atualiza o livro com esse ID.
     *
     * @param book O livro a ser salvo.
     * @return O livro salvo (com o ID, se for novo).
     */
    Book save(Book book);

    /**
     * Busca um livro pelo seu ID.
     *
     * @param id O ID do livro.
     * @return um Optional contendo o livro, ou vazio se não encontrado.
     */
    Optional<Book> findById(Long id);

    /**
     * Busca um livro pelo seu ISBN.
     *
     * @param isbn O ISBN de 13 dígitos.
     * @return um Optional contendo o livro, ou vazio se não encontrado.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Retorna todos os livros cadastrados.
     *
     * @return Uma lista de todos os livros.
     */
    List<Book> findAll();

    /**
     * Busca livros cujo título, autor ou ISBN contenham o termo de busca.
     * A busca não é case-sensitive.
     *
     * @param searchTerm O termo a ser buscado.
     * @return Uma lista de livros que correspondem ao critério.
     */
    List<Book> search(String searchTerm);

    /**
     * Exclui um livro pelo seu ID.
     *
     * @param id O ID do livro a ser excluído.
     * @return true se o livro foi encontrado e excluído, false caso contrário.
     */
    boolean deleteById(Long id);
}