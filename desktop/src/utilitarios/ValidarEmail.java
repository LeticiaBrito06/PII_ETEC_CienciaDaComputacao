package utilitarios;
public class ValidarEmail{

    private static final String Dominio_Aluno = "@aluno.cps.sp.gov.br";
    private static final String Dominio_Professor = "@cps.sp.gov.br";

    private ValidarEmail(){}
    
    public static boolean EmailValido(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(Dominio_Aluno) || emailLower.endsWith(Dominio_Professor);
    }

    public static boolean isEmailAluno(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.trim().toLowerCase().endsWith(Dominio_Aluno);
    }

    public static boolean isEmailProfessor(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailLower = email.trim().toLowerCase();
        return emailLower.endsWith(Dominio_Professor) && !emailLower.endsWith(Dominio_Aluno);   
    }

    public static String getMensagemErro(String email) {
        if (email == null || email.isEmpty()) {
            return "O campo de e-mail não pode estar vazio.";
        }
        if (!EmailValido(email)) {
            return "E-mail inválido. Use seu e-mail institucional.";
        }
        return null;
    }

    public static String extrairNomeUsuario(String email){
        if (email == null || !email.contains("@")) {
            return "";
        }
        return email.trim().split("@")[0];
    }
}