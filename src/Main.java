import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Main extends JFrame {
    public Main() throws SQLException {
        setTitle("Controle Condomínios");
        setMinimumSize(new Dimension(600, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CardLayout cardLayout = new CardLayout();

        Condominios condominio = new Condominios(0, null,0);
        Boletos boleto = new Boletos(0, null, null, 0,null);

        JTabbedPane tabbed = new JTabbedPane();

        JPanel panelCondominio = new JPanel(new BorderLayout());
        JPanel buttonPanelCondominio = new JPanel();
        JPanel cardPanelCondominio = new JPanel(cardLayout);

        JButton criarCondominio = new JButton("+ Cadastrar");
        JButton editarCondominio = new JButton("Editar");
        JButton listarCondominio = new JButton("Listar");

        criarCondominio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    condominio.cadastrar(cardPanelCondominio, cardLayout);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        editarCondominio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                condominio.editar(cardPanelCondominio, cardLayout);
            }
        });
        listarCondominio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                condominio.listar(cardPanelCondominio, cardLayout);
            }
        });
        buttonPanelCondominio.add(criarCondominio);
        buttonPanelCondominio.add(editarCondominio);
        buttonPanelCondominio.add(listarCondominio);

        panelCondominio.add(buttonPanelCondominio, BorderLayout.NORTH);
        panelCondominio.add(cardPanelCondominio, BorderLayout.CENTER);

        tabbed.add("CONDOMÍNIOS", panelCondominio);

        JPanel panelBoleto = new JPanel(new BorderLayout());
        JPanel buttonPanelBoleto = new JPanel();
        JPanel cardPanelBoleto = new JPanel(cardLayout);

        JButton criarBoleto = new JButton("+ Cadastrar");
        JButton editarBoleto = new JButton("Editar");
        JButton listarBoleto = new JButton("Listar");

        criarBoleto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boleto.cadastrar(cardPanelBoleto, cardLayout);
            }
        });
        editarBoleto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boleto.editar(cardPanelBoleto, cardLayout);
            }
        });
        listarBoleto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boleto.listar(cardPanelBoleto, cardLayout);
            }
        });
        buttonPanelBoleto.add(criarBoleto);
        buttonPanelBoleto.add(editarBoleto);
        buttonPanelBoleto.add(listarBoleto);
        
        panelBoleto.add(buttonPanelBoleto, BorderLayout.NORTH);
        panelBoleto.add(cardPanelBoleto, BorderLayout.CENTER);

        tabbed.add("BOLETOS", panelBoleto);
        add(tabbed);
    }

    public static void main(String[] args) throws SQLException {
       Main main = new Main();
       main.setVisible(true);
    }
}
