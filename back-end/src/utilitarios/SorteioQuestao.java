package utilitarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import modelos.Questao;
import modelos.tipos.NivelDificuldade;
public class SorteioQuestao{
    private SorteioQuestao(){}

    //Metodos
    //Embaralha e retorna todas as questões de forma aleatoria + Não permite repetição
    public static List<Questao> embaralhar(List<Questao> banco) {
        if (banco == null || banco.isEmpty()) {
            return new ArrayList<>();
        }
        List <Questao> copia = new ArrayList<>(banco);
        Collections.shuffle(copia);
        return copia;
    }

    //Sorteia uma questão sem contar as já respondidas
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

    // Sorteia uma questão de acordo com o nível de dificuldade
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

    //Retorna um grupo de questões aleatórias para montar uma partida
    public static List<Questao> sortearConjunto(List<Questao> banco, int quantidade){
        if (banco == null || banco.isEmpty() || quantidade <= 0){
            return new ArrayList<>();
        }
        List<Questao> embaralhadas = embaralhar(banco);

        return embaralhadas.subList(0, Math.min(quantidade, embaralhadas.size()));
    }

    //Verificar se ainda existem questões disponíveis para sortear
    public static boolean existemQuestoesDisponiveis(List<Questao> banco, List<Questao> jaRespondidas){
        if (banco == null || banco.isEmpty()){
            return false;
        }
        return banco.stream().anyMatch(q -> !jaRespondidas.contains(q));
    }
}