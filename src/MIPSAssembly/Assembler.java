package MIPSAssembly;
/*
In this class the only job is to get and fetch the instructions
and turned them in to the machine code

the only function in this class that you need is Assember.getInstructions()

the manual of how to give the instruction to the program will appear when you
run the code
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Assembler {
    private static final Scanner input = new Scanner(System.in);

    private static HashMap<String, String> instructionOpcode = new HashMap<>();
    private static HashMap<String, String> instFunction = new HashMap<>();


// different components that a machine code of mips can have
    private static String opcode;
    private static String rs;
    private static String constant;
    private static String function;
    private static String rd;
    private static String rt;
    private static String shiftamn;
    private static String address;

    public static int pc = 0;
// set of zero and ones of each line of state will save in :
    private static String finalMachineCode = "";

// Using these two for lables and jump and branch statements
    private static HashMap<String, Integer> lablesNotUsed= new HashMap<>();
    private static HashMap<String, Integer> jumpNOLable = new HashMap<>();

    private static String singleInstruction;

//The final machine code after the "end" will be in :
    private static Boolean[][] memory = new Boolean[65536][8];
//------------------------------------------------------------
    public static Boolean[][] getInstructions(){
        setInstructionOpcode();
        setInstFunction();

        for (int i = 0; i < 65536; ++i) {
            for (int j = 0; j < 8; ++j) {
                memory[i][j] = false;
            }
        }

        String tmp;
        boolean isLable = false;
        System.out.println("Enter instruction Like example (each instruction takes one Line)");
        System.out.println("add $3, $7, $31 ->> don't use the reg's name just number is enough");
        System.out.println("when you finished type \'end\' at finish line");
        while(!((tmp = input.nextLine().toLowerCase()).equals("end"))){
            finalMachineCode = "";
            isLable = false;
            singleInstruction = tmp.trim();
            if(singleInstruction.contains(":"))
                isLable = true;
            recognizeInstrucion();
            if(!finalMachineCode.equals("")){
                exchangeToBoolean(finalMachineCode , pc);
            }
            if(!isLable)
                pc += 4;
        }
        System.out.println("----------------------------------");
        if(!jumpNOLable.isEmpty()) memory = null;
        return memory;
    }

    private static void recognizeInstrucion(){
        Scanner type = new Scanner(singleInstruction);
        String instCopy = singleInstruction;
        boolean isLable = singleInstruction.contains(":");

        String tmpType = type.next();
        if(isLable) {
            ArrayList<String>keys = new ArrayList<>();
            String mainLable = findLableInInstruction(instCopy).trim();
            lablesNotUsed.put(mainLable, pc);
            for(String key : jumpNOLable.keySet()){
                if(key.contains(mainLable)){
                    Scanner getType = new Scanner(key);
                    opcode = instructionOpcode.get(getType.next());
                    if(opcode.charAt(4) == '1'){
                        int temPC = pc/4;
                        String  zero26 = "00000000000000000000000000";
                        address = Integer.toBinaryString(temPC);
                        if(address.length() > 26)
                            address = address.substring(address.length() - 26, address.length());

                        address = zero26.substring(0, 26-address.length()) + address;
                        finalMachineCode = opcode + address;
                        exchangeToBoolean(finalMachineCode , jumpNOLable.get(key));
                        keys.add(key);
                        }
                    else if(opcode.charAt(3) == '1'){
                        singleInstruction = key;
                        getTwoBranchReg();
                        String zero16 = "0000000000000000";
                        int pctemp = (pc - jumpNOLable.get(key))/4 -1;
                        constant = Integer.toBinaryString(pctemp);
                        if(constant.length() > 16)
                            constant = constant.substring(constant.length()-16 , constant.length());
                        constant = zero16.substring(0, 16 - constant.length()) + constant;
                        finalMachineCode = opcode + rs + rt + constant;
                        exchangeToBoolean(finalMachineCode , jumpNOLable.get(key));
                        keys.add(key);
                    }
                }
            }
            for(String key:keys)
                jumpNOLable.remove(key);
            return;
        }

        opcode = instructionOpcode.get(tmpType);
        singleInstruction = type.nextLine();
        if(opcode.equals("000000")){
            function = instFunction.get(tmpType);
            if(function.charAt(0) == '0'){
                if(function.charAt(2) == '1'){
                    shiftamn = "00000";
                    rd = "00000";
                    rt = "00000";
                    getRFormatJRInst();
                }
                else {
                    rs = "00000";
                    getRFormatForShift();
                }
            }
            else{
                shiftamn = "00000";
                getRformatRegisters();
            }
            finalMachineCode = opcode + rs + rt + rd + shiftamn + function;
        }
        else if(opcode.substring(0, 3).equals("001")){
            getImmediateComponents();
            finalMachineCode = opcode + rs + rt + constant;
        }
        else if(opcode.substring(0,2).equals("10")){
            getLoadInst();
            finalMachineCode = opcode + rs + rt + constant;
        }
        else if (opcode.substring(0,3).equals("000")){
            if(opcode.charAt(4) == '1'){
                String lable  = singleInstruction.trim();
                if(lablesNotUsed.containsKey(lable)){
                    int temPC = lablesNotUsed.get(lable) / 4;
                    String  zero26 = "00000000000000000000000000";
                    address = Integer.toBinaryString(temPC);
                    if(address.length() > 26)
                        address = address.substring(address.length() - 26, address.length());
                    address = zero26.substring(0, 26-address.length()) + address;
                    finalMachineCode = opcode + address;
                    exchangeToBoolean(finalMachineCode , pc);
                }else
                    jumpNOLable.put(instCopy, pc);
            }
            if(opcode.charAt(3) == '1'){
                String branchLable = findLable(instCopy);
                if(lablesNotUsed.containsKey(branchLable)){
                    getTwoBranchReg();
                    String zero16 = "0000000000000000";
                    int pctemp = (lablesNotUsed.get(branchLable) - pc)/4 - 1 ;
                    constant = Integer.toBinaryString(pctemp);
                    if(constant.length() > 16)
                        constant = constant.substring(constant.length()-16 , constant.length());
                    constant = zero16.substring(0, 16 - constant.length()) + constant;
                    finalMachineCode = opcode + rs + rt + constant;
                    exchangeToBoolean(finalMachineCode , pc);
                }else
                    jumpNOLable.put(instCopy, pc);

            }
        }

    }

    //Lable functions
    private static String findLable(String instruction){
        String lable = "";
        for(int i = instruction.length() - 1; i>=0 ; i-- ){
            if(instruction.charAt(i) == ',')
                break;
            lable = instruction.charAt(i) + lable;
        }
        return lable.trim();
    }
    private static String findLableInInstruction(String lable){
        String lableTemp = "";
        for(char ch : lable.toCharArray()){
            if(ch == ':')
                break;
            lableTemp += ch;
        }
        return lableTemp;
    }

    //branch functions
    private static void getTwoBranchReg(){
        int index = 0;
        boolean isReach = false;
        String fiveZero = "00000";
        String number = "";
        int strLen = singleInstruction.length();
        while(index < strLen){
            if(singleInstruction.charAt(index) == '$'){
                index++;
                isReach = true;
                continue;
            }else if (singleInstruction.charAt(index) == ','){
                index ++;
                break;
            }
            if(isReach && '0' <= singleInstruction.charAt(index) && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index ++;
        }
        isReach = false;
        rs = Integer.toBinaryString(Byte.parseByte(number));
        rs = fiveZero.substring(0, 5 - rs.length()) + rs;
        number = "";
        while(index < strLen){
            if(singleInstruction.charAt(index) == '$'){
                index++;
                isReach = true;
                continue;
            }else if (singleInstruction.charAt(index) == ','){
                index ++;
                break;
            }
            if(isReach && '0' <= singleInstruction.charAt(index) && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index ++;
        }
        rt = Integer.toBinaryString(Byte.parseByte(number));
        rt = fiveZero.substring(0, 5 - rt.length()) + rt;
    }

    //R-Format functions for rs rd rt shifmn
    private static void getRformatRegisters(){
        int index = 0;
        boolean isReached = false;
        String number = "";
        String setOfOnes = "00000";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        rd = Integer.toBinaryString(Byte.parseByte(number));
        rd = setOfOnes.substring(0, 5 - rd.length()) + rd;
        number = "";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        rs = Integer.toBinaryString(Byte.parseByte(number));
        rs = setOfOnes.substring(0, 5 - rs.length()) + rs;
        number = "";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        rt = Integer.toBinaryString(Byte.parseByte(number));
        rt = setOfOnes.substring(0, 5 - rt.length()) + rt;
    }
    private static void getRFormatJRInst(){
        int index = 0;
        boolean isReached = false;
        String number = "";
        String setOfOnes = "00000";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        rs = Integer.toBinaryString(Byte.parseByte(number));
        rs = setOfOnes.substring(0, 5 - rs.length()) + rs;
    }
    private static void getRFormatForShift(){
        int index = 0;
        boolean isReached = false;
        String number = "", setOfZero = "00000";
        int stringLen = singleInstruction.length();
        while(index < stringLen){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }

        rd = Integer.toBinaryString(Byte.parseByte(number));
        rd = setOfZero.substring(0, 5 - rd.length()) + rd;
        isReached = false;
        number = "";
        while(index < stringLen){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        rt = Integer.toBinaryString(Byte.parseByte(number));
        rt = setOfZero.substring(0, 5 - rt.length()) + rt;
        isReached = false;
        number = "";

        while(index < stringLen){
            if(singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9')
                number += singleInstruction.charAt(index);
            index++;
        }
        shiftamn = Integer.toBinaryString(Byte.parseByte(number));
        shiftamn = setOfZero.substring(0, 5 - shiftamn.length()) + shiftamn;
    }
    //Immediate instructions rs rt const functions
    private static void getImmediateComponents(){
        int index = 0;
        boolean isReached = false;
        String number = "";
        String setOfOnes = "00000";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        rt = Integer.toBinaryString(Byte.parseByte(number));
        rt = setOfOnes.substring(0, 5 - rt.length()) + rt;
        number = "";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        rs = Integer.toBinaryString(Byte.parseByte(number));
        rs = setOfOnes.substring(0, 5 - rs.length()) + rs;
        number = "";

        while(index < singleInstruction.length()){
            if(((singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9') || singleInstruction.charAt(index) == '-')){
                number += singleInstruction.charAt(index);
            }
            index++;
        }


        String setOfZeroFor16bit = "0000000000000000";
        String tmp = Integer.toBinaryString(Integer.parseInt(number));
        if(tmp.length() > 16)
            tmp = tmp.substring(tmp.length() - 16 , tmp.length());
        constant = tmp;
        constant = setOfZeroFor16bit.substring(0, 16 - constant.length()) + constant;
    }
    //Load instuction functions that I use for store Instructions too
    private static void getLoadInst(){
        int index = 0;
        boolean isReached = false;
        String number = "";
        String setOfOnes = "00000";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
            }
            else if(singleInstruction.charAt(index) == ',') {
                index++;
                break;
            }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        rt = Integer.toBinaryString(Byte.parseByte(number));
        rt = setOfOnes.substring(0, 5 - rt.length()) + rt;
        number = "";
        while(index < singleInstruction.length()){

            if(singleInstruction.charAt(index) == '(') {
                index++;
                break;
            }
            if(singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        isReached = false;
        String setOfZeroFor16bit = "0000000000000000";
        String tmp = Integer.toBinaryString(Integer.parseInt(number));
        if(tmp.length() > 16)
            tmp = tmp.substring(tmp.length() - 16 , tmp.length());
        constant = tmp;
        constant = setOfZeroFor16bit.substring(0, 16 - constant.length()) + constant;
        number = "";
        while(index < singleInstruction.length()){
            if(singleInstruction.charAt(index) == '$'){
                isReached = true;
                index++;
                continue;
        }
        else if(singleInstruction.charAt(index) == ')') {
            index++;
            break;
        }
            if(isReached && singleInstruction.charAt(index) >= '0' && singleInstruction.charAt(index) <= '9'){
                number += singleInstruction.charAt(index);
            }
            index++;
        }
        rs = Integer.toBinaryString(Byte.parseByte(number));
        rs = setOfOnes.substring(0, 5 - rs.length()) + rs;
    }
    //
    private static void setInstructionOpcode(){
        instructionOpcode.put("add", "000000");
        instructionOpcode.put("sub", "000000");
        instructionOpcode.put("jr", "000000");
        instructionOpcode.put("slt", "000000");
        instructionOpcode.put("sltu", "000000");
        instructionOpcode.put("nor", "000000");
        instructionOpcode.put("or", "000000");
        instructionOpcode.put("and", "000000");
        instructionOpcode.put("srl", "000000");
        instructionOpcode.put("sll", "000000");
        instructionOpcode.put("addi", "001000");
        instructionOpcode.put("andi", "001100");
        instructionOpcode.put("ori", "001101");
        instructionOpcode.put("slti", "001010");
        instructionOpcode.put("j", "000010");
        instructionOpcode.put("jal", "000011");
        instructionOpcode.put("beq", "000100");
        instructionOpcode.put("bne", "000101");
        instructionOpcode.put("lw", "100011");
        instructionOpcode.put("sw", "101011");
        instructionOpcode.put("lh", "100001");
        instructionOpcode.put("lhu", "100101");
        instructionOpcode.put("sh", "101001");
        instructionOpcode.put("lb", "100000");
        instructionOpcode.put("lbu", "100100");
        instructionOpcode.put("sb", "101000");
    }
    private static void setInstFunction(){
        instFunction.put("add", "100000");
        instFunction.put("sub", "100010");
        instFunction.put("jr",  "001000");
        instFunction.put("slt", "101010");
        instFunction.put("sltu","101011");
        instFunction.put("nor", "100111");
        instFunction.put("or",  "100101");
        instFunction.put("and", "100100");
        instFunction.put("srl", "000010");
        instFunction.put("sll", "000000");
    }

    //set the boolean 2-d array
    private static void exchangeToBoolean(String machCode, int instPc){
        int instructionLen =0;
        for(int i = instPc ; i<instPc + 4;  i++){
            for(int j = 0; j<8 ; j++){
                memory[i][j]=charToBool(machCode.charAt(instructionLen));
                instructionLen++;
            }
        }
    }
    private static Boolean charToBool(char ch){
        if(ch == '0')
            return false;
        return true;
    }
}
