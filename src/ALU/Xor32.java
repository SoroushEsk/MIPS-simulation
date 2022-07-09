package ALU;

import simulator.gates.combinational.Xor;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Xor32 extends Wrapper {
    public Xor32(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Xor[] xors = new Xor[32];
        for (int i = 0; i < 32; i++) {
            xors[i] = new Xor("xors"+i, getInput(i), getInput(i+32));
        }
        for (int i = 0; i < 32; i++) {
            addOutput(xors[i].getOutput(0));
        }
    }
}
