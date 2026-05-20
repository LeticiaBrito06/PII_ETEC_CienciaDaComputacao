package utilitarios;
public class ValidarEmail{
    // Emails aceitos
    private static final String Dominio_Aluno = "@aluno.cps.sp.gov.br";
    private static final String Dominio_Professor = "@cps.sp.gov.br";

    private ValidarEmail(){}
    
    //Verificar se o e-mail é valido    
    public static boolean EmailValido(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(Dominio_Aluno) || emailLower.endsWith(Dominio_Professor);
    }


    // Verificar se o e-mail é de um aluno
    public static boolean isEmailAluno(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.trim().toLowerCase().endsWith(Dominio_Aluno);
    }

    // Verificar se o e-mail é de um professor
    public static boolean isEmailProfessor(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(Dominio_Professor) && !emailLower.endsWith(Dominio_Aluno);   
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