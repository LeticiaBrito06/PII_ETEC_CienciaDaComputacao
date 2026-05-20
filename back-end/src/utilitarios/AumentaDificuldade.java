package utilitarios;
import modelos.tipos.NivelDificuldade;

public class AumentaDificuldade {
    private static final int LIMITE_AUMENTAR = 3;
    private static final int LIMITE_DIMINUIR = 3;

    private AumentaDificuldade(){}

    //Metodos
    //Calcular o próximo nível de dificuldade
    public static NivelDificuldade calcularProximoNivel(NivelDificuldade nivelAtual, int acertosConsecutivos, int errosConsecutivos) {
        if (nivelAtual == null){
            return NivelDificuldade.FACIL;
        }
        if (acertosConsecutivos >= LIMITE_AUMENTAR) {
            return subirNivel(nivelAtual);
        } else if (errosConsecutivos >= LIMITE_DIMINUIR) {
            return descerNivel(nivelAtual);
        } else {
            return nivelAtual;
        }
    }

    //Sobe um nivel de dificuldade
    public static NivelDificuldade subirNivel(NivelDificuldade nivel) {
        return switch (nivel) {
            case FACIL -> NivelDificuldade.MEDIO;
            case MEDIO -> NivelDificuldade.DIFICIL;
            case DIFICIL -> NivelDificuldade.DIFICIL;
        };
    }
    //Diminui um nivel de dificuldade
    public static NivelDificuldade descerNivel(NivelDificuldade nivel){
        return switch (nivel) {
            case FACIL -> NivelDificuldade.FACIL;
            case MEDIO -> NivelDificuldade.FACIL;
            case DIFICIL -> NivelDificuldade.MEDIO;
        };
    }

    //Verifica se pode aumentar a dificuldade
    public static boolean podeAumentar(NivelDificuldade nivel){
        return nivel != NivelDificuldade.DIFICIL;
    }

    //Verifica se pode diminuir a dificuldade
    public static boolean podeDiminuir(NivelDificuldade nivel){
        return nivel != NivelDificuldade.FACIL;
    }

    //Reinicia a dificuldade para o nível inicial
    public static NivelDificuldade getNivelInicial(){
        return NivelDificuldade.FACIL;
    }
}