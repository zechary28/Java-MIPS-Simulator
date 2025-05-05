package mips.stage;

import mips.MIPS;
import mips.util.Word;

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

    public static String draw() {
        StringBuilder output = new StringBuilder();
        String shiftprint = shiftleft 
            ? "| shift: left, " + shift + "                        |"
            : shiftright
            ? "| shift: right, " + shift + "                       |"
            : "| shift: false                          |";
        String jumpprint = ControlUnit.getJump()
            ? "| jump: to address " + JumpAddress + "                    |"
            : "| jump: no jump                         |";
        output.append("+------------[Decode Stage]-------------+");
        output.append("| opcode: " + opcode + "                        |");
        output.append("| rs: " + rs + "                             |");
        output.append("| rt: " + rt + "                             |");
        output.append("| rd: " + rd + "                             |");
        output.append("| shamt: " + shamt + "                          |");
        output.append("| func: " + func + "                          |");
        output.append("| immediate: " + immediate + "           |");
        output.append("| done: " + done + "                            |");
        output.append("| RR1: " + RR1 + "                                |");
        output.append("| RR2: " + RR2 + "                                |");
        output.append("| RD1: " + RD1 + " |");
        output.append("| RD2: " + RD2 + " |");
        output.append(jumpprint);
        output.append(shiftprint);
        output.append("+---------------------------------------+");
        return output.toString();
    }
}
