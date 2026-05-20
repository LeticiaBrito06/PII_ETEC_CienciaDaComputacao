package modelos;
import java.util.ArrayList;
import java.util.List;

import modelos.tipos.NivelDificuldade;
import modelos.tipos.TipoQuestao;

public class Questao {

    private int id;
    private String enunciado;
    private String imagemEnunciado;   // caminho ou URL da imagem do enunciado
    private TipoQuestao tipo;
    private NivelDificuldade nivelDificuldade;
    private String categoria;
    private List<Alternativa> alternativas;

    // Construtores
    public Questao() {
        this.alternativas = new ArrayList<>();
    }

    public Questao(int id, String enunciado, TipoQuestao tipo, NivelDificuldade nivelDificuldade, String categoria) {
        this.id = id;
        this.enunciado = enunciado;
        this.tipo = tipo;
        this.nivelDificuldade = nivelDificuldade;
        this.categoria = categoria;
        this.alternativas = new ArrayList<>();
    }

    // Métodos de comportamento

    // Retorna a lista de alternativas da questão.
    public List<Alternativa> getAlternativas() {
        return new ArrayList<>(alternativas);
    }

    // Adiciona uma alternativa à questão.
    public void adicionarAlternativa(Alternativa alternativa) {
        if (alternativa == null) {
            throw new IllegalArgumentException("Alternativa não pode ser nula.");
        }
        this.alternativas.add(alternativa);
    }

    // Retorna a alternativa correta da questão.
    public Alternativa getAlternativaCorreta() {
        for (Alternativa alt : alternativas) {
            if (alt.isECorreta()) {
                return alt;
            }
        }
        return null;
    }

    // Verifica se a questão está completa e válida para ser utilizada em uma partida.
    public boolean isValida() {
        return enunciado != null && !enunciado.isBlank() && tipo != null && nivelDificuldade != null && !alternativas.isEmpty() && getAlternativaCorreta() != null;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getImagemEnunciado() {
        return imagemEnunciado;
    }

    public void setImagemEnunciado(String imagemEnunciado) {
        this.imagemEnunciado = imagemEnunciado;
    }

    public TipoQuestao getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuestao tipo) {
        this.tipo = tipo;
    }

    public NivelDificuldade getNivelDificuldade() {
        return nivelDificuldade;
    }

    public void setNivelDificuldade(NivelDificuldade nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setAlternativas(List<Alternativa> alternativas) {
        this.alternativas = alternativas;
    }

    @Override
    public String toString() {
        return "Questao{" +
                "id=" + id +
                ", enunciado='" + enunciado + '\'' +
                ", tipo=" + tipo +
                ", nivelDificuldade=" + nivelDificuldade +
                ", categoria='" + categoria + '\'' +
                ", totalAlternativas=" + alternativas.size() +
                '}';
    }
}