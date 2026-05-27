// Constantes e chamada base
export * from './servicos/api';

// Autenticação
export * from './servicos/servicoAutenticacao';

// Questões
export * from './servicos/servicoQuestao';

// Partida / jogo
export * from './servicos/servicoPartida';

// Desempenho e relatórios
export * from './servicos/servicoDesempenho';

// Contexto global de autenticação
export { ProviderAutenticacao, useContextoAuth } from './contexto/ContextoAutenticacao';

// Hook de partida
export { usePartida } from './hooks/usePartida';