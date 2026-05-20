package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelos.Aluno;
import modelos.Professor;
import modelos.Usuario;
import modelos.tipos.TipoUsuario;

public class UsuarioDAO {

    //Inserir um novo usuário no banco e devolve o id gerado.
    public int inserir(Usuario usuario, String salt) throws SQLException {
        String sql = """
                INSERT INTO usuario (nome, email, senha, salt, tipo, turma, ra)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, salt);
            ps.setString(5, usuario.getTipo().name());
            if (usuario instanceof Aluno aluno) {
                ps.setString(6, aluno.getTurma());
                ps.setString(7, aluno.getRa());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    usuario.setId(idGerado);
                    return idGerado;
                }
            }
        }
        return -1;
    }

    // READ
    // Busca qualquer usuário ativo pelo id. Retorna Aluno ou Professor conforme o tipo.
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ? AND ativo = TRUE";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    // Busca pelo e-mail (usado no login).
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ? AND ativo = TRUE";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    //Retorna todos os alunos ativos.
    public List<Aluno> listarAlunos() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE tipo = 'ALUNO' AND ativo = TRUE ORDER BY nome";
        List<Aluno> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAluno(rs));
            }
        }
        return lista;
    }

    //Retorna todos os professores ativos.
    public List<Professor> listarProfessores() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE tipo = 'PROFESSOR' AND ativo = TRUE ORDER BY nome";
        List<Professor> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProfessor(rs));
            }
        }
        return lista;
    }

    // Retorna o salt de um usuário pelo e-mail (necessário para recalcular o hash no login).
    public String buscarSaltPorEmail(String email) throws SQLException {
        String sql = "SELECT salt FROM usuario WHERE email = ? AND ativo = TRUE";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("salt");
                }
            }
        }
        return null;
    }

    // Atualiza nome, e-mail e turma/ra de um usuário existente.
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = """
                UPDATE usuario
                   SET nome = ?, email = ?, turma = ?, ra = ?
                 WHERE id = ?
                """;

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());

            if (usuario instanceof Aluno aluno) {
                ps.setString(3, aluno.getTurma());
                ps.setString(4, aluno.getRa());
            } else {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Troca a senha (hash) de um usuário.
    public boolean atualizarSenha(int idUsuario, String novaSenhaHash) throws SQLException {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, novaSenhaHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // Desativa um usuário (soft-delete) sem remover o registro.
    public boolean desativar(int id) throws SQLException {
        String sql = "UPDATE usuario SET ativo = FALSE WHERE id = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        TipoUsuario tipo = TipoUsuario.valueOf(rs.getString("tipo"));
        return switch (tipo) {
            case ALUNO -> mapearAluno(rs);
            case PROFESSOR -> mapearProfessor(rs);
        };
    }

    private Aluno mapearAluno(ResultSet rs) throws SQLException {
        return new Aluno(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("turma"),
                rs.getString("ra")
        );
    }

    private Professor mapearProfessor(ResultSet rs) throws SQLException {
        return new Professor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha")
        );
    }
}