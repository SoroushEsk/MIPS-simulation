package ALU;

import simulator.control.Simulator;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class ALU extends Wrapper {
    public ALU(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Mux16to1[] mux16to1s = new Mux16to1[32];
        ArithmeticUnit arithmeticUnit = new ArithmeticUnit("AU", "65x64");
        LogicalUnit logicalUnit = new LogicalUnit("LU", "64x64");

        // 0:63 -> rs, rt value (0 and 32 : MSB)


        for (int i = 0; i < 64; i++) {
            arithmeticUnit.addInput(getInput(i));
            logicalUnit.addOutput(getInput(i));
        }
        arithmeticUnit.addInput(getInput(65));

        for (int i = 0; i < 32; i++) {
            // 64:67 -> ALU Input control (64 : MSB)
            mux16to1s[i] = new Mux16to1("ALUmux"+i, "20x1", getInput(64), getInput(65), getInput(66), getInput(67));

            // not sure if 0000 should be first input after addressing bits!!!

            mux16to1s[i].addInput(logicalUnit.getOutput(i));                // 0000: and
            mux16to1s[i].addInput(logicalUnit.getOutput(i + 32));     // 0001: or
            mux16to1s[i].addInput(arithmeticUnit.getOutput(i));            // 0010: add
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(arithmeticUnit.getOutput(i));            // 0110: sub
            mux16to1s[i].addInput(arithmeticUnit.getOutput(i + 32)); // 0111: set on less than
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);
            mux16to1s[i].addInput(Simulator.falseLogic);

            // more operations can add here, up to 16 operation
            // if more operations added, output of each unit gets bigger

        }

        Zero zero = new Zero("ALU0Flag","32x1");

        // 0:31 is output value
        for (int i = 0; i < 32; i++) {
            addOutput(mux16to1s[i].getOutput(0));
            zero.addInput(mux16to1s[i].getOutput(0));
        }
        
        //32 is ZERO Flag
        addOutput(zero.getOutput(0));
    }
}
