package ALU;

import simulator.gates.combinational.And;
import simulator.gates.combinational.Not;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Zero extends Wrapper {
    public Zero(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Not32 not32 = new Not32("not0flag", "32x32");
        And and = new And("and0flag");
        for (int i = 0; i < 32; i++) {
            not32.addInput(getInput(i));
        }
        for (int i = 0; i < 32; i++) {
            and.addInput(not32.getOutput(i));
        }
        addOutput(and.getOutput(0));
    }
}
