package stbi;

import stbi.common.Loader;
import stbi.common.util.Calculator;
import stbi.indexer.ModifiedInvertedIndexer;
import stbi.indexer.RawDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Application Logic.
 * Handle all application features.
 */
public class ApplicationLogic {
    private final Loader loader;
    private final Calculator calculator;
    private final ModifiedInvertedIndexer modifiedInvertedIndexer;

    private ApplicationLogic() {
        loader = new Loader();
        calculator = new Calculator();
        modifiedInvertedIndexer = new ModifiedInvertedIndexer(calculator);
    }

    public void indexDocuments(File documentsFile, File stopwords) throws IOException {
        List<RawDocument> documents = loader.loadAllDocuments(documentsFile);
        loader.loadStopwords(stopwords);
        modifiedInvertedIndexer.createIndex();

    }

    private static final ApplicationLogic oneInstance = new ApplicationLogic();

    public static ApplicationLogic getInstance() {
        return oneInstance;
    }
}
