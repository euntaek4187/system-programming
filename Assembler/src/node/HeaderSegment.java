package node;
import java.io.BufferedWriter;
import java.io.IOException;

import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import opcode.EKeyword;
import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class HeaderSegment extends Node{
	private int sizeStack;
	private int sizeHeap;
	public HeaderSegment(LexicalAnalyzer lexicalAnalyzer, SymbolTable simbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, simbolTable, codeGenerator);
	}
	public void initialize() {}
	@Override
	public String parse(String token) throws Exception {
		String keyword = lexicalAnalyzer.getToken();
		while(!keyword.equals(EKeyword.eData.getText())) {
			Symbol simbol = symbolTable.retrieveSymbol(keyword);
			String size = lexicalAnalyzer.getToken();
			simbol.setSize(size);
			if (keyword.equals(EKeyword.eStack.getText())) {
				this.sizeStack = Integer.parseInt(size);
			} else if (keyword.equals(EKeyword.eHeap.getText())) {
				this.sizeHeap = Integer.parseInt(size);
			} else {
				throw new Exception();
			}
			keyword = lexicalAnalyzer.getToken();
		}
		return keyword;
	}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException {
		fileWriter.write(sizeStack+"\n");
		fileWriter.write(sizeHeap+"\n");
		fileWriter.flush();
	}
}