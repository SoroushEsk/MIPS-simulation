// ALU must be added.


package central_processor_unit;


import simulator.wrapper.Wrapper;
import simulator.network.Link;


public class CPU extends Wrapper {


    // Constructor:
    public CPU(String label, String stream, Link... links) {
        super(label, stream, links);
    }


    // Initialize:
    @Override
    public void initialize() {

        MainControlUnit mainControlUnit = new MainControlUnit("controlUnit", "6x10");

        // ALU alu = new ALU("ALU","67x33");
        ALUControlUnit aluControlUnit = new ALUControlUnit("ALUControl", "6x3");

    }
}
