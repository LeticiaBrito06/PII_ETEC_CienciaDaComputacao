package utilitarios;
import model.tipos.NivelDificuldade;

public class AumentaDificuldade {
    private static final int Limite_Aumentar = 3;
    private static final int Limite_Diminuir = 3;

    private AumentaDificuldade(){}

    public static NivelDificuldade calcularProximoNivel(NivelDificuldade nivelAtual, int acertosConsecutivos, int errosConsecutivos) {
        if (nivelAtual == null){
            return NivelDificuldade.FACIL;
        }
        if (acertosConsecutivos >= Limite_Aumentar) {
            return subirNivel(nivelAtual);
        } else if (errosConsecutivos >= Limite_Diminuir) {
            return descerNivel(nivelAtual);
        } else {
            return nivelAtual;
        }
    }

    public static NivelDificuldade subirNivel(NivelDificuldade nivel) {
        return switch (nivel) {
            case FACIL -> NivelDificuldade.MEDIO;
            case MEDIO -> NivelDificuldade.DIFICIL;
            case DIFICIL -> NivelDificuldade.DIFICIL;
        };
    }
    public static NivelDificuldade descerNivel(NivelDificuldade nivel){
        return switch (nivel) {
            case FACIL -> NivelDificuldade.FACIL;
            case MEDIO -> NivelDificuldade.FACIL;
            case DIFICIL -> NivelDificuldade.MEDIO;
        };
    }

    public static boolean podeAumentar(NivelDificuldade nivel){
        return nivel != NivelDificuldade.DIFICIL;
    }

    public static boolean podeDiminuir(NivelDificuldade nivel){
        return nivel != NivelDificuldade.FACIL;
    }

    public static NivelDificuldade getNivelInicial(){
        return NivelDificuldade.FACIL;
    }
}