package mips.util;
import java.util.ArrayList;

public class RAWArrayList {
    private static final ArrayList<RAW> rawlist = new ArrayList<>();

    public void step() {
        rawlist.forEach(raw -> 
                {
                    if (raw.getDelay() < 1) {
                        raw.settle();
                        rawlist.remove(raw);
                    } else {
                        raw.dec();
                    }
                }
        );
    }

    public void showRAW() {
        rawlist.forEach(raw -> {System.out.println(raw);});
    }

    public void add(RAW raw) {
        rawlist.add(raw);
    }
}
