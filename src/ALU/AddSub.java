package ALU;

import simulator.gates.combinational.Xor;
import simulator.network.Link;
import simulator.wrapper.Wrapper;
import simulator.wrapper.wrappers.FullAdder;


public class AddSub extends Wrapper {
    public AddSub(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {

        FullAdder[] fullAdders = new FullAdder[32];
        Xor[] xors = new Xor[32];
        for (int i = 0; i < 32; i++) {
            xors[i] = new Xor("XorAdd"+i, getInput(64), getInput(i+32));
        }

        for (int i = 0; i < 32; i++) {
            fullAdders[i] = new FullAdder("FA"+i, "3x2", getInput(i), xors[i].getOutput(0));
        }

        fullAdders[31].addInput(getInput(64));

        for (int i = 30; i >=0; i--) {
            fullAdders[i].addInput(fullAdders[i+1].getOutput(0));
        }

        for (int i = 0; i < 32; i++) {
            addOutput(fullAdders[i].getOutput(1));
        }
    }
}
