package model;

import java.util.ArrayList;
import java.util.List;

import model.tipos.TipoUsuario;

public class Aluno extends Usuario {

    private List<Partida> historicoPartidas;

    public Aluno() {
        super();
        this.historicoPartidas = new ArrayList<>();
        setTipo(TipoUsuario.ALUNO);
    }

    public Aluno(String nome, String email, String senha) {
        super(nome, email, senha, TipoUsuario.ALUNO);
        this.historicoPartidas = new ArrayList<>();
    }

    public Aluno(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoUsuario.ALUNO);
        this.historicoPartidas = new ArrayList<>();
    }

    public Partida jogar() {
        Partida novaPartida = new Partida(this);
        historicoPartidas.add(novaPartida);
        return novaPartida;
    }

    public void adicionarPartida(Partida partida) {
        if (partida != null) {
            this.historicoPartidas.add(partida);
        }
    }

    public List<Partida> verHistorico() {
        return new ArrayList<>(historicoPartidas);
    }

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

    public List<Partida> getHistoricoPartidas() {
        return new ArrayList<>(historicoPartidas);
    }

    public void setHistoricoPartidas(List<Partida> historicoPartidas) {
        this.historicoPartidas = historicoPartidas;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
