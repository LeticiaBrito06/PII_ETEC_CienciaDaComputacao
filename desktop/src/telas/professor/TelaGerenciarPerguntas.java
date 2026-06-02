package telas.professor;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import database.DAO.QuestaoDAO;
import model.Alternativa;
import model.Questao;
import model.tipos.NivelDificuldade;
import model.tipos.TipoQuestao;
import serviços.SessaoUsuario;

public class TelaGerenciarPerguntas extends JFrame {

    private final List<PerguntaCadastro> bancoPerguntas = new ArrayList<>();
    private final CardLayout layoutConteudo = new CardLayout();
    private JPanel painelConteudo;

    private String telaAtual = "VISUALIZAR";
    private int indicePerguntaSelecionada = 0;

    private String caminhoImagemAdicionar = "";
    private String caminhoImagemEditar = "";

    private JTextField campoEnunciadoAdicionar;
    private JTextField[] camposAlternativasAdicionar;
    private JRadioButton[] opcoesCorretasAdicionar;
    private JRadioButton radioFacilAdicionar;
    private JRadioButton radioMedioAdicionar;
    private JRadioButton radioDificilAdicionar;
    private TipoQuestao tipoGerenciado;

    private JTextField campoEnunciadoAssociacaoAdicionar;
    private JTextField[] camposTextosAssociacaoAdicionar;
    private String[] caminhosImagensAssociacaoAdicionar;
    private JRadioButton radioFacilAssociacaoAdicionar;
    private JRadioButton radioMedioAssociacaoAdicionar;
    private JRadioButton radioDificilAssociacaoAdicionar;

    public TelaGerenciarPerguntas() {
    this(TipoQuestao.MULTIPLA_ESCOLHA);
}

    public TelaGerenciarPerguntas(TipoQuestao tipoGerenciado) {
        this.tipoGerenciado = tipoGerenciado;
        carregarPerguntasDoBanco();
        configurarJanela();
        montarTela();
        mostrarTela("VISUALIZAR");
    }

    private void configurarJanela() {
    if (tipoGerenciado == TipoQuestao.ASSOCIACAO) {
        setTitle("LabQuest - Gerenciar Perguntas de Associação");
    } else {
        setTitle("LabQuest - Gerenciar Perguntas de Alternativa");
    }

    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
}

