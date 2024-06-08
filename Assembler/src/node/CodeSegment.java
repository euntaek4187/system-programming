package node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import opcode.EKeyword;
import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class CodeSegment extends Node{
	private Vector<Instruction> instructions;
	private boolean isSymbolTabelSetting = false;
	public CodeSegment(LexicalAnalyzer lexicalAnalyzer, SymbolTable symbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, symbolTable, codeGenerator);
		this.instructions = new Vector<>();
	}
	public void initialize() {}
	public Vector<Instruction> getInstructions(){
		return instructions;
	}
	@Override
	public String parse(String token) throws Exception {
		String command = lexicalAnalyzer.getToken();
		while(!command.equals(EKeyword.eEnd.getText())) {
			Instruction instruction = new Instruction(lexicalAnalyzer, symbolTable, codeGenerator);
			instruction.association(instructions);
			command = instruction.parse(command);
			instructions.add(instruction);
		}
		return command;
	}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException {
		if (!isSymbolTabelSetting) {
			ArrayList<Symbol> symbols = symbolTable.getSymbolByType("label");
			for(Symbol symbol : symbols) {
				for(int i = 0; i < instructions.size(); i++) {
					if (instructions.get(i).getIsLabel()) {
						instructions.remove(i);
						symbol.setOffset(Integer.toString(Integer.parseInt(symbol.getOffset())+i+1));
						break;
					}
				}
			}
			fileWriter.write(instructions.size()+"\n");
			fileWriter.flush();
			this.isSymbolTabelSetting = true;
		} else {
			for(Instruction instruction : instructions) {
				instruction.generate(fileWriter);
			}
		}
	}
}