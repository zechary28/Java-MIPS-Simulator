package mips;

import mips.stage.*;
import mips.util.Word;

import java.util.ArrayList;
import java.util.List;

public class MIPS {
    private static int ProgramCounter = 0;
    private static int instructionNumber = 1;
    private static final int stall = 0;
    private static int totalCycles = 0;

    private static ArrayList<Word> registers;
    private static ArrayList<Word> instructions;
    private static ArrayList<Word> memory;
    //private static RAWArrayList rawlist;

    public static boolean enableForwarding = false;
    public static boolean earlyBranching = false;
    public static boolean enableBranchPredict = false;
    public static boolean predictBranchTaken = false;

    private static StringBuilder output = new StringBuilder();
    
    public MIPS() {
        registers = new ArrayList<>();
        instructions = new ArrayList<>();
        memory = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            registers.add(i, new Word());
        }

        // for (int i = 0; i < 32; i++) {
        //     memory.add(i, new Word());
        // }
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

    public static String branch(int branchsteps) {
        output = new StringBuilder();
        ProgramCounter += branchsteps;
        if (branchsteps != 0) {
            output.append("branch taken");
        }
        return output.toString();
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
    public static String cycle(boolean draw) {

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

        output = new StringBuilder();

        if (draw) {
            output.append(InstructionFetchStage.draw());
            output.append(ControlUnit.draw());
            output.append(DecodeStage.draw());
            output.append(ALUStage.draw());
            output.append(MemoryStage.draw());
            output.append(WriteBackStage.draw());
        }

        ProgramCounter += 4;

        if (ControlUnit.getJump()) {
            ProgramCounter = DecodeStage.getJumpAddress();
        }

        output.append(drawRegs());
        output.append(drawMem());

        return output.toString();
    }

    // all stages run once
    // all inputs are taken from PipelineRegs
    // then all outputs are stored back in PipelineRegs for the next cycle
    // for first few, PipelineRegs will have nop instruction
    // each instruction will take 5 calls of pipeline() to complete
    // to work on branching
    public String pipeline(boolean draw) {
        output = new StringBuilder();
        // back to front

        // PIPELINE 3 WB
        output.append(PipelineRegs.ReadData3);

        Word WD;
        if (PipelineRegs.MemToReg3 == '1') {
            WD = PipelineRegs.ReadData3;
            output.append("read data 3 chosen: ");
            output.append(WD);
        } else {
            WD = PipelineRegs.ALUResult3;
            output.append("alu res 3 chosen: ");
            output.append(WD);
        }
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
            output.append("shift left chosen");
        } else if (DecodeStage.shiftright) {
            result = DecodeStage.getRD2().logicalShiftRight(DecodeStage.getShift());
            output.append("shift right chosen");
        } else {
            output.append("ALU result chosen");

        }
        output.append("result: ");
        output.append(result);
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
            output.append("PC jump to ");
            output.append(ProgramCounter);
        }

        // IF Stage
        InstructionFetchStage.update();
        InstructionFetchStage.run();

        if (draw) {
            output.append(InstructionFetchStage.draw());
            output.append(ControlUnit.draw());
            output.append(DecodeStage.draw());
            output.append(ALUStage.draw());
            output.append(MemoryStage.draw());
            output.append(WriteBackStage.draw());
        }
        output.append(drawRegs());
        output.append(drawMem());

