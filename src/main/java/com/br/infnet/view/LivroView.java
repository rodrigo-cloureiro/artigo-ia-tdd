package com.br.infnet.view;

import com.br.infnet.model.Livro;
import com.br.infnet.service.LivroService;

import java.util.List;
import java.util.Map;

public class LivroView {

    public static String renderList(List<Livro> livros) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Livros do Acervo"));
        html.append("<div class='container'>");
        html.append("<h1>Sistema de Gerenciamento da Biblioteca Nacional</h1>");
        html.append("<nav class='nav'>");
        html.append("<a href='/livros/novo' class='btn'>Cadastrar novo livro</a>");
        html.append("<a href='/emprestimos' class='btn'>Empréstimos</a>");
        html.append("<a href='/buscar' class='btn'>Buscar</a>");
        html.append("</nav>");

        if (livros.isEmpty()) {
            html.append("<p>Nenhum livro cadastrado.</p>");
        } else {
            html.append("<table class='table'>");
            html.append("<tr><th>ID</th><th>Título</th><th>Autor</th><th>ISBN</th><th>Status</th><th>Ações</th></tr>");
            for (Livro livro : livros) {
                html.append("<tr>");
                html.append("<td>").append(livro.getId()).append("</td>");
                html.append("<td>").append(livro.getTitulo()).append("</td>");
                html.append("<td>").append(livro.getAutor()).append("</td>");
                html.append("<td>").append(livro.getIsbn()).append("</td>");
                html.append("<td>").append(livro.isDisponivel() ? "Disponível" : "Emprestado").append("</td>");
                html.append("<td>");
                html.append("<div class='button-group'>");
                html.append("<a href='/livros/").append(livro.getId()).append("/editar' class='btn btn-small btn-secondary'>Editar</a>");
                if (livro.isDisponivel()) {
                    html.append("<a href='/livros/").append(livro.getId()).append("/emprestar' class='btn btn-small'>Emprestar</a>");
                }
                html.append("<form style='display:inline' method='post' action='/livros/").append(livro.getId()).append("/remover'>");
                html.append("<button type='submit' class='btn-small btn-danger' onclick='return confirm(\"Confirma exclusão?\")'>Remover</button>");
                html.append("</form>");
                html.append("</div>");
                html.append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }


    public static String renderForm(Map<String, Object> model) {
        boolean isEdit = model.containsKey("id");
        String title = isEdit ? "Editar Livro" : "Novo Livro";
        String action = isEdit ? "/livros/" + model.get("id") + "/editar" : "/livros";

        StringBuilder html = new StringBuilder();
        html.append(getHeader(title));
        html.append("<div class='container'>");
        html.append("<h1>").append(title).append("</h1>");

        if (model.containsKey("erro")) {
            html.append("<div class='error'>").append(model.get("erro")).append("</div>");
        }

        html.append("<form method='post' action='").append(action).append("'>");
        html.append("<div class='form-group'>");
        html.append("<label for='titulo'>Título:</label>");
        html.append("<input type='text' id='titulo' name='titulo' value='").append(model.getOrDefault("titulo", "")).append("' required>");
        html.append("</div>");

        html.append("<div class='form-group'>");
        html.append("<label for='autor'>Autor:</label>");
        html.append("<input type='text' id='autor' name='autor' value='").append(model.getOrDefault("autor", "")).append("' required>");
        html.append("</div>");

        html.append("<div class='form-group'>");
        html.append("<label for='isbn'>ISBN (13 caracteres):</label>");
        html.append("<input type='text' id='isbn' name='isbn' value='").append(model.getOrDefault("isbn", "")).append("' required>");
        html.append("</div>");

        html.append("<div class='form-actions'>");
        html.append("<button type='submit' class='btn'>").append(isEdit ? "Atualizar" : "Cadastrar").append("</button>");
        html.append("<a href='/livros' class='btn btn-secondary'>Cancelar</a>");
        html.append("</div>");
        html.append("</form>");
        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }


    public static String renderEmprestimos(List<Livro> emprestimos) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Livros em empréstimos"));
        html.append("<div class='container'>");
        html.append("<h1>Livros Emprestados</h1>");
        html.append("<a href='/livros' class='btn'>Voltar ao Acervo</a>");

        if (emprestimos.isEmpty()) {
            html.append("<p>Nenhum livro em empréstimo.</p>");
        } else {
            html.append("<table class='table'>");
            html.append("<tr><th>ID</th><th>Título</th><th>Autor</th><th>Data Empréstimo</th><th>Prazo</th><th>Data Estimada para Devolução</th><th>Ações</th></tr>");
            for (Livro livro : emprestimos) {
                html.append("<tr>");
                html.append("<td>").append(livro.getId()).append("</td>");
                html.append("<td>").append(livro.getTitulo()).append("</td>");
                html.append("<td>").append(livro.getAutor()).append("</td>");
                html.append("<td>").append(livro.getDataEmprestimo()).append("</td>");
                html.append("<td>").append(livro.getPrazoDevolucao()).append(" dias</td>");
                html.append("<td>").append(livro.getDataEstimadaDevolucao()).append("</td>");
                html.append("<td>");
                html.append("<form style='display:inline' method='post' action='/livros/").append(livro.getId()).append("/devolver'>");
                html.append("<button type='submit' class='btn-small'>Devolver livro</button>");
                html.append("</form>");
                html.append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }
        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }

    public static String renderFormEmprestimo(Livro livro) {
        return renderFormEmprestimo(livro, null);
    }

    public static String renderFormEmprestimo(Livro livro, String erro) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Emprestar Livro"));
        html.append("<div class='container'>");
        html.append("<h1>Emprestar Livro</h1>");
        html.append("<p><strong>Título:</strong> ").append(livro.getTitulo()).append("</p>");
        html.append("<p><strong>Autor:</strong> ").append(livro.getAutor()).append("</p>");

        // Exibir erro se existir
        if (erro != null && !erro.trim().isEmpty()) {
            html.append("<div class='error'>").append(erro).append("</div>");
        }

        html.append("<form method='post' action='/livros/").append(livro.getId()).append("/emprestar'>");
        html.append("<div class='form-group'>");
        html.append("<label for='prazo'>Prazo (dias):</label>");
        html.append("<input type='number' id='prazo' name='prazo' value='7' required>");
        html.append("</div>");
        html.append("<div class='form-actions'>");
        html.append("<button type='submit' class='btn'>Emprestar</button>");
        html.append("<a href='/livros' class='btn btn-secondary'>Cancelar</a>");
        html.append("</div>");
        html.append("</form>");
        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }



