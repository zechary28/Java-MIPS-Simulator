package mips;

import java.util.ArrayList;
import java.util.List;

public class MIPS {
    private static int ProgramCounter = 0;
    private static int instructionNumber = 1;
    private static int stall = 0;
    private static int totalCycles = 0;

    private static ArrayList<Word> registers;
    private static ArrayList<Word> instructions;
    private static ArrayList<Word> memory;
    //private static RAWArrayList rawlist;

    public static boolean enableForwarding = false;
    public static boolean earlyBranching = false;
    public static boolean enableBranchPredict = false;
    public static boolean predictBranchTaken = false;
    
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

    public static int getPC() {
        return ProgramCounter;
    }

    public static int getInstructionNumber() {
        return instructionNumber;
    }

    public static int getTotalCycles() {
        return totalCycles;
    }

    public static void branch(int branchsteps) {
        ProgramCounter += branchsteps;
        if (branchsteps != 0) {
            System.out.println("branch taken");
        }
    }

    // public static void jump(int address) {
    //     // keeps 4 msbits of PC
    //     ProgramCounter = address % 67108864;
    // }

    public static void configure(boolean enableForwarding, boolean earlyBranching, boolean enableBranchPredict, boolean predictBranchTaken) {
        enableForwarding = enableForwarding;
        earlyBranching = earlyBranching;
        enableBranchPredict = enableBranchPredict;
        predictBranchTaken = predictBranchTaken;
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
    
    // encode j format (instruction number)
    public static Word jump(int instnum) {
        return new Word("000010" +
            toBinary(instnum).subword(26));
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
            ? DecodeStage.getRD()
            : DecodeStage.getRT(); //////

        // ALU
        Word operand2 = ControlUnit.getALUSrc() == '1' 
            ? signExtend(DecodeStage.getImmediate()) 
            : DecodeStage.getRD2(); //////
        ALUStage.update(DecodeStage.getRD1(), 
                operand2,
                ControlUnit.getALUControl());
        ALUStage.run();

        // for shift instructions
        Word result = DecodeStage.shiftleft 
            ? DecodeStage.getRD2().logicalShiftLeft(DecodeStage.getShift())
            : DecodeStage.shiftright
            ? DecodeStage.getRD2().logicalShiftRight(DecodeStage.getShift())
            : ALUStage.getResult();

        // Memory
        MemoryStage.update(result, 
                DecodeStage.getRD2(), 
                ControlUnit.getMemRead(), 
                ControlUnit.getMemWrite());
        MemoryStage.run();

        // branching
        char PCSrc = (ControlUnit.getBranch() == '1') && (ALUStage.isZero() == '1')
            ? '1'
            : '0'; ///////
        int BranchSteps = PCSrc == '1' 
            ? signExtend(DecodeStage.getImmediate()).logicalShiftLeft(2).toDec()
            : 0; ///////
        ProgramCounter += BranchSteps;
        //////
        
        // Write Back
        Word WD = ControlUnit.getMemToReg() == '1' 
            ? MemoryStage.getReadData()
            : result; ///////
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

        ProgramCounter += 4;

        if (ControlUnit.getJump()) {
            ProgramCounter = DecodeStage.getJumpAddress();
        }

        drawRegs();
        drawMem();

    }

    // all stages run once
    // all inputs are taken from PipelineRegs
    // then all outputs are stored back in PipelineRegs for the next cycle
    // for first few, PipelineRegs will have nop instruction
    // each instruction will take 5 calls of pipeline() to complete
    // to work on branching
    public void pipeline(boolean draw) {
        // back to front

        // PIPELINE 3 WB
        Word WD = PipelineRegs.MemToReg3 == '1' 
            ? PipelineRegs.ReadData3
            : PipelineRegs.ALUResult3;
        WriteBackStage.update(PipelineRegs.WriteRegister3, WD, PipelineRegs.RegWrite3);
        WriteBackStage.run();
        PipelineRegs.store3();

        // PIPELINE 2 MEM
        char PCSrc = (PipelineRegs.Branch2 == '1') && (PipelineRegs.isZero2 == '1')
            ? '1'
            : '0'; ///////
        int BranchSteps = PCSrc == '1' 
            ? PipelineRegs.BranchResult2
            : 0; ///////  LOGIC FOR CALCULATING BRANCH, TO BE BEFORE DIVIDE BY 4
        ProgramCounter += BranchSteps;
        MemoryStage.update(PipelineRegs.ALUResult2, PipelineRegs.ReadData2_2, PipelineRegs.MemRead2, PipelineRegs.MemWrite2);
        MemoryStage.run();

        //PIPELINE 1: ALU
        int BranchRes = PipelineRegs.PCplus4_1 + 
                PipelineRegs.immSignExtended1.logicalShiftLeft(2).toDec();
        Word operand2 = PipelineRegs.ALUSrc1 == '1' 
            ? PipelineRegs.immSignExtended1
            : PipelineRegs.ReadData2_1; //////
        ALUStage.update(PipelineRegs.ReadData1_1, 
                operand2,
                PipelineRegs.ALUControl1);
        ALUStage.run();
        // for shift instructions
        Word result = ALUStage.getResult();
        if (DecodeStage.shiftleft) {
            result = DecodeStage.getRD2().logicalShiftLeft(DecodeStage.getShift());
            System.out.println("shift left chosen");
        } else if (DecodeStage.shiftright) {
            result = DecodeStage.getRD2().logicalShiftRight(DecodeStage.getShift());
            System.out.println("shift right chosen");
        } else {
            System.out.println("ALU result chosen");

        }
        System.out.println("result: " + result);
        PipelineRegs.store2(result, BranchRes);

        // WR decided at pipeline 1 and stored using PipelineRegs.store1()
        // RAW detected at IF stage run() depending on WR in PipelineRegs
        // stall is done in Pipeline by injecting a nop instruction by stall()

        // Decode Stage
        DecodeStage.update(PipelineRegs.instruction0);
        DecodeStage.run();
        int WR = ControlUnit.getRegDst() == '1' 
            ? DecodeStage.getRD()
            : DecodeStage.getRT(); //////
        Word SignExtImm = signExtend(DecodeStage.getImmediate());
        PipelineRegs.store1(SignExtImm, WR);
        
        if (ControlUnit.getJump()) {
            ProgramCounter = DecodeStage.getJumpAddress();
            System.out.println("PC jump to " + ProgramCounter);
        }

        // IF Stage
        InstructionFetchStage.update();
        InstructionFetchStage.run();

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

        if (InstructionFetchStage.RAW2 || InstructionFetchStage.RAW1) {
            PipelineRegs.stall();
            System.out.println("RAW DETECTED");
            System.out.println("will stall next instruction");
        } else {
            PipelineRegs.store0(ProgramCounter + 4);
            ProgramCounter = PCSrc == '0'
            ? PipelineRegs.PCplus4_0
            : PipelineRegs.BranchResult2;
            System.out.println("PC to " + ProgramCounter + " for next instr");
            instructionNumber++;
        }
        totalCycles++;
        System.out.println("cycles done: " + getTotalCycles());
    }

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

    // shortcut for jshell
    public void test() {
        // set memory
        this.loadMemory(List.of(
            new mips.Word("1000 0000 0000 0000 0000 0000 0000 0000"),
            new mips.Word("0111 1111 1111 1111 1111 1111 1111 1111"),
            new mips.Word("0000 0000 0000 0000 0000 0000 0000 1010"),
            MIPS.toBinary(31)
        ));
        // set instructions
        // add sub addi and or
        /*
        this.loadInstruction(List.of(
                MIPS.add(2, 1, 1), // 1
                MIPS.add(4, 2, 2), // 2 RAW
                MIPS.sub(3, 4, 1), // 3 RAW
                MIPS.add(7, 3, 4), // 4 RAW
                MIPS.add(5, 2, 3), // 5 
                MIPS.add(10, 5, 5), // 6 1010 RAW
                MIPS.addi(12, 10, 2), // 7 1100 RAW
                MIPS.and(8, 10, 12), // 8 and 1000 RAW
                MIPS.or(14, 10, 12) // 9 or 1110
                ));
        */
        // shift and jump
        this.loadInstruction(List.of(
                MIPS.add(2, 1, 1), // 1
                MIPS.sll(2, 2, 1), // 2 RAW 2 stalls
                new Word(),
                MIPS.jump(2) // 3 no raw on next step
                ));
        
    }
}
