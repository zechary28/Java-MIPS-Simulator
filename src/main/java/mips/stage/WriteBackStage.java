package mips.stage;

import mips.MIPS;
import mips.util.Word;

public class WriteBackStage {
    
    // inputs
    private static int WriteRegister;
    private static Word WriteData;
    private static char RegWrite;

    private static boolean done;

    public static void update(int WR, Word WD, char regwrite) {
        WriteRegister = WR;
        WriteData = WD;
        RegWrite = regwrite;
        done = false;
    }

    public static void run() {
        if (RegWrite == '1') {
            MIPS.getRegisters().set(WriteRegister, WriteData);
        }
        done = true;
    }

    public static String draw() {
        StringBuilder output = new StringBuilder();
        output.append("+------------[ Write Back Stage ]-------------+");
        output.append("| WriteRegister: " + WriteRegister + "                            |");
        output.append("| WriteData: " + WriteData + " |");
        output.append("| done: " + done + "                                  |");
        output.append("+---------------------------------------------+");
        return output.toString();
    }
}
