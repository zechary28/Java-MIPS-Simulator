package mips.stage;

public class ControlUnit {
    // inputs
    private static boolean rformat;
    private static boolean lw;
    private static boolean sw;
    private static boolean beq;
    private static boolean addi;
    private static boolean andi;
    private static boolean ori;

    private static boolean done;

    // outputs
    private static char RegDst;
    private static char ALUSrc;
    private static char MemToReg;
    private static char RegWrite;
    private static char MemRead;
    private static char MemWrite;
    private static char Branch;
    private static boolean jump;
    
    // ALUControl
    private static String func;
    private static char ALUop1;
    private static char ALUop0;
    private static String ALUControl;

    public static void update(String opcode, String fun) {
        // (CS2100 Lec 12 Control Slide 32)
        // r-format instructions
        rformat = opcode.equals("000000");
        lw = opcode.equals("100011");
        sw = opcode.equals("101011");
        beq = opcode.equals("000100");
        addi = opcode.equals("001000");
        andi = opcode.equals("001100");
        ori = opcode.equals("001101");
        jump = opcode.equals("000010");
        func = fun;     
        done = false;
    }

    public static void run() {
        // (CS2100 Lec 12 Control Slide 32)
        RegDst = rformat ? '1' : '0';
        ALUSrc = lw || sw || (addi || andi || ori) ? '1' : '0';
        MemToReg = lw ? '1' : '0';
        RegWrite = rformat || lw || (addi || andi || ori) ? '1' : '0';
        MemRead = lw ? '1' : '0';
        MemWrite = sw ? '1' : '0';
        Branch = beq ? '1' : '0';
        ALUop0 = beq ? '1' : '0';
        ALUop1 = rformat ? '1' : '0';
        done = true;

        // generate ALUControl (CS2100 Lec 12 Control Slide 27)
        char f0 = func.charAt(5);
        char f1 = func.charAt(4);
        char f2 = func.charAt(3);
        char f3 = func.charAt(2);
        String ALUc0 = (ALUop1 == '1' && (f3 == '1' || f0 == '1')) ? "1" : "0";
        String ALUc1 = !(ALUop1 == '1' && f2 == '1') ? "1" : "0";
        String ALUc2 = ALUop0 == '1' || (ALUop1 == '1' && f1 == '1') ? "1" : "0";
        String ALUc3 = "0";
        // cheating here for i instructions bcs it's not covered
        ALUControl = addi
                ? "0010"
                : andi
                ? "0000"
                : ori
                ? "0001"
                : ALUc3 + ALUc2 + ALUc1 + ALUc0;
    }

    // getters and setters
    public static char getRegDst() {
        return RegDst;
    }
    public static char getALUSrc() {
        return ALUSrc;
    }
    public static char getMemToReg() {
        return MemToReg;
    }
    public static char getRegWrite() {
        return RegWrite;
    }
    public static char getMemRead() {
        return MemRead;
    }
    public static char getMemWrite() {
        return MemWrite;
    }
    public static char getBranch() {
        return Branch;
    }
    public static String getALUControl() {
        return ALUControl;
    }
    public static boolean getJump() {
        return jump;
    }
    public static boolean getLW() {
        return lw;
    }
    public static boolean isDone() {
        return done;
    }

    public static String draw() {
        StringBuilder output = new StringBuilder();
        String typeprint;
        if (rformat) {
            typeprint = "| type: rformat          |";
        } else if (lw) {
            typeprint = "| type: lw               |";
        } else if (sw) {
            typeprint = "| type: sw               |";
        } else if (beq) {
            typeprint = "| type: beq              |";
        } else if (addi || andi || ori) {
            typeprint = "| type: alu imm          |";
        } else if (jump) {
            typeprint = "| type: jump             |";
        } else {
            typeprint = "| type: invalid opcode   |";
        }
        output.append("+----[ Control Unit ]----+");
        output.append(typeprint);
        output.append("| done: " + done + "             |");
        output.append("+------[ Signals ]-------+");
        output.append("| RegDst: " + RegDst + "              |");
        output.append("| ALUSrc: " + ALUSrc + "              |");
        output.append("| MemToReg: " + MemToReg + "            |");
        output.append("| RegWrite: " + RegWrite + "            |");
        output.append("| MemRead: " + MemRead + "             |");
        output.append("| MemWrite: " + MemWrite + "            |");
        output.append("| Branch: " + Branch + "              |");
        output.append("+----[ ALU Control ]-----+");
        output.append("| Func: " + func + "           |");
        output.append("| ALUop: " + ALUop1 + ALUop0 + "              |");
        output.append("| ALUControl: " + ALUControl + "       |");
        output.append("+------------------------+");
        return output.toString();
    }
}
