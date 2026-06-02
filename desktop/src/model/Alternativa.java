package model;

public class Alternativa {
 
    private int id;
    private String texto;
    private String imagem;
    private boolean estaCorreta;
    private boolean errada;

    public Alternativa() {}

    public Alternativa(int id, String texto, boolean estaCorreta) {
        this.id = id;
        this.texto = texto;
        this.estaCorreta = estaCorreta;
        this.errada = false;
    }

    public Alternativa(int id, String texto, String imagem, boolean estaCorreta, boolean errada) {
        this.id = id;
        this.texto = texto;
        this.imagem = imagem;
        this.estaCorreta = estaCorreta;
        this.errada = errada;
    }

    public boolean validar() {
        return estaCorreta;
    }

    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getTexto() {
        return texto;
    }
 
    public void setTexto(String texto) {
        this.texto = texto;
    }
 
    public String getImagem() {
        return imagem;
    }
 
    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
 
    public boolean isECorreta() {
        return estaCorreta;
    }
 
    public void setECorreta(boolean estaCorreta) {
        this.estaCorreta = estaCorreta;
    }
 
    public boolean isErrada() {
        return errada;
    }
 
    public void setErrada(boolean errada) {
        this.errada = errada;
    }

    @Override
    public String toString() {
        return "Alternativa{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", estaCorreta=" + estaCorreta +
                ", errada=" + errada +
                '}';
    }
}