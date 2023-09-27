import org.junit.Assert;
import org.junit.Test;
public class FunctionTest {
    @Test
    public void AddBitsTest(){
        Assert.assertEquals("0001" , Assembler.addZeroBits("1",4));
        Assert.assertEquals("0011" , Assembler.addZeroBits("11",4));
        Assert.assertEquals("0111" , Assembler.addZeroBits("111",4));
        Assert.assertEquals("1111" , Assembler.addZeroBits("1111",4));
    }
}
