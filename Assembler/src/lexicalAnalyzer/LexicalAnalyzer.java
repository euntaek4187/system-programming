package lexicalAnalyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import GUI.MainFrameGUI;
import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class LexicalAnalyzer {
    private String fileName;
    private Scanner scanner;
    private SymbolTable symbolTable;
    private String[] currentTokens;
    private int tokenIndex;
    MainFrameGUI mainFrameGUI;
    public LexicalAnalyzer(String fileName, SymbolTable symbolTable, MainFrameGUI mainFrameGUI) {
        this.fileName = fileName;
        this.symbolTable = symbolTable;
        this.mainFrameGUI = mainFrameGUI;
        this.currentTokens = new String[0];
        this.tokenIndex = 0;
    }
    public void initialize() throws FileNotFoundException {
        this.scanner = new Scanner(new File("source/" + fileName + ".txt"));
    }
    public String getToken() {
        while (tokenIndex >= currentTokens.length && scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            if (line.contains("nop")) continue;
            processLine(line);
        }
        String token = currentTokens[tokenIndex++];
        mainFrameGUI.updateSymbolTabelGUIState(token);
        return token;
    }
    private void processLine(String line) {
        tokenIndex = 0;
        if (line.contains(".")) {
            Symbol symbol = new Symbol();
            symbol.setToken(line.split(" ")[0]);
            symbol.setType("segment");
            this.symbolTable.add(symbol);
        } else if (line.contains(":")) {
            Symbol symbol = new Symbol();
            symbol.setToken(line.substring(0, line.indexOf(":")).trim());
            symbol.setType("label");
            symbol.setOffset(Integer.toString(0));
            this.symbolTable.add(symbol);
        } else {
            Symbol symbol = new Symbol();
            symbol.setToken(line.split(" ")[0]);
            symbol.setType("keyword");
            this.symbolTable.add(symbol);
        }
        currentTokens = line.split(" ");
    }
    public void finalize() {
        if (this.scanner != null) {
            this.scanner.close();
        }
    }
    public void finish() {
    }
}