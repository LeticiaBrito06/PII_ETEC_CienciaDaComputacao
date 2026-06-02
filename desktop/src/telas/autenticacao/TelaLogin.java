package telas.autenticacao;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.DAO.UsuarioDAO;
import serviços.SessaoUsuario;
import telas.aluno.TelaMenuAluno;
import telas.professor.TelaMenuProfessor;
import static utilitarios.Criptografia.criptografar;

public class TelaLogin extends JFrame {

    private JTextField campoEmail;
    private JPasswordField campoSenha;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public TelaLogin() {
        configurarJanela();
        montarTela();
    }

    private void configurarJanela() {
        setTitle("LabQuest - Login");
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

        PainelArredondado painelLogin = new PainelArredondado(30);
        painelLogin.setLayout(null);
        painelLogin.setBounds(275, 135, 410, 480);
        painelLogin.setBackground(Color.WHITE);
        conteinerCentral.add(painelLogin);

        JLabel titulo = new JLabel("LabQuest", SwingConstants.CENTER);
        titulo.setBounds(35, 30, 340, 80);
        titulo.setFont(new Font("Verdana", Font.BOLD, 62));
        titulo.setForeground(new Color(47, 76, 113));
        painelLogin.add(titulo);

        JLabel textoEmail = new JLabel("E-mail institucional");
        textoEmail.setBounds(45, 125, 250, 25);
        textoEmail.setFont(new Font("Verdana", Font.BOLD, 15));
        textoEmail.setForeground(new Color(47, 76, 113));
        painelLogin.add(textoEmail);

        campoEmail = new CampoArredondado(15);
        campoEmail.setBounds(42, 150, 326, 45);
        campoEmail.setFont(new Font("Verdana", Font.PLAIN, 15));
        campoEmail.setBackground(new Color(245, 247, 251));
        painelLogin.add(campoEmail);

        JLabel textoSenha = new JLabel("Senha (CPF)");
        textoSenha.setBounds(45, 205, 250, 25);
        textoSenha.setFont(new Font("Verdana", Font.BOLD, 15));
        textoSenha.setForeground(new Color(47, 76, 113));
        painelLogin.add(textoSenha);

        campoSenha = new CampoSenhaArredondado(15);
        campoSenha.setBounds(42, 230, 326, 45);
        campoSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        campoSenha.setBackground(new Color(245, 247, 251));
        ((javax.swing.text.AbstractDocument) campoSenha.getDocument())
                .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                    @Override
                    public void insertString(FilterBypass fb, int offset, String string,
                            javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                        if (string.matches("\\d+") && (fb.getDocument().getLength() + string.length()) <= 11) {
                            super.insertString(fb, offset, string, attr);
                        }
                    }

                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text,
                            javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                        if (text.matches("\\d*") && (fb.getDocument().getLength() - length + text.length()) <= 11) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });
        painelLogin.add(campoSenha);

        BotaoArredondado botaoEntrar = new BotaoArredondado("Entrar", 15);
        botaoEntrar.setBounds(42, 335, 326, 45);
        botaoEntrar.addActionListener((ActionEvent evento) -> validarLogin());
        painelLogin.add(botaoEntrar);
    }

    private static class PainelArredondado extends JPanel {
        private final int raio;

        public PainelArredondado(int raio) {
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CampoArredondado extends JTextField {
        private final int raio;

        public CampoArredondado(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CampoSenhaArredondado extends JPasswordField {
        private final int raio;

        public CampoSenhaArredondado(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotaoArredondado extends JButton {
        private final int raio;

        public BotaoArredondado(String texto, int raio) {
            super(texto);
            this.raio = raio;
            setFont(new Font("Verdana", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setBackground(new Color(36, 73, 130));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private static class PainelFundo extends JPanel {
        private Image imagemFundo;

        public PainelFundo() {
            try {
                imagemFundo = new ImageIcon("imagens/login.png").getImage();
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem de fundo: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

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

    private void validarLogin() {
        String emailDigitado = campoEmail.getText().trim().toLowerCase();
        String senhaDigitada = new String(campoSenha.getPassword());

        if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o e-mail institucional e a senha.",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!senhaDigitada.matches("^\\d{11}$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Senha inválida!\nA senha deve ser o seu CPF (apenas os 11 números, sem caracteres).",
                    "Erro de Autenticação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            var usuario = usuarioDAO.buscarPorEmail(emailDigitado);
            if (usuario != null) {
                String salt = usuarioDAO.buscarSaltPorEmail(emailDigitado);
                String senhaHashDigitada = criptografar(senhaDigitada, salt);
                if (!senhaHashDigitada.equals(usuario.getSenha())) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Senha incorreta. Tente novamente.",
                            "Erro de Autenticação",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    SessaoUsuario.iniciarSessao(usuario);

                    if (usuario.getTipo() == model.tipos.TipoUsuario.ALUNO) {
                        new TelaMenuAluno().setVisible(true);
                        dispose();
                    } else if (usuario.getTipo() == model.tipos.TipoUsuario.PROFESSOR) {
                        new TelaMenuProfessor().setVisible(true);
                        dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "E-mail institucional não encontrado.",
                        "Erro de Autenticação",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro ao tentar autenticar. Tente novamente mais tarde.",
                    "Erro de Autenticação",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Erro ao autenticar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
        });
    }
}