        if (InstructionFetchStage.RAW3 || InstructionFetchStage.RAW2 || InstructionFetchStage.RAW1) {
            PipelineRegs.stall();
            output.append("RAW DETECTED");
            output.append("will stall next instruction");
        } else {
            PipelineRegs.store0(ProgramCounter + 4);
            ProgramCounter = PCSrc == '0'
            ? PipelineRegs.PCplus4_0
            : PipelineRegs.BranchResult2;
            output.append("PC to ");
            output.append(ProgramCounter);
            output.append(" for next instr");
            instructionNumber++;
        }
        totalCycles++;
        output.append("cycles done: ");
        output.append(getTotalCycles());
        output.append(PipelineRegs.ReadData3);
        return output.toString();
    }

    public static String drawRegs() {
        StringBuilder temp = new StringBuilder();
        temp.append("+--[ Registers ] --+");
        for (int i = 0; i < 32; i++) {
            temp.append("| Reg ");
            temp.append(i);
            temp.append(": ");
            temp.append(registers.get(i).toDec());
            temp.append("        |");
        }
        temp.append("+------------------+");
        return temp.toString();
    }

    public static String drawMem() {
        StringBuilder temp = new StringBuilder();
        temp.append("+--[ Memory ]--+");
        int len = memory.size();
        for (int i = 0; i < len; i++) {
            temp.append("| Mem ");
            temp.append(i);
            temp.append(": ");
            temp.append(memory.get(i).toDec());
        }
        temp.append("+--------------+");
        return temp.toString();
    }

    // shortcut for jshell
    public void test() {
        /////////////// Enter your src.main.java.mips assembly program here
        /*
        // fibonacci
        this.loadMemory(List.of(
            toBinary(0), // input n
            toBinary(0) // output f(n)
        ));

        this.loadInstruction(List.of(
                // $1 loop variable n
                // n =       2 3 4 5 6 7
                // $2 f(n)   1 1 2 3 5 8
                // $3 f(n-1) 0 1 1 2 3 5
                // $4 f(n-2) 0 0 1 1 2 3
                // next iteration: $4 = $3, $3 = $2, $2 = $3 + $4
                lw(1, 0, "0"),  // load n from M[0]
                addi(5, 0, 2),  // offset for loop variable
                addi(2, 0, 1),  // set $2 to 1
                addi(3, 0, 0),  // set $3 to 0
                addi(4, 0, 0),  // set $4 to 0
        /*Loop*//*add(4, 3, 0),    // $4 = $3
                add(3, 2, 0),    // $3 = $2
                add(2, 3, 4),    // $2 = $3 + $4
                addi(1, 1, -1),
                beq(1, 5, "1"),
                jump(6), // Loop
                sw(2, 0, "100") // store result in M[1]
                ));
        */
        // misc tests
        /*
        this.loadMemory(List.of(
            new src.main.java.mips.Word("1000 0000 0000 0000 0000 0000 0000 0000"),
            new src.main.java.mips.Word("0111 1111 1111 1111 1111 1111 1111 1111"),
            new src.main.java.mips.Word("0000 0000 0000 0000 0000 0000 0000 1010"),
            MIPS.toBinary(31)
        ));
        */
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
        /*
        this.loadInstruction(List.of(
                MIPS.add(2, 1, 1), // 1
                MIPS.sll(2, 2, 1), // 2 RAW 2 stalls
                new Word(),
                MIPS.jump(2) // 3 no raw on next step
                ));
        */
        
        // test for lw, sw
        this.loadMemory(List.of(
            toBinary(0),
            toBinary(1), // n
            toBinary(0),
            toBinary(0),
            toBinary(4), 
            toBinary(0), // 5
            toBinary(0), 
            toBinary(0), 
            toBinary(0), 
            toBinary(9), 
            toBinary(0), // 10
            toBinary(0), 
            toBinary(0), 
            toBinary(0), 
            toBinary(0), 
            toBinary(0), // 15
            toBinary(0) 
        ));

        this.loadInstruction(List.of(
                lw(1, 0, "100"),   // load n from M[0]
                sll(4, 1, 2),
                addi(2, 1, 1),
                addi(15, 0, 15),
                lw(8, 15, "1"), // load 4 to $8
                sll(8, 8, 1),
                lw(9, 15, "10101"), // 15 + 21 = 36, address for 9
                sw(2, 4, "100")      // $8 stores 8 which is the address for M[2]
                ));
                // registers: 1, 2, 4, 8, 9, 15
                // mem: 1, 2, 4, 9
        
        ///////////// 
    }
}
