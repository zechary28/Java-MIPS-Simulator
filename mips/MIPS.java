package mips;

import java.util.ArrayList;
import java.util.List;

public class MIPS {
    private static int ProgramCounter;
    private static int instructionNumber;

    private static ArrayList<Word> registers;
    private static ArrayList<Word> instructions;
    private static ArrayList<Word> memory;
    
    public MIPS() {
        registers = new ArrayList<>();
        instructions = new ArrayList<>();
        memory = new ArrayList<>();

        registers.add(0, new Word());
        registers.add(1, new Word("01"));

        for (int i = 2; i < 32; i++) {
            registers.add(i, new Word());
        }
    }

    // getters and setters
    public void loadInstruction(Word inst) {
        instructions.add(inst);
    }

    public void loadInstruction(List<Word> insts) {
        instructions.addAll(insts);
    }

    public static ArrayList<Word> getInstructions() {
        return instructions;
    }
    public static ArrayList<Word> getRegisters() {
        return registers;
    }
    public static ArrayList<Word> getMemory() {
        return memory;
    }

    public void loadMemory(int index, Word data) {
        memory.add(index, data);
    }

    public void loadMemory(List<Word> data) {
        memory.addAll(data);
    }

    public static Word signExtend(String str) {
        String signBit = str.charAt(0) == '1' ? "1" : "0";
        int bitsToExtend = 32 - str.length();
        for (int i = 0; i < bitsToExtend; i++) {
            str = signBit + str;
        }
        return new Word(str);
    }
    public static String zeroExtend(String str) {
        for (int i = 0; i < 16 - str.length(); i++) {
            str = "0".concat(str);
        }
        return str;
    }

    // encoding instructions to binary 2s comp
    public static Word toBinary(int sum) {
        String s = "";
        int magnitude = Math.abs(sum);
        while (magnitude != 0) {
            if (magnitude % 2 == 1) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
            magnitude /= 2;
        }
        Word w = new Word(s);
        if (sum < 0) {
            w = w.negate();
        }
        return w;
    }

    // instructions are encoded as static functions to manually set opcode and function
    // this is to load the instruction list with instructions which is then decoded again and executed
    // could be improved in the future using hashmaps

    // r instructions: add , sub , and , or , sll , srl
    // parameters: (int rd, int rs, int rt) index of registers
    // shift params: (int rd, rt, shamt)

    // i instructions: addi,       andi, ori, 
    //                 lw  , sw  , beq
    // encode r formats
    public static Word add(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "100000");
    }
    public static Word sub(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "100010");
    }
    public static Word and(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "100100");
    }
    public static Word or(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "100101");
    }
    public static Word sll(int rd, int rt, int shamt) {
        return new Word("000000" + 
            "00000" + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            toBinary(shamt).subword(5) + 
            "000000");
    }
    public static Word srl(int rd, int rt, int shamt) {
        return new Word("000000" + 
            "00000" + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            toBinary(shamt).subword(5) + 
            "000010");
    }
    public static Word slt(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "101010");
    }

    // encode immediate ALU instructions
    public static Word addi(int rt, int rs, int imm) {
        return new Word("001000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(imm).subword(16));
    }
    public static Word andi(int rt, int rs, String imm) {
        return new Word("001100" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            new Word(imm).subword(16));
    }
    public static Word ori(int rt, int rs, String imm) {
        return new Word("001101" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            new Word(imm).subword(16));
    }

    // encode immediate instructions lw, sw, branch
    public static Word lw(int rt, int rs, String imm) {
        return new Word("100011" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            new Word(imm).subword(16));
    }
    public static Word sw(int rt, int rs, String imm) {
        return new Word("101011" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            new Word(imm).subword(16));
    }
    public static Word beq(int rt, int rs, String imm) {
        return new Word("000100" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            new Word(imm).subword(16));
    }
    
    // encode j format (address divisible by 4)
    public static Word jump(int address) {
        return new Word("000010" +
            toBinary(address).logicalShiftRight(2).subword(26));
    }

    // process one instruction fully
    // abstraction of the datapath diagram, parameters show how the 'wires' are connected
    // for each stage:
    // update receives input
    // run processes the input and changes its fields for output
    // multiplexer decision is made here using ? : statements
    public static void cycle(boolean draw) {
        // Instruction Fetch
        InstructionFetchStage.update();
        InstructionFetchStage.run();
                
        // Decode
        DecodeStage.update(InstructionFetchStage.getInstruction());
        DecodeStage.run();
        int WR = ControlUnit.getRegDst() == '1' 
            ? new Word(DecodeStage.getRD()).toDec()
            : new Word(DecodeStage.getRT()).toDec();

        // ALU
        // Word operand2 = ControlUnit.getALUSrc() == '1' 
        //     ? signExtend(DecodeStage.getImmediate()) 
        //     : DecodeStage.getRD2();
        ALUStage.update(DecodeStage.getRD1(), 
                ControlUnit.getALUSrc() == '1' 
                    ? signExtend(DecodeStage.getImmediate()) 
                    : DecodeStage.getRD2(), 
                ControlUnit.getALUControl());
        ALUStage.run();

        // Memory
        MemoryStage.update(ALUStage.getResult(), 
                DecodeStage.getRD2(), 
                ControlUnit.getMemRead(), 
                ControlUnit.getMemWrite());
        MemoryStage.run();

        // branching
        char PCSrc = (ControlUnit.getBranch() == '1') && (ALUStage.isZero() == '1')
            ? '1'
            : '0';
        int BranchSteps = PCSrc == '1' 
            ? signExtend(DecodeStage.getImmediate()).logicalShiftLeft(2).toDec()
            : 0;
        if (PCSrc == '1') {
            InstructionFetchStage.branch(BranchSteps);
        }
        
        // Write Back
        Word WD = ControlUnit.getMemToReg() == '1' 
            ? MemoryStage.getReadData()
            : ALUStage.getResult();
        WriteBackStage.update(WR, WD, ControlUnit.getRegWrite());
        WriteBackStage.run();

        if (draw) {
            InstructionFetchStage.draw();
            ControlUnit.draw();
            DecodeStage.draw();
            ALUStage.draw();
            MemoryStage.draw();
            WriteBackStage.draw();
        }
        drawRegs();
        drawMem();

    }

    // each stage will 
    // public void pipeline() {
    //     InstructionFetchStage.update();
    //     DecodeStage.update(InstructionFetchStage.getInstruction());

    //     ALUStage.update(DecodeStage.getRD1(), operand2, DecodeStage.getALUControl());

    //     InstructionFetchStage.run();
    //     DecodeStage.run();
    //     ALUStage.run();
    //     MemoryStage.run();
    //     WriteBackStage.run();

        
    // }

    public static void drawRegs() {
        System.out.println("+--[ Registers ] --+");
        for (int i = 0; i < 32; i++) {
            System.out.println("| Reg " + i + ": " + registers.get(i).toDec() + "        |");
        }
        System.out.println("+------------------+");
    }

    public static void drawMem() {
        System.out.println("+--[ Memory ]--+");
        int len = memory.size();
        for (int i = 0; i < len; i++) {
            System.out.println("| Mem " + i + ": " + memory.get(i).toDec());
        }
        System.out.println("+--------------+");
    }
}
