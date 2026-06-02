package telas.jogo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import database.DAO.PartidaDAO;
import model.Alternativa;
import model.Aluno;
import model.Partida;
import model.Questao;
import model.Resposta;
import model.tipos.TipoQuestao;
import serviços.SessaoUsuario;

public class TelaJogo extends JFrame {

    private Partida partida;
    private List<Questao> questoes;
    private int indiceQuestaoAtual = 0;

    private JLabel labelPergunta;
    private JLabel labelImagemQuestao;
    private JPanel painelConteudo;
    private List<BotaoAlternativa> botoesAlternativas;
    private List<BotaoAssociacao> botoesEsq;
    private List<BotaoAssociacao> botoesDir;

    private boolean ajuda5050Usada = false;
    private boolean chanceExtraUsada = false;
    private boolean pularUsada = false;
    private boolean chanceExtraAtiva = false;

    private BotaoAssociacao selecionadoEsquerda = null;
    private BotaoAssociacao selecionadoDireita = null;
    private int paresResolvidos = 0;

    private final int LARGURA_CONTEUDO = 900;
    private final int ALTURA_CONTEUDO = 580;

    private JButton botaoAjuda;
    private JPanel painelIcones;

    public TelaJogo(Partida partida, List<Questao> questoes) {
        this.partida = partidaComAlunoLogado(partida);
        this.questoes = questoes;
        this.botoesAlternativas = new ArrayList<>();
        this.botoesEsq = new ArrayList<>();
        this.botoesDir = new ArrayList<>();

        configurarJanela();
        montarEstruturaBase();
        carregarQuestaoAtual();
    }

