package symbolTable;
import java.util.ArrayList;
public class SymbolTable {
	ArrayList<Symbol> symbols;
	public SymbolTable() {
		this.symbols = new ArrayList<>();
	}
	public ArrayList<Symbol> getSymbolTable(){
		return symbols;
	}
	public void add(Symbol symbol) {
		symbols.add(symbol);
	}
	public Symbol retrieveSymbol(String token) {
		for(Symbol simbol : symbols) {
			if (simbol.getToken().equals(token)) {
				return simbol;
			}
		}
		return null;
	}
	public void setTypes(String token, String type) {
		for(Symbol simbol : symbols) {
			if (simbol.getToken().equals(token)) {
				simbol.setType(type);
			}
		}
	}
	public ArrayList<Symbol> getSymbolByType(String type) {
		ArrayList<Symbol> typeBySymbols = new ArrayList<>();
		for(Symbol simbol : symbols) {
			if (simbol.getType().equals(type)) {
				typeBySymbols.add(simbol);
			}
		}
		return typeBySymbols;
	}
//	public void setOffsetUpdate(ArrayList<Symbol> symbolByType) {
//		for(Symbol symbol : symbolByType) {
//			System.out.println(symbol.getOffset());
//			int originOffset = Integer.parseInt(symbol.getOffset());
//			symbol.setOffset(Integer.toString(originOffset+3));
//		}
//	}
//	public void setOffsetUpdate(int size, ArrayList<Symbol> symbolByType) {
//		for(int i = 0; i>size; i++) {
//			
//		}
//		for(int i = 0; i < symbols.size(); i++) {
//			
//		}
//	}
}