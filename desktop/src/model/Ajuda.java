package model;
import model.tipos.TipoAjuda;

public class Ajuda {
    private int id;
    private TipoAjuda tipoAjuda;
    private int quantidadeDisponivel;

    public Ajuda() {}

    public Ajuda(int id, TipoAjuda tipoAjuda, int quantidadeDisponivel) {
        this.id = id;
        this.tipoAjuda = tipoAjuda;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
    public void utilizarNaPartida(Partida partida) {
        if (!verificarDisponibilidade()) {
            throw new IllegalStateException("Ajuda do tipo " + tipoAjuda + " não está mais disponível.");
        }
        this.quantidadeDisponivel--;
        System.out.println("Ajuda utilizada: " + tipoAjuda + " | Restantes: " + quantidadeDisponivel);
    }

    public boolean verificarDisponibilidade() {
        return quantidadeDisponivel > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoAjuda getTipoAjuda() {
        return tipoAjuda;
    }

    public void setTipoAjuda(TipoAjuda tipoAjuda) {
        this.tipoAjuda = tipoAjuda;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    @Override
    public String toString() {
        return "Ajuda{" +
                "id=" + id +
                ", tipoAjuda=" + tipoAjuda +
                ", quantidadeDisponivel=" + quantidadeDisponivel +
                '}';
    }
}