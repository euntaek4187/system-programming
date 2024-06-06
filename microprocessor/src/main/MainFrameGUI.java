package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
public class MainFrameGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	public MicroProcessor microprocessor;
	public CPU cpu;
	public Memory memory;
	ArrayList<Integer> memorySlot;
	//panel
	public JPanel panel1;
	public JPanel panel2;
	public JPanel panel3;
	public JPanel panel4;
	public JPanel panel5;
	// attribute
	JTextArea instructionArea;
	JTable codeSegmentTable;
	JTextArea dataSegmentArea;
	JTextArea stackSegmentArea;
	JTextArea explanationTextArea;
	public JButton button;
	ArrayList<JTextField> registerTextField;
	// main
	public MainFrameGUI(MicroProcessor microprocessor) {
		// basic  settings
		this.microprocessor = microprocessor;
		this.cpu = microprocessor.getCPU();
		this.memory = microprocessor.getMemory();
		this.setSize(1000, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setTitle("60221320 - Choi Eun Tack - Computer Simulation");
        microprocessor.initialize();
        this.memorySlot = memory.getMemory();
        
        // main source settings
        panel1 = new JPanel(new GridLayout(10, 1));
        panel2 = new JPanel(new GridLayout(CPU.ERegisters.values().length, 2));
        panel3 = new JPanel();
        panel4 = new JPanel(new GridLayout(4, 1));
        panel5 = new JPanel(new BorderLayout());
        
        // panel1
        panel1.setBorder(BorderFactory.createTitledBorder("Operating toolbox"));
        panel1.add(new JLabel("Main Processing"));
        button = new JButton("nextStep");
        button.addActionListener(e -> nextBtn());
        panel1.add(button);

        // panel2
        this.registerTextField = new ArrayList<>();
        panel2.setBorder(BorderFactory.createTitledBorder("CPU Registers"));
        for (CPU.ERegisters reg : CPU.ERegisters.values()) {
        	panel2.add(new JLabel(reg.name()));
        	JTextField jTextField = new JTextField(10);
        	this.registerTextField.add(jTextField);
        	panel2.add(jTextField);
        }
        // panel3
        panel3.setBorder(BorderFactory.createTitledBorder("Explanation Info"));
        explanationTextArea = new JTextArea(7, 80); // 나중에 에러날 수도 -> 재확인
        panel3.add(explanationTextArea);

        // panel4
        panel4.add(new JLabel("test"));
        panel4.add(new JLabel("test"));

        // panel5
        JPanel panel5_1 = new JPanel(new GridLayout(1, 4));
        JPanel panel5_2 = new JPanel(new GridLayout(1, 4));
        
        this.instructionArea = new JTextArea(20, 10); // 부모영역 다 차지하고 있음. 확인 필요.. 안해도 괜찮나?
        this.instructionArea.setBorder(BorderFactory.createTitledBorder(""));
        
        this.codeSegmentTable = new JTable(new DefaultTableModel(new Object[]{"Code Segment"}, 0));
        this.codeSegmentTable.setBorder(BorderFactory.createTitledBorder(""));
        this.codeSegmentTable.setShowGrid(true);
        this.codeSegmentTable.setGridColor(Color.BLACK);

        this.dataSegmentArea = new JTextArea(20, 10);
        this.dataSegmentArea.setBorder(BorderFactory.createTitledBorder(""));
        this.stackSegmentArea = new JTextArea(20,10);
        this.stackSegmentArea.setBorder(BorderFactory.createTitledBorder(""));

        
        panel5.setBorder(BorderFactory.createTitledBorder("Memory Info"));
        panel5_1.add(new JLabel("Instruction"));
        panel5_1.add(new JLabel("Code Segment"));
        panel5_1.add(new JLabel("Data Segment"));
        panel5_1.add(new JLabel("Stack Segment"));
        
        JScrollPane scrollPane = new JScrollPane(codeSegmentTable);
        JScrollPane scrollPane2 = new JScrollPane(dataSegmentArea);

        panel5_2.add(instructionArea);
        panel5_2.add(scrollPane);
        panel5_2.add(scrollPane2);
        panel5_2.add(stackSegmentArea);

        panel5.add(panel5_1, BorderLayout.NORTH);
        panel5.add(panel5_2, BorderLayout.CENTER);
        
        // end settings
        this.add(panel1, BorderLayout.EAST);
        this.add(panel2, BorderLayout.WEST);
        this.add(panel3, BorderLayout.SOUTH);
        this.add(panel4, BorderLayout.NORTH);
        this.add(panel5, BorderLayout.CENTER);
        
        // first GUI settings
        updateCPUState();
		updateExplanation();
		updateInstructionState();
		updateCodeSegmentState();
		updateDataSegmentState();
	}
	private void nextBtn() {
		microprocessor.nextStep();
		updateCPUState();
		updateExplanation();
		updateInstructionState();
		updateCodeSegmentState();
		updateDataSegmentState();
		updateStackSegmentState();
	}
	public void updateExplanation() {
		explanationTextArea.setText(cpu.getExplanation());
    }
	public void updateCPUState() {
	    for (int i = 0; i < CPU.ERegisters.values().length; i++) {
	        JTextField textField = this.registerTextField.get(i);
	        CPU.ERegisters register = CPU.ERegisters.values()[i];
	        if (register == CPU.ERegisters.eIR) textField.setText(String.format("0x%08X", this.microprocessor.getCPU().get(register)));
	        else if(register == CPU.ERegisters.eMAR || register == CPU.ERegisters.eMBR) {
	        	String data = Integer.toString(this.microprocessor.getCPU().get(register));
	        	if(data.length() >= 8) textField.setText(String.format("0x%08X", this.microprocessor.getCPU().get(register)));
				else if (data.length() >= 4) textField.setText(String.format("0x%04X", this.microprocessor.getCPU().get(register)));
				else textField.setText(String.valueOf(this.microprocessor.getCPU().get(register)));
	        } else textField.setText(String.valueOf(this.microprocessor.getCPU().get(register)));
	    }
	}
	private void updateInstructionState() {
		int opCode = cpu.getOpCode();
	    int operand1 = cpu.getOperand1();
	    int operand2 = cpu.getOperand2();
	    int constant = cpu.getConstant();
	    int storeAddress = cpu.getStoreAddress();
	    int storeOperand2 = cpu.getStoreOperand2();
	    int labelAddress = cpu.getLabelAddress();
	    String settingText = "";
	    settingText += ""+String.format("0x%08X", this.cpu.get(CPU.ERegisters.eIR))+"\n"+"\n";
	    if (opCode != -1) settingText += String.format("0x%02X", opCode)+": "+cpu.getOpcodeByIndex(opCode)+"\n";
	    if (operand1 != -1) settingText += String.format("0x%02X", operand1)+": "+cpu.getERegisterByIndex(operand1)+"\n";
	    if (operand2 != -1) settingText += String.format("0x%02X", operand2)+": "+cpu.getERegisterByIndex(operand2)+"\n";
	    if (constant != -1) settingText += String.format("0x%04X", constant)+": "+"value "+constant+"\n";
	    if (storeAddress != -1) settingText += String.format("0x%04X", storeAddress)+": "+"address "+storeAddress+"\n";
	    if (storeOperand2 != -1) settingText += String.format("0x%02X", storeOperand2)+": "+cpu.getERegisterByIndex(storeOperand2)+"\n";
	    if (labelAddress != -1) settingText += String.format("0x%02X", labelAddress)+": "+"CS + "+labelAddress+"\n";
		this.instructionArea.setText(settingText);
	}
	private void updateCodeSegmentState() {
	    DefaultTableModel model = (DefaultTableModel) codeSegmentTable.getModel();
	    model.setRowCount(0);
	    for (int i = memory.getCodeSegmentStart(); i < memory.getCodeSegmentSize(); i++) {
	        model.addRow(new Object[]{i + 1 + ". " + String.format("0x%08X", this.memorySlot.get(i))});
	    }
	    int presentInstruction = this.cpu.get(CPU.ERegisters.ePC)-1;
	    if (presentInstruction != -1) {
	        codeSegmentTable.changeSelection(presentInstruction, 0, false, false);
	    }
	}
	private void updateDataSegmentState() { // 이건 파일 읽는 것 기반으로 다시 수정해야할 듯
		String dataState = "";
		int dataSegmentStart = memory.getDataSegmentStart();
		for (int i = dataSegmentStart; i < dataSegmentStart+memory.getDataSegmentSize(); i=i+4) {
			dataState += String.format("0x%04X", i)+": "+this.memorySlot.get(i)+ "\n";
		}
		this.dataSegmentArea.setText(dataState);
	}
	private void updateStackSegmentState() {
		String stackState = "";
		for (int i = memory.getStackSegmentStart(); memorySlot.get(i) != -1; i++) {
			stackState += this.memorySlot.get(i)+ "\n";
		}
		this.stackSegmentArea.setText(stackState);
	}
}