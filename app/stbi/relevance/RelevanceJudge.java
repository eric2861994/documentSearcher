package stbi.relevance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    private Map<Integer, Set<Integer>> queryRelation;

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

                        if (line.length() > 3) id = Integer.parseInt(line.substring(3));
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

                String[] token = line.split("\\s+");

                int queryId = Integer.parseInt(token[0]);
                int documentId = Integer.parseInt(token[1]);

                if (!queryRelation.containsKey(queryId)) {
                    queryRelation.put(queryId, new HashSet<Integer>());
                }
                queryRelation.get(queryId).add(documentId);

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
        evaluation.nonInterpolatedPrecision = computeNonInterpolatedPrecision(queryId, documentIdList,0);
        evaluation.precision = computePrecision(queryId, documentIdList);
        evaluation.recall = computeRecall(queryId, documentIdList,0);
        evaluation.interpolatedPrecision = computeInterpolatedPrecision(queryId, documentIdList,0);
        return evaluation;
    }

    public Evaluation evaluate(int queryId, List<Integer> documentIdList, List<Integer> filterIdList) {
        Evaluation evaluation = new Evaluation();

        int numFilter = 0;
        for (Integer filterId : filterIdList) {
            if (isDocumentRelevant(queryId,filterId)) numFilter += 1;
        }

        evaluation.nonInterpolatedPrecision = computeNonInterpolatedPrecision(queryId, documentIdList,numFilter);
        evaluation.precision = computePrecision(queryId, documentIdList);
        evaluation.recall = computeRecall(queryId, documentIdList,numFilter);
        evaluation.interpolatedPrecision = computeInterpolatedPrecision(queryId, documentIdList,numFilter);
        return evaluation;
    }


    private boolean isDocumentRelevant(int queryId, int documentId) {
        Set<Integer> documentIdList = queryRelation.get(queryId);
        if (documentIdList == null) return false;

        return documentIdList.contains(documentId);
    }

    public float computeRecall(int queryId, List<Integer> documentIdList, int numFilter) {
        if (queryRelation.get(queryId) == null) return 0;
        else {
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) nOfRelevantDocumentInQuery++;
            }
            int nRelevantDocument = queryRelation.get(queryId).size() - numFilter;
            if (nRelevantDocument == 0) return 0;
            return (float) nOfRelevantDocumentInQuery / nRelevantDocument;
        }
    }

    public float computePrecision(int queryId, List<Integer> documentIdList) {
        if (queryRelation.get(queryId) == null || documentIdList.size() == 0) return 0;
        else {
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) nOfRelevantDocumentInQuery++;
            }
            if (documentIdList.size() == 0) return 0;
            return (float) nOfRelevantDocumentInQuery / documentIdList.size();
        }
    }

    public float computeNonInterpolatedPrecision(int queryId, List<Integer> documentIdList, int numFilter) {
        if (queryRelation.get(queryId) == null) return 0;
        else {
            float precisionAccum = 0;
            int nOfRelevantDocumentInQuery = 0;
            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) {
                    nOfRelevantDocumentInQuery++;
                    float precision = (float) nOfRelevantDocumentInQuery / (i + 1);
                    precisionAccum += precision;
                }
            }
            int nRelevantDocument = queryRelation.get(queryId).size() - numFilter;
            if (nRelevantDocument == 0) return 0;
            return precisionAccum / nRelevantDocument;
        }
    }

    public float computeInterpolatedPrecision(int queryId, List<Integer> documentIdList, int numFilter) {
        if (queryRelation.get(queryId) == null) return 0;
        else {

            float precisionAccum = 0;
            int nOfRelevantDocumentInQuery = 0;
            int prevStep = 0;
            int nOfRelevantDocument = queryRelation.get(queryId).size() - numFilter;

            for (int i = 0; i < documentIdList.size(); i++) {
                if (isDocumentRelevant(queryId, documentIdList.get(i))) {
                    nOfRelevantDocumentInQuery++;
                    float precision = (float) nOfRelevantDocumentInQuery / (i + 1);
                    float recall = (float) nOfRelevantDocumentInQuery / nOfRelevantDocument;

                    int stepCount = ((int) Math.floor(recall * 10) - prevStep);
                    precisionAccum += (stepCount * precision);

                    prevStep = (int) Math.floor(recall * 10);
                }
            }
            return precisionAccum / 10;
        }
    }

    public Map<Integer, Set<Integer>> getQueryRelation() {
        return queryRelation;
    }
}
