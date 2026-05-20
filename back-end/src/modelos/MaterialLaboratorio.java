package modelos;

import java.util.ArrayList;
import java.util.List;

public class MaterialLaboratorio {

    private int id;
    private String nome;
    private String descricao;
    private String imagem;
    private String categoria;
    private String funcao;

    // Sistemas experimentais nos quais este material é utilizado
    private List<SistemaExperimental> sistemasAssociados;

    // Construtores
    public MaterialLaboratorio() {
        this.sistemasAssociados = new ArrayList<>();
    }

    public MaterialLaboratorio(int id, String nome, String descricao, String imagem, String categoria, String funcao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.categoria = categoria;
        this.funcao = funcao;
        this.sistemasAssociados = new ArrayList<>();
    }

    // Métodos
    // Retorna a lista de sistemas experimentais associados a este material
    public List<SistemaExperimental> getSistemasAssociados() {
        return new ArrayList<>(sistemasAssociados);
    }

    // Associa este material a um sistema experimental
    public void associarSistema(SistemaExperimental sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("Sistema experimental não pode ser nulo.");
        }
        this.sistemasAssociados.add(sistema);
    }

    // Getters e Setters
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public void setSistemasAssociados(List<SistemaExperimental> sistemasAssociados) {
        this.sistemasAssociados = sistemasAssociados;
    }

    @Override
    public String toString() {
        return "MaterialLaboratorio{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", funcao='" + funcao + '\'' +
                '}';
    }
}