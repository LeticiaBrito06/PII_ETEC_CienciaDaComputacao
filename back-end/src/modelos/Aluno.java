package modelos;
import java.util.ArrayList;
import java.util.List;
import modelos.tipos.TipoUsuario;

public class Aluno extends Usuario {
 
    private String turma;
    private String ra;
 
    // Histórico de partidas jogadas pelo aluno
    private List<Partida> historicoPartidas;

    // Construtores
    public Aluno() {
        super();
        this.historicoPartidas = new ArrayList<>();
    }
 
    public Aluno(int id, String nome, String email, String senha, String turma, String ra) {
        super(id, nome, email, senha, TipoUsuario.ALUNO);
        this.turma = turma;
        this.ra = ra;
        this.historicoPartidas = new ArrayList<>();
    }

    // Métodos de comportamento
    //Inicia uma nova partida para o aluno.
    public Partida jogar() {
        Partida novaPartida = new Partida(this);
        historicoPartidas.add(novaPartida);
        return novaPartida;
    }
 
    //Retorna o histórico de partidas do aluno.
    public List<Partida> verHistorico() {
        return new ArrayList<>(historicoPartidas);
    }
 
    //Calcula o desempenho geral do aluno com base no histórico de partidas.
    public Desempenho verDesempenho() {
        int totalAcertos = 0;
        int totalErros = 0;
 
        for (Partida partida : historicoPartidas) {
            totalAcertos += partida.getTotalAcertos();
            totalErros += partida.getTotalErros();
        }
 
        Desempenho desempenho = new Desempenho();
        desempenho.setTotalPartidas(historicoPartidas.size());
        desempenho.setTotalAcertos(totalAcertos);
        desempenho.setTotalErros(totalErros);
        desempenho.calcularPercentualAcerto();
        desempenho.calcularNivelMedio();
        return desempenho;
    }

    // Getters e Setters
    public String getTurma() {
        return turma;
    }
 
    public void setTurma(String turma) {
        this.turma = turma;
    }
 
    public String getRa() {
        return ra;
    }
 
    public void setRa(String ra) {
        this.ra = ra;
    }
 
    public List<Partida> getHistoricoPartidas() {
        return new ArrayList<>(historicoPartidas);
    }
 
    public void setHistoricoPartidas(List<Partida> historicoPartidas) {
        this.historicoPartidas = historicoPartidas;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", turma='" + turma + '\'' +
                ", ra='" + ra + '\'' +
                '}';
    }
}
