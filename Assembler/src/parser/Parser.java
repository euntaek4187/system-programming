package parser;
import java.io.BufferedWriter;
import java.io.IOException;
import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import node.Node;
import node.Program;
import opcode.EKeyword;
import symbolTable.SymbolTable;
public class Parser extends Node{
	private Program program;
	public Parser(LexicalAnalyzer lexicalAnalyzer, SymbolTable symbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, symbolTable, codeGenerator);
	}
	public void initialize() {

	}
	@Override
	public String parse(String token) throws Exception{
		token = this.lexicalAnalyzer.getToken();
		if (token.equals(EKeyword.eProgram.getText())) {
			this.program = new Program(this.lexicalAnalyzer, symbolTable, codeGenerator);
			this.program.parse(token);
			return "success";
		}
		throw new Exception();
	}
	public Program getProgram() {
		return program;
	}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException {
		this.codeGenerator.codeGeneration(program);
	}
	public void associate(CodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}
	public void finish() {

	}
}