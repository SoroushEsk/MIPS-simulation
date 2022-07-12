package RegisterFile;

import simulator.gates.combinational.And;
import simulator.gates.combinational.Not;
import simulator.gates.combinational.Or;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Mul2To1 extends Wrapper {

    public Mul2To1(String label, String stream, Link... links) {
        super(label, stream, links);
    }
    // multiplexer 2bit to 1
    // inputs index:
    // 0 first bit
    // 1 : second bit
    // 2: select signal
    // if select == 1 second bit get select
    @Override
    public void initialize() {
        Or or1 = new Or("Or1");
        And and0 = new And("And0");
        And and1 = new And("And1");
        Not notSelect = new  Not("not");

        and1.addInput(getInput(1),getInput(2));

        notSelect.addInput(getInput(2));
        and0.addInput(getInput(0), notSelect.getOutput(0));


        or1.addInput(and0.getOutput(0), and1.getOutput(0));

        addOutput(or1.getOutput(0));

    }
}
