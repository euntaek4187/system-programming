package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class MainFrameGUI extends JFrame {
    private SymbolTable symbolTable;
    private String binaryCodeFile;
    private String assemblyCodeFile;
    // panel
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    // attribute
    private JTextField jTextField;
    private JTextArea assemblyFileArea;
    private JTextArea exeFileArea;
    private JTable symbolJTable;
    private JButton jButton;
    private CountDownLatch latch;
//	Color backgroundColor = new Color(222, 222, 220);
    public MainFrameGUI(String binaryCodeFile, String assemblyCodeFile, SymbolTable symbolTable) {
        // basic setting
        this.binaryCodeFile = binaryCodeFile;
        this.assemblyCodeFile = "source/" + assemblyCodeFile + ".txt";
        this.symbolTable = symbolTable;
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setTitle("60221320 - Choi Eun Taek - Compiler Simulation");

        // main source settings
        this.panel1 = new JPanel();
        this.panel2 = new JPanel();
        this.panel3 = new JPanel();
        this.panel4 = new JPanel();
        this.panel5 = new JPanel(new GridLayout(1, 4));
        // panel1
        this.panel1.add(new Label("       "));
        // panel2
        this.panel2.add(new Label("       "));
        // panel3
        this.panel3.add(new Label("       "));
        // panel4
        this.jTextField = new JTextField(10);
        this.jButton = new JButton("Next Step");
        this.jButton.addActionListener(e -> nextTokenClicked());
        this.panel4.add(new JLabel("Token: "));
        this.panel4.add(jTextField);
        this.panel4.add(jButton);
        // panel5
        JPanel panel5_1 = new JPanel(new GridLayout(1, 1));
        panel5_1.setBorder(BorderFactory.createTitledBorder("Assembler File"));
        this.assemblyFileArea = new JTextArea();
        JScrollPane scrollPane3 = new JScrollPane(assemblyFileArea);
        panel5_1.add(scrollPane3);
        this.panel5.add(panel5_1);
        JPanel panel5_2 = new JPanel(new GridLayout(1, 1));
        panel5_2.setBorder(BorderFactory.createTitledBorder("Exe File"));
        this.exeFileArea = new JTextArea();
        JScrollPane scrollPane2 = new JScrollPane(exeFileArea);
        panel5_2.add(scrollPane2);
        this.panel5.add(panel5_2);
        JPanel panel5_3 = new JPanel(new GridLayout(1, 1));
        panel5_3.setBorder(BorderFactory.createTitledBorder("Symbol Table"));
        this.symbolJTable = new JTable(new DefaultTableModel(
                new Object[]{"Token", "Type", "Size", "Initial Value", "Offset"}, 0));
        JScrollPane scrollPane1 = new JScrollPane(symbolJTable);
        panel5_3.add(scrollPane1);
        this.panel5.add(panel5_3);
        // end setting
        this.add(panel1, BorderLayout.EAST);
        this.add(panel2, BorderLayout.WEST);
        this.add(panel3, BorderLayout.SOUTH);
        this.add(panel4, BorderLayout.NORTH);
        this.add(panel5, BorderLayout.CENTER);
        setAssemblyFileState();
    }
	private void nextTokenClicked() {
        if (latch != null) latch.countDown();
        updateSymbolTableState();
    }
    private void setAssemblyFileState() {
    	StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(assemblyCodeFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assemblyFileArea.setText(content.toString());
	}
    public void updateSymbolTabelGUIState(String token) {
        try {
            SwingUtilities.invokeAndWait(() -> jTextField.setText(token));
            latch = new CountDownLatch(1);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateGeneratedCodeGUIState() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                StringBuilder content = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(binaryCodeFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                exeFileArea.setText(content.toString());
            });
            latch = new CountDownLatch(1);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateSymbolTableState() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) symbolJTable.getModel();
            model.setRowCount(0);
            ArrayList<Symbol> symbols;
            synchronized(symbolTable) {
                symbols = new ArrayList<>(symbolTable.getSymbolTable());
            }
            for (Symbol symbol : symbols) {
                model.addRow(new Object[]{
                        symbol.getToken(),
                        symbol.getType(),
                        symbol.getSize(),
                        symbol.getInitialValue(),
                        symbol.getOffset()
                });
            }
        });
    }
}