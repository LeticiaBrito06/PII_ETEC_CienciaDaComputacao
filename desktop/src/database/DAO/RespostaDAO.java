package database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Alternativa;
import model.Questao;
import model.Resposta;

public class RespostaDAO {

    public List<Resposta> listarPorPartida(int idPartida) throws SQLException {
        String sql = """
                SELECT *
                FROM resposta
                WHERE id_partida = ?
                ORDER BY id
                """;

        List<Resposta> respostas = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPartida);

            try (ResultSet rs = ps.executeQuery()) {
                QuestaoDAO questaoDAO = new QuestaoDAO();

                while (rs.next()) {
                    respostas.add(mapearResposta(con, rs, questaoDAO));
                }
            }
        }

        return respostas;
    }

    public int inserir(int idPartida, Resposta resposta) throws SQLException {
        try (Connection con = Conexao.getConnection()) {
            return inserirComConexao(con, idPartida, resposta);
        }
    }

    int inserirComConexao(Connection con, int idPartida, Resposta resposta) throws SQLException {
        String sql = """
                INSERT INTO resposta
                    (id_partida, id_questao, id_alternativa, correta, tempo_resposta)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPartida);
            ps.setInt(2, resposta.getQuestao().getId());

            if (resposta.getAlternativaEscolhida() == null || resposta.getAlternativaEscolhida().getId() <= 0) {
                ps.setObject(3, null);
            } else {
                ps.setInt(3, resposta.getAlternativaEscolhida().getId());
            }

            ps.setBoolean(4, resposta.isCorreta());
            ps.setInt(5, 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Falha ao obter id da resposta.");
                }

                return rs.getInt(1);
            }
        }
    }

    private Resposta mapearResposta(Connection con, ResultSet rs, QuestaoDAO questaoDAO) throws SQLException {
    Questao questao = questaoDAO.buscarPorIdIncluindoInativas(rs.getInt("id_questao"));

    int idAlternativa = rs.getInt("id_alternativa");

    if (rs.wasNull()) {
        idAlternativa = 0;
    }

    Alternativa alternativa = buscarAlternativaPorId(con, idAlternativa);

    if (alternativa == null) {
        alternativa = new Alternativa(
                0,
                "",
                rs.getBoolean("correta"));
    }

    Resposta resposta = new Resposta();
    resposta.setQuestao(questao);
    resposta.setAlternativaEscolhida(alternativa);

    return resposta;
}

    private Alternativa buscarAlternativaPorId(Connection con, int idAlternativa) throws SQLException {
        if (idAlternativa <= 0) {
            return null;
        }

        String sql = "SELECT * FROM alternativa WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlternativa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Alternativa(
                            rs.getInt("id"),
                            rs.getString("texto"),
                            rs.getString("imagem_url"),
                            rs.getBoolean("e_correta"),
                            !rs.getBoolean("e_correta"));
                }
            }
        }

        return null;
    }
}
