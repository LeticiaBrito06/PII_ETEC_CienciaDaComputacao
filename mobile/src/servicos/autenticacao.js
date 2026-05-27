import AsyncStorage from '@react-native-async-storage/async-storage';
import { chamadaApi, DOMINIO_ALUNO, DOMINIO_PROFESSOR, TipoUsuario } from './api';

const CHAVE_TOKEN = '@labtech:token';
const CHAVE_USUARIO = '@labtech:usuario';

// Validação de e-mail

export function emailValido(email) {
  if (!email) return false;
  const lower = email.trim().toLowerCase();
  return lower.endsWith(DOMINIO_ALUNO) || lower.endsWith(DOMINIO_PROFESSOR);
}

export function isEmailAluno(email) {
  if (!email) return false;
  return email.trim().toLowerCase().endsWith(DOMINIO_ALUNO);
}

export function isEmailProfessor(email) {
  if (!email) return false;
  const lower = email.trim().toLowerCase();
  return lower.endsWith(DOMINIO_PROFESSOR) && !lower.endsWith(DOMINIO_ALUNO);
}

export function getMensagemErroEmail(email) {
  if (!email || email.trim() === '') return 'O campo de e-mail não pode estar vazio.';
  if (!emailValido(email)) return 'E-mail inválido. Use seu e-mail institucional.';
  return null;
}

export function extrairNomeUsuario(email) {
  if (!email || !email.includes('@')) return '';
  return email.trim().split('@')[0];
}

// Autenticação

// O back-end recebe { email, senha }, busca o salt, recalcula o hash e devolve { token, usuario }.
export async function login(email, senha) {
  const erroEmail = getMensagemErroEmail(email);
  if (erroEmail) throw new Error(erroEmail);
  if (!senha || senha.trim() === '') throw new Error('A senha não pode estar vazia.');

  const dados = await chamadaApi('/auth/login', 'POST', { email: email.trim(), senha });

  await AsyncStorage.setItem(CHAVE_TOKEN, dados.token);
  await AsyncStorage.setItem(CHAVE_USUARIO, JSON.stringify(dados.usuario));

  return dados;
}

// Remove token e usuário do armazenamento local (logout).
export async function logout() {
  await AsyncStorage.multiRemove([CHAVE_TOKEN, CHAVE_USUARIO]);
}

// Recupera o token JWT armazenado, ou null se não houver sessão.
export async function getToken() {
  return AsyncStorage.getItem(CHAVE_TOKEN);
}

// Recupera os dados do usuário logado, ou null.
export async function getUsuarioLogado() {
  const json = await AsyncStorage.getItem(CHAVE_USUARIO);
  return json ? JSON.parse(json) : null;
}

// Verifica se há uma sessão ativa.
export async function estaLogado() {
  const token = await getToken();
  return token !== null;
}

// Determina o tipo de usuário a partir do e-mail (sem precisar de chamada à API)
export function inferirTipoUsuario(email) {
  if (isEmailAluno(email)) return TipoUsuario.ALUNO;
  if (isEmailProfessor(email)) return TipoUsuario.PROFESSOR;
  return null;
}