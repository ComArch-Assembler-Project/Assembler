import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Assembler {

    /** Important **/
    private static final String zero_bit = "0000000"; // 31-25 (7 bit) must be 0 bit
    private static final Pattern number_pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private AssemblerTokenizer tkz;
    Assembler(String assembly) {
        tkz = new AssemblerTokenizer(assembly);
    }
    private List<String> machineCodes = new ArrayList<>();
    private String machineCode = zero_bit; // add zero-bit first before load
    private int curr_Line = 0;
    private List<String> Data_list = new ArrayList<>();
    private Map<String, Integer> Label_Mapping = new HashMap<>();

    private static final Map<String , String> Opcode_mapping = Map.of(
            "add"  , "000",
            "nand" , "001",
            "lw" , "010",
            "sw" , "011",
            "beq" , "100",
            "jalr" , "101",
            "halt" , "110",
            "noop" , "111",
            ".fill" , "FIL"
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
    private void parseToData(){
        Data_list = new ArrayList<>();

        while (tkz.hasNext()) { // parse to data until newline
            String token = tkz.next();
            System.out.print(token);
            if (newlineCheck(token)) { // new line
                break;
            }
            Data_list.add(token);
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


}
