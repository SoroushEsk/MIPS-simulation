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
        LogicalUnit logicalUnit = new LogicalUnit("LU", "64x160");

        // input: 0:63 -> rs, rt value (0 and 32 : MSB)


        for (int i = 0; i < 64; i++) {
            arithmeticUnit.addInput(getInput(i));
            logicalUnit.addInput(getInput(i));
        }
        arithmeticUnit.addInput(getInput(66));

        for (int i = 0; i < 32; i++) {
            // 64:67 -> ALU Input control (64 : MSB)
            mux16to1s[i] = new Mux16to1("ALUmux"+i, "20x1", Simulator.falseLogic, getInput(64), getInput(65), getInput(66));

            // ***you can change the order according to ALU-control-input***

            mux16to1s[i].addInput(arithmeticUnit.getOutput(i));             // 0000: add
            mux16to1s[i].addInput(arithmeticUnit.getOutput(i));             // 0001: sub
            mux16to1s[i].addInput(logicalUnit.getOutput(i));                // 0010: and
            mux16to1s[i].addInput(logicalUnit.getOutput(i + 32));     // 0011: or
            mux16to1s[i].addInput(logicalUnit.getOutput(i + 64));     // 0100: nor
            mux16to1s[i].addInput(logicalUnit.getOutput(i + 96));     // 0101: sll
            mux16to1s[i].addInput(logicalUnit.getOutput(i + 128));    // 0110: srl
            mux16to1s[i].addInput(arithmeticUnit.getOutput(i + 32));  // 0111: set on less than


            mux16to1s[i].addInput(Simulator.falseLogic);                  // 1000
            mux16to1s[i].addInput(Simulator.falseLogic);                  // 1001
            mux16to1s[i].addInput(Simulator.falseLogic);                  // 1010
            mux16to1s[i].addInput(Simulator.falseLogic);                  // 1011
            mux16to1s[i].addInput(Simulator.falseLogic);                  // 1100
            mux16to1s[i].addInput(Simulator.falseLogic);                   // 1101
            mux16to1s[i].addInput(Simulator.falseLogic);                   // 1110
            mux16to1s[i].addInput(Simulator.falseLogic);                   // 1111


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
