package model;
import java.util.ArrayList;
import java.util.List;

public class SistemaExperimental {

    private int id;
    private String nome;
    private String imagem;
    private String descricao;

    private List<MaterialLaboratorio> materiaisNecessarios;

    public SistemaExperimental() {
        this.materiaisNecessarios = new ArrayList<>();
    }

    public SistemaExperimental(int id, String nome, String imagem, String descricao) {
        this.id = id;
        this.nome = nome;
        this.imagem = imagem;
        this.descricao = descricao;
        this.materiaisNecessarios = new ArrayList<>();
    }

    public List<MaterialLaboratorio> getMateriaisNecessarios() {
        return new ArrayList<>(materiaisNecessarios);
    }

    public void adicionarMaterial(MaterialLaboratorio material) {
        if (material == null) {
            throw new IllegalArgumentException("Material não pode ser nulo.");
        }
        this.materiaisNecessarios.add(material);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setMateriaisNecessarios(List<MaterialLaboratorio> materiaisNecessarios) {
        this.materiaisNecessarios = materiaisNecessarios;
    }

    @Override
    public String toString() {
        return "SistemaExperimental{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", totalMateriais=" + materiaisNecessarios.size() +
                '}';
    }
}