// Configuração base da API
// Lembrar de altera a BASE_URL para o endereço do servidor Java
const BASE_URL = process.env.EXPO_PUBLIC_API_URL ?? 'http://10.0.2.2:8080/api';

// Tipos espelhados
export const TipoUsuario = Object.freeze({
  ALUNO: 'ALUNO',
  PROFESSOR: 'PROFESSOR',
});

export const TipoQuestao = Object.freeze({
  MULTIPLA_ESCOLHA: 'MULTIPLA_ESCOLHA',
  ASSOCIACAO: 'ASSOCIACAO',
});

export const NivelDificuldade = Object.freeze({
  FACIL: 'FACIL',
  MEDIO: 'MEDIO',
  DIFICIL: 'DIFICIL',
});

export const TipoAjuda = Object.freeze({
  ELIMINAR_ALTERNATIVA: 'ELIMINAR_ALTERNATIVA',
  DICA_TEXTUAL: 'DICA_TEXTUAL',
  CHANCE_EXTRA: 'CHANCE_EXTRA',
  PULAR_PERGUNTA: 'PULAR_PERGUNTA',
});

// Pontuação por nível
export const PONTOS_POR_NIVEL = Object.freeze({
  FACIL: 10,
  MEDIO: 20,
  DIFICIL: 30,
});

// Limites de acertos/erros consecutivos para mudar dificuldade
export const LIMITE_ACERTOS_CONSECUTIVOS = 3;
export const LIMITE_ERROS_CONSECUTIVOS = 3;

// Domínios institucionais — espelha ValidarEmail
export const DOMINIO_ALUNO = '@aluno.cps.sp.gov.br';
export const DOMINIO_PROFESSOR = '@cps.sp.gov.br';

export async function chamadaApi(endpoint, method = 'GET', body = null, token = null) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const opcoes = { method, headers };
  if (body) opcoes.body = JSON.stringify(body);

  const resposta = await fetch(`${BASE_URL}${endpoint}`, opcoes);

  if (!resposta.ok) {
    // Tenta extrair mensagem de erro do servidor
    let mensagem = `Erro ${resposta.status}`;
    try {
      const erro = await resposta.json();
      mensagem = erro.mensagem ?? erro.message ?? mensagem;
    } catch (_) { /* ignora erros de parse */ }
    throw new Error(mensagem);
  }

  // 204 No Content — sem corpo
  if (resposta.status === 204) return null;
  return resposta.json();
}