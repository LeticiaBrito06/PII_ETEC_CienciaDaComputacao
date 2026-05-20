package modelos;
import java.util.ArrayList;
import java.util.List;

import modelos.tipos.TipoUsuario;
 
public class Professor extends Usuario {
 
    // Lista de alunos que o professor acompanha
    private List<Aluno> alunos;

    // Construtores
    public Professor() {
        super();
        this.alunos = new ArrayList<>();
        setTipo(TipoUsuario.PROFESSOR);
    }
 
    public Professor(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoUsuario.PROFESSOR);
        this.alunos = new ArrayList<>();
    }

    // Métodos de comportamento — Gerenciamento de questões

    //Cadastra uma nova questão no banco de perguntas.
    public void cadastrarQuestao(Questao questao, List<Questao> listaBanco) {
        if (questao == null) {
            throw new IllegalArgumentException("A questão não pode ser nula.");
        }
        listaBanco.add(questao);
        System.out.println("Questão adicionada: " + questao.getEnunciado());
    }
 
    //Edita uma questão existente no banco de perguntas.
    public void editarQuestao(int indice, Questao nova, List<Questao> listaBanco) {
        if (indice < 0 || indice >= listaBanco.size()) {
            throw new IndexOutOfBoundsException("Índice de questão inválido.");
        }
        listaBanco.set(indice, nova);
        System.out.println("Questão editada no índice " + indice);
    }
 
    //Remove uma questão do banco de perguntas.
    public void removerQuestao(int indice, List<Questao> listaBanco) {
        if (indice < 0 || indice >= listaBanco.size()) {
            throw new IndexOutOfBoundsException("Índice de questão inválido.");
        }
        Questao removida = listaBanco.remove(indice);
        System.out.println("Questão removida: " + removida.getEnunciado());
    }
 
    // Métodos de comportamento — Acompanhamento de alunos
    //Gera um relatório de desempenho para um aluno específico.
    public Relatorio verRelatorio(Aluno aluno) {
        Desempenho desempenho = aluno.verDesempenho();
        Relatorio relatorio = new Relatorio();
        relatorio.setAluno(aluno);
        relatorio.setDesempenho(desempenho);
        return relatorio;
    }
 
    // Retorna a lista de alunos que o professor acompanha.
    public List<Aluno> gerenciarAlunos() {
        return new ArrayList<>(alunos);
    }

    // Getters e Setters
    public List<Aluno> getAlunos() {
        return new ArrayList<>(alunos);
    }
 
    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }
 
    public void adicionarAluno(Aluno aluno) {
        this.alunos.add(aluno);
    }

    @Override
    public String toString() {
        return "Professor{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", totalAlunos=" + alunos.size() +
                '}';
    }
}
