package mips;

public class ALUStage {
    
    // inputs
    private static Word operand1;
    private static Word operand2;
    private static String ALUControl;

    private static boolean done;

    // outputs
    private static Word ALUresult;
    private static char isZero;

    /*
    // functions / transformers
    Function<Word, Word, Word> add = (w1, w2) -> {
        int c = '0'; // Cin to be zero, update for Cout, use as Cin for next bit
        String s = ""; // accumulate s
        for (int i = Word.WORD_LENGTH - 1; i >= 0; i--) {
            char a = w1.word.charAt(i);
            char b = w2.word.charAt(i);
            // implementation of full adder
            // only case where c and s overlap is A.B.C
            if (a == '1' && b == '1' && c == '1') {
                s = "1".concat(s);
                c = '1';
            // SOP for sum : A XOR B XOR C
            } else if ((a == '1' ^ b == '1') ^ c == '1') {
                s = "1".concat(s);
                c = '0';
            // SOP for carry : AB + BC + AC
            } else if ((a == '1' && b == '1') || (b == '1' && c == '1') || (a == '1' && c == '1')) {
                s = "0".concat(s);
                c = '1';
            } else {
                s = "0".concat(s);
                c = '0';
            }
        }
        return new Word(s);
    };

    Function<Word, Word> invert = w -> {
        String s = "";
        for (int i = Word.WORD_LENGTH - 1; i >= 0; i--) {
            if (this.w.charAt(i) == '1') {
                s = "0".concat(s);
            } else {
                s = "1".concat(s);
            }
        }
        return new Word(s);
    };
    */

    public ALUStage() {
        
    }

    public static void update(Word op1, Word op2, String ALUCtrl) {
        operand1 = op1;
        operand2 = op2;
        ALUControl = ALUCtrl;
        done = false;
    }

    public static void run() {
        // isZero
        isZero = operand1.negate().add(operand2, '0').equals(new Word())
            ? '1'
            : '0';
        // Lecture 12 Control Slide 21
        // ALU operation
        boolean ainvert = ALUControl.charAt(0) == '1';
        Word A = ainvert ? operand1.invert() : operand1;
        boolean binvert = ALUControl.charAt(1) == '1';
        Word B = binvert ? operand2.invert() : operand2;
        String ALUoperation = ALUControl.substring(2); // not to be confused with ALUop from Control Unit
        // if subtract or slt, Cin = 1 (+1 required for negation in 2s complement)
        char Cin = binvert && ALUoperation.equals("10") || ALUoperation.equals("11") ? '1' : '0';

        if (ALUoperation.equals("00")) {
            ALUresult = A.bitWiseAnd(B);
        } else if (ALUoperation.equals("01")) {
            ALUresult = A.bitWiseOr(B);
        } else if (ALUoperation.equals("10")) {
            ALUresult = A.add(B, Cin);
        } else { // assume slt instruction
            ALUresult = A.add(B, Cin);
            ALUresult = new Word("" + ALUresult.getWord().charAt(0));
        }
        done = true;
    }

    public static boolean isDone() {
        return done;
    }
    public static Word getResult() {
        return ALUresult;
    }
    public static char isZero() {
        return isZero;
    }

    public static void draw() {
        System.out.println("+------------------[ ALU Stage ]-----------------+");
        System.out.println("| operand1: " + operand1 + "     |");
        System.out.println("| operand2: " + operand2 + "     |");
        System.out.println("| ALUControl " + ALUControl + "                                |");
        System.out.println("| done: " + done + "                                     |");
        System.out.println("| ALUresult: " + ALUresult + "    |");
        System.out.println("| isZero: " + isZero + "                                      |");
        System.out.println("+------------------------------------------------+");
    }
}
