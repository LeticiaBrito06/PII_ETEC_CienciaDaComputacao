import { chamadaApi, NivelDificuldade } from './api';
import { getToken } from './servicoAutenticacao';

// Utilitários locais
export function embaralhar(banco) {
  if (!banco || banco.length === 0) return [];
  const copia = [...banco];
  for (let i = copia.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copia[i], copia[j]] = [copia[j], copia[i]];
  }
  return copia;
}

// Sorteia a próxima questão excluindo as já respondidas
export function sortearProxima(banco, idsJaRespondidas = []) {
  if (!banco || banco.length === 0) return null;
  const disponiveis = banco.filter(q => !idsJaRespondidas.includes(q.id));
  if (disponiveis.length === 0) return null;
  const embaralhadas = embaralhar(disponiveis);
  return embaralhadas[0];
}

// Sorteia questão por nível excluindo as já respondidas.
export function sortearPorNivel(banco, idsJaRespondidas = [], nivel) {
  if (!banco || banco.length === 0 || !nivel) return null;
  const disponiveis = banco.filter(
    q => q.nivelDificuldade === nivel && !idsJaRespondidas.includes(q.id)
  );
  if (disponiveis.length === 0) return null;
  return embaralhar(disponiveis)[0];
}

//Retorna um subconjunto aleatório de questões.
export function sortearConjunto(banco, quantidade) {
  if (!banco || banco.length === 0 || quantidade <= 0) return [];
  return embaralhar(banco).slice(0, Math.min(quantidade, banco.length));
}

// Verifica se ainda existem questões disponíveis.
export function existemQuestoesDisponiveis(banco, idsJaRespondidas = []) {
  if (!banco || banco.length === 0) return false;
  return banco.some(q => !idsJaRespondidas.includes(q.id));
}

// Chamadas à API (espelham QuestaoDAO.java)

// Lista todas as questões ativas (banco completo).
export async function listarTodas() {
  const token = await getToken();
  return chamadaApi('/questao', 'GET', null, token);
}

// Lista questões ativas por nível de dificuldade.
export async function listarPorNivel(nivel) {
  if (!Object.values(NivelDificuldade).includes(nivel)) {
    throw new Error(`Nível inválido: ${nivel}`);
  }
  const token = await getToken();
  return chamadaApi(`/questao/nivel/${nivel}`, 'GET', null, token);
}

// Lista questões cadastradas pelo professor logado.
export async function listarPorProfessor() {
  const token = await getToken();
  return chamadaApi('/questao/minhas', 'GET', null, token);
}

// Busca uma questão com suas alternativas pelo id.
export async function buscarPorId(id) {
  const token = await getToken();
  return chamadaApi(`/questao/${id}`, 'GET', null, token);
}

// Cria uma nova questão com suas alternativas.
export async function criarQuestao(questao) {
  const token = await getToken();
  return chamadaApi('/questao', 'POST', questao, token);
}

// Atualiza os dados de uma questão existente.
export async function atualizarQuestao(id, questao) {
  const token = await getToken();
  return chamadaApi(`/questao/${id}`, 'PUT', questao, token);
}

// Desativa (soft-delete) uma questão.
export async function removerQuestao(id) {
  const token = await getToken();
  return chamadaApi(`/questao/${id}`, 'DELETE', null, token);
}