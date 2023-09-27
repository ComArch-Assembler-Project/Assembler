import org.junit.Assert;
import org.junit.Test;
public class FunctionTest {
    @Test
    public void toFillBits(){
        Assert.assertEquals("0001" , Assembler.fillBits("1",4));
        Assert.assertEquals("0011" , Assembler.fillBits("11",4));
        Assert.assertEquals("0111" , Assembler.fillBits("111",4));
        Assert.assertEquals("1111" , Assembler.fillBits("1111",4));
    }
}
