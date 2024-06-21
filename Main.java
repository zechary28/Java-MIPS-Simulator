import mips.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MIPS mips = new MIPS();
        mips.loadMemory(List.of(
            new mips.Word("1000 0000 0000 0000 0000 0000 0000 0000"),
            new mips.Word("0111 1111 1111 1111 1111 1111 1111 1111"),
            new mips.Word("0000 0000 0000 0000 0000 0000 0000 1010"),
            MIPS.toBinary(31)
        ));
        mips.loadInstruction(List.of(
                MIPS.add(2, 1, 1), // 1
                MIPS.add(3, 2, 1), // 2 RAW
                MIPS.add(4, 2, 2), // 3 
                MIPS.add(5, 3, 2), // 4 
                MIPS.add(10, 5, 5), // 5 1010 RAW
                MIPS.addi(12, 10, 2), // 6 1100
                MIPS.andi(8, 10, "1100"), // 7
                MIPS.ori(14, 10, "1100") // 8
                ));
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
        mips.cycle(true);
    }

    /* test
    mips.loadInstruction(List.of(
        MIPS.add(2,1,1), 
        MIPS.add(3,2,1), 
        MIPS.add(4,2,2), 
        MIPS.add(5,3,2), 
        MIPS.add(10,5,5)));
    */
}
