import { useCallback, useEffect, useReducer, useRef } from 'react';
import { TipoAjuda, NivelDificuldade } from '../servicos/api';
import {
  criarEstadoPartida,
  registrarResposta,
  utilizarAjuda,
  eliminarDuasAlternativas,
  getTotalAcertos,
  getTotalErros,
  iniciarPartidaServidor,
  finalizarPartidaServidor,
} from '../servicos/servicoPartida';
import { listarPorNivel } from '../servicos/servicoQuestao';
import { sortearPorNivel, existemQuestoesDisponiveis } from '../servicos/servicoQuestao';

//Reducer

const ACOES = {
  INICIAR: 'INICIAR',
  CARREGAR_QUESTOES: 'CARREGAR_QUESTOES',
  NOVA_QUESTAO: 'NOVA_QUESTAO',
  REGISTRAR_RESPOSTA: 'REGISTRAR_RESPOSTA',
  USAR_AJUDA: 'USAR_AJUDA',
  ELIMINAR_ALTERNATIVAS: 'ELIMINAR_ALTERNATIVAS',
  FINALIZAR: 'FINALIZAR',
  ERRO: 'ERRO',
};

function estadoHookInicial() {
  return {
    idPartidaServidor: null,
    partida: criarEstadoPartida(),
    banco: [], // todas as questões do nível atual
    questaoAtual: null,
    alternativas: [], // alternativas com estado visual (eliminada, etc.)
    carregando: true,
    finalizada: false,
    erro: null,
    tempoInicio: null, // Date para calcular tempoResposta
  };
}

function hookReducer(state, action) {
  switch (action.type) {
    case ACOES.INICIAR:
      return {
        ...state,
        idPartidaServidor: action.idPartidaServidor,
        carregando: false,
      };
    case ACOES.CARREGAR_QUESTOES:
      return { ...state, banco: action.banco };
    case ACOES.NOVA_QUESTAO:
      return {
        ...state,
        questaoAtual: action.questao,
        alternativas: action.alternativas,
        tempoInicio: new Date(),
        carregando: false,
      };
    case ACOES.REGISTRAR_RESPOSTA:
      return {
        ...state,
        partida: action.novaPartida,
      };
    case ACOES.USAR_AJUDA:
      return { ...state, partida: action.novaPartida };
    case ACOES.ELIMINAR_ALTERNATIVAS:
      return { ...state, alternativas: action.alternativas };
    case ACOES.FINALIZAR:
      return { ...state, finalizada: true, carregando: false };
    case ACOES.ERRO:
      return { ...state, erro: action.mensagem, carregando: false };
    default:
      return state;
  }
}

//Hook

export function usePartida() {
  const [estado, despachar] = useReducer(hookReducer, null, estadoHookInicial);
  const bancoRef = useRef([]); // referencia para acessar banco dentro de callbacks sem stale closure

  // Inicializa a partida ao montar o hook
  useEffect(() => {
    async function iniciar() {
      try {
        const idPartidaServidor = await iniciarPartidaServidor();
        despachar({ type: ACOES.INICIAR, idPartidaServidor });
        await carregarNivel(NivelDificuldade.FACIL);
      } catch (e) {
        despachar({ type: ACOES.ERRO, mensagem: e.message });
      }
    }
    iniciar();
  }, []);

  // Sempre que o nível da partida mudar, recarrega questões do servidor
  const nivelAnteriorRef = useRef(null);
  useEffect(() => {
    const nivel = estado.partida.nivelAtual;
    if (nivel !== nivelAnteriorRef.current && nivelAnteriorRef.current !== null) {
      carregarNivel(nivel);
    }
    nivelAnteriorRef.current = nivel;
  }, [estado.partida.nivelAtual]);

  async function carregarNivel(nivel) {
    try {
      const questoes = await listarPorNivel(nivel);
      bancoRef.current = questoes;
      despachar({ type: ACOES.CARREGAR_QUESTOES, banco: questoes });
      avancarQuestao(questoes, estado.partida.idsRespondidas);
    } catch (e) {
      despachar({ type: ACOES.ERRO, mensagem: e.message });
    }
  }

  function avancarQuestao(banco, idsRespondidas) {
    const proxima = sortearPorNivel(banco, idsRespondidas, estado.partida.nivelAtual)
      ?? sortearPorNivel(bancoRef.current, idsRespondidas, estado.partida.nivelAtual);

    if (!proxima) {
      // Sem mais questões vai finalizar
      encerrarPartida();
      return;
    }

    // Embaralha as alternativas para apresentação
    const altsEmbaralhadas = [...proxima.alternativas].sort(() => Math.random() - 0.5);
    despachar({
      type: ACOES.NOVA_QUESTAO,
      questao: proxima,
      alternativas: altsEmbaralhadas,
    });
  }

  const responder = useCallback((alternativa) => {
    if (!estado.questaoAtual || estado.finalizada) return;

    const tempoResposta = estado.tempoInicio
      ? Math.round((new Date() - estado.tempoInicio) / 1000)
      : 0;

    const novaPartida = registrarResposta(
      estado.partida,
      estado.questaoAtual,
      alternativa,
      tempoResposta
    );

    despachar({ type: ACOES.REGISTRAR_RESPOSTA, novaPartida });

    // Aguarda um breve delay para feedback visual antes de avançar
    setTimeout(() => {
      const temMais = existemQuestoesDisponiveis(bancoRef.current, novaPartida.idsRespondidas);
      if (!temMais) {
        encerrarPartida(novaPartida);
      } else {
        avancarQuestao(bancoRef.current, novaPartida.idsRespondidas);
      }
    }, 1200);
  }, [estado]);

  const usarAjuda = useCallback((tipoAjuda) => {
    try {
      const novaPartida = utilizarAjuda(estado.partida, tipoAjuda);
      despachar({ type: ACOES.USAR_AJUDA, novaPartida });

      // Aplica efeito visual da ajuda
      if (tipoAjuda === TipoAjuda.ELIMINAR_ALTERNATIVA) {
        const novasAlts = eliminarDuasAlternativas(estado.alternativas);
        despachar({ type: ACOES.ELIMINAR_ALTERNATIVAS, alternativas: novasAlts });
      }
    } catch (e) {
      despachar({ type: ACOES.ERRO, mensagem: e.message });
    }
  }, [estado]);

  async function encerrarPartida(partidaFinal = estado.partida) {
    try {
      despachar({ type: ACOES.FINALIZAR });
      if (estado.idPartidaServidor) {
        await finalizarPartidaServidor(estado.idPartidaServidor, partidaFinal);
      }
    } catch {
      // Falha silenciosa no envio a pontuação local ainda é exibida
    }
  }

  return {
    questaoAtual: estado.questaoAtual,
    alternativas: estado.alternativas,
    nivelAtual: estado.partida.nivelAtual,
    pontuacao: estado.partida.pontuacao,
    ajudasDisponiveis: estado.partida.ajudas,
    totalAcertos: getTotalAcertos(estado.partida),
    totalErros: getTotalErros(estado.partida),
    carregando: estado.carregando,
    finalizada: estado.finalizada,
    erro: estado.erro,
    responder,
    usarAjuda,
  };
}