package ALU;

import simulator.control.Simulator;
import simulator.network.Link;
import simulator.wrapper.Wrapper;

public class ShiftLeft extends Wrapper {
    public ShiftLeft(String label, String stream, Link... links) {
        super(label, stream, links);
    }

    @Override
    public void initialize() {
        Link[] shAmount = new Link[32];
        Link[] input = new Link[32];
        for (int i = 0; i < 32; i++) {
            shAmount[i] = getInput(i+32);
            input[i] = getInput(i);
        }
        int DECShAmount = getShiftAmount(shAmount);
        for (int i = 0; i < DECShAmount; i++) {
            for (int j = 0; j < 32-(i+1); j++) {
                input[j] = input[j+1];
            }
        }
        for (int i = 0; i < 32-DECShAmount; i++) {
            addOutput(input[i]);
        }
        for (int i = 32-DECShAmount; i < 32; i++) {
            addOutput(Simulator.falseLogic);
        }
    }

    public static int getShiftAmount(Link[] links){
        int res = 0;
        for (int i = 0; i < links.length; i++) {
            if (links[i].getSignal()==Simulator.trueLogic.getSignal()){
                res += Math.pow(2, links.length -(i+1));
            }
        }
        return res;
    }
}
