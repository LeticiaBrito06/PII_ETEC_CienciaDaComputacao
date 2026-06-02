package telas.professor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import database.DAO.PartidaDAO;
import database.DAO.UsuarioDAO;
import model.Alternativa;
import model.Aluno;
import model.Partida;
import model.Questao;
import serviços.SessaoUsuario;

public class TelaDesempenho extends JFrame {

    private JPanel painelPrincipal;
    private CardLayout navegador;
    private List<Aluno> listaAlunos;

    private final Color AZUL_ESCURO = new Color(34, 62, 107);
    private final Color AZUL_CLARO = new Color(160, 205, 245);
    private final Color AZUL_MEDIO = new Color(70, 130, 230);
    private final Color CINZA_CLARO = new Color(210, 210, 210);
    private final Color BRANCO_GELO = new Color(238, 242, 248);

    private static final PDFont FONTE_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final PDFont FONTE_NEGRITO = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public TelaDesempenho(List<Aluno> alunos) {
        this.listaAlunos = alunos;
        configurarJanela();
        montarEstrutura();
    }

    public TelaDesempenho() {
        this(ObterDadosDoBanco());
    }

    private static List<Aluno> ObterDadosDoBanco() {
        List<Aluno> alunos = new ArrayList<>();

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            PartidaDAO partidaDAO = new PartidaDAO();

            alunos = usuarioDAO.listarAlunos();

            for (Aluno aluno : alunos) {
                List<Partida> partidas = partidaDAO.listarPorAluno(aluno);
                aluno.setHistoricoPartidas(partidas);
            }
        } catch (SQLException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao carregar dados reais de desempenho do banco.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        return alunos;
    }

