package DataPath;

import ALU.ALU;
import ControlUnit.*;
import MIPSAssembly.Assembler;
import RegisterFile.Mul2To1;
import RegisterFile.*;
import RegisterFile.Mul64To32;
import RegisterFile.Register;
import RegisterFile.RegisterFile;
import simulator.control.Simulator;
import simulator.gates.combinational.And;
import simulator.gates.combinational.ByteMemory;
import simulator.gates.combinational.Not;
import simulator.gates.combinational.Or;
import simulator.gates.sequential.BigClock;
import simulator.gates.sequential.Clock;
import simulator.network.Link;
import simulator.wrapper.wrappers.DFlipFlop;
//import simulator.wrapper.wrappers.AddSub;
import ALU.AddSub;

public class Main {

    public static Register[] showRegisters = null;
    public static void main(String[] args) {

//____________________set Instruction MEM--------------------
        Boolean[][] setIMem = null;

        try {
            setIMem = Assembler.getInstructions();
            if (setIMem == null) {
                System.out.println("Not enough Labels!!");
                return;
            }
        }catch (Exception e){
            System.out.println("Invalid set of instructions!!");
            return;
        }

//__________________________set pc_____________________________
        BigClock clk = new BigClock("Clk");
        Not nClk = new Not("~Clock", clk.getOutput(0));

  //DFlipFlop helpFF = new DFlipFlop("helpFF", "2x2", clk.getOutput(0));


        DFlipFlop flipFlop = new DFlipFlop("MuxSelector", "2X2", nClk.getOutput(0), Simulator.falseLogic);

        Register PC = new Register("PC" , "34X32", clk.getOutput(0));


        Mul64To32 mull = new Mul64To32("mull", "65X32");

        for(int i  = 0 ; i < 29 ; i++)
            mull.addInput(Simulator.trueLogic);
        mull.addInput(Simulator.trueLogic);
        mull.addInput(Simulator.falseLogic);
        mull.addInput(Simulator.falseLogic);

        for(int i = 0 ; i < 32 ; i++)
            mull.addInput(PC.getOutput(i));
        mull.addInput(flipFlop.getOutput(1));

        AddSub add1 = new AddSub("add1", "65X32");
        for(int i = 0 ; i < 32 ; i++)
            add1.addInput(mull.getOutput(i));

        for(int i = 0 ; i < 29 ; i++)
            add1.addInput(Simulator.falseLogic);
        add1.addInput(Simulator.trueLogic);
        add1.addInput(Simulator.falseLogic);
        add1.addInput(Simulator.falseLogic);


        add1.addInput(Simulator.falseLogic);
//
        Register pcHelpReg = new Register("HelpReg", "34x32", nClk.getOutput(0));

        Or andUnusualPcMove = new Or("pcHelpAnd" ); // jump or jr or beq or bnq

//        Mul2To1[] finalPcVal = new Mul2To1[32];
//
//        for (int i = 0; i < 32; i++) {
//            finalPcVal[i] = new Mul2To1("fPcMux"+i, "3x1");
//            finalPcVal[i].addInput(add1.getOutput(i), pcHelpReg.getOutput(i));
//            finalPcVal[i].addInput(andUnusualPcMove.getOutput(0)); //  not sure if Q or ~Q
//        }
        Mul2To1[] helpOrPc = new Mul2To1[32];
        for(int i = 0 ; i <  32 ; i++){
            helpOrPc[i] = new Mul2To1("helpOrPc" + i, "3x1");
            helpOrPc[i].addInput(add1.getOutput(i));
            helpOrPc[i].addInput(pcHelpReg.getOutput(i));
            helpOrPc[i].addInput(andUnusualPcMove.getOutput(0));
        }
        for (int i = 0; i < 32; i++) {
            PC.addInput(helpOrPc[i].getOutput(0));
        }
        PC.addInput(nClk.getOutput(0));



        AddSub add = new AddSub("add", "65X32");
        for(int i = 0 ; i < 29 ; i++)
            add.addInput(Simulator.falseLogic);
        add.addInput(Simulator.trueLogic);
        add.addInput(Simulator.falseLogic);
        add.addInput(Simulator.falseLogic);

        for(int i = 0 ; i < 32 ; i++)
            add.addInput(PC.getOutput(i));

        add.addInput(Simulator.falseLogic);



//        for(int i = 0; i < 32 ; i++)
//            pcHelpReg.addInput(Simulator.falseLogic);
//        pcHelpReg.addInput(Simulator.trueLogic);
//        Not not = new Not("not ");
//        not.addInput(helpFF.getOutput(0));
//        helpFF.addInput(not.getOutput(0));
//____________________________Instruction Memory_______________________________

        ByteMemory instMemory = new ByteMemory("InstructionMemory");
        instMemory.setMemory(setIMem);

        instMemory.addInput(Simulator.falseLogic);

        for(int bit = 16 ; bit < 32; bit++)
            instMemory.addInput(PC.getOutput(bit));

        //because we don't want to write on instruction memory any way




        Link[] instruction = new Link[32];
        for (int i = 0; i < 32; i++) {
            instruction[i] = instMemory.getOutput(i);
        }



        MainControlUnit cu = new MainControlUnit("mcu", "6x19");
        for (int i = 0; i < 6; i++) {
            cu.addInput(instruction[i]);
        }

//___________________________ Register File________________________________

        Mul4To1[] regDstMuxs = new Mul4To1[5];
        for (int i = 0; i < 5; i++) {
            regDstMuxs[i] = new Mul4To1("regDstMux"+i, "6x1");
            regDstMuxs[i].addInput(instruction[i + 11], instruction[i + 16], Simulator.trueLogic, Simulator.falseLogic);
            regDstMuxs[i].addInput(cu.getOutput(4), cu.getOutput(5));  // signal = regDst1,0
        }

        RegisterFile registerFile = new RegisterFile("regFile", "49X64");
        registerFile.addInput(clk.getOutput(0));

        for (int i = 0; i < 5; i++) {
            registerFile.addInput(instruction[i+6]);            // ReadRegister1
        }
        for (int i = 0; i < 5; i++) {
            registerFile.addInput(instruction[i+11]);           // ReadRegister2
        }
        for (int i = 0; i < 5; i++) {
            registerFile.addInput(regDstMuxs[i].getOutput(0)); //WriteRegister
        }
        And andWithClk =  new And("andClock" , clk.getOutput(0), cu.getOutput(3));
        registerFile.addInput(cu.getOutput(3));   //signal =regWrite
        // registerFile.addInput(Write data)



//______________________________alu inputs_______________________________

        SignExtender signExtend = new SignExtender("sigExt", "16x32");
        for (int i = 16; i < 32; i++) {
            signExtend.addInput(instruction[i]);
        }

        Mul2To1[] ALUSrcMuxs = new Mul2To1[32];
        for (int i = 0; i < 32; i++) {
            ALUSrcMuxs[i] = new Mul2To1("aluSrcMux"+i, "3x1");
            ALUSrcMuxs[i].addInput(registerFile.getOutput(i+32), signExtend.getOutput(i));
            ALUSrcMuxs[i].addInput(cu.getOutput(6));            //signal = aluSrc
        }


//_________________________________ALU____________________________________
        ALU alu = new ALU("alu", "67x33");

        // ALU inputs:
        for (int i = 0; i < 32; i++) {
            alu.addInput(registerFile.getOutput(i));
        }
        for (int i = 0; i < 32; i++) {
            alu.addInput(ALUSrcMuxs[i].getOutput(0));
        }


        ALUControlUnit aluControl = new ALUControlUnit("aluCtrl", "10x4");

        for (int i = 26; i < 32; i++) {
            aluControl.addInput(instruction[i]);                //funct
        }
        aluControl.addInput(cu.getOutput(15), cu.getOutput(16), cu.getOutput(17), cu.getOutput(18));  //sig = ALUops

        alu.addInput(aluControl.getOutput(1));
        alu.addInput( aluControl.getOutput(2));
        alu.addInput(aluControl.getOutput(3));


//_______________________________BEQ______________________________

        AddSub branchAdder = new AddSub("branchAdder", "65x32");
        LeftShifter32Bit branchShift = new LeftShifter32Bit("branchShift", "32x32");

        for (int i = 0; i < 32; i++) {
            branchShift.addInput(signExtend.getOutput(i));
        }
        for (int i = 0; i < 32; i++) {
            branchAdder.addInput(add.getOutput(i));
        }
        for (int i = 0; i < 32; i++) {
            branchAdder.addInput(branchShift.getOutput(i));
        }
        branchAdder.addInput(Simulator.falseLogic);

        Mul2To1[] branchMuxs = new Mul2To1[32];

        And branchAnd = new And("bnchAdd",cu.getOutput(7), alu.getOutput(32));      //signal = beq, zero

        for (int i = 0; i < 32; i++) {
            branchMuxs[i] = new Mul2To1("bnchMux"+i, "3x1");
            branchMuxs[i].addInput(PC.getOutput(i), branchAdder.getOutput(i));
            branchMuxs[i].addInput(branchAnd.getOutput(0));
        }

//______________________________BNE______________________________

        Mul2To1[] bneMuxs = new Mul2To1[32];
        Not branchNotZero = new Not("brachNot",alu.getOutput(32));
        And branchNotAnd = new And("bnchAdd",cu.getOutput(8), branchNotZero.getOutput(0));
        for (int i = 0; i < 32; i++) {
            bneMuxs[i] = new Mul2To1("bneMux"+i, "3x1");
            bneMuxs[i].addInput(branchMuxs[i].getOutput(0), branchAdder.getOutput(i));
            bneMuxs[i].addInput(branchNotAnd.getOutput(0));                                              //signal = bne
        }

//______________________________JMP______________________________

        LeftShifter32Bit jShift = new LeftShifter32Bit("jShifter", "32x32");
        for (int i = 0; i < 32; i++) {
            jShift.addInput(instruction[i]);
        }

        Mul2To1[] jMuxs = new Mul2To1[32];
        for (int i = 0; i < 4; i++) {
            jMuxs[i] = new Mul2To1("jMux"+i, "3x1");
            jMuxs[i].addInput(bneMuxs[i].getOutput(0), add.getOutput(i));
            jMuxs[i].addInput(cu.getOutput(12));                                            //signal = jump
        }
        for (int i = 4; i < 32; i++) {
            jMuxs[i] = new Mul2To1("jMux"+i, "3x1");
            jMuxs[i].addInput(bneMuxs[i].getOutput(0), jShift.getOutput(i));
            jMuxs[i].addInput(cu.getOutput(12));                                            // signal = jump
        }

//-------------------------------jr-----------------------------

        Mul2To1[] jrMuxs = new Mul2To1[32];
        for (int i = 0; i < 32; i++) {
            jrMuxs[i] = new Mul2To1("jrMux"+i, "3x1");
            jrMuxs[i].addInput(jMuxs[i].getOutput(0), alu.getOutput(i));
            jrMuxs[i].addInput(aluControl.getOutput(0));                                    // sig = isJr
        }

//----------------------------PC write help-----------------------


        andUnusualPcMove.addInput(cu.getOutput(12), aluControl.getOutput(0),branchAnd.getOutput(0),
        branchNotAnd.getOutput(0));

        for (int i = 0; i < 32; i++) {
            pcHelpReg.addInput(jrMuxs[i].getOutput(0));
        }
        pcHelpReg.addInput(Simulator.trueLogic);            // not sure if trueLogic!!!
//



//_______________________Data Memory____________________________

        Boolean[][] setDMem = new Boolean[65536][8];
        // default value of data memory
        for (int i = 0; i < 65536; i++) {
            for (int j = 0; j < 8; j++) {
                setDMem[i][j] = false;
            }
        }

        HalfByteUnsigned halfDMemIn = new HalfByteUnsigned("HalfmemIn","35x32");


        ByteMemory dataMemory = new ByteMemory("dataMem");
        dataMemory.setMemory(setDMem);

        dataMemory.addInput(cu.getOutput(9));                          //signal = memWrite

        //Address input
        for (int i = 16; i < 32; i++) {
            dataMemory.addInput(alu.getOutput(i));
        }
        // WriteData input
        for (int i = 32; i < 64; i++) {
            halfDMemIn.addInput(registerFile.getOutput(i));
        }
        halfDMemIn.addInput(cu.getOutput(0));                       // sig = half
        halfDMemIn.addInput(cu.getOutput(1));                       //      byte
        halfDMemIn.addInput(cu.getOutput(2));                      //      ZeroExt

        for (int i = 0; i < 32; i++) {
            dataMemory.addInput(halfDMemIn.getOutput(i));
        }

        HalfByteUnsigned halfDMenOut = new HalfByteUnsigned("HalfmemOut", "35x32");
        for (int i = 0; i < 32; i++) {
            halfDMenOut.addInput(dataMemory.getOutput(i));
        }
        halfDMenOut.addInput(cu.getOutput(0));
        halfDMenOut.addInput(cu.getOutput(1));
        Not dMemSWNot = new Not("dMemNot", instMemory.getOutput(3));                                 //signal = memWrite
        halfDMenOut.addInput(dMemSWNot.getOutput(0));                      //      ZeroExt


//__________________________write back___________________________

        Or memToRegOr = new Or("memToRegOr", cu.getOutput(10), cu.getOutput(11));       //memToReg1, 0
        Mul2To1[] memToRegMuxs = new Mul2To1[32];
        for (int i = 0; i < 32; i++) {
            memToRegMuxs[i] = new Mul2To1("memToRegMux"+i, "3x1");
            memToRegMuxs[i].addInput(alu.getOutput(i), halfDMenOut.getOutput(i));
            memToRegMuxs[i].addInput(memToRegOr.getOutput(0));
        }

        Mul2To1[] jalMux = new Mul2To1[32];
        for (int i = 0; i < 32; i++) {
            jalMux[i] = new Mul2To1("jalMux"+i, "3x1");
            jalMux[i].addInput(memToRegMuxs[i].getOutput(0), PC.getOutput(i));
            jalMux[i].addInput(cu.getOutput(13));                               //signal = jal
        }

        for (int i = 0; i < 32; i++) {
            registerFile.addInput(jalMux[i].getOutput(0));
        }

//      show register required


        Simulator.debugger.addTrackItem(clk ,PC,dataMemory,instMemory,cu,alu);
        for ( int i = 0 ; i < 32 ; i++)
            Simulator.debugger.addTrackItem(showRegisters[i]);
        Simulator.debugger.setDelay(0);
        Simulator.circuit.startCircuit((Assembler.pc)>>1);
    }


}