    public static String renderBusca(String tipo, String termo, LivroService service) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Buscar Livros"));
        html.append("<div class='container'>");
        html.append("<h1>Buscar Livros</h1>");

        html.append("<form method='get' action='/buscar'>");
        html.append("<div class='form-group'>");
        html.append("<label for='tipo'>Buscar por:</label>");
        html.append("<select id='tipo' name='tipo' required>");
        html.append("<option value=''>Selecione...</option>");
        html.append("<option value='titulo'").append("titulo".equals(tipo) ? " selected" : "").append(">Título</option>");
        html.append("<option value='autor'").append("autor".equals(tipo) ? " selected" : "").append(">Autor</option>");
        html.append("<option value='id'").append("id".equals(tipo) ? " selected" : "").append(">ID</option>");
        html.append("</select>");
        html.append("</div>");

        html.append("<div class='form-group'>");
        html.append("<label for='termo'>Busca:</label>");
        html.append("<input type='text' id='termo' name='termo' value='").append(termo != null ? termo : "").append("' required>");
        html.append("</div>");

        html.append("<div class='form-actions'>");
        html.append("<button type='submit' class='btn'>Buscar</button>");
        html.append("<a href='/livros' class='btn btn-secondary'>Voltar</a>");
        html.append("</div>");
        html.append("</form>");

