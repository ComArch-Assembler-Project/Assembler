import java.util.List;
public class main {

    static String inputDIR = "Input/";
    static String fileExtension = ".s";
    static String OUTPUT_DIR = "Output/";

    /**
     *  outExtension[0] for file.bin
     *  outExtension[1] for file.txt
     */
    static String[] outExtension = {
            ".bin",".txt"
    };
    public static void main(String[] args) throws Exception {
        compute("R_TYPE");
        print("R_TYPE");
    }
    public static void compute(String File){
        Assembler Encoder = new Assembler(
                FileOperator.FileToString(inputDIR + File + fileExtension)
        );

        List<String> machineCodes = Encoder.computeToMachineCode();
        FileOperator.StringToFile(OUTPUT_DIR + File + outExtension[0], machineCodes);
    }

    public static void print(String File){
        String Input = OUTPUT_DIR + File + outExtension[0];
        System.out.println("\n");
        System.out.println("Print : " + Input);
        System.out.println(" " + FileOperator.FileToString(Input));
    }
}