import { NivelDificuldade, chamadaApi } from './api';
import { getToken } from './servicoAutenticacao';

//Lógica de cálculo local

// Calcula o percentual de acertos.
export function calcularPercentualAcerto(totalAcertos, totalErros) {
  const total = totalAcertos + totalErros;
  if (total === 0) return 0;
  return (totalAcertos / total) * 100;
}

// Define o nível médio com base no percentual de acertos.
export function calcularNivelMedio(percentual) {
  if (percentual >= 70) return NivelDificuldade.DIFICIL;
  if (percentual >= 40) return NivelDificuldade.MEDIO;
  return NivelDificuldade.FACIL;
}

// Constrói um objeto Desempenho calculado localmente.
export function montarDesempenhoLocal(totalPartidas, totalAcertos, totalErros) {
  const percentualAcerto = calcularPercentualAcerto(totalAcertos, totalErros);
  return {
    totalPartidas,
    totalAcertos,
    totalErros,
    percentualAcerto,
    nivelMedio: calcularNivelMedio(percentualAcerto),
  };
}

//Desempenho do aluno

// Busca o desempenho geral do aluno logado.
export async function getDesempenhoAluno() {
  const token = await getToken();
  return chamadaApi('/desempenho/meu', 'GET', null, token);
}

// Busca o histórico de partidas do aluno logado.
export async function getHistoricoPartidas() {
  const token = await getToken();
  return chamadaApi('/partida/historico', 'GET', null, token);
}

// Busca o desempenho mensal do aluno para exibição no gráfico
export async function getDesempenhoMensal() {
  const token = await getToken();
  return chamadaApi('/desempenho/mensal', 'GET', null, token);
}

//Relatórios do professor
// Busca o desempenho de um aluno específico.
export async function getRelatorioAluno(idAluno) {
  const token = await getToken();
  return chamadaApi(`/relatorio/aluno/${idAluno}`, 'GET', null, token);
}

// Busca o relatório de desempenho da turma completa.
export async function getRelatorioDaTurma() {
  const token = await getToken();
  return chamadaApi('/relatorio/turma', 'GET', null, token);
}

// Lista todos os alunos ativos.
export async function listarAlunos() {
  const token = await getToken();
  return chamadaApi('/usuario/alunos', 'GET', null, token);
}