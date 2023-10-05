import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;
import static java.lang.Integer.toBinaryString;

public class Assembler {

    /** Important parameters **/
    boolean TestTool = true;
    private static final String zero_bit = "0000000";   // 31-25 (7 bit) must be 0 bit
    private static final Pattern number_pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final AssemblerTokenizer tkz;
    Assembler(String assembly) {
        tkz = new AssemblerTokenizer(assembly);
    }
    private final List<String> machineCodes = new ArrayList<>();
    private String machineCode = zero_bit;              // add zero-bit first before load
    private int curr_Line = 0;
    public List<String> Data_list = new ArrayList<>(); // to collect parsed line
    private final Map<String, Integer> Label_Mapping = new HashMap<>();

    private static final Map<String , String> Opcode_mapping = Map.of(
            "add"  , "000",
            "nand" , "001",
            "lw" , "010",
            "sw" , "011",
            "beq" , "100",
            "jalr" , "101",
            "halt" , "110",
            "noop" , "111",
            ".fill" , ".fi"
    );
    private static final Map<String , String> Instruction_mapping = Map.of(
            "add"  , "R",
            "nand" , "R",
            "lw" , "I",
            "sw" , "I",
            "beq" , "I",
            "jalr" , "J",
            "halt" , "O",
            "noop" , "O",
            ".fill", "F"
    );
    private static final Map<String , Integer> FieldNum = Map.of(
            "add"  , 3,
            "nand" , 3,
            "lw" , 2,
            "sw" , 2,
            "beq" , 2,
            "jalr" , 2,
            "halt" , 0,
            "noop" , 0,
            ".fill", 0
    );

    /** Parser **/
    void parseLineToData(){
        Data_list = new ArrayList<>();
        while (tkz.hasNext()) {         // parse to data until newline
            String token = tkz.next();
            if (token.equals("\n")) {  // new line
                break;
            }
            Data_list.add(token);
        }

        if(TestTool){
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("parsed! -> " + Data_list);
        }
    }


    /**
     *  Main machineCode Encoder
     */
    public List<String> computeToMachineCode(){
        System.out.println("*** Start mapping Label before compute! ***");
        LabelMapping();               // parse all line to get label and put to Label_Mapping
        if(TestTool) System.out.println("Label Mapping : " + Label_Mapping);
        return(compute());          // get MachineCode by instruction-type
    }

    public void reset(){
        tkz.repositionToStart();    // reposition to start token!!!
        curr_Line = 0;
    }

    private ArrayList<String> compute() {
        reset();
        parseLineToData();
        while (tkz.hasNext()) {
            int index = 0;
            if(TestTool) System.out.println("Line : " + (curr_Line + 1));
            if (Data_list.isEmpty()) {
                parseLineToData();
                continue;
            }

            /**
             * Check start with instruct or label
             */
            if (!isInstruction(Data_list.get(index))) {
                if (!isLabel(Data_list.get(index))) {
                    System.out.println("First token isn't Label");
                    exit(1);
                } else {
                    index++;
                    if (!isInstruction(Data_list.get(index))) {
                        System.out.println("Second token isn't Instruction");
                        exit(1);
                    }
                }
            }


            /**
             * get instruction from Data_list[index]
             * get type and opcode from instruction mapping
             */
            String instruction = Data_list.get(index);
            String type = Instruction_mapping.get(instruction);
            String opcode = Opcode_mapping.get(instruction);

            if(TestTool){
                System.out.println("Instruction : " + instruction);
                System.out.println("Type : " + type);
                System.out.println("Opcode : " + opcode);
            }


            /**
             * set Starter-package
             */
            int FieldNum = Assembler.FieldNum.get(instruction);
            String[] fields = {"", "", ""};
            machineCode = zero_bit;
            machineCode += opcode;

            for (int i = 0; i < FieldNum; ++i) {
                fields[i] = Data_list.get(index + 1 + i);

                if (!isInteger(fields[i])) {
                    System.out.println("Fields -> " + i + " is not an integer.");
                    exit(1);
                }

                int integerFields = toInteger(fields[i]);

                if (integerFields < 0 || integerFields > 7) {
                    System.out.println("Fields -> " + i + " is out of [0,7]");
                    exit(1);
                }
                /**
                 *  Convert to binary
                 */
                fields[i] = toBinaryString(integerFields);  // import lib
                fields[i] = addZeroBits(fields[i], 3); // 3-bits size per field
            }

            /**
             * Type check then compute to machineCodes
             */
            switch (type) {
                case "R" -> R_type(fields);                          // finished
                case "I" -> I_type(fields,index,instruction);        // in-progress
                case "J" -> J_type(fields);                          // in-progress
                case "O" -> O_type();                                // finished
                case "F" -> F_type(index);                           // in-progress
            }

            machineCodes.add(machineCode);      // add to machineCode-list
            curr_Line++;
            parseLineToData();
        }
        if(TestTool) System.out.println("------------------------------------------------------------------------------");
        return new ArrayList<String>(machineCodes);
    }

    private void R_type(String[] fields){
            machineCode += fields[0]; // regA
            machineCode += fields[1]; // regB
            machineCode += "0000000000000";
            machineCode += fields[2]; // destReg
    }

