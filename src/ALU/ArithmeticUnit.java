package ALU;

import simulator.control.Simulator;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class ArithmeticUnit extends Wrapper {
    public ArithmeticUnit(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        AddSub addOrSub = new AddSub("arithAddSub", "65x32");
        Compare cmp = new Compare("arithCmp", "65x32");

        for (int i = 0; i < 64; i++) {
            addOrSub.addInput(getInput(i));
            cmp.addInput(getInput(i));
        }
        addOrSub.addInput(getInput(64));
        cmp.addInput(Simulator.trueLogic);

        //0:31 andOrSub output
        for (int i = 0; i < 32; i++) {
            addOutput(addOrSub.getOutput(i));
        }

        //32:63 cmp output
        for (int i = 0; i < 32; i++) {
            addOutput(cmp.getOutput(i));
        }
    }
}