    private void configurarJanela() {
        setTitle("LabQuest - Desafio de Laboratório");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void montarEstruturaBase() {
        PainelFundo painelFundo = new PainelFundo();
        painelFundo.setLayout(new GridBagLayout());
        setContentPane(painelFundo);

        JPanel conteinerCentral = new JPanel(null);
        conteinerCentral.setPreferredSize(new Dimension(1000, 750));
        conteinerCentral.setOpaque(false);
        painelFundo.add(conteinerCentral);

        JPanel painelIconesLocal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        painelIconesLocal.setOpaque(false);
        painelIconesLocal.setBounds(800, 10, 180, 80);
        this.painelIcones = painelIconesLocal;

        JButton botaoPerfil = criarBotaoIconeReal("imagens/Perfil.png");
        botaoPerfil.addActionListener(e -> abrirPerfil());

        JButton botaoSair = criarBotaoIconeReal("imagens/Sair.png");
        botaoSair.addActionListener(e -> confirmarSaida());

        painelIconesLocal.add(botaoPerfil);
        painelIconesLocal.add(botaoSair);
        conteinerCentral.add(painelIconesLocal);

        this.botaoAjuda = criarBotaoIconeReal("imagens/Ajuda.png");
        this.botaoAjuda.setBounds(20, 20, 50, 50);
        this.botaoAjuda.addActionListener(e -> mostrarPopUpAjuda());
        conteinerCentral.add(this.botaoAjuda);

        painelConteudo = new JPanel();
        painelConteudo.setLayout(null);
        painelConteudo.setOpaque(false);
        painelConteudo.setBounds(50, 100, 900, 580);
        conteinerCentral.add(painelConteudo);
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
        if (this.partida != null && this.partida.getAluno() != null) {
            model.Aluno aluno = this.partida.getAluno();
            JOptionPane.showMessageDialog(
                    this,
                    "Nome: " + aluno.getNome() + "\nE-mail: " + aluno.getEmail(),
                    "Perfil do Jogador",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Informações do jogador não encontradas.");
        }
    }

    private JButton criarBotaoIcone(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(65, 65));
        btn.setFont(new Font("Verdana", Font.BOLD, 32));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void carregarQuestaoAtual() {
        if (indiceQuestaoAtual >= questoes.size()) {
            finalizarPartida();
            return;
        }

        Questao questao = questoes.get(indiceQuestaoAtual);
        painelConteudo.removeAll();
        botoesAlternativas.clear();
        botoesEsq.clear();
        botoesDir.clear();

        chanceExtraAtiva = false;
        paresResolvidos = 0;
        selecionadoEsquerda = null;
        selecionadoDireita = null;

        if (questao.getTipo() == TipoQuestao.ASSOCIACAO) {
            montarInterfaceAssociacao(questao);
        } else {
            montarInterfaceAlternativa(questao);
        }

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void montarInterfaceAlternativa(Questao questao) {
        PainelArredondado painelPergunta = new PainelArredondado(30);
        painelPergunta.setLayout(null);
        painelPergunta.setBounds(50, 10, 800, 320);
        painelPergunta.setBackground(new Color(47, 76, 113));
        painelConteudo.add(painelPergunta);

        String enunciado = questao.getEnunciado();
        boolean perguntaLonga = enunciado.length() > 65;

        JTextPane textoPergunta = new JTextPane();
        textoPergunta.setText(enunciado);
        textoPergunta.setBounds(
                40,
                perguntaLonga ? 10 : 20,
                720,
                perguntaLonga ? 110 : 70);

        textoPergunta.setFont(new Font(
                "Verdana",
                Font.BOLD,
                perguntaLonga ? 24 : 28));

        textoPergunta.setForeground(Color.WHITE);
        textoPergunta.setOpaque(false);
        textoPergunta.setEditable(false);
        textoPergunta.setFocusable(false);
        textoPergunta.setBorder(null);
        textoPergunta.setHighlighter(null);

        StyledDocument documento = textoPergunta.getStyledDocument();
        SimpleAttributeSet alinhamento = new SimpleAttributeSet();
        StyleConstants.setAlignment(alinhamento, StyleConstants.ALIGN_CENTER);
        documento.setParagraphAttributes(0, documento.getLength(), alinhamento, false);

        painelPergunta.add(textoPergunta);

        labelImagemQuestao = new JLabel("", SwingConstants.CENTER);
        labelImagemQuestao.setBounds(
                250,
                perguntaLonga ? 130 : 95,
                300,
                perguntaLonga ? 165 : 205);

        if (questao.getImagemEnunciado() != null && !questao.getImagemEnunciado().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(questao.getImagemEnunciado());

                int tamanhoImagem = perguntaLonga ? 165 : 200;

                Image img = icon.getImage().getScaledInstance(
                        tamanhoImagem,
                        tamanhoImagem,
                        Image.SCALE_SMOOTH);

                labelImagemQuestao.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                labelImagemQuestao.setText("🧪");
                labelImagemQuestao.setFont(new Font("Verdana", Font.PLAIN, perguntaLonga ? 80 : 100));
                labelImagemQuestao.setForeground(Color.WHITE);
            }
        }

        painelPergunta.add(labelImagemQuestao);

        botoesAlternativas.clear();

        List<Alternativa> alternativas = questao.getAlternativas();

        int y = 350;
        char letra = 'a';

        for (Alternativa alt : alternativas) {
            BotaoAlternativa btn = new BotaoAlternativa(letra + ") " + alt.getTexto());
            btn.setBounds(50, y, 800, 48);
            btn.addActionListener(e -> processarRespostaAlternativa(alt, btn));

            painelConteudo.add(btn);
            botoesAlternativas.add(btn);

            y += 58;
            letra++;
        }
    }

    private void montarInterfaceAssociacao(Questao questao) {
        PainelArredondado painelInstrucao = new PainelArredondado(20);
        painelInstrucao.setLayout(new BorderLayout());
        painelInstrucao.setBounds(100, 5, 700, 70);
        painelInstrucao.setBackground(new Color(47, 76, 113));

        JLabel lblMsg = new JLabel("Conecte o material ao sistema experimental correspondente", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Verdana", Font.BOLD, 18));
        lblMsg.setForeground(Color.WHITE);
        painelInstrucao.add(lblMsg);
        painelConteudo.add(painelInstrucao);

        List<Alternativa> pares = questao.getAlternativas();

List<Integer> ordemEsq = new ArrayList<>();
List<Integer> ordemDir = new ArrayList<>();

for (int i = 0; i < pares.size(); i++) {
    ordemEsq.add(i);
    ordemDir.add(i);
}

Collections.shuffle(ordemEsq);
Collections.shuffle(ordemDir);

int y = 90;

for (int i = 0; i < pares.size(); i++) {
    final int idxEsq = ordemEsq.get(i);
    final int idxDir = ordemDir.get(i);

    Alternativa parEsq = pares.get(idxEsq);
    Alternativa parDir = pares.get(idxDir);

    String idParEsq = String.valueOf(parEsq.getId());
    String idParDir = String.valueOf(parDir.getId());

    BotaoAssociacao btnEsq = new BotaoAssociacao("", parEsq.getImagem(), true);
    btnEsq.setBounds(50, y, 230, 110);
    btnEsq.addActionListener(e -> lidarCliqueAssociacao(btnEsq, true, idParEsq));
    btnEsq.setIdPar(idParEsq);
    painelConteudo.add(btnEsq);
    botoesEsq.add(btnEsq);

    BotaoAssociacao btnDir = new BotaoAssociacao(parDir.getTexto(), null, false);
    btnDir.setBounds(620, y, 230, 110);
    btnDir.addActionListener(e -> lidarCliqueAssociacao(btnDir, false, idParDir));
    btnDir.setIdPar(idParDir);
    painelConteudo.add(btnDir);
    botoesDir.add(btnDir);

    y += 120;
        }
    }

    private void lidarCliqueAssociacao(BotaoAssociacao btn, boolean esq, String idCorreto) {
        if (btn.isResolvido())
            return;

        if (esq) {
            if (selecionadoEsquerda != null)
                selecionadoEsquerda.setSelecionado(false);
            selecionadoEsquerda = btn;
            selecionadoEsquerda.setSelecionado(true);
            selecionadoEsquerda.setIdPar(idCorreto);
        } else {
            if (selecionadoDireita != null)
                selecionadoDireita.setSelecionado(false);
            selecionadoDireita = btn;
            selecionadoDireita.setSelecionado(true);
            selecionadoDireita.setIdPar(idCorreto);
        }

        if (selecionadoEsquerda != null && selecionadoDireita != null) {
            if (selecionadoEsquerda.getIdPar().equals(selecionadoDireita.getIdPar())) {
                selecionadoEsquerda.setResolvido(true);
                selecionadoDireita.setResolvido(true);
                selecionadoEsquerda.setBackground(new Color(46, 204, 113));
                selecionadoDireita.setBackground(new Color(46, 204, 113));
                paresResolvidos++;

                if (paresResolvidos == 4) {
                    registrarRespostaAssociacao(true);

                    Timer t = new Timer(800, e -> {
                        indiceQuestaoAtual++;
                        carregarQuestaoAtual();
                    });

                    t.setRepeats(false);
                    t.start();
                }
            } else {
                selecionadoEsquerda.setBackground(new Color(231, 76, 60));
                selecionadoDireita.setBackground(new Color(231, 76, 60));
                Timer t = new Timer(500, e -> {
                    if (selecionadoEsquerda != null) {
                        selecionadoEsquerda.setSelecionado(false);
                        selecionadoEsquerda.setBackground(new Color(130, 150, 220));
                    }
                    if (selecionadoDireita != null) {
                        selecionadoDireita.setSelecionado(false);
                        selecionadoDireita.setBackground(new Color(130, 150, 220));
                    }
                    selecionadoEsquerda = null;
                    selecionadoDireita = null;
                });
                t.setRepeats(false);
                t.start();
                return;
            }
            selecionadoEsquerda = null;
            selecionadoDireita = null;
        }
    }

    private void mostrarPopUpAjuda() {
        JDialog dialog = new JDialog(this, "Suporte LabQuest", true);
        dialog.setSize(600, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(47, 76, 113));

        JLabel titulo = new JLabel("Central de Ajuda", SwingConstants.CENTER);
        titulo.setBounds(0, 20, 600, 40);
        titulo.setFont(new Font("Verdana", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        dialog.add(titulo);

        JButton btn5050 = criarBotaoAjuda("50/50", 50, 85, !ajuda5050Usada);
        btn5050.addActionListener(e -> {
            usar5050();
            dialog.dispose();
        });

        JButton btnChance = criarBotaoAjuda("Chance Extra", 310, 85, !chanceExtraUsada);
        btnChance.addActionListener(e -> {
            usarChanceExtra();
            dialog.dispose();
        });

        JButton btnPular = criarBotaoAjuda("Pular Questão", 180, 245, !pularUsada);
        btnPular.addActionListener(e -> {
            usarPular();
            dialog.dispose();
        });

        dialog.add(btn5050);
        dialog.add(btnChance);
        dialog.add(btnPular);

        JLabel lblInfo = new JLabel("Você pode usar cada ajuda apenas uma vez por partida.", SwingConstants.CENTER);
        lblInfo.setBounds(0, 400, 600, 20);
        lblInfo.setForeground(new Color(200, 200, 200));
        dialog.add(lblInfo);

        dialog.setVisible(true);
    }

    private JButton criarBotaoAjuda(String texto, int x, int y, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 240, 145);
        btn.setFont(new Font("Verdana", Font.BOLD, 22));
        btn.setBackground(ativo ? Color.WHITE : new Color(100, 100, 100));
        btn.setForeground(new Color(47, 76, 113));
        btn.setFocusPainted(false);
        btn.setEnabled(ativo);
        btn.setCursor(ativo ? new Cursor(Cursor.HAND_CURSOR) : null);
        return btn;
    }

    private void usar5050() {
        if (ajuda5050Usada)
            return;
        ajuda5050Usada = true;

        Questao q = questoes.get(indiceQuestaoAtual);

        if (q.getTipo() == TipoQuestao.MULTIPLA_ESCOLHA) {
            List<Integer> indicesIncorretos = new ArrayList<>();
            List<Alternativa> alternativas = q.getAlternativas();

            for (int i = 0; i < alternativas.size(); i++) {
                if (!alternativas.get(i).isECorreta()) {
                    indicesIncorretos.add(i);
                }
            }

            Collections.shuffle(indicesIncorretos);

            int removidas = 0;
            for (int i = 0; i < indicesIncorretos.size() && removidas < 2; i++) {
                int idx = indicesIncorretos.get(i);
                if (idx < botoesAlternativas.size()) {
                    BotaoAlternativa btn = botoesAlternativas.get(idx);
                    btn.setEnabled(false);
                    btn.setAlpha(0.0f);
                    btn.setVisible(false);
                    removidas++;
                }
            }
        } else if (q.getTipo() == TipoQuestao.ASSOCIACAO) {
            List<String> idsDisponiveis = new ArrayList<>();
            for (BotaoAssociacao btn : botoesEsq) {
                if (!btn.isResolvido()) {
                    idsDisponiveis.add(btn.getIdPar());
                }
            }

            Collections.shuffle(idsDisponiveis);
            int resolved = 0;
            for (int i = 0; i < idsDisponiveis.size() && resolved < 2; i++) {
                String id = idsDisponiveis.get(i);

                BotaoAssociacao bEsq = botoesEsq.stream().filter(b -> b.getIdPar().equals(id)).findFirst().orElse(null);
                BotaoAssociacao bDir = botoesDir.stream().filter(b -> b.getIdPar().equals(id)).findFirst().orElse(null);

                if (bEsq != null && bDir != null) {
                    bEsq.setResolvido(true);
                    bDir.setResolvido(true);
                    bEsq.setBackground(new Color(46, 204, 113));
                    bDir.setBackground(new Color(46, 204, 113));
                    paresResolvidos++;
                    resolved++;
                }
            }

            if (paresResolvidos == 4) {
                registrarRespostaAssociacao(true);

                Timer t = new Timer(800, e -> {
                    indiceQuestaoAtual++;
                    carregarQuestaoAtual();
                });

                t.setRepeats(false);
                t.start();
            }
        }

        if (painelConteudo != null) {
            painelConteudo.revalidate();
            painelConteudo.repaint();
        }
    }

    private void usarChanceExtra() {
        chanceExtraUsada = true;
        chanceExtraAtiva = true;
        JOptionPane.showMessageDialog(this, "Ajuda Ativada: Se errar agora, você terá uma segunda chance!");
    }

    private void usarPular() {
        pularUsada = true;
        indiceQuestaoAtual++;
        carregarQuestaoAtual();
    }

    private void registrarResposta(Questao questao, Alternativa alternativaEscolhida) {
        if (partida == null || questao == null || alternativaEscolhida == null) {
            return;
        }

        Resposta resposta = new Resposta();
        resposta.setQuestao(questao);
        resposta.setAlternativaEscolhida(alternativaEscolhida);

        partida.adicionarResposta(resposta);
    }

    private void registrarRespostaAssociacao(boolean correta) {
        Questao questaoAtual = questoes.get(indiceQuestaoAtual);

        Alternativa alternativaResultado = new Alternativa(
                0,
                correta ? "Associação concluída" : "Associação incorreta",
                correta);

        registrarResposta(questaoAtual, alternativaResultado);
    }

    private void salvarPartidaNoHistoricoAluno() {
        if (partida == null || partida.getAluno() == null) {
            return;
        }

        try {
            if (partida.getAluno().getHistoricoPartidas() == null
                    || !partida.getAluno().getHistoricoPartidas().contains(partida)) {
                partida.getAluno().adicionarPartida(partida);
            }
        } catch (Exception erro) {
            System.out.println("Não foi possível salvar a partida no histórico do aluno: " + erro.getMessage());
        }
    }

    private void processarRespostaAlternativa(Alternativa escolhida, BotaoAlternativa btn) {
        Questao questaoAtual = questoes.get(indiceQuestaoAtual);

        if (escolhida.isECorreta()) {
            registrarResposta(questaoAtual, escolhida);

            btn.setBackground(new Color(46, 204, 113));

            JOptionPane.showMessageDialog(
                    this,
                    "Resposta Correta! +10 pontos.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            indiceQuestaoAtual++;
            carregarQuestaoAtual();

        } else {
            if (chanceExtraAtiva) {
                btn.setEnabled(false);
                btn.setBackground(new Color(231, 76, 60));
                chanceExtraAtiva = false;

                JOptionPane.showMessageDialog(
                        this,
                        "Ops! Resposta errada, mas você tem sua Chance Extra. Tente de novo!",
                        "Segunda Chance",
                        JOptionPane.WARNING_MESSAGE);

            } else {
                registrarResposta(questaoAtual, escolhida);

                btn.setBackground(new Color(231, 76, 60));

                JOptionPane.showMessageDialog(
                        this,
                        "Incorreto!",
                        "Fim de Turno",
                        JOptionPane.ERROR_MESSAGE);

                indiceQuestaoAtual++;
                carregarQuestaoAtual();
            }
        }
    }

    private void confirmarSaida() {
        int opt = JOptionPane.showConfirmDialog(this, "Sair agora interromperá sua partida. Confirmar?", "Sair do Jogo",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            new telas.aluno.TelaMenuAluno(this.partida.getAluno()).setVisible(true);
            dispose();
        }
    }

    private void finalizarPartida() {
        partida.finalizar();

        System.out.println("Aluno da partida: " + partida.getAluno().getNome());
        System.out.println("ID do aluno na partida: " + partida.getAluno().getId());

        try {
            PartidaDAO partidaDAO = new PartidaDAO();
            partidaDAO.salvarPartidaCompleta(partida);
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "A partida foi finalizada, mas não foi possível salvar o desempenho: " + erro.getMessage(),
                    "Erro ao salvar desempenho",
                    JOptionPane.ERROR_MESSAGE);
        }

        salvarPartidaNoHistoricoAluno();

        JOptionPane.showMessageDialog(
                this,
                "Desafio Concluído!\nSua Pontuação Final: " + partida.getPontuacao(),
                "Fim de Jogo",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();

        if (partida != null && partida.getAluno() != null) {
            new telas.aluno.TelaMenuAluno(partida.getAluno()).setVisible(true);
        } else {
            new telas.aluno.TelaMenuAluno().setVisible(true);
        }
    }

    private static class PainelArredondado extends JPanel {
        private int raio;

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
        }
    }

    private static class BotaoAlternativa extends JButton {
        private float alpha = 1.0f;

        public BotaoAlternativa(String texto) {
            super(texto);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Verdana", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setBackground(new Color(130, 150, 220));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        }

        public void setAlpha(float a) {
            this.alpha = a;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private static class BotaoAssociacao extends JButton {
        private boolean resolvido = false;
        private boolean selecionado = false;
        private String idPar;

        public BotaoAssociacao(String texto, String caminhoImg, boolean comImagem) {
    setLayout(new BorderLayout());
    setBackground(new Color(130, 150, 220));
    setFocusPainted(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));

    boolean imagemCarregada = false;

    if (comImagem && caminhoImg != null && !caminhoImg.isBlank()) {
        try {
            File arquivoImagem = new File(caminhoImg);

            if (!arquivoImagem.exists()) {
                arquivoImagem = new File(System.getProperty("user.dir"), caminhoImg);
            }

            Image imagemOriginal = ImageIO.read(arquivoImagem);

            if (imagemOriginal != null) {
                Image imagemRedimensionada = imagemOriginal.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                JLabel labelImagem = new JLabel(new ImageIcon(imagemRedimensionada), SwingConstants.CENTER);
                add(labelImagem, BorderLayout.CENTER);
                imagemCarregada = true;
            }
        } catch (Exception erro) {
            System.out.println("Erro ao carregar imagem da associação: " + caminhoImg);
            System.out.println(erro.getMessage());
        }
    }

    String textoFinal = texto;

    if (comImagem && !imagemCarregada) {
        textoFinal = "[img]";
    }

    JLabel lbl = new JLabel("<html><center>" + textoFinal + "</center></html>", SwingConstants.CENTER);
    lbl.setFont(new Font("Verdana", Font.BOLD, 14));
    lbl.setForeground(Color.WHITE);
    add(lbl, comImagem && imagemCarregada ? BorderLayout.SOUTH : BorderLayout.CENTER);
}

        public void setResolvido(boolean v) {
            this.resolvido = v;
            setEnabled(!v);
        }

        public boolean isResolvido() {
            return resolvido;
        }

        public void setSelecionado(boolean v) {
            this.selecionado = v;
            repaint();
        }

        public void setIdPar(String id) {
            this.idPar = id;
        }

        public String getIdPar() {
            return idPar;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            if (selecionado)
                g2.setColor(getBackground().darker());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            if (selecionado) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 18, 18);
            }
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private static class PainelFundo extends JPanel {
        private Image img;

        public PainelFundo() {
            try {
                img = new ImageIcon("imagens/Menu.png").getImage();
            } catch (Exception e) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                double esc = Math.max((double) getWidth() / img.getWidth(null),
                        (double) getHeight() / img.getHeight(null));
                int nw = (int) (img.getWidth(null) * esc), nh = (int) (img.getHeight(null) * esc);
                g.drawImage(img, (getWidth() - nw) / 2, (getHeight() - nh) / 2, nw, nh, null);
            }
        }
    }

    private Partida partidaComAlunoLogado(Partida partidaRecebida) {
        if (partidaRecebida != null
                && partidaRecebida.getAluno() != null
                && partidaRecebida.getAluno().getId() > 0) {
            return partidaRecebida;
        }

        if (SessaoUsuario.getUsuarioLogado() instanceof Aluno alunoLogado) {
            return new Partida(alunoLogado);
        }

        throw new IllegalStateException("Não foi possível iniciar a partida: aluno logado inválido.");
    }
}