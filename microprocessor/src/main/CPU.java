package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class CPU {
    private Bus bus;
    private boolean finished = false;
    int[] registers = new int[ERegisters.values().length];
    private int stackPointer = 2048;
    // for gui
    private String explanation;
    private boolean isEnd;
    public int getConstant() {
		return constant;
	}
	private int opCode;
    private int operand1;
    private int operand2;
    private int constant;
    private int storeAddress;
    private int storeOperand2;
    private int labelAddress;
    // main
    public enum EDeviceId {
        eCpu, eMemory,
    }
    public enum EOpcode {
    	halt, lda, sta, add, and, jump, zero, bz, cmp, not, shr, move, movec, push, pop, ge;
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
    	this.isEnd = false;
    	resetValues();
    }
    private void resetValues() {
    	this.opCode=-1;
        this.operand1=-1;
        this.operand2=-1;
        this.constant=-1;
        this.storeAddress=-1;
        this.storeOperand2=-1;
        this.labelAddress=-1;
	}
	public void associate(Bus bus) {
        this.bus = bus;
    }
    public void initialize() {
        registers[ERegisters.ePC.ordinal()] = 0;
    }
    public EOpcode getOpcodeByIndex(int index) {
        if (index < 0 || index >= EOpcode.values().length) {
            throw new IndexOutOfBoundsException("Invalid index for EOpcode: " + index);
        }
        return EOpcode.values()[index];
    }
    public ERegisters getERegisterByIndex(int index) {
        if (index < 0 || index >= ERegisters.values().length) {
            throw new IndexOutOfBoundsException("Invalid index for ERegisters: " + index);
        }
        return ERegisters.values()[index];
    }
    public int getOpcodeNum(String instruction) { return EOpcode.valueOf(instruction).ordinal(); }
    public int get(ERegisters eRegister) { return registers[eRegister.ordinal()]; }
    private void set(ERegisters eRegister, int value) {registers[eRegister.ordinal()] = value;}
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
    public String getExplanation() {return explanation;}
    public boolean IsEnd() { return isEnd;}
	public int getStoreAddress() {return storeAddress;}
	public int getStoreOperand2() {return storeOperand2;}
	public int getOpCode() {return opCode;}
	public int getOperand1() {return operand1;}
	public int getOperand2() {return operand2;}
	public int getLabelAddress() {return labelAddress;}
    public void fetch() {
    	resetValues();
        this.explanation = "";
        showExplanation("[-----------------------fetch-----------------------]");
        int address = get(ERegisters.ePC);
        set(ERegisters.eMAR, address); // MAR <- PC
        this.explanation += ERegisters.eMAR+" <- "+ERegisters.ePC+"("+address+")"+"\n";
        int instruction = bus.load(EDeviceId.eMemory, get(ERegisters.eMAR)); // MBR <- (MAR)
        if (instruction == -1) {
            this.finished = true;
            return;
        }
        set(ERegisters.eMBR, instruction); // MBR에 로드한 값 설정
        this.explanation += ERegisters.eMBR+" <- "+"("+ERegisters.eMAR+"="+String.format("0x%04X", instruction)+")"+"\n";
        set(ERegisters.eIR, get(ERegisters.eMBR)); // IR <- MBR
        this.explanation += ERegisters.eIR+" <- "+ERegisters.eMBR+"("+String.format("0x%04X",get(ERegisters.eMBR))+")"+"\n";
        incrementPC();
    }
    public void decodeAndExecute() {
        this.explanation = "";
        showExplanation("[------------------Decode & Execute-----------------]");
        int instruction = get(ERegisters.eIR);
        this.opCode = (instruction & 0xFF000000) >>> 24;
        switch (EOpcode.values()[opCode]) {
            case halt:
                halt();
                break;
            case lda:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.constant = instruction & 0x0000FFFF;
                lda(ERegisters.values()[operand1], constant);
                break;
            case sta:
                this.storeAddress = (instruction & 0x00FFFF00) >>> 8;
                this.storeOperand2 = (instruction & 0x000000FF);
                sta(storeAddress, ERegisters.values()[storeOperand2]);
                break;
            case add:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.operand2 = (instruction & 0x0000FF00) >>> 8;
                add(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case and:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.operand2 = (instruction & 0x0000FF00) >>> 8;
                and(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case jump:
                this.labelAddress = (instruction & 0x00FF0000) >>> 16;
                jmp(labelAddress);
                break;
            case zero:
                this.labelAddress = (instruction & 0x00FF0000) >>> 16;
                zero(labelAddress);
                break;
            case bz:
                this.labelAddress = (instruction & 0x00FF0000) >>> 16;
                bz(labelAddress);
                break;
            case cmp:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.operand2 = (instruction & 0x0000FF00) >>> 8;
                cmp(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case not:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                not(ERegisters.values()[operand1]);
                break;
            case shr:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                shr(ERegisters.values()[operand1]);
                break;
            case move:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.operand2 = (instruction & 0x0000FF00) >>> 8;
                mov(ERegisters.values()[operand1], ERegisters.values()[operand2]);
                break;
            case movec:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                this.constant = instruction & 0x0000FFFF;
                movc(ERegisters.values()[operand1], constant);
                break;
            case push:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                push(ERegisters.values()[operand1]);
                break;
            case pop:
                this.operand1 = (instruction & 0x00FF0000) >>> 16;
                pop(ERegisters.values()[operand1]);
                break;
            case ge:
                this.labelAddress = (instruction & 0x00FF0000) >>> 16;
                ge(labelAddress);
                break;
            default:
                break;
        }
    }
    private void bz(int labelAddress) {
        if (getSign()) {
            set(ERegisters.ePC, labelAddress - 1);
            this.explanation += ERegisters.ePC + " <- " + (labelAddress - 1) + "\n";
            showExplanation("~BZ) Sign flag is set. Condition met. Jump to line CS+" + labelAddress);
        } else {
            showExplanation("~BZ) Sign flag is not set. Condition not met. No change occurs.");
        }
    }
	private void showExplanation(String explanation) {
		this.explanation += explanation+"\n";
		System.out.println(explanation);
	}
    private void not(ERegisters eTarget) {
        int targetValue = get(eTarget);
        int result = ~targetValue;
        set(eTarget, result);
        setZero(result == 0);
        showExplanation("~NOT) " + eTarget + " <- NOT " + eTarget + " (" + targetValue + "). Result: " + result);
    }

    private void and(ERegisters eTarget, ERegisters eSource) {
        int targetValue = get(eTarget);
        int sourceValue = get(eSource);
        int result = targetValue & sourceValue;
        set(eTarget, result);
        setZero(result == 0);
        showExplanation("~AND) " + eTarget + " <- " + eTarget + " (" + targetValue + ") & " + eSource + " (" + sourceValue + "). Result: " + result);
    }

    private void shr(ERegisters eTarget) {
        int targetValue = get(eTarget);
        int result = targetValue >>> 1;
        set(eTarget, result);
        setZero(result == 0);
        showExplanation("~SHR) " + eTarget + " <- " + eTarget + " (" + targetValue + ") >> 1. Result: " + result);
    }
    private void halt() {
    	showExplanation("~HALT) -----------------system end-----------------");
        this.isEnd = true;
    }
	private void cmp(ERegisters eRegisters, ERegisters eRegisters2) {
        int eRegister = get(eRegisters);
        int eRegister2 = get(eRegisters2);
        setZero(eRegister == eRegister2);
        setSign(eRegister < eRegister2);
        showExplanation("~CMP) " + eRegisters+"("+eRegister+")" + " with " + eRegisters2+"("+eRegister2+")");
    }
    private void zero(int labelAddress) {
        if (getZero()) {
            set(ERegisters.ePC, labelAddress - 1);
            this.explanation += ERegisters.ePC+" <- "+(labelAddress - 1)+"\n";
            showExplanation("~ZERO) Condition is met. Jump to line CS+" + labelAddress);
        } else showExplanation("~ZERO) Condition is not met. No change occurs.");
    }
    private void ge(int labelAddress) {
        if (getZero() || !getSign()) {
            set(ERegisters.ePC, labelAddress - 1);
            this.explanation += ERegisters.ePC+" <- "+(labelAddress - 1)+"\n";
            showExplanation("~GE) Condition is met. Jump to line : CS+" + labelAddress);
        } else showExplanation("~GE) Condition is not met. No change occurs.");

    }
    private void jmp(int labelAddress) {
        set(ERegisters.ePC, labelAddress - 1);
        this.explanation += ERegisters.ePC+" <- "+(labelAddress - 1)+"\n";
    	showExplanation("~JMP) Jump to line : CS+" + labelAddress);
    }
    private void jne(int labelAddress) {
        if (!getZero()) {
            set(ERegisters.ePC, labelAddress - 1);
            this.explanation += ERegisters.ePC+" <- "+(labelAddress - 1)+"\n";
            showExplanation("~JNE) Condition is met. Jump to line : CS+" + labelAddress);
        }else showExplanation("~JNE) Condition is not met. No change occurs.");
    }
    private void mov(ERegisters eTarget, ERegisters eSource) {
        int sourceValue = get(eSource);
        int targetValue = get(eTarget);
        set(eTarget, sourceValue);
        this.explanation += eTarget+" <- "+eSource+"("+sourceValue+")"+"\n";
        showExplanation("~MOV) " + eSource +"("+sourceValue+")"+ " to " + eTarget+"("+targetValue+")" + " has stored."); // 테스트 못해봄 -> 확인필요
    }
    private void movc(ERegisters eTarget, int value) {
        int targetValue = get(eTarget);
        set(eTarget, value);
        this.explanation += eTarget+" <- "+value+"\n";
        showExplanation("~MOVC) " + eTarget+"("+targetValue+")" + " to " + value + " has stored.");
    }
    private void lda(ERegisters eTarget, int address) {
        set(ERegisters.eMAR, address); // MAR <- address
    	this.explanation += ERegisters.eMAR+" <- "+String.format("0x%04X", address)+"\n";
        int value = bus.load(EDeviceId.eMemory, get(ERegisters.eMAR)); // MBR <- (MAR)
        set(ERegisters.eMBR, value);
        this.explanation += ERegisters.eMBR+" <- "+value;
        set(eTarget, get(ERegisters.eMBR)); // eTarget <- MBR
        this.explanation += eTarget+" <- "+ERegisters.eMBR+"("+get(ERegisters.eMBR)+")"+"\n";
        showExplanation("~LDA) Loaded value: " + value + " into " + eTarget);
    }
    private void sta(int address, ERegisters eSource) {
        set(ERegisters.eMAR, address); // MAR <- address
    	this.explanation += ERegisters.eMAR+" <- "+String.format("0x%04X", address)+"\n";
        set(ERegisters.eMBR, get(eSource)); // MBR <- eSource
        this.explanation += ERegisters.eMBR+" <- "+eSource+"("+get(eSource)+")"+"\n";
        bus.store(EDeviceId.eMemory, get(ERegisters.eMAR), get(ERegisters.eMBR)); // (MAR) <- MBR
        this.explanation += "(MAR="+String.format("0x%04X", get(ERegisters.eMAR))+")"+" <- "+ERegisters.eMBR+"("+get(ERegisters.eMBR)+")"+"\n";
        showExplanation("~STA) Stored value of MBR: " + get(ERegisters.eMBR) + " into address of MAR: " + String.format("0x%04X",get(ERegisters.eMAR)));
    }
    private void add(ERegisters eTarget, ERegisters eSource) {
        int targetValue = get(eTarget);
        int sourceValue = get(eSource);
        int result = targetValue + sourceValue;
        set(eTarget, result);
        this.explanation += eTarget+" <- "+eTarget+"("+targetValue+") + "+eSource+"("+sourceValue+")"+"\n";
        showExplanation("~ADD) " + eSource +"("+sourceValue+")"+ " added to " + eTarget+"("+targetValue+")" + ". New value: " + result);
    }
    private void push(ERegisters eRegister) {
        int value = get(eRegister);
        bus.store(EDeviceId.eMemory, stackPointer, value);
        showExplanation("~PUSH) " + eRegister + "(" + value + ") to stack at address: " + (stackPointer));
        stackPointer++;
    }
    private void pop(ERegisters eRegister) {
        int value = bus.load(EDeviceId.eMemory, --stackPointer);
        set(eRegister, value);
        this.explanation += eRegister+" <- "+value+"\n";
        showExplanation("~POP) " + eRegister+ "(" + get(eRegister) + ")" + " loaded from stack. value: " + value);
    }
    private void incrementPC() {
        int currentPC = get(ERegisters.ePC);
        set(ERegisters.ePC, currentPC + 1);
        showExplanation("~Increment) PC increase to " + get(ERegisters.ePC));
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