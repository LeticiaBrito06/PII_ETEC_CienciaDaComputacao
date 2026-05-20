package database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelos.Alternativa;
import modelos.Questao;
import modelos.tipos.NivelDificuldade;
import modelos.tipos.TipoQuestao;

public class QuestaoDAO {

    //Insere uma questão e todas as suas alternativas em uma única transação + Retorna o id gerado para a questão.
    public int inserir(Questao questao, int idProfessor) throws SQLException {
        String sqlQuestao = """
                INSERT INTO questao (enunciado, tipo, nivel_dificuldade, categoria, imagem_url, id_professor)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        Connection con = Conexao.conectar();
        con.setAutoCommit(false);
 
        try (PreparedStatement ps = con.prepareStatement(sqlQuestao, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, questao.getEnunciado());
            ps.setString(2, questao.getTipo().name());
            ps.setString(3, questao.getNivelDificuldade().name());
            ps.setString(4, questao.getCategoria());
            ps.setString(5, questao.getImagemEnunciado());
            ps.setInt(6, idProfessor);
            ps.executeUpdate();
 
            int idQuestao;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("Falha ao obter id da questão.");
                idQuestao = rs.getInt(1);
                questao.setId(idQuestao);
            }
 
            inserirAlternativas(con, questao.getAlternativas(), idQuestao);
            con.commit();
            return idQuestao;
 
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
 
    //Insere as alternativas de uma questão (chamado dentro de uma transação aberta).
    private void inserirAlternativas(Connection con, List<Alternativa> alternativas, int idQuestao)
            throws SQLException {
        String sql = """
                INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url)
                VALUES (?, ?, ?, ?)
                """;
 
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Alternativa alt : alternativas) {
                ps.setInt(1, idQuestao);
                ps.setString(2, alt.getTexto());
                ps.setBoolean(3, alt.isECorreta());
                ps.setString(4, alt.getImagem());
                ps.addBatch();
            }
            ps.executeBatch();
 
            // Recupera os ids gerados e injeta nas alternativas
            try (ResultSet rs = ps.getGeneratedKeys()) {
                int i = 0;
                while (rs.next() && i < alternativas.size()) {
                    alternativas.get(i++).setId(rs.getInt(1));
                }
            }
        }
    }
 
    //Busca uma questão (com alternativas) pelo id.
    public Questao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM questao WHERE id = ? AND ativa = TRUE";
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Questao q = mapearQuestao(rs);
                    q.setAlternativas(buscarAlternativas(con, q.getId()));
                    return q;
                }
            }
        }
        return null;
    }
 
    // Lista todas as questões ativas de um determinado nível + já com suas alternativas carregadas
    public List<Questao> listarPorNivel(NivelDificuldade nivel) throws SQLException {
        String sql = "SELECT * FROM questao WHERE nivel_dificuldade = ? AND ativa = TRUE ORDER BY RAND()";
        List<Questao> lista = new ArrayList<>();
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, nivel.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Questao q = mapearQuestao(rs);
                    q.setAlternativas(buscarAlternativas(con, q.getId()));
                    lista.add(q);
                }
            }
        }
        return lista;
    }
 
    //Lista todas as questões ativas (banco completo do professor)
    public List<Questao> listarTodas() throws SQLException {
        String sql = "SELECT * FROM questao WHERE ativa = TRUE ORDER BY nivel_dificuldade, id";
        List<Questao> lista = new ArrayList<>();
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Questao q = mapearQuestao(rs);
                q.setAlternativas(buscarAlternativas(con, q.getId()));
                lista.add(q);
            }
        }
        return lista;
    }
 
    // Lista questões cadastradas por um professor.
    public List<Questao> listarPorProfessor(int idProfessor) throws SQLException {
        String sql = "SELECT * FROM questao WHERE id_professor = ? AND ativa = TRUE ORDER BY id DESC";
        List<Questao> lista = new ArrayList<>();
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, idProfessor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Questao q = mapearQuestao(rs);
                    q.setAlternativas(buscarAlternativas(con, q.getId()));
                    lista.add(q);
                }
            }
        }
        return lista;
    }
 
    // Busca as alternativas de uma questão usando uma conexão já aberta.
    private List<Alternativa> buscarAlternativas(Connection con, int idQuestao) throws SQLException {
        String sql = "SELECT * FROM alternativa WHERE id_questao = ?";
        List<Alternativa> lista = new ArrayList<>();
 
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idQuestao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlternativa(rs));
                }
            }
        }
        return lista;
    }

    // UPDATE
    // Atualiza os dados principais de uma questão (sem alterar alternativas).
    public boolean atualizar(Questao questao) throws SQLException {
        String sql = """
                UPDATE questao
                   SET enunciado = ?, tipo = ?, nivel_dificuldade = ?, categoria = ?, imagem_url = ?
                 WHERE id = ?
                """;
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, questao.getEnunciado());
            ps.setString(2, questao.getTipo().name());
            ps.setString(3, questao.getNivelDificuldade().name());
            ps.setString(4, questao.getCategoria());
            ps.setString(5, questao.getImagemEnunciado());
            ps.setInt(6, questao.getId());
            return ps.executeUpdate() > 0;
        }
    }
 
    // Desativa uma questão (soft-delete). As alternativas são mantidas.
    public boolean desativar(int idQuestao) throws SQLException {
        String sql = "UPDATE questao SET ativa = FALSE WHERE id = ?";
 
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, idQuestao);
            return ps.executeUpdate() > 0;
        }
    }

    // Mapeamento ResultSet → Model
 
    private Questao mapearQuestao(ResultSet rs) throws SQLException {
        Questao q = new Questao(
                rs.getInt("id"),
                rs.getString("enunciado"),
                TipoQuestao.valueOf(rs.getString("tipo")),
                NivelDificuldade.valueOf(rs.getString("nivel_dificuldade")),
                rs.getString("categoria")
        );
        q.setImagemEnunciado(rs.getString("imagem_url"));
        return q;
    }
 
    private Alternativa mapearAlternativa(ResultSet rs) throws SQLException {
        return new Alternativa(
                rs.getInt("id"),
                rs.getString("texto"),
                rs.getString("imagem_url"),
                rs.getBoolean("e_correta"),
                !rs.getBoolean("e_correta") // errada = !e_correta
        );
    }
}