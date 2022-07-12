package RegisterFile;

import simulator.control.Simulator;
import simulator.gates.combinational.And;
import simulator.gates.combinational.Not;
import simulator.gates.combinational.Or;
import simulator.network.Link;
import simulator.wrapper.Wrapper;
import simulator.wrapper.wrappers.DFlipFlop;

public class Register extends Wrapper {

    public Register(String label, String stream, Link... links) {
        super(label, stream, links);
    }
    // stream pattern ==> 34X32
    // **   parameters arrangement  **
    // 0 : clock output
    //1-32: content of the register
    //33 : write bit signal

    @Override
    public void initialize() {
        DFlipFlop[] register = new DFlipFlop[32];

        for(int i = 0 ; i < 32 ; i++){
            register[i] = new DFlipFlop("FlipFlop" + i, "2X2", getInput(0));
        }
//
//        Simulator.debugger.addTrackItem(chooseClk);


        Mul2To1[] mux64To32Sec = new Mul2To1[32];
        for(int i = 0 ; i < 32 ; i++){
            mux64To32Sec[i] = new Mul2To1("mux2to1" + i, "3X1");
            mux64To32Sec[i].addInput(register[i].getOutput(0));
            mux64To32Sec[i].addInput(getInput(i + 1));
        }

        for(int out = 0; out < 32; out ++) {
            mux64To32Sec[out].addInput(getInput(33));
            register[out].addInput(mux64To32Sec[out].getOutput(0));
            addOutput(register[out].getOutput(0));
        }


    }

}
