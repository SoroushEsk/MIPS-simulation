package ControlUnit;


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


        // ALU Control Unit inputs: instruction bits[5-0] (function field for R-Format instructions),
        // plus 4 bits of ALUOp (signals generated by testControlUnits Control Unit).

        // ALU Control Unit outputs: one signal specifying jr instruction plus
        // 3 bits for encoding of the operation; namely isJr, operation2, operation1, operation0.


        // Inputs Indexing:

        // input 0 ~ instruction bit 5
        // input 1 ~ instruction bit 4
        // input 2 ~ instruction bit 3
        // input 3 ~ instruction bit 2
        // input 4 ~ instruction bit 1
        // input 5 ~ instruction bit 0

        // input 6 ~ ALUOp3
        // input 7 ~ ALUOp2
        // input 8 ~ ALUOp1
        // input 9 ~ ALUOp0




        Not notInstrBit5 = new Not("notInstrBit5", getInput(0));
        Not notInstrBit4 = new Not("notInstrBit4", getInput(1));
        Not notInstrBit3 = new Not("notInstrBit3", getInput(2));
        Not notInstrBit2 = new Not("notInstrBit2", getInput(3));
        Not notInstrBit1 = new Not("notInstrBit1", getInput(4));
        Not notInstrBit0 = new Not("notInstrBit0", getInput(5));


        Not notALUOp3 = new Not("notALUOp3", getInput(6));
        Not notALUOp2 = new Not("notALUOp2", getInput(7));
        Not notALUOp1 = new Not("notALUOp1", getInput(8));
        Not notALUOp0 = new Not("notALUOp0", getInput(9));




        // If ALUOp = 1111, the instruction is R-Format:
        And isRFormat = new And("isRFormat", getInput(6), getInput(7),
                getInput(8), getInput(9));




        And addFunct100000 = new And("addFunct100000", getInput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                notInstrBit2.getOutput(0), notInstrBit1.getOutput(0),
                notInstrBit0.getOutput(0));

        And isAdd = new And("isAdd", isRFormat.getOutput(0),
                addFunct100000.getOutput(0));



        And subFunct100010 = new And("subFunct100010", getInput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                notInstrBit2.getOutput(0), getInput(4), notInstrBit0.getOutput(0));


        And isSub = new And("isSub", isRFormat.getOutput(0),
                subFunct100010.getOutput(0));



        // addi ALUOp: 0000
        And isAddi = new And("isAddi", notALUOp3.getOutput(0),
                notALUOp2.getOutput(0), notALUOp1.getOutput(0),
                notALUOp0.getOutput(0));



        // lw ALUop: 0001
        And isLw = new And("isLw", notALUOp3.getOutput(0),
                notALUOp2.getOutput(0), notALUOp1.getOutput(0), getInput(9));



        // sw ALUOp: 0010
        And isSw = new And("isSw", notALUOp3.getOutput(0),
                notALUOp2.getOutput(0), getInput(8), notALUOp0.getOutput(0));



        // lh ALUOp: 0011
        And isLh = new And("isLh", notALUOp3.getOutput(0),
                notALUOp2.getOutput(0), getInput(8), getInput(9));



        // lhu ALUOp: 0100
        And isLhu = new And("isLhu", notALUOp3.getOutput(0), getInput(7),
                notALUOp1.getOutput(0), notALUOp0.getOutput(0));



        // sh ALUOp: 0101
        And isSh = new And("isSh", notALUOp3.getOutput(0), getInput(7),
                notALUOp1.getOutput(0), getInput(9));



        // lb ALUOp: 0110
        And isLb = new And("isLb", notALUOp3.getOutput(0),
                getInput(7), getInput(8), notALUOp0.getOutput(0));



        // lbu ALUOp: 0111
        And isLbu = new And("isLbu", notALUOp3.getOutput(0),
                getInput(7), getInput(8), getInput(9));



        // sb ALUOp: 1000
        And isSb = new And("isSb", getInput(6), notALUOp2.getOutput(0),
                notALUOp1.getOutput(0), notALUOp0.getOutput(0));



        And andFunct100100 = new And("andFunct100100", getInput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                getInput(3), notInstrBit1.getOutput(0),
                notInstrBit0.getOutput(0));


        And isAnd = new And("isAnd", isRFormat.getOutput(0),
                andFunct100100.getOutput(0));



        And orFunct100101 = new And("orFunct100101", getInput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                getInput(3), notInstrBit1.getOutput(0), getInput(5));


        And isOr = new And("isOr", isRFormat.getOutput(0),
                orFunct100101.getOutput(0));



        And norFunct100111 = new And("norFunct100111", getInput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                getInput(3), getInput(4), getInput(5));

        And isNor = new And("isNor", isRFormat.getOutput(0),
                norFunct100111.getOutput(0));



        // andi ALUOp: 1001
        And isAndi = new And("isAndi", getInput(6), notALUOp2.getOutput(0),
                notALUOp1.getOutput(0), getInput(9));



        // ori ALUOp: 1010
        And isOri = new And("isOri", getInput(6), notALUOp2.getOutput(0),
                getInput(8), notALUOp0.getOutput(0));



        And sllFunct000000 = new And("sllFunct000000", notInstrBit5.getOutput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                notInstrBit2.getOutput(0), notInstrBit1.getOutput(0),
                notInstrBit0.getOutput(0));

        And isSll = new And("isSll", isRFormat.getOutput(0),
                sllFunct000000.getOutput(0));



        And srlFunct000010 = new And("srlFunct000010", notInstrBit5.getOutput(0),
                notInstrBit4.getOutput(0), notInstrBit3.getOutput(0),
                notInstrBit2.getOutput(0), getInput(4),
                notInstrBit0.getOutput(0));

        And isSrl = new And("isSrl", isRFormat.getOutput(0),
                srlFunct000010.getOutput(0));



        // beq ALUOp: 1011
        And isBeq = new And("isBeq", getInput(6), notALUOp2.getOutput(0),
                getInput(8), getInput(9));



        // bne ALUOp: 1100
        And isBne = new And("isBne", getInput(6), getInput(7),
                notALUOp1.getOutput(0), notALUOp0.getOutput(0));



        And sltFunct101010 = new And("sltFunct101010", getInput(0),
                notInstrBit4.getOutput(0), getInput(2),
                notInstrBit2.getOutput(0), getInput(4),
                notInstrBit0.getOutput(0));

        And isSlt = new And("isSlt", isRFormat.getOutput(0),
                sltFunct101010.getOutput(0));



        And sltuFunct101011 = new And("sltuFunct101011", getInput(0),
                notInstrBit4.getOutput(0), getInput(2),
                notInstrBit2.getOutput(0), getInput(4), getInput(5));

        And isSltu = new And("isSltu", isRFormat.getOutput(0),
                sltuFunct101011.getOutput(0));



        // slti ALUOp: 1101
        And isSlti = new And("isSlti", getInput(6), getInput(7),
                notALUOp1.getOutput(0), getInput(9));



        // j ALUOp: 0000
        And isJ = new And("isJ", notALUOp3.getOutput(0), notALUOp2.getOutput(0),
                notALUOp1.getOutput(0), notALUOp0.getOutput(0));



        And jrFunct001000 = new And("jrFunct001000", notInstrBit5.getOutput(0),
                notInstrBit4.getOutput(0), getInput(2),
                notInstrBit2.getOutput(0), notInstrBit1.getOutput(0),
                notInstrBit0.getOutput(0));

        And isJr = new And("isJr", isRFormat.getOutput(0),
                jrFunct001000.getOutput(0));



        // jal ALUOp: 0000
        And isJal = new And("isJal", notALUOp3.getOutput(0), notALUOp2.getOutput(0),
                notALUOp1.getOutput(0), notALUOp0.getOutput(0));



        Or operation2 = new Or("operation2", isNor.getOutput(0),
                isSll.getOutput(0), isSrl.getOutput(0), isSlt.getOutput(0),
                isSltu.getOutput(0), isSlti.getOutput(0));



        Or operation1 = new Or("operation1", isAnd.getOutput(0),
                isOr.getOutput(0), isAndi.getOutput(0),
                isOri.getOutput(0), isSrl.getOutput(0), isSlt.getOutput(0),
                isSltu.getOutput(0), isSlti.getOutput(0));



        Or operation0 = new Or("operation0", isSub.getOutput(0),
                isOr.getOutput(0), isOri.getOutput(0),
                isSll.getOutput(0), isBeq.getOutput(0), isBne.getOutput(0),
                isSlt.getOutput(0), isSltu.getOutput(0), isSlti.getOutput(0));



        // Generated outputs: one signal specifying jr instruction plus 3 bits for
        // encoding of the operation; namely isJr, operation2, operation1, operation0.

        addOutput(isJr.getOutput(0), operation2.getOutput(0),
                operation1.getOutput(0), operation0.getOutput(0));



    }
}
