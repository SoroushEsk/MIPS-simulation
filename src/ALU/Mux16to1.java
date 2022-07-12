package ALU;

import simulator.network.Link;
import simulator.wrapper.Wrapper;
import simulator.wrapper.wrappers.Multiplexer;

public class Mux16to1 extends Wrapper {
    public Mux16to1(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Multiplexer multiplexer = new Multiplexer("mux16to1", "20x1");
        // first 4 bit for addressing
        for (int i = 0; i < 4; i++) {
            multiplexer.addInput(getInput(i));
        }

        for (int i = 4; i < 20; i++) {
         multiplexer.addInput(getInput(i));
        }

        addOutput(multiplexer.getOutput(0));

    }
}
