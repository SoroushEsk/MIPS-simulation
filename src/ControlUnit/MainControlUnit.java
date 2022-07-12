

package ControlUnit;


import simulator.control.Simulator;
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


        // Main Control Unit inputs: instruction bits [31-26] (instruction opcode)

        // Main Control Unit outputs: control signals below:

        // half->0, byte_->1, zeroExt->2, regWrite->3, regDst1->4, regDst0->5, aluSrc->6,
        // branch->7, brchNe->8, memWrite->9, memToReg1->10, memToReg0->11, jump->12, jal->13,
        // memRead->14, ALUOp3->15, ALUOp2, ALUOp1, ALUOp0


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


        // Defining opcodes for all instructions (R-Format instructions are always "000000"
        // and their function field is different):


        And rFormatOpcode = new And("rFormatOpcode"); // "000000"
        rFormatOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        And addiOpcode = new And("addiOpcode"); // "001000"
        addiOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                getInput(2), notInstrBit28.getOutput(0), notInstrBit27.getOutput(0),
                notInstrBit26.getOutput(0));


        And lwOpcode = new And("lwOpcode"); // "100011"
        lwOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                getInput(4), getInput(5));


        And swOpcode = new And("swOpcode"); // "101011"
        swOpcode.addInput(getInput(0), notInstrBit30.getOutput(0), getInput(2),
                notInstrBit28.getOutput(0), getInput(4), getInput(5));


        And lhOpcode = new And("lhOpcode"); // "100001"
        lhOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                notInstrBit27.getOutput(0), getInput(5));


        And lhuOpcode = new And("lhuOpcode"); // "100101"
        lhuOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), getInput(3),
                notInstrBit27.getOutput(0), getInput(5));


        And shOpcode = new And("shOpcode"); // "101001"
        shOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                getInput(2), notInstrBit28.getOutput(0),
                notInstrBit27.getOutput(0), getInput(5));


        And lbOpcode = new And("lbOpcode"); // "100000"
        lbOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        And lbuOpcode = new And("lbuOpcode"); // "100100"
        lbuOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), getInput(3),
                notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        And sbOpcode = new And("sbOpcode"); // "101000"
        sbOpcode.addInput(getInput(0), notInstrBit30.getOutput(0),
                getInput(2), notInstrBit28.getOutput(0),
                notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        And andiOpcode = new And("andiOpcode"); // "001100"
        andiOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                getInput(2), getInput(3), notInstrBit27.getOutput(0),
                notInstrBit26.getOutput(0));


        And oriOpcode = new And("oriOpcode"); // "001101"
        oriOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                getInput(2), getInput(3), notInstrBit27.getOutput(0), getInput(5));


        And beqOpcode = new And("beqOpcode"); // "000100"
        beqOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), getInput(3),
                notInstrBit27.getOutput(0), notInstrBit26.getOutput(0));


        And bneOpcode = new And("bneOpcode"); // "000101"
        bneOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), getInput(3),
                notInstrBit27.getOutput(0), getInput(5));


        And sltiOpcode = new And("sltiOpcode"); // "001010"
        sltiOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                getInput(2), notInstrBit28.getOutput(0), getInput(4),
                notInstrBit26.getOutput(0));


        And jOpcode = new And("jOpcode"); // "000010"
        jOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                getInput(4), notInstrBit26.getOutput(0));


        And jalOpcode = new And("jalOpcode"); // "000011"
        jalOpcode.addInput(notInstrBit31.getOutput(0), notInstrBit30.getOutput(0),
                notInstrBit29.getOutput(0), notInstrBit28.getOutput(0),
                getInput(4), getInput(5));




        // Now, setting signals based on the instructions by opcode:


        Or half = new Or("half");
        half.addInput(lhOpcode.getOutput(0), lhuOpcode.getOutput(0),
                shOpcode.getOutput(0));


        Or byte_ = new Or("byte_");
        byte_.addInput(lbOpcode.getOutput(0), lbuOpcode.getOutput(0),
                sbOpcode.getOutput(0));


        Or zeroExt = new Or("zeroExt");
        zeroExt.addInput(lhuOpcode.getOutput(0), lbuOpcode.getOutput(0));


        Or regWrite = new Or("regWrite");
        regWrite.addInput(rFormatOpcode.getOutput(0), addiOpcode.getOutput(0),
                lwOpcode.getOutput(0), lhOpcode.getOutput(0), lhuOpcode.getOutput(0),
                lbOpcode.getOutput(0), lbuOpcode.getOutput(0),
                andiOpcode.getOutput(0), oriOpcode.getOutput(0),
                sltiOpcode.getOutput(0), jalOpcode.getOutput(0));


        Or regDst1 = new Or("regDst1");
        regDst1.addInput(jalOpcode.getOutput(0), jalOpcode.getOutput(0));

        Or regDst0 = new Or("regDst0");
        regDst0.addInput(rFormatOpcode.getOutput(0), rFormatOpcode.getOutput(0));


        Or aluSrc = new Or("aluSrc");
        aluSrc.addInput(addiOpcode.getOutput(0), lwOpcode.getOutput(0),
                swOpcode.getOutput(0), lhOpcode.getOutput(0),
                lhuOpcode.getOutput(0), shOpcode.getOutput(0),
                lbOpcode.getOutput(0), lbuOpcode.getOutput(0),
                sbOpcode.getOutput(0), andiOpcode.getOutput(0),
                oriOpcode.getOutput(0), sltiOpcode.getOutput(0));


        Or branch = new Or("branch");
        branch.addInput(beqOpcode.getOutput(0), beqOpcode.getOutput(0));


        Or brchNe = new Or("brchNe");
        brchNe.addInput(bneOpcode.getOutput(0), bneOpcode.getOutput(0));


        Or memWrite = new Or("memWrite");
        memWrite.addInput(swOpcode.getOutput(0), shOpcode.getOutput(0),
                sbOpcode.getOutput(0));


        Or memToReg1 = new Or("memToReg1");
        memToReg1.addInput(lhOpcode.getOutput(0), lhuOpcode.getOutput(0),
                lbOpcode.getOutput(0), lbuOpcode.getOutput(0));

        Or memToReg0 = new Or("memToReg0");
        memToReg0.addInput(lwOpcode.getOutput(0), lbOpcode.getOutput(0),
                lbuOpcode.getOutput(0));


        Or jump = new Or("jump");
        jump.addInput(jOpcode.getOutput(0), jalOpcode.getOutput(0));


        Or jal = new Or("jal");
        jal.addInput(jalOpcode.getOutput(0), jal.getOutput(0));


        Or memRead = new Or("memRead");
        memRead.addInput(lwOpcode.getOutput(0), lhOpcode.getOutput(0),
                lhuOpcode.getOutput(0), lbOpcode.getOutput(0),
                lbuOpcode.getOutput(0));


        Or ALUOp3 = new Or("ALUOp3");
        ALUOp3.addInput(rFormatOpcode.getOutput(0), sbOpcode.getOutput(0),
                andiOpcode.getOutput(0), oriOpcode.getOutput(0),
                beqOpcode.getOutput(0), bneOpcode.getOutput(0),
                sltiOpcode.getOutput(0));


        Or ALUOp2 = new Or("ALUOp2");
        ALUOp2.addInput(rFormatOpcode.getOutput(0), lhuOpcode.getOutput(0),
                shOpcode.getOutput(0), lbOpcode.getOutput(0),
                lbuOpcode.getOutput(0), bneOpcode.getOutput(0),
                sltiOpcode.getOutput(0));


        Or ALUOp1 = new Or("ALUOp1");
        ALUOp1.addInput(rFormatOpcode.getOutput(0), swOpcode.getOutput(0),
                lhOpcode.getOutput(0), lbOpcode.getOutput(0),
                lbuOpcode.getOutput(0), oriOpcode.getOutput(0),
                beqOpcode.getOutput(0));


        Or ALUOp0 = new Or("ALUOp0");
        ALUOp0.addInput(rFormatOpcode.getOutput(0), lwOpcode.getOutput(0),
                lhOpcode.getOutput(0), shOpcode.getOutput(0),
                lbuOpcode.getOutput(0), andiOpcode.getOutput(0),
                beqOpcode.getOutput(0), sltiOpcode.getOutput(0));




        // Add generated signals as output in the below order:

        // half, byte_, zeroExt, regWrite, regDst1, regDst0, aluSrc,
        // branch, brchNe, memWrite, memToReg1, memToReg0, jump, jal,
        // memRead, ALUOp3, ALUOp2, ALUOp1, ALUOp0


        addOutput(half.getOutput(0), byte_.getOutput(0),
                zeroExt.getOutput(0), regWrite.getOutput(0),
                regDst1.getOutput(0), regDst0.getOutput(0),
                aluSrc.getOutput(0), branch.getOutput(0),
                brchNe.getOutput(0), memWrite.getOutput(0),
                memToReg1.getOutput(0), memToReg0.getOutput(0),
                jump.getOutput(0), jal.getOutput(0), memRead.getOutput(0),
                ALUOp3.getOutput(0), ALUOp2.getOutput(0),
                ALUOp1.getOutput(0), ALUOp0.getOutput(0));


    }
}