    private void configurarJanela() {
        setTitle("Tela - Desempenho");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void montarEstrutura() {
        navegador = new CardLayout();
        painelPrincipal = new JPanel(navegador);
        setContentPane(painelPrincipal);

        painelPrincipal.add(criarTelaGeralDaTurma(), "GERAL");
        painelPrincipal.add(criarTelaListaDeAlunos(), "LISTA");

        navegador.show(painelPrincipal, "GERAL");
    }

    private JPanel criarTelaGeralDaTurma() {
        PainelFundo painelBase = new PainelFundo();
        painelBase.setLayout(new GridBagLayout());

        JPanel conteinerCentral = new JPanel(null);
        conteinerCentral.setPreferredSize(new Dimension(1000, 750));
        conteinerCentral.setOpaque(false);
        painelBase.add(conteinerCentral);

        adicionarIcones(conteinerCentral, false);

        Estatisticas estatisticasTurma = calcularEstatisticasTurma();

        PainelArredondado painelGrafico = new PainelArredondado(22);
        painelGrafico.setBounds(18, 80, 480, 540);
        painelGrafico.setBackground(AZUL_ESCURO);
        painelGrafico.setLayout(null);
        conteinerCentral.add(painelGrafico);

        JLabel titulo = new JLabel("Desempenho da turma", SwingConstants.CENTER);
        titulo.setBounds(0, 18, 480, 40);
        titulo.setFont(new Font("Verdana", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelGrafico.add(titulo);

        GraficoPizza grafico = new GraficoPizza(
                estatisticasTurma.percentualAcertos,
                estatisticasTurma.percentualErros,
                true);
        grafico.setBounds(20, 70, 440, 390);
        painelGrafico.add(grafico);

        JLabel resumo = new JLabel(
                "Total: " + estatisticasTurma.totalRespostas
                        + " respostas | "
                        + estatisticasTurma.totalAcertos + " acertos | "
                        + estatisticasTurma.totalErros + " erros",
                SwingConstants.CENTER);
        resumo.setBounds(0, 475, 480, 35);
        resumo.setFont(new Font("Verdana", Font.BOLD, 14));
        resumo.setForeground(Color.WHITE);
        painelGrafico.add(resumo);

        BotaoMenu botaoLista = new BotaoMenu("Lista de alunos");
        botaoLista.setBounds(535, 310, 420, 70);
        botaoLista.addActionListener(evento -> navegador.show(painelPrincipal, "LISTA"));
        conteinerCentral.add(botaoLista);

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
        botaoPdf.addActionListener(evento -> gerarPdfDaTurma());
        painelRelatorio.add(botaoPdf);

        return painelBase;
    }

    private JPanel criarTelaListaDeAlunos() {
        PainelFundo painelBase = new PainelFundo();
        painelBase.setLayout(new GridBagLayout());

        JPanel conteinerCentral = new JPanel(null);
        conteinerCentral.setPreferredSize(new Dimension(1000, 750));
        conteinerCentral.setOpaque(false);
        painelBase.add(conteinerCentral);

        adicionarIcones(conteinerCentral, true);

        PainelArredondado tituloLista = new PainelArredondado(18);
        tituloLista.setBounds(350, 28, 300, 42);
        tituloLista.setBackground(AZUL_ESCURO);
        tituloLista.setLayout(new BorderLayout());
        conteinerCentral.add(tituloLista);

        JLabel textoTitulo = new JLabel("Lista de alunos", SwingConstants.CENTER);
        textoTitulo.setFont(new Font("Verdana", Font.BOLD, 18));
        textoTitulo.setForeground(Color.WHITE);
        tituloLista.add(textoTitulo, BorderLayout.CENTER);

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setOpaque(false);

        for (Aluno aluno : listaAlunos) {
            ItemAluno item = new ItemAluno(aluno);
            painelLista.add(item);
            painelLista.add(Box.createVerticalStrut(8));
        }

        JScrollPane barraRolagem = new JScrollPane(painelLista);
        barraRolagem.setBounds(24, 90, 940, 600);
        barraRolagem.setBorder(null);
        barraRolagem.setOpaque(false);
        barraRolagem.getViewport().setOpaque(false);
        barraRolagem.getVerticalScrollBar().setUnitIncrement(18);
        conteinerCentral.add(barraRolagem);

        return painelBase;
    }

    private void adicionarIcones(JPanel painel, boolean voltarParaTelaGeral) {
        JButton botaoPerfil = criarBotaoIconeReal("imagens/Perfil.png");
        botaoPerfil.setBounds(18, 18, 45, 45);
        botaoPerfil.addActionListener(e -> abrirPerfil());
        painel.add(botaoPerfil);

        JButton botaoSair = criarBotaoIconeReal("imagens/Sair.png");
        botaoSair.setBounds(920, 18, 45, 45);
        botaoSair.addActionListener(e -> {
            dispose();
            new TelaMenuProfessor().setVisible(true);
        });
        painel.add(botaoSair);
    }

    private void abrirPerfil() {
        try {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome: " + SessaoUsuario.getNomeUsuario()
                            + "\nE-mail: " + SessaoUsuario.getEmailUsuario()
                            + "\nTipo: " + SessaoUsuario.getTipoUsuario(),
                    "Perfil do Usuário",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum usuário logado.",
                    "Perfil do Usuário",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private JButton criarBotaoIconeReal(String caminho) {
        JButton btn = new JButton();
        try {
            ImageIcon iconOriginal = new ImageIcon(caminho);
            Image img = iconOriginal.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            btn.setText("?");
        }
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Estatisticas calcularEstatisticasTurma() {
        Estatisticas total = new Estatisticas();

        for (Aluno aluno : listaAlunos) {
            Estatisticas estatisticasAluno = calcularEstatisticasAluno(aluno);

            total.totalAcertos += estatisticasAluno.totalAcertos;
            total.totalErros += estatisticasAluno.totalErros;
            total.totalRespostas += estatisticasAluno.totalRespostas;
            total.pontuacaoTotal += estatisticasAluno.pontuacaoTotal;
            total.totalPartidas += estatisticasAluno.totalPartidas;
        }

        total.calcularPercentuais();
        return total;
    }

    private Estatisticas calcularEstatisticasAluno(Aluno aluno) {
        Estatisticas estatisticas = new Estatisticas();

        if (aluno.getHistoricoPartidas() == null) {
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

    private void gerarPdfDaTurma() {
        File pastaRelatorios = criarPastaRelatorios();
        File arquivo = new File(pastaRelatorios, "Relatorio_Turma.pdf");

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                desenharRelatorioTurma(conteudo);
            }

            documento.save(arquivo);
            mostrarPdfGerado(arquivo);

        } catch (IOException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao gerar PDF da turma: " + erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarPdfDoAluno(Aluno aluno) {
        File pastaRelatorios = criarPastaRelatorios();
        String nomeArquivo = "Relatorio_" + limparNomeArquivo(aluno.getNome()) + ".pdf";
        File arquivo = new File(pastaRelatorios, nomeArquivo);

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                desenharRelatorioAluno(conteudo, aluno);
            }

            documento.save(arquivo);
            mostrarPdfGerado(arquivo);

        } catch (IOException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao gerar PDF do aluno: " + erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File criarPastaRelatorios() {
        File pasta = new File("relatorios");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        return pasta;
    }

    private void mostrarPdfGerado(File arquivo) {
        JOptionPane.showMessageDialog(
                this,
                "PDF gerado com sucesso em:\n" + arquivo.getAbsolutePath(),
                "PDF gerado",
                JOptionPane.INFORMATION_MESSAGE);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo);
            }
        } catch (IOException erro) {
            System.out.println("PDF gerado, mas não foi possível abrir automaticamente.");
        }
    }

    private void desenharRelatorioTurma(PDPageContentStream conteudo) throws IOException {
        Estatisticas estatisticas = calcularEstatisticasTurma();

        desenharCabecalhoPdf(conteudo, "LabQuest - Relatorio de desempenho da turma");

        escreverTexto(conteudo, "Resumo geral da turma", FONTE_NEGRITO, 18, 50, 730, AZUL_ESCURO);

        escreverTexto(conteudo, "Total de alunos: " + listaAlunos.size(), FONTE_NORMAL, 12, 50, 705, Color.BLACK);
        escreverTexto(conteudo, "Total de partidas: " + estatisticas.totalPartidas, FONTE_NORMAL, 12, 50, 687,
                Color.BLACK);
        escreverTexto(conteudo, "Total de respostas: " + estatisticas.totalRespostas, FONTE_NORMAL, 12, 50, 669,
                Color.BLACK);
        escreverTexto(
                conteudo, "Acertos: " + estatisticas.totalAcertos + " ("
                        + formatarPercentual(estatisticas.percentualAcertos) + ")",
                FONTE_NORMAL, 12, 50, 651, Color.BLACK);
        escreverTexto(conteudo,
                "Erros: " + estatisticas.totalErros + " (" + formatarPercentual(estatisticas.percentualErros) + ")",
                FONTE_NORMAL, 12, 50, 633, Color.BLACK);

        desenharBarraDesempenhoPdf(
                conteudo,
                50,
                585,
                480,
                28,
                estatisticas.percentualAcertos,
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

        for (Aluno aluno : listaAlunos) {
            if (y < 70) {
                break;
            }

            Estatisticas e = calcularEstatisticasAluno(aluno);

            desenharLinhaTabela(conteudo, y, false);
            escreverTexto(conteudo, limitarTexto(aluno.getNome(), 28), FONTE_NORMAL, 9, 55, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(e.totalAcertos), FONTE_NORMAL, 9, 330, y + 8, Color.BLACK);
            escreverTexto(conteudo, String.valueOf(e.totalErros), FONTE_NORMAL, 9, 410, y + 8, Color.BLACK);
            escreverTexto(conteudo, formatarPercentual(e.percentualAcertos), FONTE_NORMAL, 9, 480, y + 8, Color.BLACK);

            y -= 24;
        }
    }

    private void desenharRelatorioAluno(PDPageContentStream conteudo, Aluno aluno) throws IOException {
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

    private class ItemAluno extends JPanel {

        private boolean aberto = false;
        private JPanel painelDetalhes;
        private JLabel seta;

        public ItemAluno(Aluno aluno) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(900, 42));
            setMaximumSize(new Dimension(900, 42));

            PainelArredondado linhaAluno = new PainelArredondado(18);
            linhaAluno.setBackground(CINZA_CLARO);
            linhaAluno.setPreferredSize(new Dimension(900, 38));
            linhaAluno.setLayout(new BorderLayout());
            linhaAluno.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel nomeAluno = new JLabel(aluno.getNome());
            nomeAluno.setFont(new Font("Verdana", Font.BOLD, 15));
            nomeAluno.setForeground(AZUL_ESCURO);
            nomeAluno.setBorder(new EmptyBorder(0, 20, 0, 0));
            linhaAluno.add(nomeAluno, BorderLayout.WEST);

            seta = new JLabel("<  ");
            seta.setFont(new Font("Verdana", Font.BOLD, 16));
            seta.setForeground(AZUL_ESCURO);
            linhaAluno.add(seta, BorderLayout.EAST);

            add(linhaAluno, BorderLayout.NORTH);

            painelDetalhes = criarPainelDetalhesAluno(aluno);
            painelDetalhes.setVisible(false);
            add(painelDetalhes, BorderLayout.CENTER);

            linhaAluno.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evento) {
                    alternarDetalhes();
                }
            });
        }

        private JPanel criarPainelDetalhesAluno(Aluno aluno) {
            JPanel painel = new JPanel(null);
            painel.setOpaque(false);
            painel.setPreferredSize(new Dimension(900, 310));

            Estatisticas estatisticas = calcularEstatisticasAluno(aluno);

            PainelArredondado painelGrafico = new PainelArredondado(20);
            painelGrafico.setBounds(15, 8, 460, 285);
            painelGrafico.setBackground(AZUL_ESCURO);
            painelGrafico.setLayout(null);
            painel.add(painelGrafico);

            JLabel titulo = new JLabel("Desempenho do mês", SwingConstants.CENTER);
            titulo.setBounds(0, 10, 460, 35);
            titulo.setFont(new Font("Verdana", Font.BOLD, 20));
            titulo.setForeground(Color.WHITE);
            painelGrafico.add(titulo);

            GraficoPizza grafico = new GraficoPizza(
                    estatisticas.percentualAcertos,
                    estatisticas.percentualErros,
                    false);
            grafico.setBounds(20, 45, 420, 205);
            painelGrafico.add(grafico);

            JLabel resumo = new JLabel(
                    estatisticas.totalAcertos + " acertos | "
                            + estatisticas.totalErros + " erros | "
                            + estatisticas.pontuacaoTotal + " pontos",
                    SwingConstants.CENTER);
            resumo.setBounds(0, 250, 460, 25);
            resumo.setFont(new Font("Verdana", Font.BOLD, 13));
            resumo.setForeground(Color.WHITE);
            painelGrafico.add(resumo);

            JButton botaoPdfAluno = new JButton("Gerar PDF do aluno");
            botaoPdfAluno.setBounds(560, 120, 250, 55);
            botaoPdfAluno.setFont(new Font("Verdana", Font.BOLD, 18));
            botaoPdfAluno.setBackground(AZUL_ESCURO);
            botaoPdfAluno.setForeground(Color.WHITE);
            botaoPdfAluno.setFocusPainted(false);
            botaoPdfAluno.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoPdfAluno.addActionListener(evento -> gerarPdfDoAluno(aluno));
            painel.add(botaoPdfAluno);

            return painel;
        }

        private void alternarDetalhes() {
            aberto = !aberto;
            painelDetalhes.setVisible(aberto);
            seta.setText(aberto ? "v  " : "<  ");

            if (aberto) {
                setPreferredSize(new Dimension(900, 355));
                setMaximumSize(new Dimension(900, 355));
            } else {
                setPreferredSize(new Dimension(900, 42));
                setMaximumSize(new Dimension(900, 42));
            }

            revalidate();
            repaint();
        }
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

            int margem = grande ? 110 : 70;
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
            int raio = tamanho / 2 + (grande ? 42 : 28);

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

    private static class BotaoMenu extends JButton {

        public BotaoMenu(String texto) {
            super(texto);
            setFont(new Font("Verdana", Font.BOLD, 24));
            setForeground(Color.WHITE);
            setBackground(new Color(34, 62, 107));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D desenho = (Graphics2D) grafico.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(getBackground());
            desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            super.paintComponent(grafico);
            desenho.dispose();
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
            TelaDesempenho tela = new TelaDesempenho();
            tela.setVisible(true);
        });
    }
}