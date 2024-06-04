package mips;

// this class accesses the instruction memory and retrieves the 32 bit instruction Word
public class InstructionFetchStage {

    // inputs
    private static int ProgramCounter = 0;
    private static int instructionNumber = 0;

    private static boolean done = true;
    
    //outputs
    private static Word instruction;

    public static void update() {
        if (done) {
            instructionNumber++;
            done = false;
        }
    }

    public static void run() {
        if (!done) {
            ProgramCounter += 4;
            instruction = MIPS.getInstructions().get(ProgramCounter / 4 - 1);
            done = true;
        }
    }

    public static void branch(int branchsteps) {
        ProgramCounter += branchsteps;
        if (branchsteps != 0) {
            System.out.println("branch taken");
        }
    }

    public static void jump(int address) {
        // keeps 4 msbits of PC
        ProgramCounter = address % 67108864;
    }

    public static int getProgramCounter() {
        return ProgramCounter;
    }

    public static Word getInstruction() {
        return instruction;
    }

    public static boolean isDone() {
        return done;
    }

    public static void draw() {
        System.out.println("+--------- [Instruction Fetch Stage ] ----------+");
        System.out.println("| Program Counter: " + ProgramCounter + "                           |");
        System.out.println("| done: " + done + "                                    |");
        System.out.println("| Instruction Number: " + instructionNumber + "                        |");
        System.out.println("| Instruction: " + instruction + " |");
        System.out.println("+-----------------------------------------------+");
    }
}
