package stbi.common.term.stemmer;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PorterStemmerTest {

    @Test
    public void testStem() throws Exception {
        PorterStemmer stemmer = new PorterStemmer();
        int numOfWords = 6;
        String[] words = new String[]{"playing", "plays", "play", "temptations", "weakness", "terrible"};
        String[] expectedOutput = new String[]{"plai", "plai", "plai", "temptat", "weak", "terribl"};

        String stemedWord;
        for (int i = 0; i < numOfWords; i++) {
            stemedWord = stemmer.stem(words[i]);
            assertEquals(stemedWord, expectedOutput[i]);
        }

    }

}
