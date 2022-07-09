import ALU.ALU;
import MIPSAssembly.Assembler;
import RegisterFile.Mul2To1;
import RegisterFile.Mul64To32;
import RegisterFile.Register;
import RegisterFile.RegisterFile;
import simulator.control.Simulator;
import simulator.gates.combinational.ByteMemory;
import simulator.gates.combinational.Not;
import simulator.gates.sequential.BigClock;
import simulator.network.Link;
import simulator.wrapper.wrappers.*;

public class Main {
    public static void main(String[] args) {
        // fetch and get instructions
        Boolean[][] setIMem  = Assembler.getInstructions();
        if(setIMem == null){
            System.out.println("No Enough Lables");
            return;
        }

        BigClock clk = new BigClock("Clock1");
        Not nClk = new Not("~Clock", clk.getOutput(0));

        // use to set special amount of PC register at first
        DFlipFlop flipFlop = new DFlipFlop("MuxSelector", "2X2", nClk.getOutput(0), Simulator.falseLogic);

        // program counter
        Register PC = new Register("PC" , "34X32", clk.getOutput(0));

        // to set the first amount of PC
        Mul64To32 mull = new Mul64To32("mull", "65X32");

        for(int i  = 0 ; i < 29 ; i++)                   //===||
            mull.addInput(Simulator.trueLogic);         //===||
        mull.addInput(Simulator.trueLogic);       //====> -4 in binary
        mull.addInput(Simulator.falseLogic);            //===||
        mull.addInput(Simulator.falseLogic);             //===||

        // at second cycle of clock there is no problem
        for(int i = 0 ; i < 32 ; i++)
            mull.addInput(PC.getOutput(i));

        // select signal
        mull.addInput(flipFlop.getOutput(1));

        // add 4 to the PC
        AddSub add = new AddSub("add", "65X32");
        for(int i = 0 ; i < 32 ; i++)
            add.addInput(mull.getOutput(i));

        // adding four in binary to addSub
        for(int i = 0 ; i < 29 ; i++)
            add.addInput(Simulator.falseLogic);
        add.addInput(Simulator.trueLogic);
        add.addInput(Simulator.falseLogic);
        add.addInput(Simulator.falseLogic);

        // trueLogin ==> sub
        add.addInput(Simulator.falseLogic);

    // set PC to go
        for(int i = 0 ; i<32 ; i++)
            PC.addInput(add.getOutput(i));
        PC.addInput(clk.getOutput(0));

        //giving the input we need to ByteMemory
        ByteMemory instMemory = new ByteMemory("InstructionMemory");
        instMemory.setMemory(setIMem);
        //write in the instruction memory shouldn't happen
        instMemory.addInput(Simulator.falseLogic);
        for(int bit = 16 ; bit < 32; bit++)
            instMemory.addInput(PC.getOutput(bit));

        //start circuit
        Simulator.debugger.addTrackItem(clk );
        Simulator.debugger.setDelay(0);
        Simulator.circuit.startCircuit(Assembler.pc >> 2);
    }
}