    private void I_type(String[] fields , int index , String instruction){

        machineCode += fields[0]; // regA
        machineCode += fields[1]; // regB

        int offset = 0;
        fields[2] = Data_list.get(index + 3);      // Get the address value from the Data-list

        if(isLabel(fields[2])){
                                                   // address = PC + 1 + offset
            if(Objects.equals(instruction, "beq")) offset = Label_Mapping.get(fields[2]) - curr_Line - 1;
            else offset = Label_Mapping.get(fields[2]);    // lw & sw

        } else if(isInteger(fields[2])){
            offset = toInteger(fields[2]);
        } else {
            System.out.println("Invalid offset!!!");
            exit(1);
        }

        if(offset > 32767 || offset < - 32768){
            System.out.println("offset out of range!!!");
            exit(1);
        }

        String offsetBinary;
        if(offset >= 0){
            offsetBinary = toBinaryString(offset);
            offsetBinary = addZeroBits(offsetBinary,16);
        } else {
            offsetBinary = toBinaryString(-offset);
            offsetBinary = addZeroBits(offsetBinary,16);
            offsetBinary = twosComplement(offsetBinary);
        }
        if(TestTool){
            System.out.println("machineCode + offsetBinary : " + (machineCode + offsetBinary));
        }
        machineCode += offsetBinary; // imme
    }
    private void J_type(String[] fields){
        machineCode += fields[0];   // regA
        machineCode += fields[1];   // regB
        machineCode += "0000000000000000";
    }

    private void O_type(){ // no field
            machineCode += "0000000000000000000000";
    }

    private void F_type(int index){ // no field
        // not-implement yet
        machineCode = "";
        String value = Data_list.get(1 + index); // next field value

        if(isInteger(value)){   // Integer State
            int integer = toInteger(value);
            String binary;
            if(integer >= 0) {
                binary = toBinaryString(integer);
                machineCode = addZeroBits(binary,32);
            } else {            // value < 0 then need to do 2's compliment!!!
                binary = toBinaryString(-integer);
                binary = addZeroBits(binary,32);
                machineCode = twosComplement(binary);
            }
        } else if(isLabel(value)) { // Label State
            int integer = Label_Mapping.get(value);
            String binary = toBinaryString(integer);
            machineCode = addZeroBits(binary,32);
        } else {
            System.out.println("Invalid .fill!!!");
            exit(1);
        }
    }

    /** add 0 until field size equal to input size **/
    public static String addZeroBits(String field, int size) {
        StringBuilder result = new StringBuilder();
        while (result.length() + field.length() < size) {
            result.append('0');
        }
        result.append(field);

        return result.toString();
    }

    public static String twosComplement(String binary) {
        // Invert the bits
        StringBuilder inverted = new StringBuilder();
        for (char bit : binary.toCharArray()) {
            inverted.append((bit == '0') ? '1' : '0');
        }

        // Add 1 to the inverted value
        int carry = 1;
        StringBuilder result = new StringBuilder();
        for (int i = inverted.length() - 1; i >= 0; i--) {
            int bit = Character.getNumericValue(inverted.charAt(i)) + carry;
            if (bit > 1) {
                carry = 1;
                bit = 0;
            } else {
                carry = 0;
            }
            result.insert(0, bit);
        }
        return addZeroBits(result.toString(),binary.length());
    }

    public static int toInteger(String field) {
        return Integer.parseInt(field);
    }

    public void LabelMapping(){
        while(tkz.hasNext()){
            if(Data_list.isEmpty()){
                parseLineToData();
            }
            if(!isInstruction(Data_list.get(0))){
                if(LabelValidCheck(Data_list.get(0))){
                    Label_Mapping.put(Data_list.get(0) , curr_Line);
                } else {
                    System.out.println("First index is not Label or instruction");
                    exit(1);
                }
            }
            curr_Line++;
            parseLineToData();
        }
    }

    public boolean isLabel(String tkz){
        return Label_Mapping.containsKey(tkz);
    }

    public static boolean isInstruction(String tkz){
        return Instruction_mapping.containsKey(tkz);
    }

    public static boolean isInteger(String field) {
        if(number_pattern.matcher(field).matches()){
            double number = Double.parseDouble(field);
            return number % 1 == 0;
        }else{
            return false;
        }
    }

    private boolean LabelValidCheck(String label){
        return !Label_Mapping.containsKey(label) && label.length() <= 6 ;
    }


    public static List<String> binaryToDecimal(List<String> binaryList) {
        List<String> decimalList = new ArrayList<>();

        for (String binaryString : binaryList) {
            String decimal;

            if (binaryString.charAt(0) == '1') {
                StringBuilder invertedString = new StringBuilder();
                for (char bit : binaryString.toCharArray()) {
                    invertedString.append((bit == '0') ? '1' : '0');
                }

                BigInteger absoluteValue = new BigInteger(invertedString.toString(), 2).add(BigInteger.ONE);
                decimal = "-" + absoluteValue.toString();
            } else {
                decimal = new BigInteger(binaryString, 2).toString();
            }

            decimalList.add(decimal);
        }

        return decimalList;
    }

    public static List<String> decimalToBinary(List<String> decimalList) {
        List<String> binaryStrings = new ArrayList<>();

        for (String decimal : decimalList) {
            String binary;

            if (decimal.startsWith("-")) {
                String absoluteValue = decimal.substring(1);    // Remove the minus sign
                BigInteger absoluteBigInt = new BigInteger(absoluteValue);
                String binaryString = absoluteBigInt.toString(2);

                // Use twosComplement to get the binary representation of the absolute value
                binary = twosComplement(addZeroBits(binaryString, 32));
            } else {
                // Handle positive numbers
                BigInteger positiveBigInt = new BigInteger(decimal);
                binary = positiveBigInt.toString(2);

                // Add leading zeros using addZeroBits function
                binary = addZeroBits(binary, 32);
            }

            binaryStrings.add(binary);
        }

        return binaryStrings;
    }

    /*
        exit(i)
        i = 1 -> error
        i = 0 -> complete
     */
    public void exit(int i){
        System.out.println("exit (" + i + ") : ERROR!!!");
        System.exit(i);
    }
}
