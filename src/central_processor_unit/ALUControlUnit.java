// pretty much done;
// how about jr instruction when it's time for it to be added?


package central_processor_unit;


import simulator.wrapper.Wrapper;
import simulator.network.Link;
import simulator.gates.combinational.And;
import simulator.gates.combinational.Or;
import simulator.gates.combinational.Not;


public class ALUControlUnit extends Wrapper {


    // Constructor:
    public ALUControlUnit(String label, String stream, Link... links) {
        super(label, stream, links);
    }


    // Initialize:
    @Override
    public void initialize() {


        // ALU Control Unit inputs: instruction bits[5-0] (from instruction memory),
        // plus 2 bits of ALUOp (signals generated by Main Control Unit).

        // ALU Control Unit outputs: 3 bits; namely operation2, operation1, operation0.

        // Inputs Indexing:

        // input 0 ~ ALUOp1 (from main control unit)
        // input 1 ~ ALUOp0 (from main control unit)
        // input 2 ~ instruction bit 3 (from fetch stage)
        // input 3 ~ instruction bit 2 (from fetch stage)
        // input 4 ~ instruction bit 1 (from fetch stage)
        // input 5 ~ instruction bit 0 (from fetch stage)


        And ALUOp1AndInstrBit1 = new And("ALUOp1AndInstrBit1", getInput(0), getInput(4));
        Or operation2 = new Or("operation2",getInput(1), ALUOp1AndInstrBit1.getOutput(0));


        Not notALUOp1 = new Not("notALUOp1",getInput(0));
        Not notInstrBit2 = new Not("notInstrBit2",getInput(3));
        Or operation1 = new Or("operation1",
                                notALUOp1.getOutput(0), notInstrBit2.getOutput(0));


        Or instrBits0Or3 = new Or("instrBits0Or3", getInput(2), getInput(5));
        And operation0 = new And("operation0", getInput(0), instrBits0Or3.getOutput(0));


        addOutput(operation2.getOutput(0),
                        operation1.getOutput(0),
                        operation0.getOutput(0));


        // about jums???

//        //is jr
//        Not n0 = new Not("n0",getInput(2));
//        And a2 = new And("a2",n0.getOutput(0),getInput(3),
//          getInput(4),getInput(5)); //if 0 its jump
//        Not n3 = new Not("n3",a2.getOutput(0)); // jump


    }
}
