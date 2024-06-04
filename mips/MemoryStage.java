package mips;

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
        return ReadData;
    }

    public static void update(Word address, Word WD, char memread, char memwrite) {
        Address = address;
        WriteData = WD;
        MemRead = memread;
        MemWrite = memwrite;
        done = false;
    }

    public static void run() {
        if (MemRead == '1' && MemWrite == '0') {
            System.out.println("memory is read");
            ReadData = MIPS.getMemory().get(Address.toDec() / 4);
        } else if (MemRead == '0' && MemWrite == '1') {
            System.out.println("memory is written");
            MIPS.getMemory().set(Address.toDec() / 4, WriteData);
        } else {

        }
        done = true;
    }

    public static void draw() {
        System.out.println("+--------------[ Memory Stage ]---------------+");
        System.out.println("| Address: " + Address + "   |");
        System.out.println("| WriteData: " + WriteData + " |");
        System.out.println("| MemRead: " + MemRead + "                                  |");
        System.out.println("| MemWrite: " + MemWrite + "                                 |");
        System.out.println("| done: " + done + "                                  |");
        System.out.println("| ReadData: " + ReadData + "  |");
        System.out.println("+---------------------------------------------+");
    }
}
