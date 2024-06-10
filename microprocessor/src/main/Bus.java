package main;
public class Bus {
    private Memory memory;
    public Bus() {}
	public void initialize() {}
    public void associate(Memory memory) {
        this.memory = memory;
    }
    public int load(CPU.EDeviceId deviceId, int address) {
        if (deviceId == CPU.EDeviceId.eMemory) {
            return memory.load(address);
        }
        return -1;
    }
    public void store(CPU.EDeviceId deviceId, int address, int value) {
        if (deviceId == CPU.EDeviceId.eMemory) {
            memory.store(address, value);
        }
    }
    public int allocate(CPU.EDeviceId deviceId, int size) {
    	if (deviceId == CPU.EDeviceId.eMemory) {
    		return memory.allocate(size);
    	}
    	return -1;
    }
}