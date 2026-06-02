package database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Aluno;
import model.Partida;
import model.Resposta;
import model.tipos.NivelDificuldade;

public class PartidaDAO {

    public List<Partida> listarPorAluno(Aluno aluno) throws SQLException {
        String sql = """
                SELECT *
                FROM partida
                WHERE id_aluno = ?
                ORDER BY data_hora_inicio DESC, id DESC
                """;

        List<Partida> partidas = new ArrayList<>();
        RespostaDAO respostaDAO = new RespostaDAO();

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, aluno.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partida partida = mapearPartida(rs, aluno);

                    for (Resposta resposta : respostaDAO.listarPorPartida(partida.getId())) {
                        partida.adicionarResposta(resposta);
                    }

                    partidas.add(partida);
                }
            }
        }

        return partidas;
    }

    public int inserir(Partida partida) throws SQLException {
        String sql = """
                INSERT INTO partida
                    (id_aluno, data_hora_inicio, data_hora_fim, pontuacao, nivel_atual, finalizada)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherPreparedStatementPartida(ps, partida);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Falha ao obter id da partida.");
                }

                int idGerado = rs.getInt(1);
                partida.setId(idGerado);
                return idGerado;
            }
        }
    }

    public boolean atualizar(Partida partida) throws SQLException {
        String sql = """
                UPDATE partida
                SET id_aluno = ?, data_hora_inicio = ?, data_hora_fim = ?,
                    pontuacao = ?, nivel_atual = ?, finalizada = ?
                WHERE id = ?
                """;

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            preencherPreparedStatementPartida(ps, partida);
            ps.setInt(7, partida.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public int salvarPartidaCompleta(Partida partida) throws SQLException {
        Connection con = Conexao.getConnection();
        con.setAutoCommit(false);

        try {
            int idPartida;

            if (partida.getId() <= 0) {
                idPartida = inserirComConexao(con, partida);
            } else {
                atualizarComConexao(con, partida);
                idPartida = partida.getId();
                excluirRespostasDaPartida(con, idPartida);
            }

            RespostaDAO respostaDAO = new RespostaDAO();
            for (Resposta resposta : partida.getRespostas()) {
                respostaDAO.inserirComConexao(con, idPartida, resposta);
            }

            con.commit();
            return idPartida;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private int inserirComConexao(Connection con, Partida partida) throws SQLException {
        String sql = """
                INSERT INTO partida
                    (id_aluno, data_hora_inicio, data_hora_fim, pontuacao, nivel_atual, finalizada)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherPreparedStatementPartida(ps, partida);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Falha ao obter id da partida.");
                }

                int idGerado = rs.getInt(1);
                partida.setId(idGerado);
                return idGerado;
            }
        }
    }

    private boolean atualizarComConexao(Connection con, Partida partida) throws SQLException {
        String sql = """
                UPDATE partida
                SET id_aluno = ?, data_hora_inicio = ?, data_hora_fim = ?,
                    pontuacao = ?, nivel_atual = ?, finalizada = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencherPreparedStatementPartida(ps, partida);
            ps.setInt(7, partida.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private void excluirRespostasDaPartida(Connection con, int idPartida) throws SQLException {
        String sql = "DELETE FROM resposta WHERE id_partida = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartida);
            ps.executeUpdate();
        }
    }

    private void preencherPreparedStatementPartida(PreparedStatement ps, Partida partida) throws SQLException {
        ps.setInt(1, partida.getAluno().getId());
        ps.setTimestamp(2, partida.getDataHoraInicio() == null
                ? null
                : Timestamp.valueOf(partida.getDataHoraInicio()));
        ps.setTimestamp(3, partida.getDataHoraFim() == null
                ? null
                : Timestamp.valueOf(partida.getDataHoraFim()));
        ps.setInt(4, partida.getPontuacao());
        ps.setString(5, partida.getNivelAtual() == null ? null : partida.getNivelAtual().name());
        ps.setBoolean(6, partida.isFinalizada());
    }

    private Partida mapearPartida(ResultSet rs, Aluno aluno) throws SQLException {
        Partida partida = new Partida(aluno);
        partida.setId(rs.getInt("id"));

        Timestamp inicio = rs.getTimestamp("data_hora_inicio");
        if (inicio != null) {
            partida.setDataHoraInicio(inicio.toLocalDateTime());
        }

        Timestamp fim = rs.getTimestamp("data_hora_fim");
        if (fim != null) {
            partida.setDataHoraFim(fim.toLocalDateTime());
        }

        partida.setPontuacao(rs.getInt("pontuacao"));
        partida.setFinalizada(rs.getBoolean("finalizada"));

        String nivel = rs.getString("nivel_atual");
        if (nivel != null && !nivel.isBlank()) {
            partida.setNivelAtual(NivelDificuldade.valueOf(nivel));
        }

        return partida;
    }
}
