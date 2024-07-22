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
                MIPS.add(4, 2, 2), // 2 RAW
                MIPS.sub(3, 4, 1), // 3 RAW
                MIPS.add(7, 3, 4), // 4 RAW
                MIPS.add(5, 2, 3), // 5 
                MIPS.add(10, 5, 5), // 6 1010 RAW
                MIPS.addi(12, 10, 2), // 7 1100 RAW
                MIPS.and(8, 10, 12), // 8 and 1000 RAW
                MIPS.or(14, 10, 12) // 9 or 1110
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

        mips.loadInstruction(List.of(MIPS.add(2, 1, 1), MIPS.add(4, 2, 2), MIPS.sub(3, 4, 1), MIPS.add(7, 3, 4), MIPS.add(5, 2, 3), MIPS.add(10, 5, 5), MIPS.addi(12, 10, 2), MIPS.and(8, 10, 12), MIPS.or(14, 10, 12)));
    */
}
