package node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;
import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import symbolTable.SymbolTable;
public class Instruction extends Node {
	ECommand eCommand;
	private String opcode = null;
	private String operand1 = null;
	private String operand2 = null;
	private String label = null;
	private boolean isLabel = false;
	private Vector<Instruction> instructions;
	public enum ECommand {
		eHalt("halt"),
		eLda("lda"),
		eSta("sta"),
		eAdd("add"),
		eAnd("and"),
		eJump("jump"),
		eZero("zero"),
		eBZ("bz"),
		eCmp("cmp"),
		eNot("not"),
		eShr("shr"),
		eMove("move"),
		eMovec("movec"),
		ePush("push"),
		ePop("pop"),
		eGE("ge"),
		eEnd(".end");
		private String text;
		private ECommand(String text) {
			this.text = text;
		}
		public String getText() {
			return this.text;
		}
		public static ECommand fromString(String text) {
			for (ECommand command : ECommand.values()) {
				if (command.getText().equalsIgnoreCase(text)) {
					return command;
				}
			}
			return null;
		}
	}
	public boolean getIsLabel() {return isLabel;}
	public Instruction(LexicalAnalyzer lexicalAnalyzer, SymbolTable simbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, simbolTable, codeGenerator);
	}
	public void association(Vector<Instruction> instructions) {
		this.instructions = instructions;
	}
    private void updateInstructionOffset() {
		Instruction instruction = new Instruction(lexicalAnalyzer, symbolTable, codeGenerator);
		instructions.add(instruction);
		instructions.add(instruction);
		instructions.add(instruction);
	}
	public void initialize() {
	}
	@Override
	public String parse(String token) throws Exception {
		if (token.contains(":")) isLabel = true;
		this.eCommand = ECommand.fromString(token);
		String nextToken = null;
		if (token.contains(":")) {
			token = lexicalAnalyzer.getToken();
			return token;
		}
		if (eCommand != null) {
			symbolTable.setTypes(token, "command");
			this.opcode = eCommand.getText();
		}
		if (token.equals(ECommand.eHalt.getText())) {
			nextToken = this.lexicalAnalyzer.getToken();
		} else if(token.equals(ECommand.eNot.getText()) || token.equals(ECommand.eShr.getText())|| token.equals(ECommand.ePush.getText())|| token.equals(ECommand.ePop.getText())) {
			nextToken = this.lexicalAnalyzer.getToken();
			this.operand1 = nextToken;
			nextToken = this.lexicalAnalyzer.getToken();
		} else if (token.equals(ECommand.eGE.getText()) || token.equals(ECommand.eJump.getText()) || token.equals(ECommand.eBZ.getText())|| token.equals(ECommand.eZero.getText())) {
			nextToken = this.lexicalAnalyzer.getToken();
			this.label = nextToken;
			nextToken = this.lexicalAnalyzer.getToken();
		} else {
			nextToken = this.lexicalAnalyzer.getToken();
			if (symbolTable.retrieveSymbol(nextToken) != null) {
				updateInstructionOffset();
			}
			this.operand1 = nextToken;
			nextToken = this.lexicalAnalyzer.getToken();
			if (symbolTable.retrieveSymbol(nextToken) != null) {
				updateInstructionOffset();
			}
			this.operand2 = nextToken;
			nextToken = this.lexicalAnalyzer.getToken();
		}
		return nextToken;
	}
	public ECommand geteCommand() {
		return eCommand;
	}
	public String getOpcode() {
		return opcode;
	}
	public String getOperand1() {
		return operand1;
	}
	public String getOperand2() {
		return operand2;
	}
	public String getLabel() {
		return label;
	}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException {
		int register = -1;
		int register2 = -1;
		int constant = -1;
		int variable = -1;
		if(this.opcode == null) {
		} else if (this.opcode.equals("halt")) {
			codeGenerator.macroExpansion0operand(this.opcode);
		} else if(this.opcode.contains("not") || this.opcode.contains("shr")|| this.opcode.contains("push")|| this.opcode.contains("pop")) {
            register = Integer.parseInt(this.operand1.replace("r", "").trim());
			codeGenerator.macroExpansion1operand(this.opcode, register);
		} else if (this.opcode.contains("ge") || this.opcode.contains("jump")|| this.opcode.contains("zero")|| this.opcode.contains("bz")) {
			codeGenerator.macroExpansionLabelJump(opcode, label);
		} else if (symbolTable.retrieveSymbol(operand1) != null) { // sta
            variable =  Integer.parseInt(symbolTable.retrieveSymbol(operand1).getOffset()) + 1024;
            register = Integer.parseInt(this.operand2.replace("r", "").trim());
            codeGenerator.macroExpansionRegisterToMemory(variable, register);
		} else if (symbolTable.retrieveSymbol(operand2) != null) { // lda
            register = Integer.parseInt(this.operand1.replace("r", "").trim());
            variable =  Integer.parseInt(symbolTable.retrieveSymbol(operand2).getOffset()) + 1024;
            codeGenerator.macroExpansionMemoryToRegister(register, variable);
		} else if (this.operand1.contains("r") && this.operand2.contains("r")) {
            register = Integer.parseInt(this.operand1.replace("r", "").trim());
            register2 = Integer.parseInt(this.operand2.replace("r", "").trim());
            codeGenerator.macroExpansionRegisterToRegister(this.opcode, register, register2);
		} else {
            register = Integer.parseInt(this.operand1.replace("r", "").trim());
            constant = Integer.parseInt(this.operand2);
            codeGenerator.macroExpansionConstantToRegister(register, constant);
		}
		fileWriter.flush();
	}
}