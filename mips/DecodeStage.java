package mips;

public class DecodeStage {
    
    // inputs
    private static String opcode = "000000";
    private static String rs = "00000";
    private static String rt = "00000";
    private static String rd = "00000";
    private static String shamt = "00000";
    private static String func = "000000";
    private static String immediate = "0000000000000000";
    private static String jumpaddress = "00000000000000000000000000";
    private static int RR1 = 0;
    private static int RR2 = 0;

    private static boolean done = true;

    // outputs
    private static Word RD1 = new Word();
    private static Word RD2 = new Word();
    private static int shift = 0;
    private static int JumpAddress = 0;
    // my implementation for functionality
    public static boolean shiftright = false;
    public static boolean shiftleft = false;

    public static void update(Word word) {
        if (done) {
            String w = word.getWord();
            opcode = w.substring(0, 6);
            rs = w.substring(6, 11);
            rt = w.substring(11, 16);
            rd = w.substring(16, 21);
            shamt = w.substring(21, 26);
            func = w.substring(26);
            immediate = w.substring(16);
            jumpaddress = w.substring(6);
            
            RR1 = new Word(rs).toDec();
            RR2 = new Word(rt).toDec();
            done = false;
        }
    }

    public static void run() {
        if (!done) {
            ControlUnit.update(opcode, func);
            ControlUnit.run();
            RD1 = MIPS.getRegisters().get(RR1);
            RD2 = MIPS.getRegisters().get(RR2);
            shiftleft = opcode.equals("000000") && func.equals("000000");
            shiftright = opcode.equals("000000") && func.equals("000010");
            shift = new Word(shamt).toDec();
            JumpAddress = (new Word(jumpaddress).logicalShiftLeft(2).toDec() % 67108864) - 4;
            done = true;
        }   
    }

    // getters and setters
    public static int getRT() {
        return new Word(rt).toDec();
    }
    public static int getRD() {
        return new Word(rd).toDec();
    }
    public static Word getRD1() {
        return RD1;
    }
    public static Word getRD2() {
        return RD2;
    }
    public static int getShift() {
        return shift;
    }
    public static String getImmediate() {
        return immediate;
    }
    public static String getFunc() {
        return func;
    }
    public static int getJumpAddress() {
        return JumpAddress;
    }
    
    public static boolean isDone() {
        return done;
    }

    public static void draw() {
        String shiftprint = shiftleft 
            ? "| shift: left, " + shift + "                        |"
            : shiftright
            ? "| shift: right, " + shift + "                       |"
            : "| shift: false                          |";
        String jumpprint = ControlUnit.getJump()
            ? "| jump: to address " + JumpAddress + "                    |"
            : "| jump: no jump                         |";
        System.out.println("+------------[Decode Stage]-------------+");
        System.out.println("| opcode: " + opcode + "                        |");
        System.out.println("| rs: " + rs + "                             |");
        System.out.println("| rt: " + rt + "                             |");
        System.out.println("| rd: " + rd + "                             |");
        System.out.println("| shamt: " + shamt + "                          |");
        System.out.println("| func: " + func + "                          |");
        System.out.println("| immediate: " + immediate + "           |");
        System.out.println("| done: " + done + "                            |");
        System.out.println("| RR1: " + RR1 + "                                |");
        System.out.println("| RR2: " + RR2 + "                                |");
        System.out.println("| RD1: " + RD1 + " |");
        System.out.println("| RD2: " + RD2 + " |");
        System.out.println(jumpprint);
        System.out.println(shiftprint);
        System.out.println("+---------------------------------------+");
    }
}
