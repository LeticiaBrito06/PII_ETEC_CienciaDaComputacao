package telas.aluno;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import database.DAO.PartidaDAO;
import model.Aluno;
import model.Partida;
import serviços.SessaoUsuario;

public class TelaDesempenhoAluno extends JFrame {

    private Aluno aluno;

    private final Color AZUL_ESCURO = new Color(34, 62, 107);
    private final Color AZUL_CLARO = new Color(160, 205, 245);
    private final Color AZUL_MEDIO = new Color(70, 130, 230);
    private final Color BRANCO_GELO = new Color(238, 242, 248);
    private final Color CINZA_CLARO = new Color(210, 210, 210);

    private static final PDFont FONTE_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final PDFont FONTE_NEGRITO = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public TelaDesempenhoAluno(Aluno aluno) {
        this.aluno = carregarAlunoComHistorico(aluno);
        configurarJanela();
        montarTela();
    }

    public TelaDesempenhoAluno() {
        this(obterAlunoLogadoDaSessao());
    }

    private static Aluno obterAlunoLogadoDaSessao() {
        if (SessaoUsuario.getUsuarioLogado() instanceof Aluno alunoLogado) {
            return alunoLogado;
        }

        throw new IllegalStateException("Nenhum aluno logado na sessão.");
    }

