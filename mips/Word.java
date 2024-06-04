package mips;

// abstraction of a 32 bit long data
public class Word {
    private final String word;
    public static final int WORD_LENGTH = 32;

    public Word() {
        this.word = "00000000000000000000000000000000";
    }

    public Word(String str) {
        String s = "";
        int count = 0;
        int bitcount = 0;
        int i = str.length() - 1;
        char c;
        // take only 0s and 1s starting from left side of input
        while (count < str.length() && bitcount < WORD_LENGTH) {
            c = str.charAt(i);
            if (c == '0' || c == '1') {
                s = c + s;
                bitcount ++;
            }
            count++;
            i--;
        }
        // if input too short, add leading zeros
        String zeros = "";
        for (int j = 0; j < WORD_LENGTH - bitcount; j++) {
            zeros = zeros + "0";
        }
        this.word = zeros + s;
    }

    public String getWord() {
        return this.word;
    }

    public static Word add(int rd, int rs, int rt) {
        return new Word("000000" + 
            toBinary(rs).subword(5) + 
            toBinary(rt).subword(5) + 
            toBinary(rd).subword(5) + 
            "00000" + 
            "100000");
    }

    // ALU METHODS

    public Word bitWiseAnd(Word w) {
        String s = "";
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            if ((w.word.charAt(i) == '1') && (this.word.charAt(i) == '1')) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
        }
        return new Word(s);
    }

    public Word bitWiseOr(Word w) {
        String s = "";
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            if ((w.word.charAt(i) == '1') || (this.word.charAt(i) == '1')) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
        }
        return new Word(s);
    }

    public Word logicalShiftLeft(int shamt) {
        String substr = this.word.substring(shamt, WORD_LENGTH);
        String s = "";
        for (int i = 0; i < shamt; i++) {
            s = "0" + s;
        }
        return new Word(substr + s);
    }

    public Word logicalShiftRight(int shamt) {
        String substr = this.word.substring(0, WORD_LENGTH - shamt);
        String s = "";
        for (int i = 0; i < shamt; i++) {
            s = "0" + s;
        }
        return new Word(s + substr);
    }

    public Word invert() {
        String s = "";
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            if (this.word.charAt(i) == '1') {
                s = "0" + s;
            } else {
                s = "1" + s;
            }
        }
        return new Word(s);
    }

    public Word add(Word w, char Cin) {
        char c = Cin; // Cin to be zero for add, 1 for subtract, update for Cout, use as Cin for next bit
        String s = ""; // accumulate s
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            char a = w.word.charAt(i);
            char b = this.word.charAt(i);
            // implementation of full adder
            // only case where c and s overlap is A.B.C
            if (a == '1' && b == '1' && c == '1') {
                s = "1" + s;
                c = '1';
            // SOP for sum : A XOR B XOR C
            } else if ((a == '1' ^ b == '1') ^ c == '1') {
                s = "1" + s;
                c = '0';
            // SOP for carry : AB + BC + AC
            } else if ((a == '1' && b == '1') || (b == '1' && c == '1') || (a == '1' && c == '1')) {
                s = "0" + s;
                c = '1';
            } else {
                s = "0" + s;
                c = '0';
            }
        }
        return new Word(s);
    }

    public Word negate() {
        return this.invert().add(new Word("0001"), '0');
    }

    // MISC METHODS

    public boolean equals(Word w) {
        boolean eq = true;
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            eq = eq && w.word.charAt(i) == this.word.charAt(i);
        }
        return eq;
    }

    public String subword(int numbits) {
        return this.word.substring(WORD_LENGTH - numbits);
    }

    // encoding instructions to binary 2s comp
    public static Word toBinary(int sum) {
        String s = "";
        int magnitude = Math.abs(sum);
        while (magnitude != 0) {
            if (magnitude % 2 == 1) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
            magnitude /= 2;
        }
        Word w = new Word(s);
        if (sum < 0) {
            w = w.negate();
        }
        return w;
    }

    // 2s complement to decimal
    public int toDec() {
        int msb = 1;
        int acc = 0;
        // standard binary to dec conversion for bits 1-32
        // except msb represents -2,147,483,648
        // works because java represents ints in 32 bit 2s complement
        for (int i = WORD_LENGTH - 1; i >= 0; i--) {
            if (this.word.charAt(i) == '1') {
                acc = acc + msb; 
            }
            msb = msb * 2;
        }
        return acc;
    }

    // MIGHT NEED REWORKING
    public String toHex() {
        String[] hexArray = {"0", "1", "2", "3", "4", "5", "6", "7"
                , "8", "9", "A", "B", "C", "D", "E", "F"};
        String s = "";
        for (int j = WORD_LENGTH/4 - 1; j >= 0; j--) {
            String substr = this.word.substring(4 * j, 4 * j + 4);
            Word subword = new Word(substr);
            int index = subword.toDec();
            s = hexArray[index].concat(s);
        }
        return "0x" + s;
    }

    @Override
    public String toString() {
        return getWord();
    }
}
