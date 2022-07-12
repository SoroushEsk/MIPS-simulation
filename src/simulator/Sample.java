//Dedicated to Goli

package simulator;

import simulator.control.Simulator;
import simulator.gates.combinational.And;
import simulator.gates.combinational.ByteMemory;
import simulator.gates.combinational.Not;
import simulator.gates.sequential.BigClock;
import simulator.gates.sequential.Clock;
import simulator.network.Link;
import simulator.wrapper.wrappers.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Sample {
    public static void main(String[] args) {
        //sample circuit
        BigClock clk = new BigClock("Clock1");
        Not nClk = new Not("~Clock", clk.getOutput(0));


        DFlipFlop flipFlop = new DFlipFlop("MuxSelector", "2X2", clk.getOutput(0), Simulator.falseLogic);
        And a = new And("a", Simulator.falseLogic, Simulator.trueLogic);



//        for(int i = 0 ; i<32 ; i++)
//            PC.addInput(add.getOutput(i));
//        PC.addInput(clk.getOutput(0));

        Simulator.debugger.addTrackItem(clk);
        Simulator.debugger.setDelay(0);
        Simulator.circuit.startCircuit(10);
    }
}