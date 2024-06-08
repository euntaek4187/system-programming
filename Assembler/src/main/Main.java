package main;
import java.io.FileNotFoundException;
import java.io.IOException;
public class Main {
	public static void main(String[] args) throws IOException {
		Main main = new Main();
		main.initialize();
		main.run();
		main.finish();
	}
	private Assembler assembler;
	public Main() {
		this.assembler = new Assembler();
	}
	private void initialize() throws FileNotFoundException {
		assembler.initialize();
	}
	private void run() {
		assembler.run();
	}
	private void finish() throws IOException {
		assembler.finish();
	}
}
// ver.1 - lex 수정
// ver.2 - gui 완성, 코드 깔끔하게 정리, bz &zero 로직 추가
// ver.3 - initialize()로 코드 분리