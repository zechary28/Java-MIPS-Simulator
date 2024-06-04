package mips;

public class DecodeStage {
    
    // inputs
    private static String opcode;
    private static String rs;
    private static String rt;
    private static String rd;
    private static String shamt;
    private static String func;
    private static String immediate;

    private static boolean done = true;

    // outputs
    private static int RR1;
    private static int RR2;
    private static Word RD1;
    private static Word RD2;
    private static int shift;

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
            ControlUnit.update(opcode, func);
            done = false;
        }
    }

    public static void run() {
        if (!done) {
            ControlUnit.run();
            RR1 = new Word(rs).toDec();
            RR2 = new Word(rt).toDec();
            RD1 = MIPS.getRegisters().get(RR1);
            RD2 = MIPS.getRegisters().get(RR2);
            shift = new Word(shamt).toDec();
            done = true;
        }   
    }

    // getters and setters
    public static String getRT() {
        return rt;
    }
    public static String getRD() {
        return rd;
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
    
    public static boolean isDone() {
        return done;
    }

    public static void draw() {
        System.out.println("+---------------------------------------+");
        System.out.println("|            [Decode Stage]             |");
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
        System.out.println("| shift: " + shift + "                              |");
        System.out.println("+---------------------------------------+");
    }
}
