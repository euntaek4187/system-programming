package opcode;
public enum EOpcode{
	halt, lda, sta, add, and, jump, zero, bz, cmp, not, shr, move, movec, push, pop, ge, call, ret, newc, star, ldar;
	public static int getOpcodeNum(String instruction){
    	return EOpcode.valueOf(instruction).ordinal();
    }
}