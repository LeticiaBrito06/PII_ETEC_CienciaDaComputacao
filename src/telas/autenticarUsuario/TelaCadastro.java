package src.telas.autenticarUsuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.*;

public class TelaCadastro extends JFrame {
    private JTextField campoNome;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoCadastrar;

    public TelaCadastro(){
        configurarLookAndFeel();
        configurarTela();
        montarTela();
    }

    private void configurarLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao configurar o look and feel: " + e.getMessage());
        }
    }

    private void configurarTela(){
        setTitle("LABTECH");
        setSize(960, 680);
        setMinimumSize(new Dimension(600, 480)); //Tamanho mínimo de tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); //Para poder redimencionar a tela
    }

    private void montarTela(){
        PainelFundo painelFundo = new PainelFundo();
        painelFundo.setLayout(new GridBagLayout());
        setContentPane(painelFundo);

        //Painel de Cadastro
        PainelCadastro formularioCadastro = new PainelCadastro(30);
        formularioCadastro.setLayout(new GridBagLayout());
        formularioCadastro.setBackground(Color.WHITE);
        formularioCadastro.setPreferredSize(new Dimension(410, 480));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 45, 0, 45); //Margem para as laterais do formulário

        //Título do formulário
        JLabel tituloCadastro = new JLabel("CADASTRO", SwingConstants.CENTER);
        tituloCadastro.setFont(new Font("Verdana", Font.BOLD, 62));
        tituloCadastro.setForeground(new Color(47, 67, 113));
        formularioCadastro.add(tituloCadastro, gbc);

        //Campo de nome
        JLabel textoNome = new JLabel("Nome completo:");
        textoNome.setFont(new Font("Verdana", Font.BOLD, 15));
        gbc.gridy = 1; //Posição do campo de nome
        gbc.insets = new Insets(10, 45, 5, 45);
        formularioCadastro.add(textoNome, gbc);

        campoNome = new EspacoNomeTexto(15);
        campoNome.setFont(new Font("Verdana", Font.PLAIN, 15));
        campoNome.setBackground(new Color(245, 247, 251));
        campoNome.setPreferredSize(new Dimension(326, 45));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 45, 0, 45);
        formularioCadastro.add(campoNome, gbc);

        //Campo de e-mail
        JLabel textoEmail = new JLabel("E-mail institucional:");
        textoEmail.setFont(new Font("Verdana", Font.BOLD, 15));
        gbc.gridy = 2; //Posição do campo de e-mail
        gbc.insets = new Insets(10, 45, 5, 45);
        formularioCadastro.add(textoEmail, gbc);

        campoEmail = new CampoEmailTexto(15);
        campoEmail.setFont(new Font("Verdana", Font.PLAIN, 15));
        campoEmail.setBackground(new Color(245, 247, 251));
        campoEmail.setPreferredSize(new Dimension(326, 45));
        gbc.gridy = 3; //Posição do campo de e-mail
        gbc.insets = new Insets(5, 45, 10, 45);
        formularioCadastro.add(campoEmail, gbc);

        //Campo de senha
        JLabel textoSenha = new JLabel("Senha:");
        textoSenha.setFont(new Font("Verdana", Font.BOLD, 15));
        gbc.gridy = 4; //Posição do campo de senha
        gbc.insets = new Insets(5, 45, 5, 45);
        formularioCadastro.add(textoSenha, gbc);

        campoSenha = new EspacoCampoSenha(15);
        campoSenha.setFont(new Font("Verdana", Font.PLAIN, 15));
        campoSenha.setBackground(new Color(245, 247, 251));
        gbc.gridy = 5; //Posição do campo de senha
        gbc.insets = new Insets(5, 45, 10, 45);
        formularioCadastro.add(campoSenha, gbc);

        //Botão de cadastro
        botaoCadastrar = new BotaoCadastro("Cadastrar", 20);
        botaoCadastrar.setFont(new Font("Verdana", Font.BOLD, 15));
        gbc.gridy = 6; //Posição do botão de cadastro
        botaoCadastrar.addActionListener((ActionEvent evento) -> validarCadastro());
        gbc.insets = new Insets(10, 45, 10, 45);
        formularioCadastro.add(botaoCadastrar, gbc);

        painelFundo.add(formularioCadastro, new GridBagConstraints());
    }
    
    //Classes para os painéis personalizados
    private static class PainelCadastro extends JPanel {
        private final int raio;

        public PainelCadastro(int raio) {
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

    private static class EspacoNomeTexto extends JTextField{
        private final int raio;

        public EspacoNomeTexto(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class CampoEmailTexto extends JTextField{
        private final int raio;

        public CampoEmailTexto(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class EspacoCampoSenha extends JPasswordField{
        private final int raio;

        public EspacoCampoSenha(int raio) {
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotaoCadastro extends JButton{
        private final int raio;

        public BotaoCadastro(String texto, int raio) {
            super(texto);
            this.raio = raio;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
    private static class PainelFundo extends JPanel{
        private Image imagemFundo;

        public PainelFundo() {
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

    private void validarCadastro() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim().toLowerCase();
        String senha = new String(campoSenha.getPassword()).trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.endsWith("@cps.sp.edu.br")) {
            JOptionPane.showMessageDialog(this,
                "Apenas e-mails institucionais são aceitos.\nExemplo: seunome@cps.sp.edu.br",
                "E-mail inválido",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        // Verifica se há algo antes do @
        String parteLocal = email.substring(0, email.indexOf("@"));
        if (parteLocal.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "E-mail inválido. Informe um e-mail no formato: seunome@cps.sp.edu.br",
                "E-mail inválido",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        abrirTelaLogin();
    }

    private void abrirTelaLogin () {
        new TelaLogin().setVisible(true);
        dispose();
    }
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        SwingUtilities.invokeLater(() -> new TelaCadastro().setVisible(true));
    }
}
