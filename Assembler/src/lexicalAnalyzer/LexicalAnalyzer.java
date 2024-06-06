package lexicalAnalyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class LexicalAnalyzer{
	private String fileName;
	private Scanner scanner;
	private SymbolTable symbolTable;
	ArrayList<String> tokens;
	int callGetTokenNum = 0;
	public LexicalAnalyzer(String fileName, SymbolTable symbolTable) {
		this.fileName = fileName;
		this.symbolTable = symbolTable;
		this.tokens = new ArrayList<>();
	}
	public void initialize() throws FileNotFoundException {
		this.scanner = new Scanner(new File("source/"+fileName+".txt"));
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if (line.equals("")) continue;
			Symbol symbol = new Symbol();
			if (line.contains(".")) {
				symbol.setToken(line.split(" ")[0]);
				symbol.setType("segment");
			} else if (line.contains(":")) {
				symbol.setToken(line.substring(0, line.indexOf(":")).trim());
				symbol.setType("label");
				symbol.setOffset(Integer.toString(0));
			} else {
				symbol.setToken(line.split(" ")[0]);
				symbol.setType("keyword");
			}
			this.symbolTable.add(symbol);
        	String[] tokenBox = line.split(" ");
			for (String token : tokenBox) tokens.add(token);
		}
	}
	public String getToken() {
		String token = tokens.get(callGetTokenNum);
		if (token.equals("nop")) {
			callGetTokenNum++;
			token = tokens.get(callGetTokenNum);
		}
		callGetTokenNum++;
		return token;
	}
	public void finalize() {
		this.scanner.close();
	}
	public void finish() {
	}
}