package stbi.indexer;

import org.testng.annotations.Test;
import stbi.common.TermFrequencyWeighter;

import java.io.File;

public class ModifiedInvertedIndexerTest {

    @Test
    public void testCreateIndex() throws Exception {
        String path = "d:\\source\\DocumentSearcher\\dataset\\CISI\\cisi.all";
        File documentFile = new File(path);
        ModifiedInvertedIndexer indexer = new ModifiedInvertedIndexer(documentFile, null);

        TermFrequencyWeighter rawTF = new TermFrequencyWeighter();
        indexer.createIndex(rawTF, false, false);
    }
}
