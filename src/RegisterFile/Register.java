package RegisterFile;

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

        for(int input = 0; input < 32 ; input ++){
            register[input].addInput(getInput(input + 1));
        }

        Mul2To1[] mux64To32 = new Mul2To1[32];
        for(int i = 0 ; i < 32 ; i++){
            mux64To32[i] = new Mul2To1("mux2to1" + i, "3X1");
            mux64To32[i].addInput(register[i].getOutput(0));
            mux64To32[i].addInput(getInput(i + 1));
            mux64To32[i].addInput(getInput(33));
        }

        for(int out = 0; out < 32; out ++)
            addOutput(mux64To32[out].getOutput(0));


    }

}
