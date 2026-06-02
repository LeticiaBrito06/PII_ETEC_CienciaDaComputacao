package model;

import java.util.ArrayList;
import java.util.List;

import model.tipos.TipoUsuario;

public class Professor extends Usuario {

    private List<Aluno> alunos;

    public Professor() {
        super();
        this.alunos = new ArrayList<>();
        setTipo(TipoUsuario.PROFESSOR);
    }

    public Professor(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoUsuario.PROFESSOR);
        this.alunos = new ArrayList<>();
    }

    public void cadastrarQuestao(Questao questao, List<Questao> listaBanco) {
        if (questao == null) {
            throw new IllegalArgumentException("A questão não pode ser nula.");
        }
        listaBanco.add(questao);
        System.out.println("Questão adicionada: " + questao.getEnunciado());
    }

    public void editarQuestao(int indice, Questao nova, List<Questao> listaBanco) {
        if (indice < 0 || indice >= listaBanco.size()) {
            throw new IndexOutOfBoundsException("Índice de questão inválido.");
        }
        listaBanco.set(indice, nova);
        System.out.println("Questão editada no índice " + indice);
    }

    public void removerQuestao(int indice, List<Questao> listaBanco) {
        if (indice < 0 || indice >= listaBanco.size()) {
            throw new IndexOutOfBoundsException("Índice de questão inválido.");
        }
        Questao removida = listaBanco.remove(indice);
        System.out.println("Questão removida: " + removida.getEnunciado());
    }

    public Relatorio verRelatorio(Aluno aluno) {
        Desempenho desempenho = aluno.verDesempenho();
        Relatorio relatorio = new Relatorio();
        relatorio.setAluno(aluno);
        relatorio.setDesempenho(desempenho);
        return relatorio;
    }

    public List<Aluno> gerenciarAlunos() {
        return new ArrayList<>(alunos);
    }

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
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", totalAlunos=" + alunos.size() +
                '}';
    }
}
