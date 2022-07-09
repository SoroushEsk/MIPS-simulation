package ALU;

import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class LogicalUnit extends Wrapper {
    public LogicalUnit(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        And32 and32 = new And32("LUand32", "64x32");
        Or32 or32 = new Or32("LUor32", "64x32");

        for (int i = 0; i < 64; i++) {
            and32.addInput(getInput(i));
            or32.addInput(getInput(i));
        }

        // 0:31, output of and
        for (int i = 0; i < 32; i++) {
            addOutput(and32.getOutput(i));
        }

        //32:63 output of or
        for (int i = 0; i < 32; i++) {
            addOutput(or32.getOutput(i));
        }
    }

}
