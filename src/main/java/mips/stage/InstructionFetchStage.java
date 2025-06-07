package mips.stage;

import mips.MIPS;
import mips.util.Word;

// this class accesses the instruction memory and retrieves the 32 bit instruction Word
public class InstructionFetchStage {

    // inputs
    private static final int instructionNumber = 0;

    private static boolean done = true;

    // for detect raw, IF stage looks at fields in PipelineRegs to see if the same
    // number after RAW indicates delay in number of cycles before forwarding
    // rs and rt indicates from which register it is read and where to forward to
    public static boolean RAW1 = false;
    public static boolean RAW1rt = false;
    public static boolean RAW2 = false;
    public static boolean RAW2rt = false;
    public static boolean RAW3 = false;
    
    //outputs
    private static Word instruction = new Word();

    public static void update() {
        if (done) {
            done = false;
        }
    }

    public static void run() {
        if (!done) {
            instruction = MIPS.getInstructions().get(MIPS.getPC() / 4);
            done = true;
        }
        // logic for detecting RAW
        int rs = new Word(instruction.getWord().substring(6, 11)).toDec();
        int rt = new Word(instruction.getWord().substring(11, 16)).toDec();
        boolean jump = instruction.getWord().substring(0,6) == "000010";
        // RAW3 = (PipelineRegs.lw3) && (PipelineRegs.WriteRegister1 == rs || PipelineRegs.WriteRegister1 == rt);
        RAW3 = false;
        RAW2 = ((!jump) && (PipelineRegs.WriteRegister1 != 0) && 
                    (PipelineRegs.WriteRegister1 == rs || PipelineRegs.WriteRegister1 == rt)) || 
                    (PipelineRegs.lw2 && (PipelineRegs.WriteRegister2 == rs) || (PipelineRegs.WriteRegister2 == rt));
        RAW1 = (!jump) && (PipelineRegs.WriteRegister2 != 0) && 
                    (PipelineRegs.WriteRegister2 == rs || PipelineRegs.WriteRegister2 == rt) || 
                    (PipelineRegs.lw1 && (PipelineRegs.WriteRegister3 == rs) || (PipelineRegs.WriteRegister3 == rt));
    }

    public static Word getInstruction() {
        return instruction;
    }

    public static boolean isDone() {
        return done;
    }

    public static void stall() {
        instruction = new Word();
    }

    public static String draw() {
        StringBuilder output = new StringBuilder();
        output.append("+--------- [Instruction Fetch Stage ] ----------+\n");
        output.append("| Program Counter: " + MIPS.getPC() + "                           |\n");
        output.append("| done: " + done + "                                    |\n");
        output.append("| Instruction Number: " + MIPS.getInstructionNumber() + "                        |\n");
        output.append("| Instruction: " + instruction + " |\n");
        output.append("+-----------------------------------------------+\n");
        return output.toString();
    }
}
