package mips.stage;

import mips.MIPS;
import mips.util.Word;

public class MemoryStage {
    
    // inputs
    private static Word Address;
    private static Word WriteData;
    private static char MemRead;
    private static char MemWrite;

    private static boolean done;

    //outputs
    private static Word ReadData;

    public static Word getReadData() {
        System.out.println("get read data from mem" + ReadData);
        return ReadData;
    }

    public static void update(Word address, Word WD, char memread, char memwrite) {
        Address = address;
        WriteData = WD;
        MemRead = memread;
        MemWrite = memwrite;
        done = false;
    }

    public static String run() {
        StringBuilder output = new StringBuilder();
        if (MemRead == '1' && MemWrite == '0') {
            output.append("memory is read");
            ReadData = MIPS.getMemory().get(Address.toDec() / 4);
        } else if (MemRead == '0' && MemWrite == '1') {
            output.append("memory is written");
            MIPS.getMemory().set(Address.toDec() / 4, WriteData); /////////////////
        } else {

        }
        done = true;
        return output.toString();
    }

    public static String draw() {
        StringBuilder output = new StringBuilder();
        output.append("+--------------[ Memory Stage ]---------------+\n");
        output.append("| Address: " + Address + "   |\n");
        output.append("| WriteData: " + WriteData + " |\n");
        output.append("| MemRead: " + MemRead + "                                  |\n");
        output.append("| MemWrite: " + MemWrite + "                                 |\n");
        output.append("| done: " + done + "                                  |\n");
        output.append("| ReadData: " + ReadData + "  |\n");
        output.append("+---------------------------------------------+\n");
        return output.toString();
    }
}
