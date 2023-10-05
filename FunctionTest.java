import org.junit.Assert;
import org.junit.Test;
import java.util.List;

public class FunctionTest {

    static String inputDIR = "Input/";
    static String fileExtension = ".s";
    static String File = "TEST1";
    static String InputString = FileOperator.FileToString(inputDIR + File + fileExtension);

    Assembler as = new Assembler(InputString);
    @Test
    public void AddZeroBitsTest(){
        Assert.assertEquals("0001" , Assembler.addZeroBits("1",4));
        Assert.assertEquals("0011" , Assembler.addZeroBits("11",4));
        Assert.assertEquals("0111" , Assembler.addZeroBits("111",4));
        Assert.assertEquals("1111" , Assembler.addZeroBits("1111",4));
    }

    @Test
    public void isIntegerTest(){
        Assert.assertTrue(Assembler.isInteger("123"));
        Assert.assertFalse(Assembler.isInteger("ABC"));
    }

    @Test
    public void toIntegerTest(){
        Assert.assertEquals(123,Assembler.toInteger("123"));
    }

    @Test
    public void twoComplimentTest(){
        // 1 to -1
        Assert.assertEquals("1111111111111111", Assembler.twosCompliment("0000000000000001"));
        // 10 to -10
        Assert.assertEquals("1111111111110110", Assembler.twosCompliment("0000000000001010"));

        // reverse!!!
        // -1 to 1
        Assert.assertEquals("0000000000000001", Assembler.twosCompliment("1111111111111111"));
    }

    @Test
    public void isInstructionTest(){
//        System.out.println(Assembler.);
        Assert.assertTrue(Assembler.isInstruction("add"));
        Assert.assertTrue(Assembler.isInstruction("nand"));
        Assert.assertTrue(Assembler.isInstruction("lw"));
        Assert.assertTrue(Assembler.isInstruction("sw"));
        Assert.assertTrue(Assembler.isInstruction("beq"));
        Assert.assertTrue(Assembler.isInstruction("jalr"));
        Assert.assertTrue(Assembler.isInstruction("noop"));
        Assert.assertTrue(Assembler.isInstruction("halt"));
        Assert.assertTrue(Assembler.isInstruction(".fill"));

        Assert.assertFalse(Assembler.isInstruction("mul"));
        Assert.assertFalse(Assembler.isInstruction("sub"));
        Assert.assertFalse(Assembler.isInstruction("div"));
    }

    @Test
    public void parseLineToDataTest(){
        as.parseLineToData();
        System.out.println(" " + InputString);
        System.out.println("First Line  : " + as.Data_list);
        as.parseLineToData();
        System.out.println("Second Line : " + as.Data_list);
    }

    @Test
    public void computeMachineCode(){
        System.out.println(" " + InputString);
        List<String> bin = as.computeToMachineCode();
        List<String> dec = Assembler.binaryToDecimal(bin);
        System.out.println(bin);
        System.out.println(dec);

        Assert.assertEquals(dec, Assembler.binaryToDecimal(bin));
        Assert.assertEquals(bin, Assembler.decimalToBinary(dec));
    }

}