    private Aluno carregarAlunoComHistorico(Aluno alunoRecebido) {
        Aluno alunoCarregado = alunoRecebido;

        if (alunoCarregado == null || alunoCarregado.getId() <= 0) {
            alunoCarregado = obterAlunoLogadoDaSessao();
        }

        try {
            PartidaDAO partidaDAO = new PartidaDAO();
            List<Partida> partidas = partidaDAO.listarPorAluno(alunoCarregado);
            alunoCarregado.setHistoricoPartidas(partidas);

            System.out.println("Partidas carregadas para o aluno "
                    + alunoCarregado.getId() + ": " + partidas.size());

        } catch (SQLException erro) {
            erro.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar histórico do aluno: " + erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        return alunoCarregado;
    }

    private void configurarJanela() {
        setTitle("Tela - Desempenho do Aluno");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        PainelFundo painelBase = new PainelFundo();
        painelBase.setLayout(new GridBagLayout());
        setContentPane(painelBase);

        JPanel conteinerCentral = new JPanel(null);
        conteinerCentral.setPreferredSize(new Dimension(1000, 750));
        conteinerCentral.setOpaque(false);
        painelBase.add(conteinerCentral);

        adicionarIcones(conteinerCentral);

        Estatisticas estatisticas = calcularEstatisticasAluno(aluno);

        PainelArredondado painelGrafico = new PainelArredondado(22);
        painelGrafico.setBounds(18, 80, 480, 540);
        painelGrafico.setBackground(AZUL_ESCURO);
        painelGrafico.setLayout(null);
        conteinerCentral.add(painelGrafico);

        JLabel titulo = new JLabel("Desempenho do mês", SwingConstants.CENTER);
        titulo.setBounds(0, 18, 480, 40);
        titulo.setFont(new Font("Verdana", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelGrafico.add(titulo);

        GraficoPizza grafico = new GraficoPizza(
                estatisticas.percentualAcertos,
                estatisticas.percentualErros,
                true);
        grafico.setBounds(20, 70, 440, 390);
        painelGrafico.add(grafico);

        JLabel resumo = new JLabel(
                "Total: " + estatisticas.totalRespostas
                        + " respostas | "
                        + estatisticas.totalAcertos + " acertos | "
                        + estatisticas.totalErros + " erros",
                SwingConstants.CENTER);
        resumo.setBounds(0, 475, 480, 35);
        resumo.setFont(new Font("Verdana", Font.BOLD, 14));
        resumo.setForeground(Color.WHITE);
        painelGrafico.add(resumo);

        PainelArredondado painelRelatorio = new PainelArredondado(18);
        painelRelatorio.setBounds(535, 420, 420, 115);
        painelRelatorio.setBackground(AZUL_ESCURO);
        painelRelatorio.setLayout(null);
        conteinerCentral.add(painelRelatorio);

        JLabel textoRelatorio = new JLabel("Gerar relatório", SwingConstants.CENTER);
        textoRelatorio.setBounds(0, 8, 420, 35);
        textoRelatorio.setFont(new Font("Verdana", Font.BOLD, 20));
        textoRelatorio.setForeground(Color.WHITE);
        painelRelatorio.add(textoRelatorio);

        JButton botaoPdf = new JButton("Baixar PDF");
        botaoPdf.setBounds(26, 54, 368, 44);
        botaoPdf.setFont(new Font("Verdana", Font.BOLD, 18));
        botaoPdf.setForeground(AZUL_ESCURO);
        botaoPdf.setBackground(BRANCO_GELO);
        botaoPdf.setFocusPainted(false);
        botaoPdf.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoPdf.addActionListener(evento -> gerarPdfDoAluno());
        painelRelatorio.add(botaoPdf);
    }

    private void adicionarIcones(JPanel painel) {
        JButton botaoPerfil = criarBotaoIconeReal("imagens/Perfil.png");
        botaoPerfil.setBounds(18, 18, 45, 45);
        botaoPerfil.addActionListener(e -> abrirPerfil());
        painel.add(botaoPerfil);

        JButton botaoSair = criarBotaoIconeReal("imagens/Sair.png");
        botaoSair.setBounds(920, 18, 45, 45);
        botaoSair.addActionListener(e -> {
            dispose();
            new TelaMenuAluno(this.aluno).setVisible(true);
        });
        painel.add(botaoSair);
    }

    private void abrirPerfil() {
        if (this.aluno != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome: " + this.aluno.getNome()
                            + "\nE-mail: " + this.aluno.getEmail(),
                    "Perfil do Aluno",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Informações do aluno não encontradas.");
        }
    }

    private JButton criarBotaoIconeReal(String caminho) {
        JButton botao = new JButton();

        try {
            ImageIcon iconeOriginal = new ImageIcon(caminho);
            Image imagem = iconeOriginal.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            botao.setIcon(new ImageIcon(imagem));
        } catch (Exception erro) {
            botao.setText("?");
        }

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return botao;
    }

    private Estatisticas calcularEstatisticasAluno(Aluno aluno) {
        Estatisticas estatisticas = new Estatisticas();

        if (aluno == null || aluno.getHistoricoPartidas() == null) {
            return estatisticas;
        }

        for (Partida partida : aluno.getHistoricoPartidas()) {
            if (partida == null) {
                continue;
            }

            int acertos = partida.getTotalAcertos();
            int erros = partida.getTotalErros();
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

    private File criarPastaRelatorios() {
        File pasta = new File("relatorios");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        return pasta;
    }

    private void gerarPdfDoAluno() {
        File arquivo = null;

        try {
            File pastaRelatorios = criarPastaRelatorios();
            String nomeAluno = aluno != null ? aluno.getNome() : "Aluno";
            String nomeArquivo = "Relatorio_" + limparNomeArquivo(nomeAluno) + ".pdf";

            arquivo = new File(pastaRelatorios, nomeArquivo);

            try (PDDocument documento = new PDDocument()) {
                PDPage pagina = new PDPage(PDRectangle.A4);
                documento.addPage(pagina);

                try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                    desenharRelatorioAluno(conteudo);
                }

                documento.save(arquivo);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "PDF gerado com sucesso!\n\nArquivo salvo em:\n" + arquivo.getAbsolutePath(),
                    "PDF gerado",
                    JOptionPane.INFORMATION_MESSAGE);

            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(arquivo);
                }
            } catch (IOException erroAbertura) {
                System.out.println("PDF gerado, mas não foi possível abrir automaticamente.");
            }

        } catch (Exception erro) {
            erro.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao gerar PDF:\n" + erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desenharRelatorioAluno(PDPageContentStream conteudo) throws IOException {
        Estatisticas estatisticas = calcularEstatisticasAluno(aluno);

        desenharCabecalhoPdf(conteudo, "LabQuest - Relatorio individual do aluno");

        escreverTexto(conteudo, "Dados do aluno", FONTE_NEGRITO, 18, 50, 730, AZUL_ESCURO);
        escreverTexto(conteudo, "Nome: " + aluno.getNome(), FONTE_NORMAL, 12, 50, 705, Color.BLACK);
        escreverTexto(conteudo, "E-mail: " + aluno.getEmail(), FONTE_NORMAL, 12, 50, 669, Color.BLACK);

        escreverTexto(conteudo, "Resumo de desempenho", FONTE_NEGRITO, 15, 50, 630, AZUL_ESCURO);
        escreverTexto(conteudo, "Partidas: " + estatisticas.totalPartidas, FONTE_NORMAL, 12, 50, 608, Color.BLACK);
        escreverTexto(conteudo, "Pontuacao total: " + estatisticas.pontuacaoTotal, FONTE_NORMAL, 12, 50, 590,
                Color.BLACK);
        escreverTexto(conteudo, "Respostas: " + estatisticas.totalRespostas, FONTE_NORMAL, 12, 50, 572, Color.BLACK);
        escreverTexto(
                conteudo, "Acertos: " + estatisticas.totalAcertos + " ("
                        + formatarPercentual(estatisticas.percentualAcertos) + ")",
                FONTE_NORMAL, 12, 50, 554, Color.BLACK);
        escreverTexto(conteudo,
                "Erros: " + estatisticas.totalErros + " (" + formatarPercentual(estatisticas.percentualErros) + ")",
                FONTE_NORMAL, 12, 50, 536, Color.BLACK);

        desenharBarraDesempenhoPdf(
                conteudo,
                50,
                492,
                480,
                28,
                estatisticas.percentualAcertos,
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

        if (aluno.getHistoricoPartidas() != null) {
            DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Partida partida : aluno.getHistoricoPartidas()) {
                if (y < 70) {
                    break;
                }

                int acertos = partida.getTotalAcertos();
                int erros = partida.getTotalErros();
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
    }

    private void definirCorPreenchimento(PDPageContentStream conteudo, Color cor) throws IOException {
        conteudo.setNonStrokingColor(
                cor.getRed() / 255f,
                cor.getGreen() / 255f,
                cor.getBlue() / 255f);
    }

    private void definirCorContorno(PDPageContentStream conteudo, Color cor) throws IOException {
        conteudo.setStrokingColor(
                cor.getRed() / 255f,
                cor.getGreen() / 255f,
                cor.getBlue() / 255f);
    }

    private void desenharCabecalhoPdf(PDPageContentStream conteudo, String titulo) throws IOException {
        definirCorPreenchimento(conteudo, AZUL_ESCURO);

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

        definirCorPreenchimento(conteudo, AZUL_CLARO);
        conteudo.addRect(x, y, larguraAcertos, altura);
        conteudo.fill();

        definirCorPreenchimento(conteudo, AZUL_MEDIO);
        conteudo.addRect(x + larguraAcertos, y, larguraErros, altura);
        conteudo.fill();

        definirCorContorno(conteudo, AZUL_ESCURO);
        conteudo.addRect(x, y, largura, altura);
        conteudo.stroke();

        escreverTexto(conteudo, "Acertos: " + formatarPercentual(percentualAcertos), FONTE_NEGRITO, 10, x, y - 18,
                Color.BLACK);
        escreverTexto(conteudo, "Erros: " + formatarPercentual(percentualErros), FONTE_NEGRITO, 10, x + 180, y - 18,
                Color.BLACK);
    }

    private void desenharLinhaTabela(PDPageContentStream conteudo, float y, boolean cabecalho) throws IOException {
        if (cabecalho) {
            definirCorPreenchimento(conteudo, AZUL_ESCURO);
        } else {
            definirCorPreenchimento(conteudo, new Color(235, 235, 235));
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
        definirCorPreenchimento(conteudo, cor);
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
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("–", "-")
                .replace("—", "-");

        String semAcentos = Normalizer.normalize(textoTratado, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return semAcentos.replaceAll("[^\\x20-\\x7E]", "");
    }

    private String formatarPercentual(double valor) {
        return String.format("%.1f%%", valor);
    }

    private String limparNomeArquivo(String texto) {
        if (texto == null || texto.isBlank()) {
            return "aluno";
        }

        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return semAcento.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private class GraficoPizza extends JPanel {

        private final double percentualAcertos;
        private final double percentualErros;
        private final boolean grande;

        public GraficoPizza(double percentualAcertos, double percentualErros, boolean grande) {
            this.percentualAcertos = percentualAcertos;
            this.percentualErros = percentualErros;
            this.grande = grande;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);

            Graphics2D desenho = (Graphics2D) grafico.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int margem = grande ? 150 : 90;
            int tamanho = Math.min(getWidth(), getHeight()) - margem;

            if (tamanho < 80) {
                tamanho = 80;
            }

            int x = (getWidth() - tamanho) / 2;
            int y = (getHeight() - tamanho) / 2;

            int anguloAcertos = (int) Math.round(percentualAcertos * 3.6);
            int anguloErros = 360 - anguloAcertos;

            desenho.setColor(AZUL_CLARO);
            desenho.fillArc(x, y, tamanho, tamanho, 90, -anguloAcertos);

            desenho.setColor(AZUL_MEDIO);
            desenho.fillArc(x, y, tamanho, tamanho, 90 - anguloAcertos, -anguloErros);

            desenho.setColor(Color.WHITE);
            desenho.setFont(new Font("Verdana", Font.BOLD, grande ? 13 : 10));

            desenharTextoDoGrafico(
                    desenho,
                    x,
                    y,
                    tamanho,
                    90 - (anguloAcertos / 2),
                    "Acertou",
                    formatarPercentual(percentualAcertos));

            desenharTextoDoGrafico(
                    desenho,
                    x,
                    y,
                    tamanho,
                    90 - anguloAcertos - (anguloErros / 2),
                    "Errou",
                    formatarPercentual(percentualErros));

            desenho.dispose();
        }

        private void desenharTextoDoGrafico(
                Graphics2D desenho,
                int x,
                int y,
                int tamanho,
                int angulo,
                String linha1,
                String linha2) {
            double radiano = Math.toRadians(angulo);
            int raio = tamanho / 2 + (grande ? 35 : 20);

            int posicaoX = (int) (x + tamanho / 2 + raio * Math.cos(radiano));
            int posicaoY = (int) (y + tamanho / 2 - raio * Math.sin(radiano));

            FontMetrics medidas = desenho.getFontMetrics();

            int largura1 = medidas.stringWidth(linha1);
            int largura2 = medidas.stringWidth(linha2);

            desenho.drawString(linha1, posicaoX - largura1 / 2, posicaoY);
            desenho.drawString(linha2, posicaoX - largura2 / 2, posicaoY + medidas.getHeight());
        }
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

    private static class PainelArredondado extends JPanel {

        private final int raio;

        public PainelArredondado(int raio) {
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D desenho = (Graphics2D) grafico.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(getBackground());
            desenho.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);

            desenho.dispose();
        }
    }

    private static class PainelFundo extends JPanel {

        private Image imagemFundo;

        public PainelFundo() {
            imagemFundo = new ImageIcon("imagens/Menu.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);

            if (imagemFundo != null && imagemFundo.getWidth(null) > 0) {
                double escala = Math.max(
                        (double) getWidth() / imagemFundo.getWidth(null),
                        (double) getHeight() / imagemFundo.getHeight(null));

                int novaLargura = (int) (imagemFundo.getWidth(null) * escala);
                int novaAltura = (int) (imagemFundo.getHeight(null) * escala);

                grafico.drawImage(
                        imagemFundo,
                        (getWidth() - novaLargura) / 2,
                        (getHeight() - novaAltura) / 2,
                        novaLargura,
                        novaAltura,
                        null);
            } else {
                grafico.setColor(new Color(223, 239, 252));
                grafico.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaDesempenhoAluno tela = new TelaDesempenhoAluno();
            tela.setVisible(true);
        });
    }
}