package RegisterFile;

import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class Mul64To32 extends Wrapper {
    public Mul64To32(String label, String stream, Link... links) {
        super(label, stream, links);
    }
    //Input arrangement by index:
    // 0-31  : first 32 bit
    // 32-63 : second 32 bit
    // 64 : select signal if == 1 second set of input get picked
    @Override
    public void initialize() {
        Mul2To1[] mul64To32 = new Mul2To1[32];
        for(int i = 0 ; i < 32; i++){
            mul64To32[i] = new Mul2To1("mul" + i, "3X1", getInput(i));
        }
        for(int i = 32 ; i < 64; i++){
            mul64To32[i - 32].addInput(getInput(i), getInput(64));
        }
        for(int i = 0 ; i < 32 ; i++)
            addOutput(mul64To32[i].getOutput(0));
    }
}
