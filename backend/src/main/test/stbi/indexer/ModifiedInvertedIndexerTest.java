package stbi.indexer;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.util.Calculator;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class ModifiedInvertedIndexerTest {

    private Properties testConfiguration;
    private Calculator calculator;

    @BeforeMethod
    public void setUp() throws Exception {
        // setup test configuration
        testConfiguration = new Properties();
        InputStream inputStream = getClass().getResourceAsStream("/testConfig.properties");
        testConfiguration.load(inputStream);

        calculator = new Calculator();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        // nothing to tear down
    }

    @Test
    public void testCreateIndex() throws Exception {
        File documentFile = new File(testConfiguration.getProperty("ADIDocSet"));
        ModifiedInvertedIndexer indexer = new ModifiedInvertedIndexer(documentFile, null, calculator);

        ModifiedInvertedIndex index = indexer.createIndex(Calculator.TFType.RAW_TF, false, false);
        System.out.println("Aw yeah!");
    }

    @Test
    public void testLoadAllDocuments() throws IOException, ClassNotFoundException {
        File documentFile = new File(testConfiguration.getProperty("ADIDocSet"));
        ModifiedInvertedIndexer indexer = new ModifiedInvertedIndexer(documentFile, null, calculator);
        List<RawDocument> documents = indexer.loadAllDocuments();

        File testDocFile = new File(testConfiguration.getProperty("ADITestDocuments"));
        RawDocument[] testDocuments = loadTestDocuments(testDocFile);

        for (RawDocument oneDocument : documents) {
            int docIdx = getDocumentIndex(oneDocument.getId(), testDocuments);
            if (docIdx != -1) {
                Assert.assertEquals(oneDocument, testDocuments[docIdx]);
            }
        }
    }

    /**
     * Run this to create test documents
     */
    public void createTestDocuments() throws IOException, ClassNotFoundException {
        RawDocument[] containedDocuments = new RawDocument[5];
        containedDocuments[0] = new RawDocument(1, "the ibm dsd technical information center - a total systems approach\n" +
                "combining traditional library features\n" +
                "and mechanized computer processing", "H. S. WHITE", "the ibm data systems division technical\n" +
                " information center (tic) provides an operating developmental\n" +
                "system for integrated and compatible mechanized\n" +
                " processing of technical information received within the organization.\n" +
                "  the system offers several advantages :\n" +
                "     1 . it is a sophisticated mechanized system for dissemination\n" +
                "and retrieval;\n" +
                "     2 . it is compatible with all library mechanized\n" +
                "  records produced under a standard processing format\n" +
                "  within ibm libraries, providing such traditional tools\n" +
                "  as 3 x 5 catalog cards, circulation records and overdue\n" +
                "notices;\n" +
                "     3 . it is reversible, so that discontinuation of machine\n" +
                "processing would not cause gaps in the library's\n" +
                "  manual records;\n" +
                "     4 . it is controlled, producing statistical evaluations\n" +
                "of its own program efficiency;\n" +
                "     5 . it is user-oriented, providing 24-hour copy access\n" +
                "and immediate microfilm access to its documents;\n" +
                "     6 . it is relatively simple, relying on the ibm 1401\n" +
                "  data processing system for all its processing and output.\n" +
                "\n" +
                "  since the system has been operating for over a year, the\n" +
                "conclusions drawn are based on actual experience .");
        containedDocuments[1] = new RawDocument(42, "new photon composing machine will set type from the output of\n" +
                "a computer .", "R. G. CROCKETT\n" +
                "W. W. YOUDEN", "/zip/ is a printout device that will produce\n" +
                "justified text of typographical quality on film or paper\n" +
                "from the off-line operation of a computer .  character\n" +
                " and command codes are read at high speed from magnetic\n" +
                " tape; processed in a control console, and then are used to\n" +
                " initiate the projection of characters onto film .");
        containedDocuments[2] = new RawDocument(49, "microfiche, a new information media .", "C. P. YERKES", "standard specifications for card size, layout and reduction ratios\n" +
                "have been adopted by all major manufacturers of microfiche .\n" +
                "the use of this miniaturization of pages of information on transparent\n" +
                "film sheets should increase available information while\n" +
                "decreasing document switching time and costs .");
        containedDocuments[3] = new RawDocument(80, "design of an experiment for evaluation of the citation index\n" +
                "as a reference aid .", "B. LIPETZ", "objective evaluation of a documentation\n" +
                " technique may be accomplished by introducing it in a limited\n" +
                "test area and studying information user behavior in\n" +
                " the test area versus control areas .  such an experiment is\n" +
                " being devised to test the merit of the citation index as a\n" +
                " reference aid for physicists .");
        containedDocuments[4] = new RawDocument(82, "machine recognition of linguistic synonymy and logical deducibility .", "S. ABRAHAM", "a theory of synonymy of expression has been developed .  a comparative\n" +
                "study of grammatical and logical rules has been made .  formal\n" +
                "systems include both the rules of propositional calculus and\n" +
                "predicate calculus and the rules of transformational grammars .");

        File testDocumentsFile = new File(testConfiguration.getProperty("ADITestDocuments"));

        OutputStream outputStream = null;
        ObjectOutputStream fout = null;
        try {
            outputStream = new FileOutputStream(testDocumentsFile);
            fout = new ObjectOutputStream(outputStream);
            fout.writeObject(containedDocuments);

        } finally {
            if (fout != null) {
                fout.close();

            } else if (outputStream != null) {
                outputStream.close();
            }
        }

        RawDocument[] storedDocuments = loadTestDocuments(testDocumentsFile);
        for (int docIdx = 0; docIdx != containedDocuments.length; docIdx++) {
            Assert.assertEquals(containedDocuments[docIdx], storedDocuments[docIdx]);
        }
    }

    /**
     * Load tests documents from a file.
     *
     * @param testDocumentsFile file containing serialized raw documents.
     * @return test documents.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private RawDocument[] loadTestDocuments(File testDocumentsFile) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = null;
        ObjectInputStream fin = null;
        try {
            fileInputStream = new FileInputStream(testDocumentsFile);
            fin = new ObjectInputStream(fileInputStream);
            return (RawDocument[]) fin.readObject();

        } finally {
            if (fin != null) {
                fin.close();

            } else if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    /**
     * Get the index of the document where it's id is equals to id.
     *
     * @param id        id to find
     * @param documents document set
     * @return document index where it's id is equals to id, return -1 if not found.
     */
    private int getDocumentIndex(int id, RawDocument[] documents) {
        for (int docIdx = 0; docIdx != documents.length; docIdx++) {
            if (id == documents[docIdx].getId()) {
                return docIdx;
            }
        }

        return -1;
    }
}
