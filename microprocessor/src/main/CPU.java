package main;

import java.util.HashMap;
import java.util.Map;

public class CPU {
    private Bus bus;
    private boolean finished = false;
    int[] registers = new int[ERegisters.values().length];
    private int stackPointer = 2048;
    // for gui
    private String explanation;
    private int processNum;
    private Map<ERegisters, Integer> processData;
    // main
    public enum EDeviceId {
        eCpu, eMemory,
    }
    public enum EOpcode {
        MOV, MOVC, LDA, STA, ADD, CMP, CMPC, JEQ, JNE, JGE, JMP, HALT, PUSH, POP
    }
    enum ERegisters {
        R0, R1, R2, R3, eMAR, eMBR, ePC, eIR, eAC, eStatus,
    }
    public enum EStatus {
        eZero(0xFE, 0x01, 0x01),
        eSign(0xFD, 0x02, 0x02);
        private int nClear;
        int nSet;
        int nGet;
        private EStatus(int nClear, int nSet, int nGet) {
            this.nClear = nClear;
            this.nSet = nSet;
            this.nGet = nGet;
        }
        public int getNClear() {return this.nClear;}
        public int getNSet() {return this.nSet;}
        public int getNGet() {return this.nGet;}
    }
    public CPU() {
    	this.explanation = "";
    	this.processNum = 0;
    	this.processData = new HashMap<>();
    }
    public void associate(Bus bus) {
        this.bus = bus;
    }
    public void initialize() {
        registers[ERegisters.ePC.ordinal()] = 0;
    }
    public int getOpcodeNum(String instruction) { return EOpcode.valueOf(instruction).ordinal(); }
    public String getExplanation() {return explanation;}
    public int get(ERegisters eRegister) { return registers[eRegister.ordinal()]; }
    private void set(ERegisters eRegister, int value) { registers[eRegister.ordinal()] = value; }
    private void setZero(boolean bResult) {
        if (bResult) this.registers[ERegisters.eStatus.ordinal()] |= EStatus.eZero.getNSet();
        else this.registers[ERegisters.eStatus.ordinal()] &= EStatus.eZero.getNClear();
    }
    private boolean getZero() { return (this.registers[ERegisters.eStatus.ordinal()] & EStatus.eZero.getNGet()) != 0; }
    private void setSign(boolean bResult) {
        if (bResult) this.registers[ERegisters.eStatus.ordinal()] |= EStatus.eSign.getNSet();
        else this.registers[ERegisters.eStatus.ordinal()] &= EStatus.eSign.getNClear();
    }
    private boolean getSign() { return (this.registers[ERegisters.eStatus.ordinal()] & EStatus.eSign.getNGet()) != 0; }
    public void fetch() {
        System.out.println("[-----------------------fetch-----------------------]");
        int address = get(ERegisters.ePC);
        set(ERegisters.eMAR, address); // MAR <- PC
        int instruction = bus.load(EDeviceId.eMemory, get(ERegisters.eMAR)); // MBR <- (MAR)
        if (instruction == -1) {
            this.finished = true;
            return;
        }
        set(ERegisters.eMBR, instruction); // MBR에 로드한 값 설정
        set(ERegisters.eIR, get(ERegisters.eMBR)); // IR <- MBR
        incrementPC();
    }
    public void decodeAndExecute() {
    	System.out.println("[------------------Decode & Execute-----------------]");
        int instruction = get(ERegisters.eIR);
        int opCode = (instruction & 0xFF000000) >>> 24;
        int operand1 = (instruction & 0x00FF0000) >>> 16;
        int operand2 = (instruction & 0x0000FF00) >>> 8;
        int value = instruction & 0x0000FFFF;
        int storeAddress = (instruction & 0x00FFFF00) >>> 8;
        int storeOperand2 = (instruction & 0x000000FF);
        int labelAddress = (instruction & 0x00FF0000) >>> 16;
        switch (EOpcode.values()[opCode]) {
            case MOV:
                mov(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case MOVC:
                movc(ERegisters.values()[operand1], value);
                break;
            case LDA:
                lda(ERegisters.values()[operand1], value);
                break;
            case STA:
                sta(storeAddress, ERegisters.values()[storeOperand2]);
                break;
            case ADD:
                add(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case CMP:
                cmp(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case CMPC:
                cmpc(ERegisters.values()[operand1], value);
                break;
            case JEQ:
                jeq(labelAddress);
                break;
            case JNE:
                jne(labelAddress);
                break;
            case JGE:
                jge(labelAddress);
                break;
            case JMP:
                jmp(labelAddress);
                break;
            case HALT:
                halt();
                break;
            case PUSH:
            	push(ERegisters.R1);
                break;
            case POP:
            	pop(ERegisters.R1);
                break;
            default:
                break;
        }
    }
    private void halt() {
    	this.explanation = "~HALT) -----------------system end-----------------";
        System.out.println(this.explanation);
        System.exit(0);
    }
    private void cmp(ERegisters eRegisters, ERegisters eRegisters2) {
        int eRegister = get(eRegisters);
        int eRegister2 = get(eRegisters2);
        setZero(eRegister == eRegister2);
        setSign(eRegister < eRegister2);
    	this.explanation = "~CMP) " + eRegisters+"("+eRegister+")" + " with " + eRegisters2+"("+eRegister2+")";
        System.out.println(this.explanation);
    }
    private void jeq(int labelAddress) {
        if (getZero()) {
            set(ERegisters.ePC, labelAddress - 1);
        	this.explanation = "~JEQ) Jump to line CS+" + labelAddress;
            System.out.println(this.explanation);

        }
    }
    private void jge(int labelAddress) {
        if (getZero() || !getSign()) {
            set(ERegisters.ePC, labelAddress - 1);
        	this.explanation = "~JGE) Jump to line : CS+" + labelAddress;
            System.out.println(this.explanation);
        }
    }
    private void jmp(int labelAddress) {
        set(ERegisters.ePC, labelAddress - 1);
    	this.explanation = "~JMP) Jump to line : CS+" + labelAddress;
        System.out.println(this.explanation);

    }
    private void jne(int labelAddress) {
        if (!getZero()) {
            set(ERegisters.ePC, labelAddress - 1);
        	this.explanation = "~JNE) Jump to line : CS+" + labelAddress;
            System.out.println(this.explanation);
        }
    }
    private void mov(ERegisters eTarget, ERegisters eSource) {
        int sourceValue = get(eSource);
        int targetValue = get(eTarget);
        set(eTarget, sourceValue);
    	this.explanation = "~MOV) " + eSource +"("+sourceValue+")"+ " to " + eTarget+"("+targetValue+")" + " has stored.";// 테스트 못해봄 -> 확인필요
        System.out.println(this.explanation);
    }
    private void movc(ERegisters eTarget, int value) {
        int targetValue = get(eTarget);
        set(eTarget, value);
    	this.explanation = "~MOVC) " + eTarget+"("+targetValue+")" + " to " + value + " has stored.";
        System.out.println(this.explanation);
    }
    private void lda(ERegisters eTarget, int address) {
        set(ERegisters.eMAR, address); // MAR <- address
        int value = bus.load(EDeviceId.eMemory, get(ERegisters.eMAR)); // MBR <- (MAR)
        set(ERegisters.eMBR, value);
        set(eTarget, get(ERegisters.eMBR)); // eTarget <- MBR
    	this.explanation = "~LDA) Loaded value: " + value + " into " + eTarget;
        System.out.println(this.explanation);
    }
    private void sta(int address, ERegisters eSource) {
        set(ERegisters.eMAR, address); // MAR <- address
        set(ERegisters.eMBR, get(eSource)); // MBR <- eSource
        bus.store(EDeviceId.eMemory, get(ERegisters.eMAR), get(ERegisters.eMBR)); // (MAR) <- MBR
    	this.explanation = "~STA) Stored value of MBR: " + get(ERegisters.eMBR) + " into address of MAR: " + get(ERegisters.eMAR);
        System.out.println(this.explanation);
    }
    private void add(ERegisters eTarget, ERegisters eSource) {
        int targetValue = get(eTarget);
        int sourceValue = get(eSource);
        int result = targetValue + sourceValue;
        set(eTarget, result);
    	this.explanation = "~ADD) " + eSource +"("+sourceValue+")"+ " added to " + eTarget+"("+targetValue+")" + ". New value: " + result;
        System.out.println(this.explanation);
    }
    private void cmpc(ERegisters eRegister, int value) {
        int registerValue = get(eRegister);
        setZero(registerValue == value);
        setSign(registerValue < value);
    	this.explanation = "~CMPC) Compare " + eRegister + " with value " + value;
        System.out.println(this.explanation);
    }
    private void push(ERegisters eRegister) {
        int value = get(eRegister);
        bus.store(EDeviceId.eMemory, stackPointer, value);
    	this.explanation = "~PUSH) " + eRegister + "(" + value + ") to stack at address: " + (stackPointer);
        System.out.println(this.explanation);
        stackPointer++;
    }

    private void pop(ERegisters eRegister) {
        int value = bus.load(EDeviceId.eMemory, --stackPointer);
        set(eRegister, value);
    	this.explanation = "~POP) " + eRegister+ "(" + get(eRegister) + ")" + " loaded from stack. value: " + value;
        System.out.println(this.explanation);
    }
    private void incrementPC() {
        int currentPC = get(ERegisters.ePC);
        set(ERegisters.ePC, currentPC + 1);
    	this.explanation = "~Increment) PC increase to " + get(ERegisters.ePC);
        System.out.println(this.explanation);
    }
    public boolean isFinished() {
        return finished;
    }
    public void run() {
        while (!finished) {
        	System.out.println("[————— Execute —————]");
        	fetch();
        	if (!finished) {
        		decodeAndExecute();
        		System.out.println("");
        	}	
        }
    }
}