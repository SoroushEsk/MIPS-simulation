package RegisterFile;

import simulator.network.Link;
import simulator.wrapper.Wrapper;
import simulator.wrapper.wrappers.Multiplexer;

public class Mul4To1 extends Wrapper {
    public Mul4To1(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Multiplexer mux = new Multiplexer("4x1mux", "6x1");
        mux.addInput(getInput(4), getInput(5));
        for (int i = 0; i < 4; i++) {
            mux.addInput(getInput(i));
        }
        addOutput(mux.getOutput(0));
    }
}
