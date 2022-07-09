package ALU;

import simulator.gates.combinational.Not;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Not32 extends Wrapper {
    public Not32(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Not[] nots = new Not[32];
        for (int i = 0; i < 32; i++) {
            nots[i] = new Not("nots" + i, getInput(i), getInput(i+32));
        }
        for (int i = 0; i < 32; i++) {
            addOutput(nots[i].getOutput(0));
        }
    }
}
