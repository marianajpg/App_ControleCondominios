import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class Condominios {
    private JTextField proprietarioText, numeroText;
    private String proprietario;
    private int numero;
    private final int id;

    public Condominios(int id, String proprietario, int numero){
        this.id = id;
        this.numero = numero;
        this.proprietario = proprietario;
    }

    public void cadastrar(JPanel panelCondominio, CardLayout cardLayout) throws SQLException {
        ConexaoSQL conn = new ConexaoSQL();
        JPanel panelCadastro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        panelCadastro.setPreferredSize(new Dimension(500, 400));

        proprietarioText = new JTextField( 10);
        numeroText = new JTextField(10);

        JLabel proprietarioLabel = new JLabel("Proprietário: ");
        JLabel numeroLabel = new JLabel("Número do condomínio: ");

        JButton salvarButton = new JButton("Salvar");
        JButton limparButton = new JButton("Limpar");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCadastro.add(proprietarioLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(proprietarioText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(numeroLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(numeroText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(salvarButton, gbc);
        gbc.gridx = 1;
        panelCadastro.add(limparButton, gbc);

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    proprietario = proprietarioText.getText();
                    numero = Integer.parseInt(numeroText.getText());

                    JOptionPane.showMessageDialog(null, "Condomínio salvo com sucesso!");
                    conn.salvarCondominio(proprietario,numero);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex.getMessage());
                }
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proprietarioText.setText("");
                numeroText.setText("");

            }
        });

        panelCondominio.add(panelCadastro, "Cadastro");
        cardLayout.show(panelCondominio, "Cadastro");
    }

    public void editar(JPanel panelCondominio, CardLayout cardLayout) {
        JPanel panelEdicao = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre componentes

        final String[] selecionado = new String[1];
        String[] opcoesBusca = {"Proprietário", "ID", "Número do condomínio"};

        JComboBox<String> comboBox = new JComboBox<>(opcoesBusca);
        JLabel buscarLabel = new JLabel("Buscar por: ");
        JTextField buscarText = new JTextField(10);
        JButton buscarButton = new JButton("Buscar");

        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusca.add(buscarLabel);
        panelBusca.add(comboBox);
        panelBusca.add(buscarText);
        panelBusca.add(buscarButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelEdicao.add(panelBusca, gbc);

        selecionado[0] = (String) comboBox.getSelectedItem();
        comboBox.addActionListener(e -> selecionado[0] = (String) comboBox.getSelectedItem());

        buscarButton.addActionListener(e -> {
            String campo = "";
            if ("Proprietário".equals(selecionado[0])) {
                campo = "proprietario";
            } else if ("ID".equals(selecionado[0])) {
                campo = "id";
            } else if ("Número do condomínio".equals(selecionado[0])) {
                campo = "numero";
            }

            String valorBusca = buscarText.getText();
            ArrayList<Condominios> condominiosEncontrados = ConexaoSQL.buscarCondominios(campo, valorBusca);

            panelEdicao.removeAll();
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelEdicao.add(panelBusca, gbc);
            int row = 1;

            if (!condominiosEncontrados.isEmpty()) {
                for (Condominios condominio : condominiosEncontrados) {
                    JTextField proprietarioText = new JTextField(condominio.getProprietario(), 10);
                    JTextField numeroText = new JTextField(String.valueOf(condominio.getNumero()), 10);

                    JLabel idLabel = new JLabel("ID: " + condominio.getId());
                    JLabel proprietarioLabel = new JLabel("Proprietário: ");
                    JLabel numeroLabel = new JLabel("Número: ");
                    JButton editarButton = new JButton("Editar");
                    JButton excluirButton = new JButton("Excluir");

                    gbc.gridx = 0;
                    gbc.gridy = row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(idLabel, gbc);

                    gbc.gridy = ++row;
                    panelEdicao.add(proprietarioLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(proprietarioText, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = ++row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(numeroLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(numeroText, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = ++row;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(editarButton, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(excluirButton, gbc);

                    editarButton.addActionListener(ev -> {
                        try {
                            boolean sucesso = ConexaoSQL.editarCondominio(
                                    condominio.getId(),
                                    proprietarioText.getText(),
                                    Integer.parseInt(numeroText.getText())
                            );
                            if (sucesso) {
                                JOptionPane.showMessageDialog(null, "Condomínio editado com sucesso!");
                                buscarButton.doClick(); // Atualiza a lista após a edição
                            } else {
                                JOptionPane.showMessageDialog(null, "Erro ao editar o condomínio.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erro ao editar: " + ex.getMessage());
                        }
                    });

                    excluirButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Exibe a caixa de diálogo de confirmação
                            int resposta = JOptionPane.showConfirmDialog(null,
                                    "Tem certeza de que deseja excluir este condomínio?",
                                    "Confirmar Exclusão",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            // Se o usuário clicar em "Sim", executa a exclusão
                            if (resposta == JOptionPane.YES_OPTION) {
                                try {
                                    boolean sucesso = ConexaoSQL.excluirCondominio(condominio.getId());

                                    if (sucesso) {
                                        JOptionPane.showMessageDialog(null, "Condomínio excluído com sucesso!");
                                        // Atualiza a lista de condomínios ou retorna à tela anterior
                                        buscarButton.doClick(); // Atualiza a lista de condomínios
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Erro ao excluir o condomínio.");
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex.getMessage());
                                }
                            } else {
                                // Caso o usuário cancele a exclusão, exibe uma mensagem
                                JOptionPane.showMessageDialog(null, "Exclusão cancelada.");
                            }
                        }
                    });


                    gbc.gridy = row++;
                    panelEdicao.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
                }
            }

            panelEdicao.revalidate();
            panelEdicao.repaint();
        });

        JScrollPane scrollPane = new JScrollPane(panelEdicao);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        panelCondominio.add(scrollPane, "Edição");
        cardLayout.show(panelCondominio, "Edição");
    }



    public void listar(JPanel panelCondominio, CardLayout cardLayout) {
        JPanel panelListar = new JPanel(new BorderLayout());
        JPanel panelTabela = ConexaoSQL.listarCondominios();
        JTable tabelaCondominio = (JTable) ((JScrollPane) panelTabela.getComponent(0)).getViewport().getView();

        tabelaCondominio.setDefaultRenderer(Object.class, new CondominiumRenderer());
        tabelaCondominio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabelaCondominio.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    int condominioId = (int) tabelaCondominio.getValueAt(row, 0);
                    listarBoletosPorCondominio(condominioId);
                }
            }
        });

        panelListar.add(panelTabela, BorderLayout.CENTER);
        panelCondominio.add(panelListar, "Listar");
        cardLayout.show(panelCondominio, "Listar");
    }


    static class CondominiumRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            DefaultTableModel modeloTabela = (DefaultTableModel) table.getModel();
            int condominioId = (int) modeloTabela.getValueAt(row, 0);

            boolean temBoletosPendentes = ConexaoSQL.temBoletosPendentes(condominioId);

            if (temBoletosPendentes) {
                c.setBackground(new Color(255, 200, 200));
            } else {
                c.setBackground(Color.WHITE);
            }

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
            }

            return c;
        }
    }



    public void listarBoletosPorCondominio(int condominioId) {
        JFrame frameBoletos = new JFrame("Boletos do Condomínio " + condominioId);
        frameBoletos.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameBoletos.setSize(700, 400);
        frameBoletos.setLocationRelativeTo(null);

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Mês de Competência", "Data de Emissão", "Data de Vencimento", "Valor", "Data de Pagamento", "Ação"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        java.util.List<Object[]> boletos = (java.util.List<Object[]>) ConexaoSQL.listarBoletosPorCondominio(condominioId);


        if (((java.util.List<?>) boletos).isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum boleto encontrado para o condomínio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Object[] boleto : boletos) {
            modeloTabela.addRow(boleto);
        }

        JTable tabelaBoletos = new JTable(modeloTabela);
        tabelaBoletos.getColumn("Ação").setCellRenderer(new ButtonRenderer());
        tabelaBoletos.getColumn("Ação").setCellEditor(new ButtonEditor(new JCheckBox(), modeloTabela, condominioId));

        JScrollPane scrollPaneBoletos = new JScrollPane(tabelaBoletos);
        frameBoletos.add(scrollPaneBoletos, BorderLayout.CENTER);
        frameBoletos.setVisible(true);
    }


    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private boolean isPushed;
        private final DefaultTableModel modeloTabela;
        private int row;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel modeloTabela, int condominioId) {
            super(checkBox);
            this.modeloTabela = modeloTabela;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    pagarBoleto(row, condominioId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value == null ? "" : value.toString());
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return button.getText();
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }


        private void pagarBoleto(int row, int condominioId) {
            int boletoId = (int) modeloTabela.getValueAt(row, 0);

            Object dataColuna = modeloTabela.getValueAt(row, 3);
            LocalDate dataVencimento;
            try {
                dataVencimento = LocalDate.parse(dataColuna.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(button, "Erro ao interpretar a data de vencimento: " + dataColuna);
                return;
            }

            float valorOriginal = Float.parseFloat(modeloTabela.getValueAt(row, 4).toString());
            String dataPagamentoStr = JOptionPane.showInputDialog(
                    button,
                    "Informe a data do pagamento (formato dd/MM/yyyy):",
                    "Data de Pagamento",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (dataPagamentoStr == null || dataPagamentoStr.isEmpty()) {
                JOptionPane.showMessageDialog(button, "Pagamento cancelado.");
                return;
            }

            LocalDate dataPagamento;
            try {
                dataPagamento = LocalDate.parse(dataPagamentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(button, "Data inválida. Use o formato dd/MM/yyyy.");
                return;
            }

            double valorComDesconto = valorOriginal;
            if (dataPagamento.isBefore(dataVencimento.minusDays(2))) {
                valorComDesconto = valorOriginal * 0.95; // 5% de desconto
            }

            boolean sucesso = ConexaoSQL.atualizarBoletoPagamento(boletoId, condominioId, dataPagamento, valorComDesconto);

            if (sucesso) {
                JOptionPane.showMessageDialog(
                        button,
                        "Boleto " + boletoId + " pago com sucesso pelo condomínio " + condominioId +
                                ". Valor: " + valorComDesconto + " na data: " + dataPagamento
                );
            } else {
                JOptionPane.showMessageDialog(button, "Erro ao pagar o boleto.");
            }
        }
    }


    private int getNumero() {
        return numero;
    }

    private String getProprietario() {
        return proprietario;
    }

    private int getId() {
        return id;
    }


}

