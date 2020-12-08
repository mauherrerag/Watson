package edu.arizona.cs;
import org.apache.lucene.search.similarities.*;


public class Watson {

    private static String noneIndexPath = "src/main/resources/index/none";
    private static String stemIndexPath = "src/main/resources/index/stem";
    private static String lemmaIndexPath = "src/main/resources/index/lemma";

    private static String queryFilename = "src/main/resources/questions.txt";

    public static void main(String[] args ) {
        boolean lemma = false;
        boolean stem = false;

        // For iteration
        String[] pathsList = {noneIndexPath, stemIndexPath, lemmaIndexPath};

        // Starting up
        System.out.println("\t================STARTING WATSON================\n\n");

        // Iterating through all indices to see compare results.
        for (String path : pathsList) {
            QueryEngine queryEngine;
            // Setting flags equal to the way the index was created
            if (path.equals(stemIndexPath)) stem = true;
            else if(path.equals(lemmaIndexPath)) lemma = true;

            // Printing out results
            System.out.println("\n========CALCULATING INDEX " + path + "========");
            queryEngine = new QueryEngine(path, lemma, stem);
            System.out.println("\t\t--------Similarity Function: NONE");
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new BM25Similarity());
            System.out.println("\t\t--------Similarity Function: " + BM25Similarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new ClassicSimilarity());
            System.out.println("\t\t--------Similarity Function: " + ClassicSimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new BooleanSimilarity());
            System.out.println("\t\t--------Similarity Function: " + BooleanSimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new DFISimilarity(new IndependenceChiSquared()));
            System.out.println("\t\t--------Similarity Function: " + DFISimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new DFRSimilarity(new BasicModelBE(), new AfterEffectB(), new NormalizationH1()));
            System.out.println("\t\t--------Similarity Function: " + DFRSimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new LMDirichletSimilarity());
            System.out.println("\t\t--------Similarity Function: " + LMDirichletSimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);
            queryEngine.setSimilarityFunction(new LMJelinekMercerSimilarity((float)0.7));
            System.out.println("\t\t--------Similarity Function: " + LMJelinekMercerSimilarity.class.getName());
            queryEngine.playJeopardy(queryFilename);

            // Reversing flags when set
            lemma = false;
            stem = false;
        }

        // Terminating
        System.out.println("\n\n\t================TERMINATED================\n\n");

    }

    private void buildIndices() {
//        LuceneIndex luceneIndex;
//        System.out.println("\n\t\t========STARTING INDEX " + noneIndexPath + "========");
//        luceneIndex = new LuceneIndex(noneIndexPath, false, false, false);
//
//        System.out.println("\n\t\t========STARTING INDEX " + noLemmaIndexPath + "========");
//        luceneIndex = new LuceneIndex(noLemmaIndexPath, false, true, true);
//
//        System.out.println("\n\t\t========STARTING INDEX " + noStemIndexPath + "========");
//        luceneIndex = new LuceneIndex(noStemIndexPath, true, false, true);
    }
}
