package main;
import java.io.FileNotFoundException;
import java.io.IOException;

import GUI.MainFrameGUI;
import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import parser.Parser;
import symbolTable.SymbolTable;
public class Assembler {
    private LexicalAnalyzer lexicalAnalyzer;
    private Parser parser;
    private CodeGenerator codeGenerator;
    private SymbolTable symbolTabel;
    private MainFrameGUI mainFrameGUI;
    private String binaryCodeFile = "binary/codeGeneration.txt";
    private String assemblyCodeFileName = "test2";
    public Assembler() {
    	this.symbolTabel = new SymbolTable();
    	this.mainFrameGUI = new MainFrameGUI(binaryCodeFile, assemblyCodeFileName, symbolTabel);
    	this.codeGenerator = new CodeGenerator(binaryCodeFile, symbolTabel, this.mainFrameGUI);
    	this.lexicalAnalyzer = new LexicalAnalyzer(assemblyCodeFileName, this.symbolTabel, this.mainFrameGUI);
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
		this.mainFrameGUI.initialize();
		this.mainFrameGUI.setVisible(true);
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