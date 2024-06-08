package node;
import java.io.BufferedWriter;
import java.io.IOException;
import codeGenerator.CodeGenerator;
import lexicalAnalyzer.LexicalAnalyzer;
import opcode.EKeyword;
import symbolTable.SymbolTable;
public class Program extends Node{
	private String name;
	private HeaderSegment headerSegment;
	private CodeSegment codeSegment;
	private DataSegment dataSegment;
	public Program(LexicalAnalyzer lexicalAnalyzer, SymbolTable symbolTable, CodeGenerator codeGenerator) {
		super(lexicalAnalyzer, symbolTable, codeGenerator);
	}
	public void initialize() {}
	@Override
	public String parse(String token) throws Exception {
		this.name = lexicalAnalyzer.getToken();
		token = lexicalAnalyzer.getToken();
		if (token.equals(EKeyword.eHeader.getText())) {
			this.headerSegment = new HeaderSegment(lexicalAnalyzer, symbolTable, codeGenerator);
			token = headerSegment.parse(token);
		} else throw new Exception();
		if (token.equals(EKeyword.eData.getText())) {
			this.dataSegment = new DataSegment(lexicalAnalyzer, symbolTable, codeGenerator);
			token = dataSegment.parse(token);
		} else throw new Exception();
		if (token.equals(EKeyword.eCode.getText())) {
			this.codeSegment = new CodeSegment(lexicalAnalyzer, symbolTable, codeGenerator);
			token = codeSegment.parse(token);
		} else throw new Exception();
		return null;
	}
	public HeaderSegment getHeaderSegment() {return headerSegment;}
	public CodeSegment getCodeSegment() {return codeSegment;}
	public DataSegment getDataSegment() {return dataSegment;}
	public LexicalAnalyzer getLexicalAnalyzer() {return lexicalAnalyzer;}
	@Override
	public void generate(BufferedWriter fileWriter) throws IOException  {
		fileWriter.write(name+"\n");
		fileWriter.flush();
		this.headerSegment.generate(fileWriter);
        this.dataSegment.generate(fileWriter);
        // 2pass compiler
        this.codeSegment.generate(fileWriter);
        this.codeSegment.generate(fileWriter);
	}
}