package main;
import java.io.FileNotFoundException;
import java.io.IOException;

import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import parser.Parser;
import symbolTable.SymbolTable;
public class Assembler {
    private LexicalAnalyzer lexicalAnalyzer;
    private Parser parser;
    private CodeGenerator codeGenerator;
    private SymbolTable symbolTabel;
    public Assembler() {
    	this.symbolTabel = new SymbolTable();
    	this.codeGenerator = new CodeGenerator("binary/codeGeneration.txt", symbolTabel);
    	this.lexicalAnalyzer = new LexicalAnalyzer("test", this.symbolTabel);
    	this.parser = new Parser(this.lexicalAnalyzer, symbolTabel, codeGenerator);
    }
	public void run() {
		try {
			this.parser.parse(null);
			this.parser.generate(null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void initialize() throws FileNotFoundException {
		this.lexicalAnalyzer.initialize();
		this.parser.initialize();
		this.codeGenerator.initialize();
	}
	public void finish() throws IOException {
		this.lexicalAnalyzer.finish();
		this.parser.finish();
		this.codeGenerator.finish();
	}
}