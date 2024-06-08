package main;
public class MicroProcessor {
    private CPU cpu;
    private Memory memory;
    private Bus bus;
    private boolean nextStepCalled;
    public MicroProcessor() {
        this.cpu = new CPU();
        this.memory = new Memory("src/data/test1.txt");
        this.bus = new Bus();
        this.cpu.associate(this.bus);
        this.bus.associate(this.memory);
        this.nextStepCalled = false;
    }
    public void initialize() {
        bus.initialize();
        cpu.initialize();
        memory.initialize();
        memory.memeoyLayoutSetting();
    }
    public CPU getCPU() {return cpu;}
    public Memory getMemory() {return memory;}
    public void finish() {}
    public void nextStep() {
        if (!nextStepCalled) {
            cpu.fetch();
            nextStepCalled = true;
        } else {
        	if (cpu.isFinished()) {
				System.exit(0);
			}
            cpu.decodeAndExecute();
            nextStepCalled = false;
        }
    }
}