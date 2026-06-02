package aplicativo;

import javax.swing.SwingUtilities;

import telas.autenticacao.TelaLogin;

public class LabQuest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
        });
    }
}