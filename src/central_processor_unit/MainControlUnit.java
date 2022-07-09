// Not sure about the instruction bits order (in NOT's)
// The order must be checked with other components (instruction memory).

// double-check signals' values with the table for don't-care conditions
// when new instructions are to be added.


package central_processor_unit;


import simulator.wrapper.Wrapper;
import simulator.network.Link;
import simulator.gates.combinational.Not;
import  simulator.gates.combinational.And;
import simulator.gates.combinational.Or;


public class MainControlUnit extends Wrapper {


    // Constructor:
    public MainControlUnit(String label, String stream, Link... links) {
        super(label, stream, links);
    }


    // Initialize:
    @Override
    public void initialize() {


        // Main Control Unit inputs: instruction bits [31-26] (from instruction memory)

        // Main Control Unit outputs: control signals below:
        // RegDst, Branch, MemRead, MemToReg, ALUOp1, ALUOp0, MemWrite, ALUSrc, RegWrite


        // inputs indexing:

        // input 0 ~ instruction bit 31
        // input 1 ~ instruction bit 30
        // input 2 ~ instruction bit 29
        // input 3 ~ instruction bit 28
        // input 4 ~ instruction bit 27
        // input 5 ~ instruction bit 26


        Not notInstrBit31 = new Not("notInstrBit31",getInput(0));
        Not notInstrBit30 = new Not("notInstrBit30",getInput(1));
        Not notInstrBit29 = new Not("notInstrBit29",getInput(2));
        Not notInstrBit28 = new Not("notInstrBit28",getInput(3));
        Not notInstrBit27 = new Not("notInstrBit27",getInput(4));
        Not notInstrBit26 = new Not("notInstrBit26",getInput(5));


        And RegDst_ALUOp1 = new And("regDst_ALUOp1");
        RegDst_ALUOp1.addInput(notInstrBit26.getOutput(0), notInstrBit27.getOutput(0),
                notInstrBit28.getOutput(0),notInstrBit29.getOutput(0),
                notInstrBit30.getOutput(0),notInstrBit31.getOutput(0));


        // For LW instructions:
        And MemRead_MemToReg = new And("MemRead_MemToReg");
        MemRead_MemToReg.addInput(getInput(0),notInstrBit30.getOutput(0),notInstrBit29.getOutput(0),
                notInstrBit28.getOutput(0),getInput(4),getInput(5));


        // For SW instructions:
        And MemWrite = new And("MemWrite");
        MemWrite.addInput(getInput(0), notInstrBit30.getOutput(0),
                          getInput(2), notInstrBit28.getOutput(0),
                          getInput(4),getInput(5));


        // For Branch instructions:
        And Branch_ALUOp0 = new And("Branch_ALUOp0");
        Branch_ALUOp0.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                               notInstrBit29.getOutput(0), getInput(3),
                               notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        // For Jump Instructions:

        // For j:
        And Jump = new And("Jump");
        Jump.addInput(notInstrBit26.getOutput(0),getInput(4));

        // For jr:
        And jr = new And("jr");
        jr.addInput(getInput(0), notInstrBit26.getOutput(0),
                    notInstrBit27.getOutput(0),notInstrBit28.getOutput(0),
                    notInstrBit29.getOutput(0),notInstrBit30.getOutput(0));


        // For addi instructions:
        And isAddImmediate = new And("isAddImmediate");
        isAddImmediate.addInput(notInstrBit26.getOutput(0), notInstrBit27.getOutput(0), getInput(2),
                notInstrBit28.getOutput(0), notInstrBit30.getOutput(0), notInstrBit31.getOutput(0));


        Or ALUSrc = new Or("ALUSrc");
        ALUSrc.addInput(MemRead_MemToReg.getOutput(0),MemWrite.getOutput(0),isAddImmediate.getOutput(0));


        Or RegWrite = new Or("RegWrite");
        RegWrite.addInput(RegDst_ALUOp1.getOutput(0),MemRead_MemToReg.getOutput(0),isAddImmediate.getOutput(0));


        // Output: Generated Signals:

        addOutput(RegDst_ALUOp1.getOutput(0),
                  Branch_ALUOp0.getOutput(0),
                  MemRead_MemToReg.getOutput(0),
                  MemRead_MemToReg.getOutput(0),
                  RegDst_ALUOp1.getOutput(0),
                  Branch_ALUOp0.getOutput(0),
                  MemWrite.getOutput(0),
                  ALUSrc.getOutput(0),
                  RegWrite.getOutput(0));


        // Main order in the signal tables on the book used to be like this:
        /*
        addOutput(RegDst_ALUOp1.getOutput(0),
                  ALUSrc.getOutput(0),
                  MemRead_MemToReg.getOutput(0),
                  RegWrite.getOutput(0),
                  MemRead_MemToReg.getOutput(0),
                  MemWrite.getOutput(0),
                  Branch_ALUOp0.getOutput(0),
                  RegDst_ALUOp1.getOutput(0),
                  Branch_ALUOp0.getOutput(0),
                  Jump.getOutput(0));
        */
        // regDst_ALUOp1, ALuSrc, MemRead_MemToReg, RegWrite, memRead,
        // memWrite, Branch_ALUOp0, RegDst_ALUOp1, Branch_ALUOp0, Jump


        // For test:
        // Simulator.debugger.addTrackItem(ALUSrc,RegWrite,MemRead_MemToReg,MemWrite);
    }
}