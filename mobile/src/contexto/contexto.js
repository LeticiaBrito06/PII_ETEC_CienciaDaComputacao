import React, { createContext, useContext, useEffect, useReducer } from 'react';
import {
  login as servicoLogin,
  logout as servicoLogout,
  getToken,
  getUsuarioLogado,
} from '../servicos/servicoAutenticacao';

//Estado e Reducer
const estadoInicial = {
  token: null,
  usuario: null, // { id, nome, email, tipo, turma, ra }
  carregando: true, // true enquanto AsyncStorage não foi lido
  erro: null,
};

function reducer(state, action) {
  switch (action.type) {
    case 'RESTAURAR_SESSAO':
      return { ...state, token: action.token, usuario: action.usuario, carregando: false };
    case 'LOGIN_SUCESSO':
      return { ...state, token: action.token, usuario: action.usuario, erro: null };
    case 'LOGOUT':
      return { ...estadoInicial, carregando: false };
    case 'ERRO':
      return { ...state, erro: action.mensagem };
    case 'LIMPAR_ERRO':
      return { ...state, erro: null };
    default:
      return state;
  }
}

// Contexto
const ContextoAuth = createContext(null);

export function ProviderAutenticacao({ children }) {
  const [estado, despachar] = useReducer(reducer, estadoInicial);

  // Restaura sessão ao iniciar o app
  useEffect(() => {
    async function restaurar() {
      try {
        const [token, usuario] = await Promise.all([getToken(), getUsuarioLogado()]);
        despachar({ type: 'RESTAURAR_SESSAO', token, usuario });
      } catch {
        despachar({ type: 'RESTAURAR_SESSAO', token: null, usuario: null });
      }
    }
    restaurar();
  }, []);

  async function entrar(email, senha) {
    try {
      despachar({ type: 'LIMPAR_ERRO' });
      const { token, usuario } = await servicoLogin(email, senha);
      despachar({ type: 'LOGIN_SUCESSO', token, usuario });
      return usuario; // permite navegação condicional na tela
    } catch (e) {
      despachar({ type: 'ERRO', mensagem: e.message });
      throw e;
    }
  }

  async function sair() {
    await servicoLogout();
    despachar({ type: 'LOGOUT' });
  }

  const valor = {
    token: estado.token,
    usuario: estado.usuario,
    carregando: estado.carregando,
    erro: estado.erro,
    estaLogado: estado.token !== null,
    entrar,
    sair,
    limparErro: () => despachar({ type: 'LIMPAR_ERRO' }),
  };

  return <ContextoAuth.Provider value={valor}>{children}</ContextoAuth.Provider>;
}

// Hook para consumir o contexto de autenticação em qualquer tela.
export function useContextoAuth() {
  const ctx = useContext(ContextoAuth);
  if (!ctx) throw new Error('useContextoAuth deve ser usado dentro de <ProviderAutenticacao>');
  return ctx;
}