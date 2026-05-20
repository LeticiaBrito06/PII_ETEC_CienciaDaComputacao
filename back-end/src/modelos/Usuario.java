package modelos;
import modelos.tipos.TipoUsuario;
 
public abstract class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
 
    // Construtores
    public Usuario() {}
 
    public Usuario(int id, String nome, String email, String senha, TipoUsuario tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Métodos de comportamento
    //Verifica a senha
    public boolean autenticar(String senhaInformada) {
        if (senhaInformada == null || senhaInformada.isBlank()) {
            return false;
        }
        return this.senha.equals(senhaInformada);
    }
 
    //Logout
    public void sair() {
        System.out.println("Usuário " + nome + " saiu do sistema.");
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getNome() {
        return nome;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getSenha() {
        return senha;
    }
 
    public void setSenha(String senha) {
        this.senha = senha;
    }
 
    public TipoUsuario getTipo() {
        return tipo;
    }
 
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
 
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}