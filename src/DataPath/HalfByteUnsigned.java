package DataPath;

import RegisterFile.Mul2To1;
import RegisterFile.Mul64To32;
import simulator.control.Simulator;
import simulator.gates.combinational.Xor;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class HalfByteUnsigned extends Wrapper {
    public HalfByteUnsigned(String label, String stream, Link... links) {
        super(label, stream, links);
    }
    // input arrangement:   "35X32"
    // 0-31 : content output of read register 2
    // 32 : half bit
    // 33 : byte bit
    // 34 : signed or unsigned
    // based on type(half or byte):
    // the useless bit become false
    @Override
    public void initialize() {
        Mul64To32 pickHalf = new Mul64To32("StoreHalf" , "65X32");
        Mul64To32 pickResult = new Mul64To32("Result", "65X32");

        Link[] half = new Link[32];
        Link[] byteStore = new Link[32];


        // signed part:
        Mul2To1 byte0Half1 = new Mul2To1("byteandHalf", "3X1");
        Mul2To1 resultBit = new Mul2To1("resut", "3X1");

        // add msb byte
        byte0Half1.addInput(getInput(24));
        //add msb half
        byte0Half1.addInput(getInput(16));
        byte0Half1.addInput(getInput(32));

        // choose if we need unsigned version or not:
        resultBit.addInput(Simulator.falseLogic);
        resultBit.addInput(byte0Half1.getOutput(0));
        resultBit.addInput(getInput(34));
        // create if the half should store
        for(int h = 0 ; h < 16 ; h++) {
            half[h] = resultBit.getOutput(0);
            half[h + 16] = getInput(h + 16);
        }

        // create the byte state ment executed
        for(int b = 0 ; b < 24; b++)
            byteStore[b] = resultBit.getOutput(0);

        for(int i = 24 ; i < 32 ; i++)
            byteStore[i] = getInput(i);


        pickHalf.addInput(byteStore);
        pickHalf.addInput(half);
        pickHalf.addInput(getInput(32));

        for(int input = 0 ; input < 32 ; input++){
            pickResult.addInput(getInput(input));
        }
        for(int mux = 0 ; mux < 32  ; mux ++)
            pickResult.addInput(pickHalf.getOutput(mux));

        // check if there is byte or half signal enable
        Xor xor = new Xor("byteXorhalf" , getInput(32), getInput(33));
        pickResult.addInput(xor.getOutput(0));

        for(int out = 0 ; out < 32 ; out++)
            addOutput(pickResult.getOutput(out));


    }
}
