package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.tipos.NivelDificuldade;

public class Partida {
    private int id;
    private Aluno aluno;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private int pontuacao;
    private NivelDificuldade nivelAtual;
    private boolean finalizada;
    private List<Resposta> respostas;

    public Partida() {
        this.respostas = new ArrayList<>();
        this.finalizada = false;
        this.nivelAtual = NivelDificuldade.FACIL;
        this.dataHoraInicio = LocalDateTime.now();
    }

    public Partida(Aluno aluno) {
        this();
        this.aluno = aluno;
    }

    public Partida(int id, Aluno aluno) {
        this(aluno);
        this.id = id;
    }

    public void iniciar() {
        this.dataHoraInicio = LocalDateTime.now();
        this.finalizada = false;
        System.out.println("Partida iniciada em: " + dataHoraInicio);
    }

    public void finalizar() {
        this.dataHoraFim = LocalDateTime.now();
        this.finalizada = true;
        this.pontuacao = calcularPontuacao();
        System.out.println("Partida finalizada. Pontuação: " + pontuacao);
    }

    public int calcularPontuacao() {
        int total = 0;
        for (Resposta resposta : respostas) {
            if (resposta.isCorreta()) {
                NivelDificuldade nivel = resposta.getQuestao().getNivelDificuldade();
                switch (nivel) {
                    case FACIL -> total += 10;
                    case MEDIO -> total += 20;
                    case DIFICIL -> total += 30;
                }
            }
        }
        return total;
    }

    public void adicionarResposta(Resposta resposta) {
        if (resposta == null) {
            throw new IllegalArgumentException("Resposta não pode ser nula.");
        }
        respostas.add(resposta);
    }

    public List<Resposta> getRespostas() {
        return new ArrayList<>(respostas);
    }

    public int getTotalAcertos() {
        int acertos = 0;
        for (Resposta r : respostas) {
            if (r.isCorreta()) acertos++;
        }
        return acertos;
    }

    public int getTotalErros() {
        return respostas.size() - getTotalAcertos();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public NivelDificuldade getNivelAtual() {
        return nivelAtual;
    }

    public void setNivelAtual(NivelDificuldade nivelAtual) {
        this.nivelAtual = nivelAtual;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    @Override
    public String toString() {
        return "Partida{" +
                "id=" + id +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", pontuacao=" + pontuacao +
                ", nivelAtual=" + nivelAtual +
                ", finalizada=" + finalizada +
                ", totalRespostas=" + respostas.size() +
                '}';
    }
}