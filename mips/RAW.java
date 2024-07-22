package mips;

public class RAW {
    private boolean rsrt; // true if read from rs, false if read from rt
    private boolean ralw; // true if read after load word
    private int delay;
    private int reg_index; // register read from and to be forwarded to

    public RAW(boolean rsrt, boolean ralw, int delay, int reg_index) {
        this.rsrt = rsrt;
        this.ralw = ralw;
        this.delay = delay;
        this.reg_index = reg_index;
    }

    public int getDelay() {
        return delay;
    }

    public void settle() {
        if (!MIPS.enableForwarding) {

        } else {
            // handle forwarding
            if (rsrt) {
                PipelineRegs.ReadData1_1 = PipelineRegs.ALUResult2;
            } else if (!rsrt) {
                PipelineRegs.ReadData2_1 = PipelineRegs.ALUResult2;
            } else if (ralw) {
                
            }
        }
    }

    public void dec() {
        this.delay--;
    }

    @Override
    public String toString() {
        return reg_index + " forward to " + rsrt + " in " + delay + " steps";
    }
}
