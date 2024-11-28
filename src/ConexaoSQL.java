import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ConexaoSQL extends Component {
  private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/condominios", "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ConexaoSQL() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/condominios", "root", "root");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/condominios", "root", "root");
    }

    public void salvarCondominio(String proprietario, int numero) throws SQLException {
        String query = "INSERT INTO condominio (proprietario, numero) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, proprietario);
            pstmt.setInt(2, numero);
            pstmt.executeUpdate();
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar no banco: " + e.getMessage());
        }
    }

    public static JPanel listarCondominios() {
        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setPreferredSize(new Dimension(500, 300));

        String[] colunas = {"ID", "Proprietário", "Número"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = getConnection()) {
            String query = "SELECT id, proprietario, numero FROM condominios.condominio";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] linha = {
                        rs.getInt("id"),
                        rs.getString("proprietario"),
                        rs.getString("numero")
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JTable tabelaCondominio = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaCondominio);
        panelTabela.add(scrollPane, BorderLayout.CENTER);

        return panelTabela;
    }


    public static ArrayList<Condominios> buscarCondominios(String campo, String valor) {
        ArrayList<Condominios> resultado = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM condominios.condominio WHERE " + campo + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, valor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Condominios condominio = new Condominios(
                        rs.getInt("id"),
                        rs.getString("proprietario"),
                        rs.getInt("numero")
                );
                resultado.add(condominio);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar condomínios: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }

    public static boolean editarCondominio(int id, String novoProprietario, int novoNumero) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE condominios.condominio SET proprietario = ?, numero = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, novoProprietario);
            stmt.setInt(2, novoNumero);
            stmt.setInt(3, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao editar condomínio: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public static boolean excluirCondominio(int idCondominio) {
        String query = "DELETE FROM condominios.condominio WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCondominio);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir o condomínio: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public static List<Object[]> listarBoletosPorCondominio(int condominioId) {
        List<Object[]> boletos = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bol_id, b.bol_mesCompetencia, b.bol_dataEmissao, b.bol_dataVencimento, b.bol_valor, bc.data_pagamento " +
                    "FROM condominios.boletos_condominios bc " +
                    "JOIN condominios.boleto b ON b.bol_id = bc.id_boleto " +
                    "WHERE bc.id_condominio = ? " +
                    "ORDER BY b.bol_dataVencimento DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, condominioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] linha = {
                        rs.getInt("bol_id"),
                        rs.getString("bol_mesCompetencia"),
                        rs.getDate("bol_dataEmissao"),
                        rs.getDate("bol_dataVencimento"),
                        rs.getFloat("bol_valor"),
                        rs.getDate("data_pagamento") != null ? rs.getDate("data_pagamento").toLocalDate().format(dateFormatter) : "",
                        "Pagar"
                };
                boletos.add(linha);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return boletos;
    }


    public static boolean atualizarBoletoPagamento(int boletoId, int condominioId, LocalDate dataPagamento, double valorComDesconto) {
        try (Connection conn = getConnection()) {
            String updateQuery = "UPDATE condominios.boletos_condominios " +
                    "SET data_pagamento = ?, valor_pagamento = ? " +
                    "WHERE id_boleto = ? AND id_condominio = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setDate(1, Date.valueOf(dataPagamento));
            stmt.setDouble(2, valorComDesconto);
            stmt.setInt(3, boletoId);
            stmt.setInt(4, condominioId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public static boolean temBoletosPendentes(int condominioId) {
        String query = "SELECT COUNT(*) AS total " +
                "FROM condominios.boletos_condominios bc " +
                "JOIN condominios.boleto b ON b.bol_id = bc.id_boleto " +
                "WHERE bc.id_condominio = ? AND bc.data_pagamento IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, condominioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }



    public static JPanel listarBoletos() {
        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setPreferredSize(new Dimension(500, 300));

        String[] colunas = {"ID", "Mês Competência", "Data Emissão", "Valor", "Data Vencimento"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = getConnection()) {
            String query = "SELECT bol_id, bol_mesCompetencia, bol_dataEmissao, bol_valor, bol_dataVencimento FROM condominios.boleto";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                Object[] linha = {
                        rs.getInt("bol_id"),
                        rs.getString("bol_mesCompetencia"),
                        sdf.format(rs.getDate("bol_dataEmissao")),
                        rs.getFloat("bol_valor"),
                        sdf.format(rs.getDate("bol_dataVencimento"))
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados dos boletos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JTable tabelaBoletos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaBoletos);
        panelTabela.add(scrollPane, BorderLayout.CENTER);

        return panelTabela;
    }



    public static ArrayList<Boletos> buscarBoletos(String campo, String valor) {
        ArrayList<Boletos> resultado = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM condominios.boleto WHERE " + campo + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, valor);
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {

                Boletos boleto = new Boletos(
                        rs.getInt("bol_id"),                            
                        rs.getString("bol_mesCompetencia"),
                        rs.getDate("bol_dataEmissao").toLocalDate(),
                        rs.getFloat("bol_valor"),
                        rs.getDate("bol_dataVencimento").toLocalDate()
                );
                resultado.add(boleto);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar boletos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }


    public static boolean editarBoleto(int id, String novoMesCompetencia, float novoValor, LocalDate novaDataEmissao, LocalDate novaDataVencimento) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE condominios.boleto SET bol_mesCompetencia = ?, bol_valor = ?, bol_dataEmissao = ?, bol_dataVencimento = ? WHERE bol_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, novoMesCompetencia);
            stmt.setFloat(2, novoValor);
            stmt.setDate(3, java.sql.Date.valueOf(novaDataEmissao));
            stmt.setDate(4, java.sql.Date.valueOf(novaDataVencimento));
            stmt.setInt(5, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao editar boleto: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean excluirBoleto(int boletoId) {
        PreparedStatement ps = null;
        try (Connection conn = getConnection()){
            String sqlRemoveBoletoCondominio = "DELETE FROM condominios.boletos_condominios WHERE id_boleto = ?";
            ps = conn.prepareStatement(sqlRemoveBoletoCondominio);
            ps.setInt(1, boletoId);
            ps.executeUpdate();

            String sqlDeleteBoleto = "DELETE FROM condominios.boleto WHERE bol_id = ?";
            ps = conn.prepareStatement(sqlDeleteBoleto);
            ps.setInt(1, boletoId);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public static void adicionarBoletoParaTodosCondominios(String mesCompetencia, LocalDate dataEmissao, float valor, LocalDate dataVencimento) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String insertBoletoSQL = "INSERT INTO condominios.boleto (bol_mesCompetencia, bol_dataEmissao, bol_valor, bol_dataVencimento) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBoletoSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, mesCompetencia);
                stmt.setDate(2, java.sql.Date.valueOf(dataEmissao));
                stmt.setFloat(3, valor);
                stmt.setDate(4, java.sql.Date.valueOf(dataVencimento));
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int bolId = generatedKeys.getInt(1);

                    String selectCondominiosSQL = "SELECT id FROM condominios.condominio";
                    try (PreparedStatement selectStmt = conn.prepareStatement(selectCondominiosSQL)) {
                        ResultSet resultSet = selectStmt.executeQuery();


                        String insertAssociationsSQL = "INSERT INTO condominios.boletos_condominios (id_boleto, id_condominio, data_pagamento) VALUES (?, ?, ?)";
                        try (PreparedStatement insertAssocStmt = conn.prepareStatement(insertAssociationsSQL)) {
                            while (resultSet.next()) {
                                int idCondominio = resultSet.getInt("id");
                                insertAssocStmt.setInt(1, bolId);
                                insertAssocStmt.setInt(2, idCondominio);
                                insertAssocStmt.setNull(3, java.sql.Types.DATE);
                                insertAssocStmt.addBatch();
                            }
                            insertAssocStmt.executeBatch();
                        }
                    }
                } else {
                    throw new SQLException("Falha ao inserir o boleto, nenhum ID retornado.");
                }
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao tentar fazer rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Erro ao adicionar boleto: " + e.getMessage());
            throw new RuntimeException("Erro ao adicionar boleto e realizar operações associadas.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException autoCommitEx) {
                System.err.println("Erro ao restaurar auto-commit: " + autoCommitEx.getMessage());
            }
        }
    }
}
