package src.telas.autenticarUsuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class TelaCadastro extends JFrame {
    private JTextField campoNome;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoCadastrar;

    public TelaCadastro(){
        configurarTela();
        montarTela();
    }

    private void configurarTela(){
        setTitle("LABTECH");
        setSize(960, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void montarTela(){
        PainelFundo painelFundo = new Painelfundo();
        painelFundo.setLayout(null);
        setContentPane(painelFundo);

        //Painel de Cadastro
        PainelCadastro formularioCadastro = new PainelCadastro(30);
        formularioCadastro.setLayout(null);
        formularioCadastro.setBounds(271,135, 410, 480);
        formularioCadastro.setBackground(Color.WHITE);
        painelFundo.add(formularioCadastro);

        JLabel tituloCadastro = new JLabel("CADASTRO", SwingConstants.CENTER);
        tituloCadastro.setBounds(35,30,340,80);
        tituloCadastro.setFont(new Font("Verdana", Font.BOLD, 62));
        tituloCadastro.setForeground(new Color(47, 67, 113));
        formularioCadastro.add(tituloCadastro);

        JLabel textoEmail = new JLabel("E-mail institucional:");
        textoEmail.setBounds(45, 125, 250, 25);
        textoEmail.setFont(new Font("Verdana", Font.BOLD, 15));
        textoEmail.setForeground(new Color(47, 76, 113));
        formularioCadastro.add(textoEmail);

        campoEmail = new CampoEmailTexto(15);
        
    }
    
}
