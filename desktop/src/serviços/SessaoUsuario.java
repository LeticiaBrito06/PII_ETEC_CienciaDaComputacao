package serviços;

import model.Usuario;
import model.tipos.TipoUsuario;

public final class SessaoUsuario {

    private static Usuario usuarioLogado;

    private SessaoUsuario() {
    }

    public static void iniciarSessao(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        if (usuarioLogado == null) {
            throw new IllegalStateException("Nenhum usuário logado.");
        }

        return usuarioLogado;
    }

    public static int getIdUsuario() {
        return getUsuarioLogado().getId();
    }

    public static String getNomeUsuario() {
        return getUsuarioLogado().getNome();
    }

    public static String getEmailUsuario() {
        return getUsuarioLogado().getEmail();
    }

    public static TipoUsuario getTipoUsuario() {
        return getUsuarioLogado().getTipo();
    }

    public static boolean isAluno() {
        return getTipoUsuario() == TipoUsuario.ALUNO;
    }

    public static boolean isProfessor() {
        return getTipoUsuario() == TipoUsuario.PROFESSOR;
    }

    public static void encerrarSessao() {
        usuarioLogado = null;
    }
}