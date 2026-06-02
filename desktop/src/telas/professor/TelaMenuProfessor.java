package telas.professor;

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
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import serviços.SessaoUsuario;

public class TelaMenuProfessor extends JFrame {

    public TelaMenuProfessor() {
        configurarJanela();
        montarTela();
    }

    private void configurarJanela() {
        setTitle("Tela - Início");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        PainelFundo painelFundo = new PainelFundo();
        painelFundo.setLayout(new GridBagLayout());
        setContentPane(painelFundo);

        JPanel conteinerCentral = new JPanel(null);
        conteinerCentral.setPreferredSize(new Dimension(960, 680));
        conteinerCentral.setOpaque(false);
        painelFundo.add(conteinerCentral);

        JButton botaoPerfil = criarBotaoIconeReal("imagens/Perfil.png");
        botaoPerfil.setBounds(30, 30, 55, 55);
        botaoPerfil.addActionListener(e -> abrirPerfil());
        conteinerCentral.add(botaoPerfil);

        JButton botaoSair = criarBotaoIconeReal("imagens/Sair.png");
        botaoSair.setBounds(875, 30, 55, 55);
        botaoSair.addActionListener(e -> sairDaConta());
        conteinerCentral.add(botaoSair);

        JLabel titulo = new JLabel("LabQuest", SwingConstants.CENTER);
        titulo.setBounds(80, 100, 800, 120);
        titulo.setFont(new Font("Verdana", Font.BOLD, 110));
        titulo.setForeground(new Color(31, 65, 126));
        conteinerCentral.add(titulo);

        BotaoArredondado botaoGerenciarAlternativas = new BotaoArredondado("<html><center>Gerenciar Perguntas<br>de Alternativa</center></html>");
        botaoGerenciarAlternativas.setBounds(280, 260, 400, 65);
        botaoGerenciarAlternativas.addActionListener(evento -> abrirGerenciarPerguntasAlternativa());
        conteinerCentral.add(botaoGerenciarAlternativas);

        BotaoArredondado botaoGerenciarAssociacao = new BotaoArredondado("<html><center>Gerenciar Perguntas<br>de Associação</center></html>");
        botaoGerenciarAssociacao.setBounds(280, 345, 400, 65);
        botaoGerenciarAssociacao.addActionListener(evento -> abrirGerenciarPerguntasAssociacao());
        conteinerCentral.add(botaoGerenciarAssociacao);

        BotaoArredondado botaoRelatorios = new BotaoArredondado("Desempenhos e Relatórios");
        botaoRelatorios.setBounds(280, 430, 400, 65);
        botaoRelatorios.addActionListener((ActionEvent evento) -> abrirRelatorios());
        conteinerCentral.add(botaoRelatorios);

        BotaoArredondado botaoCadastrarAluno = new BotaoArredondado("Cadastrar Aluno");
        botaoCadastrarAluno.setBounds(280, 515, 400, 65);
        botaoCadastrarAluno.addActionListener((ActionEvent evento) -> abrirCadastrarAluno());
        conteinerCentral.add(botaoCadastrarAluno);
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

    private void abrirCadastrarAluno() {
        new telas.autenticacao.TelaCadastro().setVisible(true);
        dispose();
    }

    private void abrirGerenciarPerguntasAlternativa() {
        new TelaGerenciarPerguntas(model.tipos.TipoQuestao.MULTIPLA_ESCOLHA).setVisible(true);
        dispose();
    }

    private void abrirGerenciarPerguntasAssociacao() {
        new TelaGerenciarPerguntas(model.tipos.TipoQuestao.ASSOCIACAO).setVisible(true);
        dispose();
    }

    private void abrirRelatorios() {
        new TelaDesempenho().setVisible(true);
        dispose();
    }

    private void sairDaConta() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair da conta?",
                "Sair",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            dispose();

            new telas.autenticacao.TelaLogin().setVisible(true);
        }
    }

    private static class BotaoArredondado extends JButton {

        public BotaoArredondado(String texto) {
            super(texto);
            setFont(new Font("Verdana", Font.BOLD, 21));
            setForeground(Color.WHITE);
            setBackground(new Color(36, 73, 130));
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
            desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            super.paintComponent(grafico);
            desenho.dispose();
        }
    }

    private static class PainelFundo extends JPanel {
        private Image imagemFundo;

        public PainelFundo() {
            try {
                imagemFundo = new ImageIcon("imagens/Menu.png").getImage();
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem de fundo: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);
            Graphics2D g2 = (Graphics2D) grafico.create();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (imagemFundo != null) {
                int larguraPainel = getWidth();
                int alturaPainel = getHeight();
                int larguraImagem = imagemFundo.getWidth(this);
                int alturaImagem = imagemFundo.getHeight(this);

                if (larguraImagem > 0 && alturaImagem > 0) {
                    double escalaX = (double) larguraPainel / larguraImagem;
                    double escalaY = (double) alturaPainel / alturaImagem;
                    double escala = Math.max(escalaX, escalaY);

                    int novaLargura = (int) (larguraImagem * escala);
                    int novaAltura = (int) (alturaImagem * escala);

                    int x = (larguraPainel - novaLargura) / 2;
                    int y = (alturaPainel - novaAltura) / 2;

                    g2.drawImage(imagemFundo, x, y, novaLargura, novaAltura, this);
                } else {
                    g2.drawImage(imagemFundo, 0, 0, larguraPainel, alturaPainel, this);
                }
            } else {
                g2.setColor(new Color(223, 239, 252));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaMenuProfessor tela = new TelaMenuProfessor();
            tela.setVisible(true);
        });
    }
}