        if (tipo != null && termo != null && !termo.trim().isEmpty()) {
            try {
                List<Livro> resultados = switch (tipo) {
                    case "titulo" -> service.buscarLivroPorTituloNoAcervo(termo);
                    case "autor" -> service.buscarLivroPorAutorNoAcervo(termo);
                    case "id" -> List.of(service.buscarLivroPorIDNoAcervo(Integer.parseInt(termo)));
                    default -> List.of();
                };

                html.append("<h2>Resultados:</h2>");
                if (resultados.isEmpty()) {
                    html.append("<p>Nenhum livro encontrado.</p>");
                } else {
                    html.append("<table class='table'>");
                    html.append("<tr><th>ID</th><th>Título</th><th>Autor</th><th>ISBN</th><th>Status</th></tr>");
                    for (Livro livro : resultados) {
                        html.append("<tr>");
                        html.append("<td>").append(livro.getId()).append("</td>");
                        html.append("<td>").append(livro.getTitulo()).append("</td>");
                        html.append("<td>").append(livro.getAutor()).append("</td>");
                        html.append("<td>").append(livro.getIsbn()).append("</td>");
                        html.append("<td>").append(livro.isDisponivel() ? "Disponível" : "Emprestado").append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");
                }
            } catch (Exception e) {
                html.append("<div class='error'>Erro na busca: ").append(e.getMessage()).append("</div>");
            }
        }

        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }

    public static String renderMultaPendente(String mensagemMulta) {

        return getHeader("Multa Pendente") +
                "<div class='container'>" +
                "<h1>Multa Pendente</h1>" +
                "<div class='error'>" +
                "<h3>⚠️ Não é possível devolver o livro</h3>" +
                "<p>" + mensagemMulta + "</p>" +
                "</div>" +
                "<div class='multa-info'>" +
                "<h3>Informações sobre Multas:</h3>" +
                "<ul>" +
                "<li>Multas devem ser pagas antes da devolução</li>" +
                "<li>Procure a biblioteca para regularizar sua situação</li>" +
                "<li>Após o pagamento, você poderá devolver o livro</li>" +
                "</ul>" +
                "</div>" +
                "<div class='actions'>" +
                "<a href='/emprestimos' class='btn btn-secondary'>Voltar aos Empréstimos</a>" +
                "<a href='/livros' class='btn'>Ir para Acervo</a>" +
                "</div>" +
                "</div>" +
                getFooter();
    }


    public static String renderError(String title, String details) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Erro - " + title));
        html.append("<div class='container'>");
        html.append("<div class='error-page'>");
        html.append("<h1>❌ ").append(escapeHtml(title)).append("</h1>");

        if (details != null && !details.trim().isEmpty()) {
            html.append("<div class='error-details'>");
            html.append("<p>").append(escapeHtml(details)).append("</p>");
            html.append("</div>");
        }

