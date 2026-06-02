package model;
import model.tipos.NivelDificuldade;

public class Desempenho {

    private int id;
    private int totalPartidas;
    private int totalAcertos;
    private int totalErros;
    private double percentualAcerto;
    private NivelDificuldade nivelMedio;

    public Desempenho() {}

    public Desempenho(int id, int totalPartidas, int totalAcertos, int totalErros) {
        this.id = id;
        this.totalPartidas = totalPartidas;
        this.totalAcertos = totalAcertos;
        this.totalErros = totalErros;
        calcularPercentualAcerto();
        calcularNivelMedio();
    }

    public void calcularPercentualAcerto() {
        int totalRespostas = totalAcertos + totalErros;
        if (totalRespostas == 0) {
            this.percentualAcerto = 0.0;
        } else {
            this.percentualAcerto = ((double) totalAcertos / totalRespostas) * 100.0;
        }
    }

    public void calcularNivelMedio() {
        if (percentualAcerto >= 70.0) {
            this.nivelMedio = NivelDificuldade.DIFICIL;
        } else if (percentualAcerto >= 40.0) {
            this.nivelMedio = NivelDificuldade.MEDIO;
        } else {
            this.nivelMedio = NivelDificuldade.FACIL;
        }
    }

    public Relatorio gerarRelatorio() {
        Relatorio relatorio = new Relatorio();
        relatorio.setDesempenho(this);
        return relatorio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalPartidas() {
        return totalPartidas;
    }

    public void setTotalPartidas(int totalPartidas) {
        this.totalPartidas = totalPartidas;
    }

    public int getTotalAcertos() {
        return totalAcertos;
    }

    public void setTotalAcertos(int totalAcertos) {
        this.totalAcertos = totalAcertos;
        calcularPercentualAcerto();
        calcularNivelMedio();
    }

    public int getTotalErros() {
        return totalErros;
    }

    public void setTotalErros(int totalErros) {
        this.totalErros = totalErros;
        calcularPercentualAcerto();
        calcularNivelMedio();
    }

    public double getPercentualAcerto() {
        return percentualAcerto;
    }

    public NivelDificuldade getNivelMedio() {
        return nivelMedio;
    }

    public void setNivelMedio(NivelDificuldade nivelMedio) {
        this.nivelMedio = nivelMedio;
    }

    @Override
    public String toString() {
        return String.format(
                "Desempenho{totalPartidas=%d, acertos=%d, erros=%d, percentual=%.1f%%, nivel=%s}",
                totalPartidas, totalAcertos, totalErros, percentualAcerto, nivelMedio
        );
    }
}