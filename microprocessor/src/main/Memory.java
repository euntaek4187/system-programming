package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class Memory {
	Scanner scanner;
    private ArrayList<Integer> memory = new ArrayList<>();
    private final static int CODE_SEGMENT_START = 0;
    private final static int DATA_MEMORY_START = 1024;
    private final static int STACK_MEMORY_START = 2048;
    private final static int HEAP_MEMORY_START = 3072;
    private final static int MEMORY_SIZE = 4096;
    private String exeName;
    private int stackSegmentSize;
    private int dataSegmentSize;
    private int dataSize;
    private int codeSize;
    private int heapPointer = HEAP_MEMORY_START;
    public Memory(String filePath) {
        try {
			scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    public void initialize() {
        for (int i = 0; i < STACK_MEMORY_START; i++) memory.add(0);
        for (int i = STACK_MEMORY_START; i < MEMORY_SIZE; i++) memory.add(-1);
    }
    public void memeoyLayoutSetting() {
        int address = CODE_SEGMENT_START;
        this.exeName = scanner.nextLine().trim();
        this.stackSegmentSize = Integer.parseInt(scanner.nextLine().trim());
        this.dataSegmentSize = Integer.parseInt(scanner.nextLine().trim());
        this.dataSize = Integer.parseInt(scanner.nextLine().trim());
        this.codeSize = Integer.parseInt(scanner.nextLine().trim());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                String hexStr = line.startsWith("0x") ? line.substring(2) : line;
                memory.set(address++, Integer.parseInt(hexStr, 16));
            }
        }
    }
    public int load(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            System.err.println("Invalid memory access at address " + address);
            return -1;
        }
        int value = memory.get(address);
        if (address >= STACK_MEMORY_START && address < HEAP_MEMORY_START) {
        	compactStack(address);
		}
        return value;
    }
    public void store(int address, int value) {
        if (address < 0 || address >= MEMORY_SIZE) {
            System.err.println("Invalid memory access at address " + address);
            return;
        }
        memory.set(address, value);
    }
    public int allocate(int size) {
        if (heapPointer + size >= MEMORY_SIZE) {
            System.err.println("Heap memory overflow");
            return -1;
        }
        int allocatedAddress = heapPointer;
        heapPointer += size;
        return allocatedAddress;
    }
    private void compactStack(int address) {
        if (address >= STACK_MEMORY_START && address < HEAP_MEMORY_START) {
            for (int i = address; i < HEAP_MEMORY_START - 1; i++) {
                memory.set(i, memory.get(i + 1));
            }
            memory.set(HEAP_MEMORY_START - 1, -1);
        }
    }
    public ArrayList<Integer> getMemory(){return memory;}
    public int getCodeSegmentStart() {return CODE_SEGMENT_START;}
    public int getDataSegmentStart() {return DATA_MEMORY_START;}
    public int getStackSegmentStart() {return STACK_MEMORY_START;}
    public int getHeapSegmentStart() {return HEAP_MEMORY_START;}
    public int getDataSegmentSize() {return dataSize;}
    public int getCodeSegmentSize() {return codeSize;}
}