        html.append("<div class='error-actions'>");
        html.append("<a href='/livros' class='btn'>Voltar ao Acervo</a>");
        html.append("<a href='javascript:history.back()' class='btn btn-secondary'>Voltar</a>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append(getFooter());
        return html.toString();
    }


    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }


    private static String getHeader(String title) {
        return """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>%s</title>
                        <meta charset='UTF-8'>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                margin: 0;
                                padding: 20px;
                                background-color: #F3F5F4;
                                color: #0D1F27;
                            }
                            .container {
                                max-width: 1200px;
                                margin: 0 auto;
                                background-color: white;
                                padding: 20px;
                                border-radius: 8px;
                                box-shadow: 0 4px 6px rgba(13, 31, 39, 0.1);
                            }
                            .nav {
                                margin: 20px 0;
                                padding: 15px 0;
                                border-bottom: 2px solid #82AEC1;
                            }
                            .nav a {margin-right: 15px;}
                            .btn {
                                background-color: #2F638D;
                                color: white;
                                padding: 10px 20px;
                                text-decoration: none;
                                border-radius: 5px;
                                border: none;
                                cursor: pointer;
                                display: inline-block;
                                transition: background-color 0.3s ease;
                            }
                            .btn:hover {background-color: #193959;}
                            .btn-secondary {
                                background-color: #82AEC1;
                                color: #0D1F27;
                            }
                            .btn-secondary:hover {
                                background-color: #193959;
                                color: white;
                            }
                            .btn-danger {
                                background-color: #dc3545;
                                color: white;
                            }
                            .btn-danger:hover {background-color: #c82333;}
                            .btn-small {
                                padding: 6px 12px;
                                font-size: 13px;
                            }
                            .table {
                                width: 100%%;
                                border-collapse: collapse;
                                margin: 20px 0;
                                border: 1px solid #82AEC1;
                            }
                            .table th, .table td {
                                border: 1px solid #82AEC1;
                                padding: 12px 8px;
                                text-align: left;
                            }
                            .table th {
                                background-color: #2F638D;
                                color: white;
                                font-weight: bold;
                            }
                            .table tr:nth-child(even) {
                                background-color: #F3F5F4;
                            }
                            .form-group {
                                margin: 20px 0;
                            }
                            .form-group label {
                                display: block;
                                margin-bottom: 8px;
                                font-weight: bold;
                                color: #193959;
                            }
                            .form-group input, .form-group select {
                                width: 100%%;
                                padding: 10px;
                                border: 2px solid #82AEC1;
                                border-radius: 5px;
                                font-size: 14px;
                                box-sizing: border-box;
                            }
                            .form-group input:focus, .form-group select:focus {
                                outline: none;
                                border-color: #2F638D;
                                box-shadow: 0 0 5px rgba(47, 99, 141, 0.3);
                            }
                            .error {
                                background-color: #f8d7da;
                                color: #721c24;
                                padding: 15px;
                                border-radius: 5px;
                                margin: 15px 0;
                                border-left: 4px solid #dc3545;
                            }
                            .error-page {
                                text-align: center;
                                padding: 40px 20px;
                            }
                            .error-details {
                                background-color: #f8f9fa;
                                border: 1px solid #dee2e6;
                                border-radius: 5px;
                                padding: 20px;
                                margin: 20px 0;
                                color: #6c757d;
                            }
                            .error-actions {
                                margin: 30px 0;
                                display: flex;
                                gap: 15px;
                                justify-content: center;
                                flex-wrap: wrap;
                            }
                            .multa-info {
                                background-color: #82AEC1;
                                color: #0D1F27;
                                padding: 20px;
                                border-radius: 5px;
                                margin: 20px 0;
                                border-left: 4px solid #193959;
                            }
                            .multa-info h3 {
                                margin-top: 0;
                                color: #193959;
                            }
                            .multa-info ul {
                                margin: 10px 0;
                                padding-left: 20px;
                            }
                            .actions {
                                margin: 25px 0;
                                padding: 15px 0;
                            }
                            .actions a {
                                margin-right: 15px;
                            }
                            h1 {
                                color: #193959;
                                border-bottom: 3px solid #2F638D;
                                padding-bottom: 10px;
                                margin-bottom: 25px;
                            }
                            h2 {
                                color: #2F638D;
                                margin-top: 25px;
                            }
                            h3 {
                                color: #193959;
                            }
                            p {
                                line-height: 1.6;
                                color: #0D1F27;
                            }
                            .button-group {
                                display: flex;
                                gap: 8px;
                                flex-wrap: wrap;
                                align-items: center;
                            }
                            .button-group a, .button-group button {
                                margin: 0;
                            }
                            .form-actions {
                                margin: 25px 0;
                                display: flex;
                                gap: 15px;
                                align-items: center;
                            }
                            .form-actions a, .form-actions button {
                                margin: 0;
                            }
                        </style>
                    </head>
                    <body>
                """.formatted(title);
    }


    private static String getFooter() {
        return """
                </body>
                </html>
                """;
    }
}
