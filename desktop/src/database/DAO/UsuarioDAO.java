package database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Aluno;
import model.Professor;
import model.Usuario;
import model.tipos.TipoUsuario;
import static utilitarios.Criptografia.criptografar;
import static utilitarios.Criptografia.gerarSalt;

public class UsuarioDAO {
    public int inserir(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO usuario (nome, email, senha, salt, tipo)
                VALUES (?, ?, ?, ?, ?)
                """;

        String salt = gerarSalt();
        String senhaHash = criptografar(usuario.getSenha(), salt);

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, senhaHash);
            ps.setString(4, salt);
            ps.setString(5, usuario.getTipo().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    return idGerado;
                }
            }
        }
        return -1;
    }

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

            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizarSenha(int idUsuario, String novaSenhaHash) throws SQLException {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ?";

        try (Connection con = Conexao.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, novaSenhaHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

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
                rs.getString("senha"));
    }

    private Professor mapearProfessor(ResultSet rs) throws SQLException {
        return new Professor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"));
    }
}