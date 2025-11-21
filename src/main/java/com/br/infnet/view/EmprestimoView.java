package com.br.infnet.view;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.service.EmprestimoService;

import java.util.List;

public class EmprestimoView {

    public static String renderEmprestimos(List<Emprestimo> emprestimos, EmprestimoService emprestimoService) {
        StringBuilder html = new StringBuilder();
        html.append(getHeader("Livros Emprestados"));
        html.append("<div class='container'>");
        html.append("<h1>Empréstimos Ativos</h1>");
        html.append("<a href='/livros' class='btn'>Voltar ao Acervo</a>");

        if (emprestimos.isEmpty()) {
            html.append("<p>Nenhum empréstimo ativo.</p>");
        } else {
            html.append("<table class='table'>");
            html.append("<tr><th>ID</th><th>Título</th><th>Autor</th><th>Data Empréstimo</th><th>Prazo</th><th>Data Estimada</th><th>Ações</th></tr>");

            for (Emprestimo emprestimo : emprestimos) {
                Livro livro = emprestimoService.obterLivroPorId(emprestimo.getLivroId());
                System.out.println("Livro ID " + emprestimo.getLivroId() + " título: " + (livro != null ? livro.getTitulo() : "null"));
                if (livro != null) {
                    html.append("<tr>");
                    html.append("<td>").append(emprestimo.getId()).append("</td>");
                    html.append("<td>").append(livro.getTitulo()).append("</td>");
                    html.append("<td>").append(livro.getAutor()).append("</td>");
                    html.append("<td>").append(emprestimo.getDataEmprestimo()).append("</td>");
                    html.append("<td>").append(emprestimo.getPrazoDevolucao()).append(" dias</td>");
                    html.append("<td>").append(emprestimo.getDataEstimadaDevolucao()).append("</td>");
                    html.append("<td>");
                    html.append("<form style='display:inline' method='post' action='/emprestimos/livros/").append(emprestimo.getLivroId()).append("/devolver'>");
                    html.append("<button type='submit' class='btn-small'>Devolver</button>");
                    html.append("</form>");
                    html.append("</td>");
                    html.append("</tr>");
                }
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
        html.append("<form method='post' action='/emprestimos/livros/").append(livro.getId()).append("/emprestar'>");
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
                """.formatted("Empréstimos Ativos");
    }

    private static String getFooter() {
        return """
                </body>
                </html>
                """;
    }
}
