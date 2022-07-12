// Completely Done


package DataPath;


import simulator.wrapper.Wrapper;
import simulator.network.Link;
import simulator.gates.combinational.Or;
import simulator.control.Simulator;


public class SignExtender extends Wrapper {


    // Constructor:
    public SignExtender(String label, String stream, Link... links) {
        super(label, stream, links);
    }


    // Initialize:
    @Override
    public void initialize() {


        // bit (OR) 0 = bit; therefore,
        // most significant bit (OR) 0 = most significant bit:

        Or or = new Or("or");
        or.addInput(getInput(0), Simulator.falseLogic);


        // The sign bit can then be extended in the uppermost 16 bits:

        for (int index = 0; index < 16; index++){
            addOutput(or.getOutput(0));
        }


        // Rest of the bits are the same as before sign extension:

        for (int index = 0; index < 16; index++){
            addOutput(getInput(index));
        }
    }
}
