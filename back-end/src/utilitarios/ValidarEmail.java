package utilitarios;
public class ValidarEmail{
    // Emails aceitos
    private static final String DOMINIO_ALUNO = "@aluno.cps.sp.gov.br";
    private static final String DOMINIO_PROFESSOR = "@cps.sp.gov.br";

    private ValidarEmail(){}
    
    //Verificar se o e-mail é valido    
    public static boolean EmailValido(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(DOMINIO_ALUNO) || emailLower.endsWith(DOMINIO_PROFESSOR);
    }


    // Verificar se o e-mail é de um aluno
    public static boolean isEmailAluno(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.trim().toLowerCase().endsWith(DOMINIO_ALUNO);
    }

    // Verificar se o e-mail é de um professor
    public static boolean isEmailProfessor(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(DOMINIO_PROFESSOR) && !emailLower.endsWith(DOMINIO_ALUNO);   
    }

    // Retornar mensagem de erro para e-mail inválido
    public static String getMensagemErro(String email) {
        if (email == null || email.isEmpty()) {
            return "O campo de e-mail não pode estar vazio.";
        }
        if (!EmailValido(email)) {
            return "E-mail inválido. Use seu e-mail institucional.";
        }
        return null;
    }

    // Extrair o nome de usuário do e-mail
    public static String extrairNomeUsuario(String email){
        if (email == null || !email.contains("@")) {
            return "";
        }
        return email.trim().split("@")[0];
    }
}