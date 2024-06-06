package node;

import java.io.BufferedWriter;
import java.io.IOException;

import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import symbolTable.SymbolTable;

public abstract class Node{
	protected LexicalAnalyzer lexicalAnalyzer;
	protected SymbolTable symbolTable;
	protected CodeGenerator codeGenerator;
	public Node(LexicalAnalyzer lexicalAnalyzer, SymbolTable symbolTable, CodeGenerator codeGenerator) {
		this.lexicalAnalyzer = lexicalAnalyzer;
		this.symbolTable = symbolTable;
		this.codeGenerator = codeGenerator;
	}
	public abstract String parse(String token) throws Exception;
	public abstract void generate(BufferedWriter fileWriter) throws IOException ;
}