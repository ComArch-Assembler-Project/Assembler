import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
public class Assembler {

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
    private static final Map<String , String> Type_mapping = Map.of(
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
}
