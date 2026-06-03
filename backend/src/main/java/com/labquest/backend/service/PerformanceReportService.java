package com.labquest.backend.service;

import com.labquest.backend.entity.AnswerEntity;
import com.labquest.backend.entity.GameEntity;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.GameRepository;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceReportService {

    private static final Color AZUL_ESCURO = new Color(34, 62, 107);
    private static final Color AZUL_CLARO = new Color(160, 205, 245);
    private static final Color AZUL_MEDIO = new Color(70, 130, 230);

    private static final PDFont FONTE_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONTE_NEGRITO = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private final UserService userService;
    private final GameRepository gameRepository;

    public PerformanceReportService(UserService userService, GameRepository gameRepository) {
        this.userService = userService;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfAluno(Integer alunoId) {
        UserEntity aluno = userService.getEntityById(alunoId);
        if (aluno.getTipo() != UserType.ALUNO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O usuario informado nao e um aluno.");
        }

        return criarPdf(documento -> desenharRelatorioAluno(documento, aluno, listarPartidas(aluno)));
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfTurma() {
        List<UserEntity> alunos = userService.listEntitiesByType(UserType.ALUNO);
        return criarPdf(documento -> desenharRelatorioTurma(documento, alunos));
    }

    public String nomeArquivoAluno(Integer alunoId) {
        UserEntity aluno = userService.getEntityById(alunoId);
        return "Relatorio_" + limparNomeArquivo(aluno.getNome()) + ".pdf";
    }

    private List<GameEntity> listarPartidas(UserEntity aluno) {
        return gameRepository.findByAlunoIdOrderByDataHoraInicioDescIdDesc(aluno.getId()).stream()
                .filter(GameEntity::isFinalizada)
                .toList();
    }

    private byte[] criarPdf(PdfWriter writer) {
        try (PDDocument documento = new PDDocument();
                ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                writer.write(conteudo);
            }

            documento.save(saida);
            return saida.toByteArray();
        } catch (IOException erro) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar PDF: " + erro.getMessage());
        }
    }

    private void desenharRelatorioTurma(PDPageContentStream conteudo, List<UserEntity> alunos) throws IOException {
        Estatisticas estatisticas = calcularEstatisticasTurma(alunos);

        desenharCabecalhoPdf(conteudo, "LabQuest - Relatorio de desempenho da turma");

        escreverTexto(conteudo, "Resumo geral da turma", FONTE_NEGRITO, 18, 50, 730, AZUL_ESCURO);
        escreverTexto(conteudo, "Total de alunos: " + alunos.size(), FONTE_NORMAL, 12, 50, 705, Color.BLACK);
        escreverTexto(conteudo, "Total de partidas: " + estatisticas.totalPartidas, FONTE_NORMAL, 12, 50, 687,
                Color.BLACK);
        escreverTexto(conteudo, "Total de respostas: " + estatisticas.totalRespostas, FONTE_NORMAL, 12, 50, 669,
                Color.BLACK);
        escreverTexto(
                conteudo,
                "Acertos: " + estatisticas.totalAcertos + " (" + formatarPercentual(estatisticas.percentualAcertos)
                        + ")",
                FONTE_NORMAL,
                12,
                50,
                651,
                Color.BLACK);
        escreverTexto(
                conteudo,
                "Erros: " + estatisticas.totalErros + " (" + formatarPercentual(estatisticas.percentualErros) + ")",
                FONTE_NORMAL,
                12,
                50,
                633,
                Color.BLACK);

        desenharBarraDesempenhoPdf(conteudo, 50, 585, 480, 28, estatisticas.percentualAcertos,
                estatisticas.percentualErros);

        escreverTexto(conteudo, "Lista de alunos", FONTE_NEGRITO, 15, 50, 540, AZUL_ESCURO);

        float y = 515;

        desenharLinhaTabela(conteudo, y, true);
        escreverTexto(conteudo, "Aluno", FONTE_NEGRITO, 9, 55, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Turma", FONTE_NEGRITO, 9, 250, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Acertos", FONTE_NEGRITO, 9, 330, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Erros", FONTE_NEGRITO, 9, 410, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Aproveitamento", FONTE_NEGRITO, 9, 480, y + 8, Color.WHITE);

        y -= 24;

        for (UserEntity aluno : alunos) {
            if (y < 70) {
                break;
            }

            Estatisticas e = calcularEstatisticasAluno(listarPartidas(aluno));

            desenharLinhaTabela(conteudo, y, false);
            escreverTexto(conteudo, limitarTexto(aluno.getNome(), 28), FONTE_NORMAL, 9, 55, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(e.totalAcertos), FONTE_NORMAL, 9, 330, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(e.totalErros), FONTE_NORMAL, 9, 410, y + 8, Color.BLACK);
            escreverTexto(conteudo, formatarPercentual(e.percentualAcertos), FONTE_NORMAL, 9, 480, y + 8,
                    Color.BLACK);

            y -= 24;
        }
    }

    private void desenharRelatorioAluno(PDPageContentStream conteudo, UserEntity aluno, List<GameEntity> partidas)
            throws IOException {
        Estatisticas estatisticas = calcularEstatisticasAluno(partidas);

        desenharCabecalhoPdf(conteudo, "LabQuest - Relatorio individual do aluno");

        escreverTexto(conteudo, "Dados do aluno", FONTE_NEGRITO, 18, 50, 730, AZUL_ESCURO);
        escreverTexto(conteudo, "Nome: " + aluno.getNome(), FONTE_NORMAL, 12, 50, 705, Color.BLACK);
        escreverTexto(conteudo, "E-mail: " + aluno.getEmail(), FONTE_NORMAL, 12, 50, 669, Color.BLACK);

        escreverTexto(conteudo, "Resumo de desempenho", FONTE_NEGRITO, 15, 50, 630, AZUL_ESCURO);
        escreverTexto(conteudo, "Partidas: " + estatisticas.totalPartidas, FONTE_NORMAL, 12, 50, 608, Color.BLACK);
        escreverTexto(conteudo, "Pontuacao total: " + estatisticas.pontuacaoTotal, FONTE_NORMAL, 12, 50, 590,
                Color.BLACK);
        escreverTexto(conteudo, "Respostas: " + estatisticas.totalRespostas, FONTE_NORMAL, 12, 50, 572,
                Color.BLACK);
        escreverTexto(
                conteudo,
                "Acertos: " + estatisticas.totalAcertos + " (" + formatarPercentual(estatisticas.percentualAcertos)
                        + ")",
                FONTE_NORMAL,
                12,
                50,
                554,
                Color.BLACK);
        escreverTexto(
                conteudo,
                "Erros: " + estatisticas.totalErros + " (" + formatarPercentual(estatisticas.percentualErros) + ")",
                FONTE_NORMAL,
                12,
                50,
                536,
                Color.BLACK);

        desenharBarraDesempenhoPdf(conteudo, 50, 492, 480, 28, estatisticas.percentualAcertos,
                estatisticas.percentualErros);

        escreverTexto(conteudo, "Historico de partidas", FONTE_NEGRITO, 15, 50, 445, AZUL_ESCURO);

        float y = 420;

        desenharLinhaTabela(conteudo, y, true);
        escreverTexto(conteudo, "Data/Hora", FONTE_NEGRITO, 10, 55, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Pontuacao", FONTE_NEGRITO, 10, 200, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Acertos", FONTE_NEGRITO, 10, 300, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Erros", FONTE_NEGRITO, 10, 390, y + 8, Color.WHITE);
        escreverTexto(conteudo, "Total", FONTE_NEGRITO, 10, 470, y + 8, Color.WHITE);

        y -= 24;

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (GameEntity partida : partidas) {
            if (y < 70) {
                break;
            }

            int acertos = contarAcertos(partida);
            int erros = contarErros(partida);
            int total = acertos + erros;

            if (total == 0 && partida.getRespostas() != null) {
                total = partida.getRespostas().size();
                erros = Math.max(0, total - acertos);
            }

            String data = "-";
            if (partida.getDataHoraInicio() != null) {
                data = partida.getDataHoraInicio().format(formatoData);
            }

            desenharLinhaTabela(conteudo, y, false);
            escreverTexto(conteudo, data, FONTE_NORMAL, 9, 55, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(partida.getPontuacao()), FONTE_NORMAL, 9, 200, y + 8,
                    Color.BLACK);
            escreverTexto(conteudo, String.valueOf(acertos), FONTE_NORMAL, 9, 300, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(erros), FONTE_NORMAL, 9, 390, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(total), FONTE_NORMAL, 9, 470, y + 8, Color.BLACK);

            y -= 24;
        }
    }

    private Estatisticas calcularEstatisticasTurma(List<UserEntity> alunos) {
        Estatisticas total = new Estatisticas();

        for (UserEntity aluno : alunos) {
            Estatisticas estatisticasAluno = calcularEstatisticasAluno(listarPartidas(aluno));

            total.totalAcertos += estatisticasAluno.totalAcertos;
            total.totalErros += estatisticasAluno.totalErros;
            total.totalRespostas += estatisticasAluno.totalRespostas;
            total.pontuacaoTotal += estatisticasAluno.pontuacaoTotal;
            total.totalPartidas += estatisticasAluno.totalPartidas;
        }

        total.calcularPercentuais();
        return total;
    }

    private Estatisticas calcularEstatisticasAluno(List<GameEntity> partidas) {
        Estatisticas estatisticas = new Estatisticas();

        for (GameEntity partida : partidas) {
            if (partida == null) {
                continue;
            }

            int acertos = contarAcertos(partida);
            int erros = contarErros(partida);
            int totalRespostas = acertos + erros;

            if (totalRespostas == 0 && partida.getRespostas() != null) {
                totalRespostas = partida.getRespostas().size();
                erros = Math.max(0, totalRespostas - acertos);
            }

            estatisticas.totalAcertos += acertos;
            estatisticas.totalErros += erros;
            estatisticas.totalRespostas += totalRespostas;
            estatisticas.pontuacaoTotal += partida.getPontuacao();
            estatisticas.totalPartidas++;
        }

        estatisticas.calcularPercentuais();
        return estatisticas;
    }

    private int contarAcertos(GameEntity partida) {
        if (partida.getRespostas() == null) {
            return 0;
        }
        return (int) partida.getRespostas().stream().filter(AnswerEntity::isCorreta).count();
    }

    private int contarErros(GameEntity partida) {
        if (partida.getRespostas() == null) {
            return 0;
        }
        return (int) partida.getRespostas().stream().filter(answer -> !answer.isCorreta()).count();
    }

    private void desenharCabecalhoPdf(PDPageContentStream conteudo, String titulo) throws IOException {
        conteudo.setNonStrokingColor(AZUL_ESCURO);
        conteudo.addRect(0, 790, 595, 52);
        conteudo.fill();

        escreverTexto(conteudo, titulo, FONTE_NEGRITO, 18, 40, 810, Color.WHITE);
        escreverTexto(
                conteudo,
                "Gerado automaticamente pelo sistema LabQuest",
                FONTE_NORMAL,
                10,
                40,
                795,
                Color.WHITE);
    }

    private void desenharBarraDesempenhoPdf(
            PDPageContentStream conteudo,
            float x,
            float y,
            float largura,
            float altura,
            double percentualAcertos,
            double percentualErros) throws IOException {
        float larguraAcertos = (float) (largura * percentualAcertos / 100.0);
        float larguraErros = largura - larguraAcertos;

        conteudo.setNonStrokingColor(AZUL_CLARO);
        conteudo.addRect(x, y, larguraAcertos, altura);
        conteudo.fill();

        conteudo.setNonStrokingColor(AZUL_MEDIO);
        conteudo.addRect(x + larguraAcertos, y, larguraErros, altura);
        conteudo.fill();

        conteudo.setStrokingColor(AZUL_ESCURO);
        conteudo.addRect(x, y, largura, altura);
        conteudo.stroke();

        escreverTexto(conteudo, "Acertos: " + formatarPercentual(percentualAcertos), FONTE_NEGRITO, 10, x, y - 18,
                Color.BLACK);
        escreverTexto(conteudo, "Erros: " + formatarPercentual(percentualErros), FONTE_NEGRITO, 10, x + 180, y - 18,
                Color.BLACK);
    }

    private void desenharLinhaTabela(PDPageContentStream conteudo, float y, boolean cabecalho) throws IOException {
        if (cabecalho) {
            conteudo.setNonStrokingColor(AZUL_ESCURO);
        } else {
            conteudo.setNonStrokingColor(new Color(235, 235, 235));
        }

        conteudo.addRect(50, y, 500, 22);
        conteudo.fill();
    }

    private void escreverTexto(
            PDPageContentStream conteudo,
            String texto,
            PDFont fonte,
            float tamanho,
            float x,
            float y,
            Color cor) throws IOException {
        conteudo.beginText();
        conteudo.setFont(fonte, tamanho);
        conteudo.setNonStrokingColor(cor);
        conteudo.newLineAtOffset(x, y);
        conteudo.showText(prepararTextoPdf(texto));
        conteudo.endText();
    }

    private String prepararTextoPdf(String texto) {
        if (texto == null) {
            return "";
        }

        String textoTratado = texto
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("â€œ", "\"")
                .replace("â€", "\"")
                .replace("â€“", "-")
                .replace("â€”", "-");

        String semAcentos = Normalizer.normalize(textoTratado, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return semAcentos.replaceAll("[^\\x20-\\x7E]", "");
    }

    private String formatarPercentual(double valor) {
        return String.format("%.1f%%", valor);
    }

    private String limitarTexto(String texto, int limite) {
        if (texto == null) {
            return "";
        }

        if (texto.length() <= limite) {
            return texto;
        }

        return texto.substring(0, limite - 3) + "...";
    }

    private String limparNomeArquivo(String texto) {
        if (texto == null || texto.isBlank()) {
            return "aluno";
        }

        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return semAcento.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    @FunctionalInterface
    private interface PdfWriter {
        void write(PDPageContentStream conteudo) throws IOException;
    }

    private static class Estatisticas {
        int totalAcertos;
        int totalErros;
        int totalRespostas;
        int pontuacaoTotal;
        int totalPartidas;
        double percentualAcertos;
        double percentualErros;

        void calcularPercentuais() {
            if (totalRespostas <= 0) {
                percentualAcertos = 0;
                percentualErros = 0;
                return;
            }

            percentualAcertos = (totalAcertos * 100.0) / totalRespostas;
            percentualErros = 100.0 - percentualAcertos;
        }
    }
}
