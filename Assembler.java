import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import static java.lang.Integer.toBinaryString;

public class Assembler {

    /** Important parameters **/
    boolean TestTool = true;
    private static final String zero_bit = "0000000"; // 31-25 (7 bit) must be 0 bit
    private static final Pattern number_pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private AssemblerTokenizer tkz;
    Assembler(String assembly) {
        tkz = new AssemblerTokenizer(assembly);
    }
    private final List<String> machineCodes = new ArrayList<>();
    private String machineCode = zero_bit; // add zero-bit first before load
    private int curr_Line = 0;
    private List<String> Data_list = new ArrayList<>();
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
    private void parseLineToData(){
        Data_list = new ArrayList<>();
        while (tkz.hasNext()) { // parse to data until newline
            String token = tkz.next();
            if(!TestTool) System.out.println(token);
            if (newlineCheck(token)) { // new line
                break;
            }
            Data_list.add(token);
        }

        if(TestTool){
            System.out.println("parsed! -> " + Data_list);
        }
    }


    /**
     *  Main machineCode Encoder
     */
    public List<String> computeToMachineCode(){
        parseLineToData();
        LabelCheck();
        System.out.println("Label Mapping : " + Label_Mapping);
        System.out.println("==============================================");
        return(compute());
    }

    private ArrayList<String> compute() {
        tkz.repositionToStart();
        curr_Line = 0;
        parseLineToData();

        while (tkz.hasNext()) {
            int index = 0;
            System.out.println("Line : " + (curr_Line + 1));
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
                } else {
                    index++;
                    if (!isInstruction(Data_list.get(index))) {
                        System.out.println("Second token isn't Instruction");
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
            System.out.println("==============================================");

            int FieldNum = Assembler.FieldNum.get(instruction);
            String[] fields = {"", "", ""};

            machineCode = zero_bit;
            machineCode += opcode;

            for (int i = 0; i < FieldNum; ++i) {
                fields[i] = Data_list.get(index + 1 + i);

                if (!isInteger(fields[i])) {
                    System.out.println("Fields -> " + i + " is not an integer.");
                }

                int integerFields = toInteger(fields[i]);

                if (integerFields < 0 || integerFields > 7) {
                    System.out.println("Fields -> " + i + " is out of [0,7]");
                }
                /**
                 *  Convert to binary
                 */
                fields[i] = toBinaryString(integerFields); // import lib
                fields[i] = fillBits(fields[i], 3);
            }

            /**
             * Type check then compute to machineCodes
             */
            switch (type) {
                case "R" -> R_type(fields);
                case "I" -> I_type(fields);
                case "J" -> J_type(fields);
                case "O" -> O_type();
                case "F" -> F_type();
            }

            machineCodes.add(machineCode);
            curr_Line++;
            parseLineToData();
        }
        return new ArrayList<String>(machineCodes);
    }

    private void R_type(String[] fields){
            machineCode += fields[0];
            machineCode += fields[1];
            machineCode += "0000000000000";
            machineCode += fields[2];
    }

    private void I_type(String[] fields){
        // not-implement yet
        // two compliment!!!
    }
    private void J_type(String[] fields){
        // not-implement yet
    }

    private void O_type(){ // no field
            machineCode += "0000000000000000000000";
    }

    private void F_type(){ // no field
        // not-implement yet
    }

    public static String fillBits(String field, int i) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() + field.length() < i) {
            sb.append("0");
        }
        sb.append(field);

        return sb.toString();
    }

    private int toInteger(String field) {
        return Integer.parseInt(field);
    }

    private void LabelCheck(){
        // check Label
        while(tkz.hasNext()){
            if(Data_list.isEmpty()){
                parseLineToData();
                continue;
            }

            if(!isInstruction(Data_list.get(0))){
                Label_Mapping.put(Data_list.get(0) , curr_Line);
            }

            curr_Line++;
            parseLineToData();
        }
    }

    private boolean newlineCheck(String tkz) {
        return tkz.equals("\n");
    }

    private boolean isLabel(String tkz){
        return Label_Mapping.containsKey(tkz);
    }

    private boolean isInstruction(String tkz){
        return Instruction_mapping.containsKey(tkz);
    }

    private boolean isInteger(String field) {
        if(number_pattern.matcher(field).matches()){
            double number = Double.parseDouble(field);
            return number % 1 == 0;
        }else{
            return false;
        }
    }
}
