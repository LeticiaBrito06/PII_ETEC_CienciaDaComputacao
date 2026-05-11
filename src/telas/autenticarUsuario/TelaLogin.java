package src.telas.autenticarUsuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.*;

public class TelaLogin extends JFrame{

    private JTextField espacoEmail;
    private JPasswordField espacoSenha;
    private JCheckBox lembrarMeSenha;

    public TelaLogin (){
        configurarLookAndFeel();
        configurarJanela();
        montarComponentes();
    }

    private void configurarLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao configurar o look and feel: " + e.getMessage());
        }
    }

    private void configurarJanela(){
        setTitle("LabTech - Login");
        setSize(960, 680);
        setMinimumSize(new Dimension(600, 480)); //Tamanho mínimo de tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); //Para poder redimencionar a tela
    }

    private void montarComponentes(){
        FundoTela painelFundo = new FundoTela();
        painelFundo.setLayout(new GridBagLayout()); //Para centralizar o formulário
        setContentPane(painelFundo);

        //Formulário de Login
        FormularioLogin formularioLogin = new FormularioLogin(30);
        formularioLogin.setLayout(new GridBagLayout());
        formularioLogin.setBackground(Color.WHITE);
        formularioLogin.setPreferredSize(new Dimension(410, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 45, 0, 45); //Para as margens laterais

        //Título do formulário
        JLabel titulo = new JLabel("LABTECH", SwingConstants.CENTER);
        titulo.setFont(new Font("Verdana", Font.BOLD, 52));
        titulo.setForeground(new Color(47, 76, 113));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 30, 20); //Margem superior e inferior
        formularioLogin.add(titulo);


        //Campo de E-mail
        JLabel email = new JLabel("E-mail institucional:");
        email.setFont (new Font("Verdana", Font.PLAIN, 15));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 45, 5, 45); //Margem entre o título e o campo de email
        formularioLogin.add(email, gbc);

        espacoEmail = new EspacoEmailTexto(15);
        espacoEmail.setFont(new Font("Verdana", Font.PLAIN, 15));
        espacoEmail.setBackground(new Color(245, 247, 251));
        espacoEmail.setPreferredSize(new Dimension(326, 45));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 45, 0, 45); //Margem entre o campo de email e a senha
        formularioLogin.add(espacoEmail, gbc);


        //Campo de Senha
        JLabel senha = new JLabel ("Senha:");
        senha.setFont(new Font("Verdana", Font.PLAIN, 15));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 45, 5, 45); //Margem entre o campo de email e a senha
        formularioLogin.add(senha, gbc);

        espacoSenha = new espacoSenhaTexto(15);
        espacoSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        espacoSenha.setBackground(new Color(245, 247, 251));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 45, 0, 45); //Margem entre o campo de senha e o checkbox de lembrar senha
        formularioLogin.add(espacoSenha, gbc);

        //Lembrar senha e Esqueci minha senha
        JPanel painelOpcoesSenha = new JPanel(new BorderLayout());
        painelOpcoesSenha.setOpaque(false);

        lembrarMeSenha = new JCheckBox("Lembrar minha senha");
        lembrarMeSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        lembrarMeSenha.setOpaque(false);
        lembrarMeSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel esqueceuSenha = new JLabel("Esqueci minha senha!");
        esqueceuSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        esqueceuSenha.setForeground(new Color(150, 40, 27));
        esqueceuSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelOpcoesSenha.add(lembrarMeSenha, BorderLayout.WEST);
        painelOpcoesSenha.add(esqueceuSenha, BorderLayout.EAST);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 45, 10, 45);
        formularioLogin.add(painelOpcoesSenha, gbc);

        //Botão de Entrar
        BotaoEntrar botaoLogin = new BotaoEntrar("Entrar", 15);
        botaoLogin.setPreferredSize(new Dimension(326, 45));
        gbc.gridy = 6;
        botaoLogin.addActionListener((ActionEvent evento) -> validarLogin());
        gbc.insets = new Insets(20, 45, 10, 45); //Margem entre o checkbox e o botão de entrar
        formularioLogin.add(botaoLogin, gbc);

        //Link para criar conta
        JLabel criarConta = new JLabel ("Não tem uma conta Professor? Crie agora!", SwingConstants.CENTER);
        criarConta.setFont(new Font("Verdana", Font.PLAIN, 13));
        criarConta.setForeground(new Color(47, 76, 113));
        criarConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 30, 20);
        formularioLogin.add(criarConta, gbc);

        //Adiciona o formulário ao painel de fundo
        painelFundo.add(formularioLogin, new GridBagConstraints());
    }

    // Classes Customizadas para User Interface
    private static class FormularioLogin extends JPanel {
        private final int raio;

        public FormularioLogin(int raio) {
            this.raio = raio;
            setOpaque(false);
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
        private final int raio;

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
        private final int raio;

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
        private final int raio;

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
                URL urlImagem = getClass().getResource("/imagens/menu.png");
                if (urlImagem != null) {
                    imagemFundo = new ImageIcon(urlImagem).getImage();
                } else {
                    System.err.println("Imagem de fundo não encontrada no classpath: /imagens/menu.png");
                }
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
                    double escala = Math.max((double) larguraPainel / larguraImagem, (double) alturaPainel  / alturaImagem);

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
                GradientPaint gradiente = new GradientPaint(0, 0, new Color(223, 239, 252), getWidth(), getHeight(), new Color(47, 76, 113));
                g2.setPaint(gradiente);
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
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
