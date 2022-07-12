// Completely Done


package DataPath;


import simulator.wrapper.Wrapper;
import simulator.network.Link;
import simulator.control.Simulator;


public class LeftShifter32Bit extends Wrapper {


    // Constructor:
    public LeftShifter32Bit(String label, String stream, Link... links ) {
        super(label, stream , links);
    }


    // Initialize:
    @Override
    public void initialize() {


        // Shift all bits 2 positions to the left. The two uppermost bits
        // (indices 0 and 1) are therefore missed. Index 2, thus, becomes
        // the first (index 0); index 3 becomes the second (index 1) and so on:

        for (int index = 2; index < 32; index++) {
            addOutput(getInput(index));
        }


        // Fill the two lowermost bits with zero's:

        addOutput(Simulator.falseLogic);
        addOutput(Simulator.falseLogic);
    }
}
