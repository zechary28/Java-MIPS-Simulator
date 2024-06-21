package mips;

// this class accesses the instruction memory and retrieves the 32 bit instruction Word
public class InstructionFetchStage {

    // inputs
    private static int instructionNumber = 0;

    private static boolean done = true;
    public static boolean RAW1 = false;
    public static boolean RAW2 = false;
    
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
        String rs = instruction.getWord().substring(6, 11);
        String rt = instruction.getWord().substring(11, 16);
        RAW2 = (PipelineRegs.WriteRegister1 != 0) && 
                    (PipelineRegs.WriteRegister1 == new Word(rs).toDec() || PipelineRegs.WriteRegister1 == new Word(rt).toDec());
        RAW1 = (PipelineRegs.WriteRegister2 != 0) && 
                    (PipelineRegs.WriteRegister2 == new Word(rs).toDec() || PipelineRegs.WriteRegister2 == new Word(rt).toDec());
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

    public static void draw() {
        System.out.println("+--------- [Instruction Fetch Stage ] ----------+");
        System.out.println("| Program Counter: " + MIPS.getPC() + "                           |");
        System.out.println("| done: " + done + "                                    |");
        System.out.println("| Instruction Number: " + MIPS.getInstructionNumber() + "                        |");
        System.out.println("| Instruction: " + instruction + " |");
        System.out.println("+-----------------------------------------------+");
    }
}
