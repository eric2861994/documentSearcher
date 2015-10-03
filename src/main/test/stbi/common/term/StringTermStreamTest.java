package stbi.common.term;

import org.testng.annotations.Test;
import stbi.common.exception.NoTermException;

import static org.testng.Assert.*;

/**
 * Test StringTermStream.
 */
public class StringTermStreamTest {

    @Test
    public void testHasNextAndNext() throws Exception {
        StringTermStream stringTermStream = new StringTermStream("The quick brown fox jumps over the lazy dog");
        String[] expected = new String[]{"The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"};

        for (String currentExpectation : expected) {
            assertTrue(stringTermStream.hasNext());
            Term term = stringTermStream.next();
            assertEquals(currentExpectation, term.getText());
        }

        assertFalse(stringTermStream.hasNext());
        try {
            stringTermStream.next();
            fail();

        } catch (NoTermException ex) {
            // do nothing, test succeed
        }
    }
}
