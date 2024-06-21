package mips;

public class PipelineRegs {
    // pipeline 1
    public static Word instruction0 = new Word();
    public static int PCplus4_0 = 0;

    // pineline 2: Decode to ALU
    public static char RegWrite1 = '0';
    public static char MemToReg1 = '0';
    public static char Branch1 = '0';
    public static char MemRead1 = '0';
    public static char MemWrite1 = '0';
    public static char RegDst1 = '1';
    public static char ALUSrc1 = '0';

    public static String ALUControl1 = "0010";
    public static int PCplus4_1 = 0;
    public static Word ReadData1_1 = new Word();
    public static Word ReadData2_1 = new Word();
    public static Word immSignExtended1 = new Word();
    public static int RT1 = 0;
    public static int RD1 = 0;
    public static int WriteRegister1 = 0;

    // pipeline 3: ALU to Mem
    public static char RegWrite2 = '0';
    public static char MemToReg2 = '0';
    public static char Branch2 = '0';
    public static char MemRead2 = '0';
    public static char MemWrite2 = '0';

    public static int BranchResult2 = 0;
    public static char isZero2 = '0';
    public static Word ALUResult2 = new Word();
    public static Word ReadData2_2 = new Word();
    public static int WriteRegister2 = 0;

    // pipeline 4: Mem to WB
    public static char RegWrite3 = '0';
    public static char MemToReg3 = '0';

    public static Word ReadData3 = new Word();
    public static Word ALUResult3 = new Word();
    public static int WriteRegister3 = 0;

    public static void stall() {
        instruction0 = new Word();
    }
    public static void store0(int PCplus4) {
        instruction0 = InstructionFetchStage.getInstruction();
        PCplus4_0 = PCplus4;
    }

    public static void store1(Word immsignex, int WR) {
        RegWrite1 = ControlUnit.getRegWrite();
        MemToReg1 = ControlUnit.getMemToReg();
        Branch1 = ControlUnit.getBranch();
        MemRead1 = ControlUnit.getMemRead();
        MemWrite1 = ControlUnit.getMemWrite();
        RegDst1 = ControlUnit.getRegDst();
        ALUSrc1 = ControlUnit.getALUSrc();
        ALUControl1 = ControlUnit.getALUControl(); // amended implementation since ALUControl is generated in ALUStage.run()

        PCplus4_1 = PCplus4_0;
        ReadData1_1 = DecodeStage.getRD1();
        ReadData2_1 = DecodeStage.getRD2();
        immSignExtended1 = immsignex;
        RT1 = DecodeStage.getRT();
        RD1 = DecodeStage.getRD();
        WriteRegister1 = WR;
    }

    public static void store2(int BranchRes) {
        RegWrite2 = RegWrite1;
        MemToReg2 = MemToReg1;
        Branch2 = Branch1;
        MemRead2 = MemRead1;
        MemWrite2 = MemWrite1;

        BranchResult2 = BranchRes;
        isZero2 = ALUStage.isZero();
        ALUResult2 = ALUStage.getResult();
        ReadData2_2 = ReadData2_1;
        WriteRegister2 = WriteRegister1;
    }

    public static void store3() {
        RegWrite3 = RegWrite2;
        MemToReg3 = MemToReg2;

        ReadData3 = MemoryStage.getReadData();
        ALUResult3 = ALUResult2;
        WriteRegister3 = WriteRegister2;
    }
    
}
