package telas.aluno;

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
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.DAO.QuestaoDAO;
import model.Aluno;
import model.Partida;
import model.Questao;
import serviços.SessaoUsuario;
import telas.jogo.TelaJogo;

public class TelaMenuAluno extends JFrame {

    private model.Aluno aluno;

    public TelaMenuAluno(model.Aluno aluno) {
        this.aluno = aluno;
        configurarJanela();
        montarTela();
    }

    public TelaMenuAluno() {
        this((Aluno) SessaoUsuario.getUsuarioLogado());
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

        JButton botaoJogar = criarBotaoMenu("Jogar");
        botaoJogar.setBounds(280, 280, 400, 65);
        botaoJogar.addActionListener((ActionEvent evento) -> abrirTelaJogo());
        conteinerCentral.add(botaoJogar);

        JButton botaoDesempenho = criarBotaoMenu("Desempenho");
        botaoDesempenho.setBounds(280, 365, 400, 65);
        botaoDesempenho.addActionListener((ActionEvent evento) -> abrirTelaDesempenho());
        conteinerCentral.add(botaoDesempenho);
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
        if (this.aluno != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome: " + this.aluno.getNome() + "\nE-mail: " + this.aluno.getEmail(),
                    "Perfil do Aluno",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Informações do aluno não encontradas.");
        }
    }

    private JButton criarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Verdana", Font.BOLD, 21));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(36, 73, 130));
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return botao;
    }

    private void abrirTelaJogo() {
        try {
            Aluno alunoLogado = (Aluno) SessaoUsuario.getUsuarioLogado();

            QuestaoDAO questaoDAO = new QuestaoDAO();
            List<Questao> questoes = questaoDAO.listarTodas();

            if (questoes.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não há questões cadastradas para iniciar o jogo.",
                        "Jogo indisponível",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Partida partida = new Partida(alunoLogado);

            new TelaJogo(partida, questoes).setVisible(true);
            dispose();

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar questões do banco: " + erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalStateException | ClassCastException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível iniciar o jogo. Faça login novamente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaDesempenho() {
        new TelaDesempenhoAluno((Aluno) SessaoUsuario.getUsuarioLogado()).setVisible(true);
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
            TelaMenuAluno tela = new TelaMenuAluno();
            tela.setVisible(true);
        });
    }
}
