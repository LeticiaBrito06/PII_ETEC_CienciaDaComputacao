package model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Relatorio {

    private int id;
    private LocalDateTime dataGeracao;
    private Aluno aluno;
    private Desempenho desempenho;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    public Relatorio() {
        this.dataGeracao = LocalDateTime.now();
    }

    public Relatorio(int id, Aluno aluno, Desempenho desempenho, LocalDate periodoInicio, LocalDate periodoFim) {
        this();
        this.id = id;
        this.aluno = aluno;
        this.desempenho = desempenho;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
    }

    public String exportar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório do aluno\n");
        sb.append("Gerado em: ").append(dataGeracao).append("\n");

        if (aluno != null) {
            sb.append("Aluno: ").append(aluno.getNome()).append("\n");
        }

        if (periodoInicio != null && periodoFim != null) {
            sb.append("Período: ").append(periodoInicio).append(" a ").append(periodoFim).append("\n");
        }

        if (desempenho != null) {
            sb.append("Total de Partidas: ").append(desempenho.getTotalPartidas()).append("\n");
            sb.append("Acertos: ").append(desempenho.getTotalAcertos()).append("\n");
            sb.append("Erros: ").append(desempenho.getTotalErros()).append("\n");
            sb.append(String.format("Percentual de Acerto: %.1f%%\n", desempenho.getPercentualAcerto()));
            sb.append("Nível Médio: ").append(desempenho.getNivelMedio()).append("\n");
        }
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Desempenho getDesempenho() {
        return desempenho;
    }

    public void setDesempenho(Desempenho desempenho) {
        this.desempenho = desempenho;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "id=" + id +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", dataGeracao=" + dataGeracao +
                '}';
    }
}