package codeGenerator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import GUI.MainFrameGUI;
import node.Program;
import opcode.EOpcode;
import symbolTable.Symbol;
import symbolTable.SymbolTable;
public class CodeGenerator {
    private String fileName;
    BufferedWriter fileWriter;
    SymbolTable symbolTable;
    MainFrameGUI mainFrameGUI;
    public void initialize() {
    }
    public CodeGenerator(String fileName, SymbolTable symbolTable, MainFrameGUI mainFrameGUI) {
        this.fileName = fileName;
        this.symbolTable = symbolTable;
        this.mainFrameGUI = mainFrameGUI;
    }
    public void codeGeneration(Program program) throws IOException {
        this.fileWriter = new BufferedWriter(new FileWriter(fileName, false));
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        this.fileWriter = new BufferedWriter(new FileWriter(fileName, true));
        program.generate(fileWriter);
    }
    public void macroExpansionMemoryToRegister(int register, int variable) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("push")) + String.format("%02X", 3) + String.format("%04X", 0) + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("lda")) + String.format("%02X", 3) + String.format("%04X", variable) + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("move")) + String.format("%02X", register) + String.format("%02X", 3) + "00" + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("pop")) + String.format("%02X", 3) + String.format("%04X", 0) + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
    public void macroExpansionRegisterToMemory(int variable, int register) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("push")) + String.format("%02X", 3) + String.format("%04X", 0) + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("move")) + String.format("%02X", 3) + String.format("%02X", register) + "00" + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("sta")) + String.format("%04X", variable) + String.format("%02X", 3) + "\n");
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("pop")) + String.format("%02X", 3) + String.format("%04X", 0) + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
    public void macroExpansionConstantToRegister(int register, int constant) throws IOException {
    	fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum("movec")) + String.format("%02X", register) + String.format("%04X", constant) + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
    public void macroExpansionRegisterToRegister(String opcode, int register, int register2) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum(opcode)) + String.format("%02X", register) + String.format("%02X", register2) + "00" + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
    public void macroExpansionLabelJump(String opcode, String label) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum(opcode)) + String.format("%02X", Integer.parseInt(symbolTable.retrieveSymbol(label).getOffset())) + "0000" + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
	public void macroExpansion1operand(String opcode, int register) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum(opcode)) + String.format("%02X", register)+"0000" + "\n");
        mainFrameGUI.updateGeneratedCodeGUIState();
	}
    public void macroExpansion0operand(String opcode) throws IOException {
        fileWriter.write("0x" + String.format("%02X", EOpcode.getOpcodeNum(opcode)) + String.format("%02X", 0) + String.format("%04X", 0) + "\n");
        fileWriter.flush();
        mainFrameGUI.updateGeneratedCodeGUIState();
    }
    public void finish() throws IOException {
        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
        }
        System.out.println("--------------[symbolTable]--------------");
        for (Symbol symbol : symbolTable.getSymbolTable()) {
            System.out.println("token: " + symbol.getToken() + " /" + "type: " + symbol.getType() + " /" + "size: " + " /" + "offset: " + symbol.getOffset());
        }
        System.out.println();
        System.out.println("--------------[exe]--------------");
        Scanner scanner = new Scanner(new File(fileName));
        while (scanner.hasNext()) System.out.println(scanner.nextLine());
    }
}