import {
  NivelDificuldade,
  TipoAjuda,
  PONTOS_POR_NIVEL,
  LIMITE_ACERTOS_CONSECUTIVOS,
  LIMITE_ERROS_CONSECUTIVOS,
  chamadaApi,
} from './api';
import { getToken } from './servicoAutenticacao';

// Lógica de dificuldade adaptativa
// Sobe um nível de dificuldade. Máximo é DIFICIL.
export function subirNivel(nivel) {
  if (nivel === NivelDificuldade.FACIL) return NivelDificuldade.MEDIO;
  if (nivel === NivelDificuldade.MEDIO) return NivelDificuldade.DIFICIL;
  return NivelDificuldade.DIFICIL;
}

// Desce um nível de dificuldade. Mínimo é FACIL.
export function descerNivel(nivel) {
  if (nivel === NivelDificuldade.DIFICIL) return NivelDificuldade.MEDIO;
  if (nivel === NivelDificuldade.MEDIO) return NivelDificuldade.FACIL;
  return NivelDificuldade.FACIL;
}

// Calcula o próximo nível com base em acertos/erros consecutivos.
export function calcularProximoNivel(nivelAtual, acertosConsecutivos, errosConsecutivos) {
  const base = nivelAtual ?? NivelDificuldade.FACIL;
  if (acertosConsecutivos >= LIMITE_ACERTOS_CONSECUTIVOS) return subirNivel(base);
  if (errosConsecutivos >= LIMITE_ERROS_CONSECUTIVOS) return descerNivel(base);
  return base;
}

//Estado local de uma partida

// Cria um objeto de estado de partida vazio (começa em FACIL).
export function criarEstadoPartida() {
  return {
    dataHoraInicio: new Date().toISOString(),
    pontuacao: 0,
    nivelAtual: NivelDificuldade.FACIL,
    finalizada: false,
    respostas: [], // { questaoId, alternativaId, correta, tempoResposta }
    idsRespondidas: [], // só os ids, para filtrar banco
    acertosConsecutivos: 0,
    errosConsecutivos: 0,
    // Ajudas disponíveis
    ajudas: {
      [TipoAjuda.ELIMINAR_ALTERNATIVA]: 1,
      [TipoAjuda.DICA_TEXTUAL]: 1,
      [TipoAjuda.CHANCE_EXTRA]: 1,
      [TipoAjuda.PULAR_PERGUNTA]: 1,
    },
  };
}

// Registra uma resposta e retorna o estado atualizado.
export function registrarResposta(estado, questao, alternativa, tempoResposta = 0) {
  const correta = alternativa?.eCorreta === true;

  const resposta = {
    questaoId: questao.id,
    alternativaId: alternativa?.id ?? null,
    correta,
    tempoResposta,
  };

  // Pontuação
  const pontos = correta ? (PONTOS_POR_NIVEL[questao.nivelDificuldade] ?? 0) : 0;

  const acertosConsecutivos = correta ? estado.acertosConsecutivos + 1 : 0;
  const errosConsecutivos = !correta ? estado.errosConsecutivos + 1 : 0;

  const nivelAtual = calcularProximoNivel(
    estado.nivelAtual,
    acertosConsecutivos,
    errosConsecutivos
  );

  return {
    ...estado,
    pontuacao: estado.pontuacao + pontos,
    nivelAtual,
    acertosConsecutivos,
    errosConsecutivos,
    respostas: [...estado.respostas, resposta],
    idsRespondidas: [...estado.idsRespondidas, questao.id],
  };
}

// Utiliza uma ajuda. Lança erro se não houver mais disponível.
export function utilizarAjuda(estado, tipoAjuda) {
  const disponivel = estado.ajudas[tipoAjuda] ?? 0;
  if (disponivel <= 0) {
    throw new Error(`Ajuda do tipo ${tipoAjuda} não está mais disponível.`);
  }
  return {
    ...estado,
    ajudas: { ...estado.ajudas, [tipoAjuda]: disponivel - 1 },
  };
}

// Elimina aleatoriamente duas alternativas erradas (ajuda 50/50).
export function eliminarDuasAlternativas(alternativas) {
  const erradas = alternativas.filter(a => !a.eCorreta);
  // Embaralha e pega as duas primeiras
  const shuffled = [...erradas].sort(() => Math.random() - 0.5).slice(0, 2);
  const idsEliminar = new Set(shuffled.map(a => a.id));
  return alternativas.map(a => ({ ...a, eliminada: idsEliminar.has(a.id) }));
}

// Contadores
export function getTotalAcertos(estado) {
  return estado.respostas.filter(r => r.correta).length;
}

export function getTotalErros(estado) {
  return estado.respostas.filter(r => !r.correta).length;
}

// Persistência via API

// Inicia uma partida no servidor e retorna o id gerado.
export async function iniciarPartidaServidor() {
  const token = await getToken();
  const dados = await chamadaApi('/partida', 'POST', {}, token);
  return dados.id;
}

// Finaliza a partida no servidor, enviando o resumo completo.
export async function finalizarPartidaServidor(idPartida, estado) {
  const token = await getToken();
  const payload = {
    pontuacao: estado.pontuacao,
    nivelFinal: estado.nivelAtual,
    respostas: estado.respostas,
    dataHoraInicio: estado.dataHoraInicio,
    dataHoraFim: new Date().toISOString(),
  };
  return chamadaApi(`/partida/${idPartida}/finalizar`, 'PUT', payload, token);
}