package src.telas.autenticarUsuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class TelaLogin extends JFrame{

    private JTextField espacoEmail;
    private JPasswordField espacoSenha;
    private JCheckBox lembrarMeSenha;

    public TelaLogin (){
        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela(){
        setTitle("LabTech - Login");
        setSize(960, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void montarComponentes(){
        FundoTela painelFundo = new FundoTela();
        painelFundo.setLayout(null);
        setContentPane(painelFundo);

        //Formulário de Login
        FormularioLogin formularioLogin = new FormularioLogin(30);
        formularioLogin.setLayout(null);
        formularioLogin.setBounds(275, 135, 410, 480);
        formularioLogin.setBackground(Color.WHITE);
        painelFundo.add(formularioLogin);

        JLabel titulo = new JLabel("LABTECH", SwingConstants.CENTER);
        titulo.setBounds(35, 30, 340, 80);
        titulo.setFont(new Font("Vernada", Font.BOLD, 62));
        titulo.setForeground(new Color(47, 76, 113));
        formularioLogin.add(titulo);

        JLabel email = new JLabel("E-mail institucional:");
        email.setBounds(45, 125, 250, 25);
        email.setFont (new Font("Vernada", Font.PLAIN, 15));
        email.setBackground(new Color(47, 76, 113));
        formularioLogin.add(email);

        espacoEmail = new EspacoEmailTexto(15);
        espacoEmail.setBounds(45, 150, 326, 45);
        espacoEmail.setFont(new Font("Verdana", Font.PLAIN, 15));
        espacoEmail.setBackground(new Color(245, 247, 251));
        formularioLogin.add(espacoEmail);

        JLabel senha = new JLabel ("Senha:");
        senha.setBounds(45, 205, 250, 25);
        senha.setFont(new Font("Verdana", Font.PLAIN, 15));
        senha.setBackground(new Color(245, 247, 251));
        formularioLogin.add(senha);

        espacoSenha = new espacoSenhaTexto(15);
        espacoSenha.setBounds(45, 230, 326, 45);
        espacoSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        espacoSenha.setBackground(new Color(245, 247, 251));
        formularioLogin.add(espacoSenha);

        lembrarMeSenha = new JCheckBox("Lembrar minha senha");
        lembrarMeSenha.setBounds(45, 280, 250, 25);
        lembrarMeSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        lembrarMeSenha.setBackground(new Color(245, 247, 251));
        formularioLogin.add(lembrarMeSenha);

        JLabel esqueceuSenha = new JLabel("Esqueci minha senha!");
        esqueceuSenha.setBounds(230, 290, 150, 25);
        esqueceuSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        esqueceuSenha.setForeground(new Color(150, 40, 27));
        formularioLogin.add(esqueceuSenha);

        BotaoEntrar botaoLogin = new BotaoEntrar("Entrar", 15);
        botaoLogin.setBounds(45, 350, 326, 45);
        botaoLogin.addActionListener((ActionEvent evento) -> validarLogin());
        formularioLogin.add(botaoLogin);

        JLabel criarConta = new JLabel ("Não tem uma conta Professor? Crie agora!", SwingConstants.CENTER);
        criarConta.setBounds(35, 410, 340, 25);
        criarConta.setFont(new Font("Verdana", Font.PLAIN, 25));
        criarConta.setForeground(new Color(47, 76, 113));
        criarConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formularioLogin.add(criarConta);
    }

    // Classes Customizadas para User Interface
    private static class FormularioLogin extends JPanel {
        private int raio;

        public FormularioLogin(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
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

    private static class EspacoEmailTexto extends JTextField {
        private int raio;

        public EspacoEmailTexto(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
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

    private static class espacoSenhaTexto extends JPasswordField {
        private int raio;

        public espacoSenhaTexto(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
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

    private static class BotaoEntrar extends JButton {
        private int raio;

        public BotaoEntrar(String texto, int raio) {
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
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class FundoTela extends JPanel {
        private Image imagemFundo;

        public FundoTela() {
            try {
                // Carrega a imagem de fundo menu.png
                imagemFundo = new ImageIcon("imagens/menu.png").getImage();
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem de fundo: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            
            // Habilita interpolação de alta qualidade para o redimensionamento
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (imagemFundo != null) {
                int larguraPainel = getWidth();
                int alturaPainel = getHeight();
                int larguraImagem = imagemFundo.getWidth(this);
                int alturaImagem = imagemFundo.getHeight(this);

                if (larguraImagem > 0 && alturaImagem > 0) {
                    // Calcula a escala para cobrir todo o painel mantendo a proporção (tipo "cover")
                    double escalaX = (double) larguraPainel / larguraImagem;
                    double escalaY = (double) alturaPainel / alturaImagem;
                    double escala = Math.max(escalaX, escalaY);

                    int novaLargura = (int) (larguraImagem * escala);
                    int novaAltura = (int) (alturaImagem * escala);

                    // Centraliza a imagem no painel
                    int x = (larguraPainel - novaLargura) / 2;
                    int y = (alturaPainel - novaAltura) / 2;

                    g2.drawImage(imagemFundo, x, y, novaLargura, novaAltura, this);
                } else {
                    // Fallback se as dimensões não forem lidas corretamente
                    g2.drawImage(imagemFundo, 0, 0, larguraPainel, alturaPainel, this);
                }
            } else {
                g2.setColor(new Color(223, 239, 252));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            
            g2.dispose();
        }
    }

    private void validarLogin(){
        String emailString = espacoEmail.getText().trim().toLowerCase();
        String senhaString = new String(espacoSenha.getPassword());

        if (emailString.isEmpty() || senhaString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (emailAluno(emailString)){
            JOptionPane.showMessageDialog(this, "Login de aluno identificado com sucesso.", "Aluno", JOptionPane.INFORMATION_MESSAGE);
        }

        else if (emailProfessor(emailString)){
            JOptionPane.showMessageDialog(this, "Login de professor identificado com sucesso.", "Professor", JOptionPane.INFORMATION_MESSAGE);
        } 
        
        else {
            JOptionPane.showMessageDialog(this, "E-mail institucional inválido!", "E-mail inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean emailAluno(String email) {
        return email.endsWith("@aluno.cps.sp.edu.br");
    }

    private boolean emailProfessor(String email) {
        return email.endsWith("@cps.sp.edu.br");
    }

    private void abrirTelaPrincipalAluno () {
        // Quando a tela aluno tiver pronta usa
        // new telas.aluno.TelaPrincipalAluno().setVisible(true);
        // dispose();
    }

    private void abrirTelaPrincipalProfessor () {
        // Quando a tela professor tiver pronta usa
        // new telas.professor.TelaPrincipalProfessor().setVisible(true);
        // dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}
