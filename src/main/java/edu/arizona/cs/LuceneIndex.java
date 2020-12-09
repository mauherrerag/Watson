/*
File: LuceneIndex.java
Author: Mauricio Herrera
Purpose: This class builds the index or indices indicated.
Course: CSC 483 - Text Retrieval and Web Search
 */

package edu.arizona.cs;

import edu.stanford.nlp.simple.Sentence;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class LuceneIndex {

    // Fields
    private String indexPath;
    private boolean lemmatize;
    private boolean stem;

    // This is the path for the wiki files directory
    private static String wikiSubsetDir = "src/main/resources/wiki-subset/";

    // Constructor
    public LuceneIndex(String indexPath, boolean lemmatize, boolean stem) {
        this.indexPath = indexPath;
        this.lemmatize = lemmatize;
        this.stem = stem;

        // Checking parameters
        if (lemmatize && stem) {
            this.lemmatize = true;
            this.stem = false;
            System.out.println("Error: cannot lemmatize and stem at the same time. Defaulting to lemmatize only.");
        }

        // Creating index
        this.buildIndex();
    }

    // Methods

    /*
    This method uses the static variables holding the paths where the indices will be stored to iterate through them
    and create the indices. It iterartes through the files in the wiki-subset directory and passes each one to parseDocument().
     */
    public void buildIndex() {
        // Creating analyzer for tokenization
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

        try {
            // Creating Index
            Directory index = FSDirectory.open(new File(indexPath).toPath());
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);
            IndexWriter indexWriter = new IndexWriter(index, indexWriterConfig);

            // Getting wiki files
            File dir = new File(wikiSubsetDir);
            int documentNumber = 1;
            for (String file : dir.list()) {
                System.out.println(String.format("Indexing document %d: %s", documentNumber, file));
                parseDocument(wikiSubsetDir + file, indexWriter);
                documentNumber ++;
            }

            indexWriter.close();
            index.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    This method takes in the path for the document to be parsed, as well as the IndexWriter object where it will be
    written to. It goes through the document line by line, identifying if a line contains the title, categories, or
    article content. Then, once all three variables are set, they are passed to addDocToIndex().
     */
    private void parseDocument(String filename, IndexWriter indexWriter) {
        String documentTitle = "";
        String categories = "";
        List<String> contents = new ArrayList<>();
        String categoriesDescriptor = "CATEGORIES:";
        String[] ignoredHeaders = {"See also", "References", "Further reading", "External links"};

        // Opening file
        File file = new File(filename);

        // Reading file
        try {
            Scanner in = new Scanner(file);

            while (in.hasNextLine()) {
                String line = in.nextLine();

                // TITLES
                if (line.startsWith("[[") && line.endsWith("]]") & line.length() > 4) {
                    if (!documentTitle.equals("")){

                        addDocToIndex(indexWriter, documentTitle, categories.trim(), String.join(" ", contents).trim());
                    }

                    // Document ended. Start parsing new one
                    documentTitle = line.substring(2, line.length() - 2);
                    contents = new ArrayList<>();
                }

                // CATEGORIES
                else if (line.startsWith(categoriesDescriptor)) {
                    categories = line.substring(categoriesDescriptor.length() + 1);
                }
                // HEADERS
                else if (line.startsWith("=") && line.endsWith("=") && line.length() > 2){
                    line = line.replace("=", "");
                    boolean addHeader = true;
                    for (String ignoredHeader : ignoredHeaders) {
                        if (line.contains(ignoredHeader)) addHeader = false;
                    }
                    if (addHeader) contents.add(line);
                }
                // CONTENT
                else {
                    contents.add(line);
                }
            }

            // Adding last document created
            addDocToIndex(indexWriter, documentTitle, categories.trim(), String.join(" ", contents).trim());

            // Closing scanner
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
    This method takes in the title, categories, contents of the article, and the IndexWriter object and it
    lemmatizes/stems the strings accordingly and adds them to the Document object. Finally, it writes the document
    into the index.
     */
    private void addDocToIndex(IndexWriter indexWriter, String title, String categories, String contents) {
        // Error checking for empty categories or content
        if (categories.equals("")) categories = ".";
        if (contents.equals("")) contents = ".";

        Document document = new Document();
        List<String> finalCategories = new ArrayList<>();
        List<String> finalContents = new ArrayList<>();
        Sentence categorySentence = new Sentence(categories.toLowerCase());
        Sentence contentsSentence = new Sentence(contents.toLowerCase());

        if (lemmatize) {
            for (String lemma : categorySentence.lemmas()) {
                finalCategories.add(lemma);
            }
            for (String lemma : contentsSentence.lemmas()) {
                finalContents.add(lemma);
            }
        } else if (stem) {
            for (String word : categorySentence.words()) {
                finalCategories.add(stemTerm(word));
            }
            for (String word : contentsSentence.words()) {
                finalContents.add(stemTerm(word));
            }
        } else {
            finalCategories.addAll(categorySentence.words());
            finalContents.addAll(contentsSentence.words());
        }


        // Adding fields to document
        document.add(new StringField("title", title, Field.Store.YES));
        document.add(new TextField("categories", String.join(" ", finalCategories), Field.Store.YES));
        String text = title + " " + String.join(" ", finalCategories) + " " + String.join(" ", finalContents);
        document.add(new TextField("contents", text.trim(), Field.Store.YES));

        // Adding document to index
        try {
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    This method uses a PorterStemmer to stem a given string and returns it. This method is static in order to use it
    in the QueryEngine class.
     */
    public static String stemTerm(String term) {
        PorterStemmer porterStemmer = new PorterStemmer();
        porterStemmer.setCurrent(term);
        porterStemmer.stem();
        return porterStemmer.getCurrent();
    }
}
