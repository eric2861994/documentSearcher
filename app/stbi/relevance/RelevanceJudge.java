package stbi.relevance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelevanceJudge {

    public static class Query {
        public Query(int a_id, String a_queryString) {
            id = a_id;
            queryString = a_queryString;
        }

        public int id;
        public String queryString;
    }

    public static class Evaluation {
        public float precision;
        public float recall;
        public float nonInterpolatedPrecision;
        public float interpolatedPrecision;
    }

    private static final char QUERY_SECTION_TOKEN = '.';
    private static final char QUERY_SECTION_ID = 'I';
    private static final char QUERY_SECTION_QUERY_STRING = 'W';

    private List<Query> queryList;
    private Map<Integer, List<Integer>> queryRelation;

    public RelevanceJudge(File queryFile, File queryRelationFile) throws IOException {
        queryList = new ArrayList<>();
        queryRelation = new HashMap<>();

        loadQueryFromFile(queryFile);
        loadQueryRelationFromFile(queryRelationFile);
    }

    private void loadQueryFromFile(File queryFile) throws IOException {

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(queryFile);
            bufferedReader = new BufferedReader(fileReader);

            int id = -1;
            StringBuilder queryString = new StringBuilder("");

            int numOfQuery = 0;

            char currentSection = '-';
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line.length() > 0 && line.charAt(0) == QUERY_SECTION_TOKEN) {
                    currentSection = line.charAt(1);
                    if (currentSection == QUERY_SECTION_ID) {

                        if (numOfQuery != 0) {
                            queryList.add(new Query(id, queryString.toString()));
                            id = -1;
                            queryString.setLength(0);
                        }

                        if (line.length() > 3) id = Integer.parseInt(line.substring(3)); // TODO fix this kayu
                    }
                    numOfQuery++;
                } else {
                    switch (currentSection) {
                        case QUERY_SECTION_QUERY_STRING:
                            if (!queryString.toString().equals((""))) queryString.append("\n");
                            queryString.append(line);
                            break;
                        default:
                            break;
                    }
                }
                line = bufferedReader.readLine();
            }

            if (id != -1) queryList.add(new Query(id, queryString.toString()));

        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (fileReader != null) fileReader.close();
        }
    }

    private void loadQueryRelationFromFile(File queryRelationFile) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(queryRelationFile);
            bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            while (line != null) {

                String[] token = line.split(" ");

                int queryId = Integer.parseInt(token[0]);
                int documentId = Integer.parseInt(token[1]);

                List<Integer> documentList = queryRelation.get(queryId);
                if (documentList == null) documentList = new ArrayList<Integer>();
                documentList.add(documentId);

                line = bufferedReader.readLine();
            }


        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (fileReader != null) fileReader.close();
        }
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public Evaluation evaluate(int queryId, List<Integer> documentIdList) {
        Evaluation evaluation = new Evaluation();
        evaluation.precision = computePrecision(queryId, documentIdList);
        evaluation.recall = computeRecall(queryId, documentIdList);
        evaluation.nonInterpolatedPrecision = computeNonInterpolatedPrecision(queryId, documentIdList);
        evaluation.interpolatedPrecision = computeInterpolatedPrecision(queryId, documentIdList);
        return evaluation;
    }

    private boolean isDocumentRelevant(int queryId, int documentId) {
        List<Integer> documentIdList = queryRelation.get(queryId);
        if (documentIdList == null) return false;

        for (int i = 0; i < documentIdList.size(); i++) {
            int currentDocumentIdinList = documentIdList.get(i);
            if (currentDocumentIdinList == documentId) return true;
        }
        return false;
    }

    public float computeRecall(int queryId, List<Integer> documentIdList) {
        if (queryRelation.get(queryId) == null) return 0;
        else {
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) nOfRelevantDocumentInQuery++;
            }
            return (float) nOfRelevantDocumentInQuery / queryRelation.get(queryId).size();
        }
    }

    public float computePrecision(int queryId, List<Integer> documentIdList) {
        if (queryRelation.get(queryId) == null) return 0;
        else {
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) nOfRelevantDocumentInQuery++;
            }
            return (float) nOfRelevantDocumentInQuery / documentIdList.size();
        }
    }

    public float computeNonInterpolatedPrecision(int queryId, List<Integer> documentIdList) {
        if (queryRelation.get(queryId) == null) return 0;
        else {
            float precisionAccum = 0;
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) {
                    nOfRelevantDocumentInQuery++;
                    float precision = (float) nOfRelevantDocumentInQuery / i;
                    precisionAccum += precision;
                }
            }
            return precisionAccum / queryRelation.get(queryId).size();
        }
    }

    public float computeInterpolatedPrecision(int queryId, List<Integer> documentIdList) {
        if (queryRelation.get(queryId) == null) return 0;
        else {

            float precisionAccum = 0;
            int nOfRelevantDocumentInQuery = 0;
            int prevStep = 0;
            int nOfRelevantDocument = queryRelation.get(queryId).size();

            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) {
                    nOfRelevantDocumentInQuery++;
                    float precision = (float) nOfRelevantDocumentInQuery / i;
                    float recall = (float) nOfRelevantDocumentInQuery / nOfRelevantDocument;

                    int stepCount = ((int) Math.floor(recall * 10) - prevStep);
                    precisionAccum += (stepCount * precision);

                    prevStep = (int) Math.floor(recall * 10);
                }
            }
            return precisionAccum / 10;
        }
    }
}
