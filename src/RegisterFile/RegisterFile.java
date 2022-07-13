package RegisterFile;

import DataPath.Main;
import simulator.control.Simulator;
import simulator.gates.combinational.And;
import simulator.gates.combinational.Not;
import simulator.gates.combinational.Or;
import simulator.network.Link;
import simulator.wrapper.Wrapper;
import simulator.wrapper.wrappers.Decoder;
import simulator.wrapper.wrappers.Multiplexer;

public class RegisterFile extends Wrapper {

    public RegisterFile(String label, String stream, Link... links) {
        super(label, stream, links);
    }
    // input arrangement by index : stream is = 49X64
    //0     : clock bit
    //1-5   : read register number one
    //6-10  : read register number two
    //11-15 : write register number
    //16    : write register signal
    //other 32 bits are the (31:0)th bit of write register
    // out put:
    // 0-31 : first read register
    // 32-64: second read register
    @Override
    public void initialize() {
        Register[] registers = new Register[32];
        Main.showRegisters = registers;
        //save the 5 bit reg number to avoid repetition
        Link[] read1 = new Link[5];
        Link[] read2 = new Link[5];
        Link[] write = new Link[5];
        for(int i = 1 ; i <= 5 ; i++ ){
            read1[i - 1]  = getInput(i);
            read2[i - 1] = getInput(i + 5);
            write[i - 1]  = getInput(i + 10);
        }


        //creating instance of register class
        for(int reg = 0; reg < 32 ; reg ++){
            registers[reg] = new Register("Register" + reg, "34X32", getInput(0));
        }

        // choose the output of register for read 1
        Multiplexer[] read1Mux = new Multiplexer[32];
        for(int muxI = 0 ; muxI < 32 ; muxI++){
            read1Mux[muxI] = new Multiplexer("Mux37X1" + muxI, "37X1", read1);
            for(int regCounter = 0; regCounter<32 ; regCounter++){
                read1Mux[muxI].addInput(registers[regCounter].getOutput(muxI));
            }
        }
        for(int out1 = 0 ; out1 < 32 ; out1++) {
            addOutput(read1Mux[out1].getOutput(0));
        }

        // choose output for register read 2
        Multiplexer[] read2Mux = new Multiplexer[32];
        for(int muxI = 0 ; muxI < 32 ; muxI++){
            read2Mux[muxI] = new Multiplexer("Mux37X1" + (muxI<<1) , "37X1", read2);
            for(int regCounter = 0; regCounter<32 ; regCounter++){
                read2Mux[muxI].addInput(registers[regCounter].getOutput(muxI));
            }
        }
        for(int out2 = 0 ; out2 < 32 ; out2++)
            addOutput(read2Mux[out2].getOutput(0));

        //choose  write register and set the content in it
        Decoder findWrite = new Decoder("DecWrite", "5X32", write);


        // zero register shouldn't change
        Not not = new Not("not" , findWrite.getOutput(0));
        And and = new And ("and", not.getOutput(0), getInput(16));
        Link writeSignal = and.getOutput(0);
        // by anding the two signal up there find and set write register
        And andAll = new And("andAll");
        for(int i=0; i<32; i++)
            andAll.addInput(registers[0].getOutput(i));

        Link[] content = new Link[32];
        Mul2To1[] mul2To1s = new Mul2To1[32];
        for(int i = 0; i<32; i++){
            mul2To1s[i] = new Mul2To1("mul2x1" + i, "3x1");
            mul2To1s[i].addInput(getInput(i+17));
            mul2To1s[i].addInput(Simulator.falseLogic);
            mul2To1s[i].addInput(andAll.getOutput(0));
        }
        for(int bit = 0 ; bit < 32 ; bit++){
            // 17 because it's the start of the 32bit content to be written
            content[bit] = mul2To1s[bit].getOutput(0);
        }
        for (int i = 0 ; i < 32 ;i++)
            registers[0].addInput(Simulator.falseLogic);
        registers[0].addInput(Simulator.trueLogic);
        for(int wrt = 1 ; wrt < 32 ; wrt++){
            And tmp = new And("AndTemp", findWrite.getOutput(wrt), writeSignal);
            Or tmp1 = new Or("orTemp", tmp.getOutput(0), andAll.getOutput(0));
            registers[wrt].addInput(content);
            registers[wrt].addInput(tmp1.getOutput(0));
        }
        // java code for giving register to debugger at the Main Class

//        for(int reg = 2; reg < 32 ; reg++) {
//            if(reg == 26 || reg == 27) continue;
//            Simulator.debugger.addTrackItem(registers[reg]);
//        }


   }

    }

