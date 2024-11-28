import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.*;
import java.util.ArrayList;


public class Boletos {
    private JTextField dataEmissaoText, mesCompetenciaText, valorText, dataVencimentoText;
    private JLabel dataEmissaoLabel, mesCompetenciaLabel, valorLabel, dataVencimentoLabel;

    private int id;
    private String mesCompetencia;
    private LocalDate dataEmissao;
    private float valor;
    private LocalDate dataVencimento;


    public Boletos(int id, String mesCompetencia, LocalDate dataEmissao, float valor, LocalDate dataVencimento) {
        this.id = id;
        this.mesCompetencia = mesCompetencia;
        this.dataEmissao = dataEmissao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
    }

    public void cadastrar(JPanel panelBoleto, CardLayout cardLayout) {
        JPanel panelCadastro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        panelCadastro.setPreferredSize(new Dimension(500, 400));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dataEmissaoText = new JTextField("dd/mm/aaaa", 10);
        mesCompetenciaText = new JTextField(10);
        valorText = new JTextField(10);
        dataVencimentoText = new JTextField("dd/mm/aaaa", 10);

        dataEmissaoLabel = new JLabel("Data da emissão: ");
        mesCompetenciaLabel = new JLabel("Mês de competência: ");
        valorLabel = new JLabel("Valor: ");
        dataVencimentoLabel = new JLabel("Data de vencimento: ");

        dataEmissaoText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dataEmissaoText.getText().equals("dd/mm/aaaa")) {
                    dataEmissaoText.setText("");
                    dataEmissaoText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dataEmissaoText.getText().isEmpty()) {
                    dataEmissaoText.setText("dd/mm/aaaa");
                    dataEmissaoText.setForeground(Color.GRAY);
                }
            }
        });

        dataVencimentoText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dataVencimentoText.getText().equals("dd/mm/aaaa")) {
                    dataVencimentoText.setText("");
                    dataVencimentoText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dataVencimentoText.getText().isEmpty()) {
                    dataVencimentoText.setText("dd/mm/aaaa");
                    dataVencimentoText.setForeground(Color.GRAY);
                }
            }
        });

        JButton salvarButton = new JButton("Salvar");
        JButton limparButton = new JButton("Limpar");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCadastro.add(dataEmissaoLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(dataEmissaoText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(mesCompetenciaLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(mesCompetenciaText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(valorLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(valorText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(dataVencimentoLabel, gbc);
        gbc.gridx = 1;
        panelCadastro.add(dataVencimentoText, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCadastro.add(salvarButton, gbc);
        gbc.gridx = 1;
        panelCadastro.add(limparButton, gbc);

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mesCompetencia = mesCompetenciaText.getText();
                    dataEmissao = LocalDate.parse(dataEmissaoText.getText(), dateFormatter);
                    valor = Float.parseFloat(valorText.getText());
                    dataVencimento = LocalDate.parse(dataVencimentoText.getText(), dateFormatter);

                    ConexaoSQL.adicionarBoletoParaTodosCondominios(mesCompetencia, dataEmissao, valor, dataVencimento);


                    JOptionPane.showMessageDialog(null, "Boleto salvo com sucesso!");
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar: Formato de data inválido! Use dd/MM/yyyy.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex.getMessage());
                }
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataEmissaoText.setText("");
                mesCompetenciaText.setText("");
                valorText.setText("");
                dataVencimentoText.setText("");
            }
        });
        panelBoleto.add(panelCadastro, "Cadastro");
        cardLayout.show(panelBoleto, "Cadastro");
    }


    public void editar(JPanel panelBoleto, CardLayout cardLayout) {
        JPanel panelEdicao = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre componentes

        final String[] selecionado = new String[1];
        String[] opcoesBusca = {"Mês de Competência", "ID", "Valor"};

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
            if ("Mês de Competência".equals(selecionado[0])) {
                campo = "bol_mesCompetencia";
            } else if ("ID".equals(selecionado[0])) {
                campo = "bol_id";
            } else if ("Valor".equals(selecionado[0])) {
                campo = "bol_valor";
            }

            String valorBusca = buscarText.getText();
            ArrayList<Boletos> boletosEncontrados = ConexaoSQL.buscarBoletos(campo, valorBusca);

            panelEdicao.removeAll();
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelEdicao.add(panelBusca, gbc);
            int row = 1;

            if (!boletosEncontrados.isEmpty()) {
                for (Boletos boleto : boletosEncontrados) {
                    JTextField mesCompetenciaText = new JTextField(boleto.getMesCompetencia(), 10);
                    JTextField valorText = new JTextField(String.valueOf(boleto.getValor()), 10);
                    JTextField dataEmissaoText = new JTextField(boleto.getDataEmissao().toString(), 10);
                    JTextField dataVencimentoText = new JTextField(boleto.getDataVencimento().toString(), 10);

                    JLabel idLabel = new JLabel("ID: " + boleto.getId());
                    JLabel mesCompetenciaLabel = new JLabel("Mês de Competência: ");
                    JLabel valorLabel = new JLabel("Valor: ");
                    JLabel dataEmissaoLabel = new JLabel("Data de Emissão: ");
                    JLabel dataVencimentoLabel = new JLabel("Data de Vencimento: ");
                    JButton editarButton = new JButton("Editar");
                    JButton excluirButton = new JButton("Excluir");

                    gbc.gridx = 0;
                    gbc.gridy = row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(idLabel, gbc);

                    gbc.gridy = ++row;
                    panelEdicao.add(mesCompetenciaLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(mesCompetenciaText, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = ++row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(valorLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(valorText, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = ++row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(dataEmissaoLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(dataEmissaoText, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = ++row;
                    gbc.anchor = GridBagConstraints.LINE_START;
                    panelEdicao.add(dataVencimentoLabel, gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelEdicao.add(dataVencimentoText, gbc);

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
                            boolean sucesso = ConexaoSQL.editarBoleto(
                                    boleto.getId(),
                                    mesCompetenciaText.getText(),
                                    Float.parseFloat(valorText.getText()),
                                    LocalDate.parse(dataEmissaoText.getText()),
                                    LocalDate.parse(dataVencimentoText.getText())
                            );
                            if (sucesso) {
                                JOptionPane.showMessageDialog(null, "Boleto editado com sucesso!");
                                buscarButton.doClick(); // Atualiza a lista após a edição
                            } else {
                                JOptionPane.showMessageDialog(null, "Erro ao editar o boleto.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erro ao editar: " + ex.getMessage());
                        }
                    });

                    excluirButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            int resposta = JOptionPane.showConfirmDialog(null,
                                    "Tem certeza de que deseja excluir este boleto de todos os condomínios?",
                                    "Confirmar Exclusão",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            if (resposta == JOptionPane.YES_OPTION) {
                                try {
                                    boolean sucesso = ConexaoSQL.excluirBoleto(boleto.getId());

                                    if (sucesso) {
                                        JOptionPane.showMessageDialog(null, "Boleto excluído de todos os condomínios com sucesso!");
                                        buscarButton.doClick();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Erro ao excluir o boleto.");
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
        scrollPane.setPreferredSize(new Dimension(400, 300)); // Define o tamanho do painel com rolagem
        panelBoleto.add(scrollPane, "Edição");
        cardLayout.show(panelBoleto, "Edição");
    }


    public void listar(JPanel panelBoletos, CardLayout cardLayout) {
        JPanel panelListar = new JPanel(new BorderLayout());
        JPanel panelTabela = ConexaoSQL.listarBoletos();

        panelListar.add(panelTabela, BorderLayout.CENTER);
        panelBoletos.add(panelListar, "Listar");
        cardLayout.show(panelBoletos, "Listar");
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public float getValor() {
        return valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }


    public int getId() {
        return id;
    }

    public String getMesCompetencia() {
        return mesCompetencia;
    }



}
