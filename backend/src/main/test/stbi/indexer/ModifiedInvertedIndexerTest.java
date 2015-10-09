package stbi.indexer;

import org.testng.annotations.Test;
import stbi.common.TermFrequencyWeighter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModifiedInvertedIndexerTest {

    @Test
    public void testCreateIndex() throws Exception {
        String path = "/Users/kevinyu/Developer/documentSearcher/dataset/CISI/cisi.all";
        File documentFile = new File(path);
        ModifiedInvertedIndexer indexer = new ModifiedInvertedIndexer(documentFile, null);

        TermFrequencyWeighter rawTF = new TermFrequencyWeighter();
        indexer.createIndex(rawTF, false, false);
    }
}
