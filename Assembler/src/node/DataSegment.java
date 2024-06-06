package node;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import opcode.EKeyword;
import symbolTable.Symbol;
import symbolTable.SymbolTable;

public class DataSegment extends Node{
	public DataSegment(LexicalAnalyzer lexicalAnalyzer, SymbolTable symbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, symbolTable, codeGenerator);
	}
	public void initialize() {

	}
	@Override
	public String parse(String token) throws Exception {
		int offset = 0;
		String name = lexicalAnalyzer.getToken();
		while(!name.equals(EKeyword.eCode.getText())) {
			String size = lexicalAnalyzer.getToken();
			
			Symbol symbol = symbolTable.retrieveSymbol(name);
			symbol.setType("data");
			symbol.setSize("4");
			symbol.setInitialValue(size);
			symbol.setOffset(Integer.toString(offset));
			offset += 4;
			
			name = lexicalAnalyzer.getToken();
		}
		return name;
	}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException {
		ArrayList<Symbol> symbols = symbolTable.getSymbolByType("data");
		fileWriter.write(symbols.size()*4+"\n");
		fileWriter.flush();
	}
}