package com.biblioteca.properties;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import com.biblioteca.model.Livro;
import com.biblioteca.security.SecurityUtils;
import static org.assertj.core.api.Assertions.*;

class LivroProperties {

    @Property
    void todos_livros_validos_devem_ser_aceitos(
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String titulo,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String autor,
            @ForAll("isbnValido") String isbn) {

        Assume.that(SecurityUtils.isValidTitle(titulo));
        Assume.that(SecurityUtils.isValidAuthor(autor));

        Livro livro = new Livro(titulo, autor, isbn);

        assertThat(livro.validate().isValid()).isTrue();
    }

    @Property
    void livros_com_titulo_invalido_devem_ser_rejeitados(
            @ForAll @StringLength(min = 1, max = 255) String tituloInvalido,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String autor,
            @ForAll("isbnValido") String isbn) {

        Assume.that(!SecurityUtils.isValidTitle(tituloInvalido));

        Livro livro = new Livro(tituloInvalido, autor, isbn);

        assertThat(livro.validate().isValid()).isFalse();
    }

    @Provide
    Arbitrary<String> isbnValido() {
        return Arbitraries.of(
                "9788535930001", "9788572320002", "9788535910003",
                "9788572320004", "9788572320005", "9788535930006"
        );
    }
}