    private void montarTela() {
        PainelFundoImagem painelBase = new PainelFundoImagem();
        painelBase.setLayout(new GridBagLayout());
        setContentPane(painelBase);

        JPanel painelPrincipal = new JPanel(null);
        painelPrincipal.setPreferredSize(new Dimension(980, 720));
        painelPrincipal.setOpaque(false);
        painelBase.add(painelPrincipal);

        JButton botaoPerfil = criarBotaoIconeReal("imagens/Perfil.png");
        botaoPerfil.setBounds(18, 18, 45, 45);
        botaoPerfil.addActionListener(e -> abrirPerfil());
        painelPrincipal.add(botaoPerfil);

        JButton botaoAdicionar = criarBotaoTopo("Adicionar<br>pergunta");
        botaoAdicionar.setBounds(248, 18, 148, 60);
        botaoAdicionar.addActionListener(evento -> mostrarTela("ADICIONAR"));
        painelPrincipal.add(botaoAdicionar);

        JButton botaoEditar = criarBotaoTopo("Editar<br>pergunta");
        botaoEditar.setBounds(410, 18, 148, 60);
        botaoEditar.addActionListener(evento -> mostrarTela("EDITAR"));
        painelPrincipal.add(botaoEditar);

        JButton botaoRemover = criarBotaoTopo("Remover<br>pergunta");
        botaoRemover.setBounds(572, 18, 160, 60);
        botaoRemover.addActionListener(evento -> mostrarTela("REMOVER"));
        painelPrincipal.add(botaoRemover);

        JButton botaoSair = criarBotaoIconeReal("imagens/Sair.png");
        botaoSair.setBounds(888, 18, 45, 45);
        botaoSair.addActionListener(e -> fecharTela());
        painelPrincipal.add(botaoSair);

        painelConteudo = new JPanel(layoutConteudo);
        painelConteudo.setOpaque(false);
        painelConteudo.setBounds(18, 98, 940, 590);
        painelPrincipal.add(painelConteudo);
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

    private JButton criarBotaoTopo(String texto) {
        BotaoArredondado botao = new BotaoArredondado("<html><center>" + texto + "</center></html>");
        botao.setFont(new Font("Verdana", Font.BOLD, 17));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(35, 74, 131));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void mostrarTela(String nomeTela) {
        this.telaAtual = nomeTela;
        reconstruirPaineis();
        layoutConteudo.show(painelConteudo, nomeTela);
    }

    private void reconstruirPaineis() {
        painelConteudo.removeAll();
        painelConteudo.add(criarPainelVisualizacao(), "VISUALIZAR");
        painelConteudo.add(criarPainelAdicionar(), "ADICIONAR");
        painelConteudo.add(criarPainelEditar(), "EDITAR");
        painelConteudo.add(criarPainelRemover(), "REMOVER");
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel criarPainelVisualizacao() {
        JPanel painelBase = new JPanel(null);
        painelBase.setOpaque(false);

        PainelArredondado painelListaExterna = new PainelArredondado(new Color(245, 245, 250), new Color(42, 82, 145),
                1);
        painelListaExterna.setLayout(null);
        painelListaExterna.setBounds(12, 8, 900, 562);
        painelBase.add(painelListaExterna);

        JPanel painelLinhas = new JPanel(null);
        painelLinhas.setOpaque(false);
        painelLinhas.setPreferredSize(new Dimension(860, Math.max(500, bancoPerguntas.size() * 42 + 16)));

        int y = 8;
        for (int i = 0; i < bancoPerguntas.size(); i++) {
            boolean selecionada = i == indicePerguntaSelecionada;
            JPanel linha = criarLinhaVisualizacao(bancoPerguntas.get(i), i, selecionada);
            linha.setBounds(12, y, 848, 34);
            painelLinhas.add(linha);
            y += 41;
        }

        JScrollPane barraRolagem = new JScrollPane(painelLinhas);
        barraRolagem.setBounds(10, 10, 878, 538);
        barraRolagem.setBorder(null);
        barraRolagem.setOpaque(false);
        barraRolagem.getViewport().setOpaque(false);
        barraRolagem.getVerticalScrollBar().setUnitIncrement(16);
        painelListaExterna.add(barraRolagem);

        return painelBase;
    }

    private JPanel criarLinhaVisualizacao(PerguntaCadastro pergunta, int indice, boolean selecionada) {
        JPanel linha = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics grafico) {
                super.paintComponent(grafico);
                Graphics2D desenho = (Graphics2D) grafico.create();
                desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                desenho.setColor(new Color(213, 213, 213));
                desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                if (selecionada) {
                    desenho.setColor(new Color(45, 125, 220));
                    desenho.setStroke(new BasicStroke(2));
                    desenho.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                }

                desenho.setColor(new Color(78, 100, 138));
                desenho.drawLine(360, 5, 360, getHeight() - 5);

                desenho.dispose();
            }
        };

        linha.setOpaque(false);
        linha.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textoPergunta = new JLabel("“" + pergunta.getEnunciado() + "”");
        textoPergunta.setBounds(18, 3, 338, 28);
        textoPergunta.setFont(new Font("Verdana", Font.BOLD, 14));
        textoPergunta.setForeground(new Color(35, 74, 131));
        linha.add(textoPergunta);

        JLabel textoResposta = new JLabel(pergunta.getResumoRespostaCorreta());
        textoResposta.setBounds(380, 3, 450, 28);
        textoResposta.setFont(new Font("Verdana", Font.BOLD, 14));
        textoResposta.setForeground(new Color(35, 74, 131));
        linha.add(textoResposta);

        linha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                indicePerguntaSelecionada = indice;
                mostrarTela("VISUALIZAR");
            }
        });

        return linha;
    }

    private JPanel criarPainelAdicionar() {
    if (tipoGerenciado == TipoQuestao.ASSOCIACAO) {
        return criarPainelAdicionarAssociacao();
    }

    JPanel painelBase = new JPanel(null);
    painelBase.setOpaque(false);

    PainelArredondado painelFormulario = new PainelArredondado(new Color(212, 212, 212), new Color(42, 82, 145), 1);
    painelFormulario.setLayout(null);
    painelFormulario.setBounds(4, 84, 904, 438);
    painelBase.add(painelFormulario);


        campoEnunciadoAdicionar = new JTextField();
        configurarCampoTexto(campoEnunciadoAdicionar);
        campoEnunciadoAdicionar.setBounds(28, 18, 840, 44);
        campoEnunciadoAdicionar.setText("");
        campoEnunciadoAdicionar.setToolTipText("Digite o enunciado");
        painelFormulario.add(campoEnunciadoAdicionar);

        JLabel textoEnunciado = new JLabel("Enunciado:");
        textoEnunciado.setBounds(38, 26, 120, 26);
        textoEnunciado.setFont(new Font("Verdana", Font.BOLD, 16));
        textoEnunciado.setForeground(new Color(170, 170, 185));
        painelFormulario.add(textoEnunciado);

        camposAlternativasAdicionar = new JTextField[4];
        opcoesCorretasAdicionar = new JRadioButton[4];
        ButtonGroup grupoCorreta = new ButtonGroup();

        String[] letras = { "a)", "b)", "c)", "d)" };
        int y = 92;

        for (int i = 0; i < 4; i++) {
            JRadioButton radio = new JRadioButton();
            radio.setBounds(36, y + 14, 22, 22);
            radio.setOpaque(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            grupoCorreta.add(radio);
            opcoesCorretasAdicionar[i] = radio;
            painelFormulario.add(radio);

            JTextField campoAlternativa = new JTextField();
            configurarCampoTexto(campoAlternativa);
            campoAlternativa.setBounds(66, y, 650, 46);
            campoAlternativa.setText("");
            camposAlternativasAdicionar[i] = campoAlternativa;
            painelFormulario.add(campoAlternativa);

            JLabel textoLetra = new JLabel(letras[i]);
            textoLetra.setBounds(76, y + 8, 35, 28);
            textoLetra.setFont(new Font("Verdana", Font.BOLD, 16));
            textoLetra.setForeground(new Color(170, 170, 185));
            painelFormulario.add(textoLetra);

            y += 59;
        }

        opcoesCorretasAdicionar[0].setSelected(true);

        PainelArredondado painelImagem = new PainelArredondado(new Color(232, 232, 236), new Color(232, 232, 236), 0);
        painelImagem.setLayout(null);
        painelImagem.setBounds(730, 125, 142, 140);
        painelFormulario.add(painelImagem);

        JButton botaoImagem = new JButton("<html><center>⬆<br>Selecionar<br>imagem</center></html>");
        botaoImagem.setBounds(20, 18, 100, 80);
        botaoImagem.setFont(new Font("Verdana", Font.BOLD, 18));
        botaoImagem.setForeground(Color.BLACK);
        botaoImagem.setBorderPainted(false);
        botaoImagem.setFocusPainted(false);
        botaoImagem.setContentAreaFilled(false);
        botaoImagem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painelImagem.add(botaoImagem);

        JLabel textoFormato = new JLabel("*.png, *.jpeg", SwingConstants.CENTER);
        textoFormato.setBounds(10, 104, 122, 24);
        textoFormato.setFont(new Font("Verdana", Font.BOLD, 14));
        textoFormato.setForeground(new Color(96, 96, 120));
        painelImagem.add(textoFormato);

        JLabel textoArquivoEscolhido = new JLabel("Nenhum arquivo", SwingConstants.CENTER);
        textoArquivoEscolhido.setBounds(10, 88, 122, 18);
        textoArquivoEscolhido.setFont(new Font("Verdana", Font.PLAIN, 11));
        textoArquivoEscolhido.setForeground(new Color(100, 100, 100));
        painelImagem.add(textoArquivoEscolhido);

        botaoImagem.addActionListener(evento -> {
            JFileChooser seletorArquivo = new JFileChooser();
            int resposta = seletorArquivo.showOpenDialog(this);

            if (resposta == JFileChooser.APPROVE_OPTION) {
                File arquivo = seletorArquivo.getSelectedFile();
                caminhoImagemAdicionar = arquivo.getAbsolutePath();
                textoArquivoEscolhido.setText(arquivo.getName());
            }
        });

        PainelArredondado painelNivel = new PainelArredondado(new Color(235, 235, 239), new Color(235, 235, 239), 0);
        painelNivel.setLayout(null);
        painelNivel.setBounds(28, 328, 840, 46);
        painelFormulario.add(painelNivel);

        JLabel textoNivel = new JLabel("Nível de dificuldade:");
        textoNivel.setBounds(10, 8, 190, 28);
        textoNivel.setFont(new Font("Verdana", Font.BOLD, 15));
        textoNivel.setForeground(new Color(170, 170, 185));
        painelNivel.add(textoNivel);

        radioFacilAdicionar = criarRadioNivel("Fácil", 200, true);
        radioMedioAdicionar = criarRadioNivel("Médio", 300, false);
        radioDificilAdicionar = criarRadioNivel("Difícil", 400, false);

        ButtonGroup grupoNivel = new ButtonGroup();
        grupoNivel.add(radioFacilAdicionar);
        grupoNivel.add(radioMedioAdicionar);
        grupoNivel.add(radioDificilAdicionar);

        painelNivel.add(radioFacilAdicionar);
        painelNivel.add(radioMedioAdicionar);
        painelNivel.add(radioDificilAdicionar);

        JButton botaoCancelar = criarBotaoAcao("Cancelar", new Color(221, 188, 188), new Color(104, 44, 44));
        botaoCancelar.setBounds(228, 385, 206, 44);
        botaoCancelar.addActionListener(evento -> mostrarTela("VISUALIZAR"));
        painelFormulario.add(botaoCancelar);

        JButton botaoAdicionar = criarBotaoAcao("Adicionar", new Color(176, 215, 172), new Color(44, 103, 48));
        botaoAdicionar.setBounds(468, 385, 206, 44);
        botaoAdicionar.addActionListener(evento -> adicionarPergunta());
        painelFormulario.add(botaoAdicionar);

        return painelBase;
    }

    private JPanel criarPainelEditar() {
    JPanel painelBase = new JPanel(null);
    painelBase.setOpaque(false);

    if (bancoPerguntas.isEmpty()) {
        return painelBase;
    }

    PerguntaCadastro perguntaSelecionada = bancoPerguntas.get(indicePerguntaSelecionada);

    if (tipoGerenciado == TipoQuestao.ASSOCIACAO) {
        return criarPainelEditarAssociacao(perguntaSelecionada);
    }

    PainelArredondado painelFormulario = new PainelArredondado(new Color(212, 212, 212), new Color(42, 82, 145), 1);
    painelFormulario.setLayout(null);
    painelFormulario.setBounds(4, 84, 904, 380);
    painelBase.add(painelFormulario);

        JTextField campoEnunciadoEditar = new JTextField(perguntaSelecionada.getEnunciado());
        configurarCampoTexto(campoEnunciadoEditar);
        campoEnunciadoEditar.setBounds(30, 18, 842, 46);
        painelFormulario.add(campoEnunciadoEditar);

        JRadioButton[] radiosCorretos = new JRadioButton[4];
        JTextField[] camposAlternativas = new JTextField[4];
        ButtonGroup grupoCorreta = new ButtonGroup();

        int y = 78;
        String[] letras = { "a)", "b)", "c)", "d)" };

        for (int i = 0; i < 4; i++) {
            JRadioButton radio = new JRadioButton();
            radio.setBounds(38, y + 14, 22, 22);
            radio.setOpaque(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            radio.setSelected(i == perguntaSelecionada.getIndiceCorreta());
            grupoCorreta.add(radio);
            radiosCorretos[i] = radio;
            painelFormulario.add(radio);

            JTextField campoAlternativa = new JTextField(perguntaSelecionada.getAlternativas().get(i));
            configurarCampoTexto(campoAlternativa);
            campoAlternativa.setBounds(68, y, 650, 46);
            camposAlternativas[i] = campoAlternativa;
            painelFormulario.add(campoAlternativa);

            JLabel textoLetra = new JLabel(letras[i]);
            textoLetra.setBounds(102, y + 8, 40, 28);
            textoLetra.setFont(new Font("Verdana", Font.BOLD, 16));
            textoLetra.setForeground(new Color(120, 120, 145));
            painelFormulario.add(textoLetra);

            y += 58;
        }

        PainelArredondado painelImagem = new PainelArredondado(new Color(235, 235, 239), new Color(235, 235, 239), 0);
        painelImagem.setLayout(null);
        painelImagem.setBounds(730, 125, 142, 140);
        painelFormulario.add(painelImagem);

        JLabel imagemLabel = new JLabel("Imagem", SwingConstants.CENTER);
        imagemLabel.setBounds(10, 10, 122, 110);
        imagemLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        imagemLabel.setForeground(new Color(80, 100, 130));
        painelImagem.add(imagemLabel);

        JButton botaoTrocarImagem = new JButton("Trocar");
        botaoTrocarImagem.setBounds(30, 106, 82, 24);
        botaoTrocarImagem.setFont(new Font("Verdana", Font.BOLD, 12));
        botaoTrocarImagem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painelImagem.add(botaoTrocarImagem);

        if (perguntaSelecionada.getCaminhoImagem() != null && !perguntaSelecionada.getCaminhoImagem().isBlank()) {
            configurarPreviaImagem(imagemLabel, perguntaSelecionada.getCaminhoImagem());
        }

        botaoTrocarImagem.addActionListener(evento -> {
            JFileChooser seletorArquivo = new JFileChooser();
            int resposta = seletorArquivo.showOpenDialog(this);

            if (resposta == JFileChooser.APPROVE_OPTION) {
                File arquivo = seletorArquivo.getSelectedFile();
                caminhoImagemEditar = arquivo.getAbsolutePath();
                configurarPreviaImagem(imagemLabel, caminhoImagemEditar);
            }
        });

        JButton botaoCancelar = criarBotaoAcao("Cancelar", new Color(221, 188, 188), new Color(104, 44, 44));
        botaoCancelar.setBounds(232, 316, 206, 50);
        botaoCancelar.addActionListener(evento -> mostrarTela("VISUALIZAR"));
        painelFormulario.add(botaoCancelar);

        JButton botaoSalvar = criarBotaoAcao("Salvar alterações", new Color(176, 215, 172), new Color(44, 103, 48));
        botaoSalvar.setBounds(472, 316, 206, 50);
        botaoSalvar.addActionListener(evento -> editarPergunta(
                perguntaSelecionada,
                campoEnunciadoEditar,
                camposAlternativas,
                radiosCorretos));
        painelFormulario.add(botaoSalvar);

        return painelBase;
    }

    private JPanel criarPainelRemover() {
        JPanel painelBase = new JPanel(null);
        painelBase.setOpaque(false);

        PainelArredondado painelListaExterna = new PainelArredondado(new Color(245, 245, 250), new Color(42, 82, 145),
                1);
        painelListaExterna.setLayout(null);
        painelListaExterna.setBounds(0, 0, 902, 320);
        painelBase.add(painelListaExterna);

        ButtonGroup grupoSelecao = new ButtonGroup();
        JPanel painelLinhas = new JPanel(null);
        painelLinhas.setOpaque(false);
        painelLinhas.setPreferredSize(new Dimension(860, Math.max(250, bancoPerguntas.size() * 42 + 16)));

        int y = 8;

        for (int i = 0; i < bancoPerguntas.size(); i++) {
            PerguntaCadastro pergunta = bancoPerguntas.get(i);

            JPanel linha = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics grafico) {
                    super.paintComponent(grafico);
                    Graphics2D desenho = (Graphics2D) grafico.create();
                    desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    desenho.setColor(new Color(213, 213, 213));
                    desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                    desenho.setColor(new Color(78, 100, 138));
                    desenho.drawLine(346, 5, 346, getHeight() - 5);

                    desenho.dispose();
                }
            };

            linha.setOpaque(false);
            linha.setBounds(10, y, 842, 34);

            JRadioButton radio = new JRadioButton();
            radio.setBounds(8, 6, 24, 24);
            radio.setOpaque(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            radio.setSelected(i == indicePerguntaSelecionada);
            grupoSelecao.add(radio);
            linha.add(radio);

            int indice = i;
            radio.addActionListener(evento -> indicePerguntaSelecionada = indice);

            JLabel textoPergunta = new JLabel("“" + pergunta.getEnunciado() + "”");
            textoPergunta.setBounds(42, 3, 294, 28);
            textoPergunta.setFont(new Font("Verdana", Font.BOLD, 14));
            textoPergunta.setForeground(new Color(35, 74, 131));
            linha.add(textoPergunta);

            JLabel textoResposta = new JLabel(pergunta.getResumoRespostaCorreta());
            textoResposta.setBounds(360, 3, 460, 28);
            textoResposta.setFont(new Font("Verdana", Font.BOLD, 14));
            textoResposta.setForeground(new Color(35, 74, 131));
            linha.add(textoResposta);

            painelLinhas.add(linha);
            y += 41;
        }

        JScrollPane barraRolagem = new JScrollPane(painelLinhas);
        barraRolagem.setBounds(10, 8, 878, 232);
        barraRolagem.setBorder(null);
        barraRolagem.setOpaque(false);
        barraRolagem.getViewport().setOpaque(false);
        barraRolagem.getVerticalScrollBar().setUnitIncrement(16);
        painelListaExterna.add(barraRolagem);

        JButton botaoCancelar = criarBotaoAcao("Cancelar", new Color(221, 188, 188), new Color(104, 44, 44));
        botaoCancelar.setBounds(228, 255, 206, 50);
        botaoCancelar.addActionListener(evento -> mostrarTela("VISUALIZAR"));
        painelListaExterna.add(botaoCancelar);

        JButton botaoRemover = criarBotaoAcao("Remover", new Color(176, 215, 172), new Color(44, 103, 48));
        botaoRemover.setBounds(468, 255, 206, 50);
        botaoRemover.addActionListener(evento -> removerPerguntaSelecionada());
        painelListaExterna.add(botaoRemover);

        return painelBase;
    }

    private void configurarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Verdana", Font.BOLD, 15));
        campo.setForeground(new Color(120, 120, 145));
        campo.setBackground(new Color(235, 235, 239));
        campo.setBorder(new EmptyBorder(0, 12, 0, 12));
    }

    private JRadioButton criarRadioNivel(String texto, int x, boolean selecionado) {
        JRadioButton radio = new JRadioButton(texto);
        radio.setBounds(x, 8, 120, 28);
        radio.setOpaque(false);
        radio.setSelected(selecionado);
        radio.setFont(new Font("Verdana", Font.BOLD, 15));
        radio.setForeground(new Color(35, 74, 131));
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return radio;
    }

    private JButton criarBotaoAcao(String texto, Color corFundo, Color corTexto) {
        BotaoArredondado botao = new BotaoArredondado(texto);
        botao.setFont(new Font("Verdana", Font.BOLD, 16));
        botao.setForeground(corTexto);
        botao.setBackground(corFundo);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void mostrarMensagem(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private void fecharTela() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair da tela de gerenciamento de perguntas?",
                "Sair",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            dispose();
            new TelaMenuProfessor().setVisible(true);
        }
    }

    private void configurarPreviaImagem(JLabel imagemLabel, String caminhoImagem) {
        try {
            Image imagem = ImageIO.read(new File(caminhoImagem));
            Image imagemRedimensionada = imagem.getScaledInstance(110, 100, Image.SCALE_SMOOTH);
            imagemLabel.setText("");
            imagemLabel.setIcon(new ImageIcon(imagemRedimensionada));
        } catch (IOException erro) {
            imagemLabel.setText("Imagem");
            imagemLabel.setIcon(null);
        }
    }

    private void carregarPerguntasDoBanco() {
        bancoPerguntas.clear();

        try {
            QuestaoDAO questaoDAO = new QuestaoDAO();
            int id = SessaoUsuario.getIdUsuario();
            System.out.println("ID do professor: " + id);
            List<Questao> questoes = questaoDAO.listarPorProfessorETipo(id, tipoGerenciado);

            for (Questao questao : questoes) {
                bancoPerguntas.add(converterQuestaoParaCadastro(questao));
            }

            if (bancoPerguntas.isEmpty()) {
                indicePerguntaSelecionada = 0;
            } else if (indicePerguntaSelecionada >= bancoPerguntas.size()) {
                indicePerguntaSelecionada = bancoPerguntas.size() - 1;
            }
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao carregar perguntas do banco: " + erro.getMessage());
        }
    }

    private PerguntaCadastro converterQuestaoParaCadastro(Questao questao) {
    List<String> alternativas = new ArrayList<>();
    List<String> imagensAssociacao = new ArrayList<>();
    int indiceCorreta = 0;

    for (Alternativa alternativa : questao.getAlternativas()) {
        if (alternativas.size() >= 4) {
            break;
        }

        alternativas.add(alternativa.getTexto());

        String imagem = alternativa.getImagem() == null ? "" : alternativa.getImagem();
        imagensAssociacao.add(imagem);

        if (alternativa.isECorreta()) {
            indiceCorreta = alternativas.size() - 1;
        }
    }

    while (alternativas.size() < 4) {
        alternativas.add("");
    }

    while (imagensAssociacao.size() < 4) {
        imagensAssociacao.add("");
    }

    String caminhoImagem = questao.getImagemEnunciado() == null ? "" : questao.getImagemEnunciado();
    String categoria = questao.getCategoria() == null ? "Materiais de laboratório" : questao.getCategoria();

    return new PerguntaCadastro(
            questao.getId(),
            questao.getEnunciado(),
            alternativas,
            imagensAssociacao,
            indiceCorreta,
            questao.getNivelDificuldade().name(),
            categoria,
            caminhoImagem);
}

    private void adicionarPergunta() {
        String enunciado = campoEnunciadoAdicionar.getText().trim();

        List<String> alternativas = obterTextosAlternativas(camposAlternativasAdicionar);

        if (!validarDadosPergunta(enunciado, alternativas)) {
            return;
        }

        int indiceCorreta = obterIndiceAlternativaCorreta(opcoesCorretasAdicionar);
        NivelDificuldade nivel = obterNivelSelecionado();

        try {
            salvarPerguntaNoBanco(enunciado, alternativas, indiceCorreta, nivel);

            caminhoImagemAdicionar = "";
            indicePerguntaSelecionada = 0;

            carregarPerguntasDoBanco();
            mostrarTela("VISUALIZAR");
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao adicionar pergunta: " + erro.getMessage());
        }
    }

    private void salvarPerguntaNoBanco(String enunciado, List<String> alternativas, int indiceCorreta,
            NivelDificuldade nivel) throws SQLException {
        Questao questao = new Questao(
                0,
                enunciado,
                TipoQuestao.MULTIPLA_ESCOLHA,
                nivel,
                "Materiais de laboratório");

        if (!caminhoImagemAdicionar.isBlank()) {
            questao.setImagemEnunciado(caminhoImagemAdicionar);
        }

        for (int i = 0; i < alternativas.size(); i++) {
            questao.adicionarAlternativa(new Alternativa(
                    0,
                    alternativas.get(i),
                    null,
                    i == indiceCorreta,
                    i != indiceCorreta));
        }

        QuestaoDAO questaoDAO = new QuestaoDAO();
        questaoDAO.inserir(questao, SessaoUsuario.getIdUsuario());
    }

    private void salvarAssociacaoNoBanco(String enunciado, List<String> textos, String[] imagens,
        NivelDificuldade nivel) throws SQLException {
    Questao questao = new Questao(
            0,
            enunciado,
            TipoQuestao.ASSOCIACAO,
            nivel,
            "Materiais de laboratório");

    for (int i = 0; i < textos.size(); i++) {
        questao.adicionarAlternativa(new Alternativa(
                0,
                textos.get(i),
                imagens[i],
                true,
                false));
    }

    QuestaoDAO questaoDAO = new QuestaoDAO();
    int idGerado = questaoDAO.inserir(questao, SessaoUsuario.getIdUsuario());

    System.out.println("Pergunta de associação salva no banco. ID: " + idGerado);
}

    private List<String> obterTextosAlternativas(JTextField[] camposAlternativas) {
        List<String> alternativas = new ArrayList<>();

        for (JTextField campo : camposAlternativas) {
            alternativas.add(campo.getText().trim());
        }

        return alternativas;
    }

    private boolean validarDadosPergunta(String enunciado, List<String> alternativas) {
        if (enunciado.isEmpty()) {
            mostrarMensagem("Digite o enunciado da pergunta.");
            return false;
        }

        for (String alternativa : alternativas) {
            if (alternativa.isEmpty()) {
                mostrarMensagem("Preencha todas as alternativas.");
                return false;
            }
        }

        return true;
    }

    private int obterIndiceAlternativaCorreta(JRadioButton[] opcoesCorretas) {
        for (int i = 0; i < opcoesCorretas.length; i++) {
            if (opcoesCorretas[i].isSelected()) {
                return i;
            }
        }

        return 0;
    }

    private NivelDificuldade obterNivelSelecionado() {
        if (radioMedioAdicionar.isSelected()) {
            return NivelDificuldade.MEDIO;
        }

        if (radioDificilAdicionar.isSelected()) {
            return NivelDificuldade.DIFICIL;
        }

        return NivelDificuldade.FACIL;
    }

    private void editarPergunta(PerguntaCadastro perguntaSelecionada, JTextField campoEnunciado,
            JTextField[] camposAlternativas, JRadioButton[] radiosCorretos) {
        String enunciado = campoEnunciado.getText().trim();
        List<String> novasAlternativas = obterTextosAlternativas(camposAlternativas);

        if (!validarDadosPergunta(enunciado, novasAlternativas)) {
            return;
        }

        int novaCorreta = obterIndiceAlternativaCorreta(radiosCorretos);
        int idPerguntaEditada = perguntaSelecionada.getId();

        try {
            salvarEdicaoPerguntaNoBanco(perguntaSelecionada, enunciado, novasAlternativas, novaCorreta);
            caminhoImagemEditar = "";

            carregarPerguntasDoBanco();
            selecionarPerguntaPorId(idPerguntaEditada);
            mostrarTela("VISUALIZAR");
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao editar pergunta: " + erro.getMessage());
        }
    }

    private void salvarEdicaoPerguntaNoBanco(PerguntaCadastro perguntaSelecionada, String enunciado,
            List<String> alternativas, int indiceCorreta) throws SQLException {
        Questao questao = new Questao(
                perguntaSelecionada.getId(),
                enunciado,
                TipoQuestao.MULTIPLA_ESCOLHA,
                converterNivel(perguntaSelecionada.getNivel()),
                perguntaSelecionada.getCategoria());

        String caminhoImagem = perguntaSelecionada.getCaminhoImagem();
        if (!caminhoImagemEditar.isBlank()) {
            caminhoImagem = caminhoImagemEditar;
        }
        questao.setImagemEnunciado(caminhoImagem);

        for (int i = 0; i < alternativas.size(); i++) {
            questao.adicionarAlternativa(new Alternativa(
                    0,
                    alternativas.get(i),
                    i == indiceCorreta));
        }

        QuestaoDAO questaoDAO = new QuestaoDAO();
        boolean atualizou = questaoDAO.atualizarComAlternativas(questao);

        if (!atualizou) {
            throw new SQLException("Nenhuma pergunta foi atualizada.");
        }
    }

    private NivelDificuldade converterNivel(String nivel) {
        if (nivel == null || nivel.isBlank()) {
            return NivelDificuldade.FACIL;
        }

        return switch (nivel.toUpperCase()) {
            case "MEDIO", "MÉDIO" -> NivelDificuldade.MEDIO;
            case "DIFICIL", "DIFÍCIL" -> NivelDificuldade.DIFICIL;
            default -> NivelDificuldade.FACIL;
        };
    }

    private void selecionarPerguntaPorId(int id) {
        for (int i = 0; i < bancoPerguntas.size(); i++) {
            if (bancoPerguntas.get(i).getId() == id) {
                indicePerguntaSelecionada = i;
                return;
            }
        }

        indicePerguntaSelecionada = 0;
    }

    private void removerPerguntaSelecionada() {
        if (bancoPerguntas.isEmpty()) {
            mostrarMensagem("Não há perguntas para remover.");
            return;
        }

        PerguntaCadastro perguntaSelecionada = bancoPerguntas.get(indicePerguntaSelecionada);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente remover a pergunta selecionada?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            QuestaoDAO questaoDAO = new QuestaoDAO();
            boolean removeu = questaoDAO.desativar(perguntaSelecionada.getId());

            if (!removeu) {
                mostrarMensagem("Nenhuma pergunta foi removida.");
                return;
            }

            carregarPerguntasDoBanco();

            if (bancoPerguntas.isEmpty()) {
                indicePerguntaSelecionada = 0;
            } else if (indicePerguntaSelecionada >= bancoPerguntas.size()) {
                indicePerguntaSelecionada = bancoPerguntas.size() - 1;
            }

            mostrarTela("VISUALIZAR");
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao remover pergunta: " + erro.getMessage());
        }
    }


    private JPanel criarPainelAdicionarAssociacao() {
        JPanel painelBase = new JPanel(null);
        painelBase.setOpaque(false);

        PainelArredondado painelFormulario = new PainelArredondado(new Color(212, 212, 212), new Color(42, 82, 145), 1);
        painelFormulario.setLayout(null);
        painelFormulario.setBounds(4, 20, 904, 500);
        painelBase.add(painelFormulario);

        campoEnunciadoAssociacaoAdicionar = new JTextField();
        configurarCampoTexto(campoEnunciadoAssociacaoAdicionar);
        campoEnunciadoAssociacaoAdicionar.setBounds(38, 18, 820, 44);
        campoEnunciadoAssociacaoAdicionar.setText("Conecte o material ao sistema experimental do qual ele faz parte.");
        painelFormulario.add(campoEnunciadoAssociacaoAdicionar);

        camposTextosAssociacaoAdicionar = new JTextField[4];
        caminhosImagensAssociacaoAdicionar = new String[4];

        int y = 88;

        for (int i = 0; i < 4; i++) {
            int indice = i;

            JButton botaoImagem = new JButton("<html><center>⬆<br>*.png, *.jpeg</center></html>");
            botaoImagem.setBounds(55, y, 90, 58);
            botaoImagem.setFont(new Font("Verdana", Font.BOLD, 12));
            botaoImagem.setFocusPainted(false);
            botaoImagem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            painelFormulario.add(botaoImagem);

            JLabel linha = new JLabel("────────────");
            linha.setBounds(150, y + 18, 350, 22);
            linha.setForeground(Color.BLACK);
            painelFormulario.add(linha);

            JTextField campoTexto = new JTextField();
            configurarCampoTexto(campoTexto);
            campoTexto.setBounds(520, y, 170, 58);
            campoTexto.setText("");
            camposTextosAssociacaoAdicionar[i] = campoTexto;
            painelFormulario.add(campoTexto);

            JLabel nomeArquivo = new JLabel("Nenhuma imagem");
            nomeArquivo.setBounds(55, y + 58, 180, 18);
            nomeArquivo.setFont(new Font("Verdana", Font.PLAIN, 10));
            nomeArquivo.setForeground(new Color(80, 80, 80));
            painelFormulario.add(nomeArquivo);

            botaoImagem.addActionListener(evento -> {
                JFileChooser seletor = new JFileChooser();
                int resposta = seletor.showOpenDialog(this);

                if (resposta == JFileChooser.APPROVE_OPTION) {
                    File arquivo = seletor.getSelectedFile();
                    caminhosImagensAssociacaoAdicionar[indice] = arquivo.getAbsolutePath();
                    nomeArquivo.setText(arquivo.getName());
                }
            });

            y += 74;
        }

        PainelArredondado painelNivel = new PainelArredondado(new Color(235, 235, 239), new Color(235, 235, 239), 0);
        painelNivel.setLayout(null);
        painelNivel.setBounds(38, 385, 820, 44);
        painelFormulario.add(painelNivel);

        JLabel textoNivel = new JLabel("Nível de dificuldade:");
        textoNivel.setBounds(10, 8, 190, 28);
        textoNivel.setFont(new Font("Verdana", Font.BOLD, 15));
        textoNivel.setForeground(new Color(170, 170, 185));
        painelNivel.add(textoNivel);

        radioFacilAssociacaoAdicionar = criarRadioNivel("Fácil", 200, true);
        radioMedioAssociacaoAdicionar = criarRadioNivel("Médio", 300, false);
        radioDificilAssociacaoAdicionar = criarRadioNivel("Difícil", 400, false);

        ButtonGroup grupoNivel = new ButtonGroup();
        grupoNivel.add(radioFacilAssociacaoAdicionar);
        grupoNivel.add(radioMedioAssociacaoAdicionar);
        grupoNivel.add(radioDificilAssociacaoAdicionar);

        painelNivel.add(radioFacilAssociacaoAdicionar);
        painelNivel.add(radioMedioAssociacaoAdicionar);
        painelNivel.add(radioDificilAssociacaoAdicionar);

        JButton botaoCancelar = criarBotaoAcao("Cancelar", new Color(221, 188, 188), new Color(104, 44, 44));
        botaoCancelar.setBounds(230, 445, 206, 44);
        botaoCancelar.addActionListener(evento -> mostrarTela("VISUALIZAR"));
        painelFormulario.add(botaoCancelar);

        JButton botaoAdicionar = criarBotaoAcao("Adicionar", new Color(176, 215, 172), new Color(44, 103, 48));
        botaoAdicionar.setBounds(470, 445, 206, 44);
        botaoAdicionar.addActionListener(evento -> adicionarPerguntaAssociacao());
        painelFormulario.add(botaoAdicionar);

        return painelBase;
    }

    private void adicionarPerguntaAssociacao() {
        String enunciado = campoEnunciadoAssociacaoAdicionar.getText().trim();

        if (enunciado.isEmpty()) {
            mostrarMensagem("Digite o enunciado da pergunta.");
            return;
        }

        List<String> textos = obterTextosAlternativas(camposTextosAssociacaoAdicionar);

        if (!validarDadosAssociacao(textos, caminhosImagensAssociacaoAdicionar)) {
            return;
        }

        NivelDificuldade nivel = obterNivelAssociacaoSelecionado();

        try {
            salvarAssociacaoNoBanco(enunciado, textos, caminhosImagensAssociacaoAdicionar, nivel);

            carregarPerguntasDoBanco();
            indicePerguntaSelecionada = 0;
            mostrarTela("VISUALIZAR");
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao adicionar pergunta de associação: " + erro.getMessage());
        }
    }

    private boolean validarDadosAssociacao(List<String> textos, String[] imagens) {
        for (int i = 0; i < 4; i++) {
            if (imagens[i] == null || imagens[i].isBlank()) {
                mostrarMensagem("Selecione a imagem do item " + (i + 1) + ".");
                return false;
            }

            if (textos.get(i).isBlank()) {
                mostrarMensagem("Digite o texto do item " + (i + 1) + ".");
                return false;
            }
        }

        return true;
    }

    private NivelDificuldade obterNivelAssociacaoSelecionado() {
        if (radioMedioAssociacaoAdicionar.isSelected()) {
            return NivelDificuldade.MEDIO;
        }

        if (radioDificilAssociacaoAdicionar.isSelected()) {
            return NivelDificuldade.DIFICIL;
        }

        return NivelDificuldade.FACIL;
    }

    private JPanel criarPainelEditarAssociacao(PerguntaCadastro perguntaSelecionada) {
        JPanel painelBase = new JPanel(null);
        painelBase.setOpaque(false);

        PainelArredondado painelFormulario = new PainelArredondado(new Color(212, 212, 212), new Color(42, 82, 145), 1);
        painelFormulario.setLayout(null);
        painelFormulario.setBounds(4, 20, 904, 500);
        painelBase.add(painelFormulario);

        JTextField campoEnunciadoEditar = new JTextField(perguntaSelecionada.getEnunciado());
        configurarCampoTexto(campoEnunciadoEditar);
        campoEnunciadoEditar.setBounds(38, 18, 820, 44);
        painelFormulario.add(campoEnunciadoEditar);

        JTextField[] camposTextosEditar = new JTextField[4];
        String[] caminhosImagensEditar = new String[4];

        int y = 88;

        for (int i = 0; i < 4; i++) {
            int indice = i;

            String imagemAtual = perguntaSelecionada.getImagensAssociacao().get(i);
            caminhosImagensEditar[i] = imagemAtual;

            JLabel previaImagem = new JLabel("Imagem", SwingConstants.CENTER);
            previaImagem.setBounds(55, y, 90, 58);
            previaImagem.setOpaque(true);
            previaImagem.setBackground(new Color(235, 235, 239));
            previaImagem.setFont(new Font("Verdana", Font.BOLD, 11));
            painelFormulario.add(previaImagem);

            if (imagemAtual != null && !imagemAtual.isBlank()) {
                configurarPreviaImagem(previaImagem, imagemAtual);
            }

            JButton botaoTrocarImagem = new JButton("Trocar");
            botaoTrocarImagem.setBounds(55, y + 60, 90, 20);
            botaoTrocarImagem.setFont(new Font("Verdana", Font.BOLD, 10));
            botaoTrocarImagem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            painelFormulario.add(botaoTrocarImagem);

            JLabel linha = new JLabel("────────────");
            linha.setBounds(150, y + 18, 350, 22);
            linha.setForeground(Color.BLACK);
            painelFormulario.add(linha);

            JTextField campoTexto = new JTextField(perguntaSelecionada.getAlternativas().get(i));
            configurarCampoTexto(campoTexto);
            campoTexto.setBounds(520, y, 170, 58);
            camposTextosEditar[i] = campoTexto;
            painelFormulario.add(campoTexto);

            botaoTrocarImagem.addActionListener(evento -> {
                JFileChooser seletor = new JFileChooser();
                int resposta = seletor.showOpenDialog(this);

                if (resposta == JFileChooser.APPROVE_OPTION) {
                    File arquivo = seletor.getSelectedFile();
                    caminhosImagensEditar[indice] = arquivo.getAbsolutePath();
                    configurarPreviaImagem(previaImagem, caminhosImagensEditar[indice]);
                }
            });

            y += 74;
        }

        PainelArredondado painelNivel = new PainelArredondado(new Color(235, 235, 239), new Color(235, 235, 239), 0);
        painelNivel.setLayout(null);
        painelNivel.setBounds(38, 385, 820, 44);
        painelFormulario.add(painelNivel);

        JLabel textoNivel = new JLabel("Nível de dificuldade:");
        textoNivel.setBounds(10, 8, 190, 28);
        textoNivel.setFont(new Font("Verdana", Font.BOLD, 15));
        textoNivel.setForeground(new Color(170, 170, 185));
        painelNivel.add(textoNivel);

        JRadioButton radioFacil = criarRadioNivel("Fácil", 200, perguntaSelecionada.getNivel().equals("FACIL"));
        JRadioButton radioMedio = criarRadioNivel("Médio", 300, perguntaSelecionada.getNivel().equals("MEDIO"));
        JRadioButton radioDificil = criarRadioNivel("Difícil", 400, perguntaSelecionada.getNivel().equals("DIFICIL"));

        ButtonGroup grupoNivel = new ButtonGroup();
        grupoNivel.add(radioFacil);
        grupoNivel.add(radioMedio);
        grupoNivel.add(radioDificil);

        painelNivel.add(radioFacil);
        painelNivel.add(radioMedio);
        painelNivel.add(radioDificil);

        JButton botaoCancelar = criarBotaoAcao("Cancelar", new Color(221, 188, 188), new Color(104, 44, 44));
        botaoCancelar.setBounds(230, 445, 206, 44);
        botaoCancelar.addActionListener(evento -> mostrarTela("VISUALIZAR"));
        painelFormulario.add(botaoCancelar);

        JButton botaoSalvar = criarBotaoAcao("Salvar alterações", new Color(176, 215, 172), new Color(44, 103, 48));
        botaoSalvar.setBounds(470, 445, 206, 44);
        botaoSalvar.addActionListener(evento -> editarPerguntaAssociacao(
                perguntaSelecionada,
                campoEnunciadoEditar,
                camposTextosEditar,
                caminhosImagensEditar,
                radioMedio,
                radioDificil));
        painelFormulario.add(botaoSalvar);

        return painelBase;
    }

    private void editarPerguntaAssociacao(PerguntaCadastro perguntaSelecionada, JTextField campoEnunciado,
            JTextField[] camposTextos, String[] caminhosImagens, JRadioButton radioMedio, JRadioButton radioDificil) {
        String enunciado = campoEnunciado.getText().trim();

        if (enunciado.isEmpty()) {
            mostrarMensagem("Digite o enunciado da pergunta.");
            return;
        }

        List<String> textos = obterTextosAlternativas(camposTextos);

        if (!validarDadosAssociacao(textos, caminhosImagens)) {
            return;
        }

        NivelDificuldade nivel = NivelDificuldade.FACIL;
        if (radioMedio.isSelected()) {
            nivel = NivelDificuldade.MEDIO;
        } else if (radioDificil.isSelected()) {
            nivel = NivelDificuldade.DIFICIL;
        }

        int idPerguntaEditada = perguntaSelecionada.getId();

        try {
            salvarEdicaoAssociacaoNoBanco(perguntaSelecionada, enunciado, textos, caminhosImagens, nivel);

            carregarPerguntasDoBanco();
            selecionarPerguntaPorId(idPerguntaEditada);
            mostrarTela("VISUALIZAR");
        } catch (SQLException | IllegalStateException erro) {
            mostrarMensagem("Erro ao editar pergunta de associação: " + erro.getMessage());
        }
    }

    private void salvarEdicaoAssociacaoNoBanco(PerguntaCadastro perguntaSelecionada, String enunciado,
            List<String> textos, String[] imagens, NivelDificuldade nivel) throws SQLException {
        Questao questao = new Questao(
                perguntaSelecionada.getId(),
                enunciado,
                TipoQuestao.ASSOCIACAO,
                nivel,
                perguntaSelecionada.getCategoria());

        for (int i = 0; i < textos.size(); i++) {
            questao.adicionarAlternativa(new Alternativa(
                    0,
                    textos.get(i),
                    imagens[i],
                    true,
                    false));
        }

        QuestaoDAO questaoDAO = new QuestaoDAO();
        boolean atualizou = questaoDAO.atualizarComAlternativas(questao);

        if (!atualizou) {
            throw new SQLException("Nenhuma pergunta de associação foi atualizada.");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaGerenciarPerguntas tela = new TelaGerenciarPerguntas();
            tela.setVisible(true);
        });
    }

    private static class PerguntaCadastro {
        private int id;
        private String enunciado;
        private List<String> alternativas;
        private List<String> imagensAssociacao;
        private int indiceCorreta;
        private String nivel;
        private String categoria;
        private String caminhoImagem;

        public PerguntaCadastro(int id, String enunciado, List<String> alternativas, List<String> imagensAssociacao,
                int indiceCorreta, String nivel, String categoria, String caminhoImagem) {
            this.id = id;
            this.enunciado = enunciado;
            this.alternativas = new ArrayList<>(alternativas);
            this.imagensAssociacao = new ArrayList<>(imagensAssociacao);
            this.indiceCorreta = indiceCorreta;
            this.nivel = nivel;
            this.categoria = categoria;
            this.caminhoImagem = caminhoImagem;
        }

        public int getId() {
            return id;
        }

        public String getEnunciado() {
            return enunciado;
        }

        public List<String> getAlternativas() {
            return alternativas;
        }

        public void setAlternativas(List<String> alternativas) {
            this.alternativas = new ArrayList<>(alternativas);
        }

        public List<String> getImagensAssociacao() {
            return imagensAssociacao;
        }

        public int getIndiceCorreta() {
            return indiceCorreta;
        }

        public void setIndiceCorreta(int indiceCorreta) {
            this.indiceCorreta = indiceCorreta;
        }

        public String getNivel() {
            return nivel;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getCaminhoImagem() {
            return caminhoImagem;
        }

        public void setCaminhoImagem(String caminhoImagem) {
            this.caminhoImagem = caminhoImagem;
        }

        public String getResumoRespostaCorreta() {
            boolean temImagemAssociacao = false;

            for (String imagem : imagensAssociacao) {
                if (imagem != null && !imagem.isBlank()) {
                    temImagemAssociacao = true;
                    break;
                }
            }

            if (temImagemAssociacao) {
                return "[img] — " + alternativas.get(0);
            }

            String[] letras = { "a", "b", "c", "d" };
            return letras[indiceCorreta] + ") " + alternativas.get(indiceCorreta);
        }
    }

    private static class BotaoArredondado extends JButton {

        public BotaoArredondado(String texto) {
            super(texto);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D desenho = (Graphics2D) grafico.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(getBackground());
            desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            super.paintComponent(grafico);
            desenho.dispose();
        }
    }

    private static class PainelArredondado extends JPanel {
        private final Color corFundo;
        private final Color corBorda;
        private final int espessuraBorda;

        public PainelArredondado(Color corFundo, Color corBorda, int espessuraBorda) {
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.espessuraBorda = espessuraBorda;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);

            Graphics2D desenho = (Graphics2D) grafico.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

            if (espessuraBorda > 0) {
                desenho.setColor(corBorda);
                desenho.setStroke(new BasicStroke(espessuraBorda));
                desenho.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            }

            desenho.dispose();
        }
    }

    private static class PainelFundoImagem extends JPanel {

        private Image imagemFundo;

        public PainelFundoImagem() {
            carregarImagem();
        }

        private void carregarImagem() {
            try {
                File arquivo = new File("imagens/Menu.png");
                imagemFundo = ImageIO.read(arquivo);
            } catch (IOException erro) {
                System.out.println("Erro ao carregar imagem de fundo: " + erro.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);

            if (imagemFundo != null) {
                grafico.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
            } else {
                grafico.setColor(new Color(223, 239, 252));
                grafico.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

}