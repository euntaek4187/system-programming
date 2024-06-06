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