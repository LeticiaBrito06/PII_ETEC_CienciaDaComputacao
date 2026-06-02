package utilitarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.Questao;
import model.tipos.NivelDificuldade;
public class SorteioQuestao{
    private SorteioQuestao(){}

    public static List<Questao> embaralhar(List<Questao> banco) {
        if (banco == null || banco.isEmpty()) {
            return new ArrayList<>();
        }
        List <Questao> copia = new ArrayList<>(banco);
        Collections.shuffle(copia);
        return copia;
    }

    public static Questao sortearProxima(List<Questao>banco, List <Questao> jaRespondidas){
        if (banco == null || banco.isEmpty()){
            return null;
        }

        List<Questao> disponiveis = banco.stream().filter(q -> !jaRespondidas.contains(q)).collect(Collectors.toList());
        if (disponiveis.isEmpty()){
            return null;
        }
        Collections.shuffle(disponiveis);
        return disponiveis.get(0);
    }

    public static Questao sortearPorNivel (List<Questao> banco, List<Questao> jaRespondidas, NivelDificuldade nivel){
        if (banco == null || banco.isEmpty() || nivel == null){
            return null;
        }

        List<Questao> disponiveis = banco.stream().filter(q -> q.getNivelDificuldade() == nivel).filter(q -> !jaRespondidas.contains(q)).collect(Collectors.toList());
        if (disponiveis.isEmpty()){
            return null;
        }
        Collections.shuffle(disponiveis);
        return disponiveis.get(0);
    }

    public static List<Questao> sortearConjunto(List<Questao> banco, int quantidade){
        if (banco == null || banco.isEmpty() || quantidade <= 0){
            return new ArrayList<>();
        }
        List<Questao> embaralhadas = embaralhar(banco);

        return embaralhadas.subList(0, Math.min(quantidade, embaralhadas.size()));
    }

    public static boolean existemQuestoesDisponiveis(List<Questao> banco, List<Questao> jaRespondidas){
        if (banco == null || banco.isEmpty()){
            return false;
        }
        return banco.stream().anyMatch(q -> !jaRespondidas.contains(q));
    }
}