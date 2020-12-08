package edu.arizona.cs;

import edu.stanford.nlp.simple.Sentence;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.*;

import java.util.*;
import java.io.File;

public class QueryEngine {

    // Fields
    private final String indexPath;
    private boolean lemmatize;
    private boolean stem;
    private Similarity similarity;

    // Constructor
    public QueryEngine(String indexPath, boolean lemmatize, boolean stem){
        this.indexPath = indexPath;
        this.lemmatize = lemmatize;
        this.stem = stem;
        this.similarity = null;

        // Checking parameters
        if (lemmatize && stem) {
            this.lemmatize = true;
            this.stem = false;
            System.out.println("Error: cannot lemmatize and stem at the same time. Defaulting to lemmatize only.");
        }
    }

    // Methods

    /*
     *   This method sets a given similarity function for the engine.
     */
    public void setSimilarityFunction(Similarity similarity) {
        this.similarity = similarity;
    }

    /*
     *   This method starts the "game" and runs all the queries
     *   in the file passed in. It then prints out the results
     *   for the number of correct answers.
     */
    public void playJeopardy(String queriesFilename) {

        // Opening queries file
        File queriesFile = new File(queriesFilename);

        // Reading
        try {
            Scanner in = new Scanner(queriesFile);

            String category = "";
            String query = "";
            String answer = "";

            int numQuestions = 0;
            int correct = 0;
            double mmr = 0;

            int lineNumber = 0;
            while(in.hasNextLine()) {
                if (lineNumber % 4 == 0) category = in.nextLine();
                else if (lineNumber % 4 == 1) query = in.nextLine();
                else if (lineNumber % 4 == 2) answer = in.nextLine();
                else {
                    // Running query
                    List<ResultClass> queryResults = this.runQuery(category + " " + query);

                    if (queryResults.size() > 0 && queryResults.get(0).docName.get("title").equals(answer)) {
                        correct++;
                        mmr += 1.0;
                    } else {
                        for (int i = 0; i < queryResults.size(); i++) {
                            if (queryResults.get(i).docName.get("title").equals(answer)) {
                                mmr += (double) (1/(i + 1));
                                break;
                            }
                        }
                    }
                    numQuestions ++;
                    in.nextLine();
                }
                lineNumber++;
            }

            // Printing out results
            String result = "\tP@1: " + correct + "/" + numQuestions + " = " + (double)correct/numQuestions
                    + "\n\tMMR: " + mmr/numQuestions;
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*
     *   This method runs a given query against the LuceneIndex
     *   and returns the top 10 results along with their scores.
     */
    private List<ResultClass> runQuery(String query) {
        // Initializing list to store answer
        List<ResultClass> results = new ArrayList<>();

        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

        // Parse and tokenize query
        query = parseQuery(query);

        try {
            // Creating Lucene query
            Query currentQuery = new QueryParser("contents", standardAnalyzer).parse(QueryParser.escape(query));
//            System.out.println(currentQuery);

            // Getting index
            Directory index = FSDirectory.open(new File(indexPath).toPath());
            int maxResults = 10;
            IndexReader indexReader = DirectoryReader.open(index);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            // Adding scoring function when available
            if ((this.similarity != null)) indexSearcher.setSimilarity(this.similarity);

            // Searching
            TopDocs topDocs = indexSearcher.search(currentQuery, maxResults);
            ScoreDoc[] searchResults = topDocs.scoreDocs;

            // Creating results objects
            for (ScoreDoc searchResult : searchResults) {
                ResultClass currentResult = new ResultClass();
                int docId = searchResult.doc;
                Document document = indexSearcher.doc(docId);
                currentResult.docName = document;
                currentResult.docScore = searchResult.score;
                results.add(currentResult);
            }

            // Closing index
            indexReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    /*
     *   This method takes in a query string and lemmatizes/stems
     *   or both if the flags for those are set.
     */
    private String parseQuery(String query) {
        // To store stemmed/lemmatized terms as we loop
        List<String> parsedTokens = new ArrayList<>();

        // Used to stem/lemmatize
        Sentence sentence = new Sentence(query.toLowerCase());

        // Lemmas enabled
        if (this.lemmatize) {
            for (String lemma : sentence.lemmas()) {
                parsedTokens.add(lemma);
            }
        }
        // Stemming enabled
        else if (this.stem) {
            for (String word : sentence.words()) {
                parsedTokens.add(LuceneIndex.stemTerm(word));
            }
            // Nothing enabled
        } else {
            parsedTokens.addAll(sentence.words());
        }

        return String.join(" ", parsedTokens).trim();
    }
}