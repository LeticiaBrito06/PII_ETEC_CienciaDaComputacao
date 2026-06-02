package model;

public class Resposta {

    private int id;
    private Questao questao;
    private Alternativa alternativaEscolhida;
    private boolean correta;
    private int tempoResposta;

    public Resposta() {}

    public Resposta(int id, Questao questao, Alternativa alternativaEscolhida, int tempoResposta) {
        this.id = id;
        this.questao = questao;
        this.alternativaEscolhida = alternativaEscolhida;
        this.tempoResposta = tempoResposta;
        this.correta = validar();
    }

    public boolean validar() {
        if (alternativaEscolhida == null || questao == null) {
            return false;
        }
        return alternativaEscolhida.isECorreta();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }

    public Alternativa getAlternativaEscolhida() {
        return alternativaEscolhida;
    }

    public void setAlternativaEscolhida(Alternativa alternativaEscolhida) {
        this.alternativaEscolhida = alternativaEscolhida;
        this.correta = validar();
    }

    public boolean isCorreta() {
        return correta;
    }

    public int getTempoResposta() {
        return tempoResposta;
    }

    public void setTempoResposta(int tempoResposta) {
        this.tempoResposta = tempoResposta;
    }

    @Override
    public String toString() {
        return "Resposta{" +
                "id=" + id +
                ", questaoId=" + (questao != null ? questao.getId() : "null") +
                ", correta=" + correta +
                ", tempoResposta=" + tempoResposta + "s" +
                '}';
    }
}