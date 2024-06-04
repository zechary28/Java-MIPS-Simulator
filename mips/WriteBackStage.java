package mips;

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

    public static void draw() {
        System.out.println("+------------[ Write Back Stage ]-------------+");
        System.out.println("| WriteRegister: " + WriteRegister + "                            |");
        System.out.println("| WriteData: " + WriteData + " |");
        System.out.println("| done: " + done + "                                  |");
        System.out.println("+---------------------------------------------+");
    }
}
