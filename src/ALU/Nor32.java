package ALU;

import simulator.gates.combinational.Nor;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Nor32 extends Wrapper {
    public Nor32(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Nor[] nors = new Nor[32];
        for (int i = 0; i < 32; i++) {
            nors[i] = new Nor("nor"+i, getInput(i), getInput(i+32));
        }
        for (int i = 0; i < 32; i++) {
            addOutput(nors[i].getOutput(0));
        }
    }
}
