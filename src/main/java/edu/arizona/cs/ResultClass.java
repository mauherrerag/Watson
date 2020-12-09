/*
File: ResultClass.java
Author: Mauricio Herrera
Purpose: This class acts as a container for the results in order to put together a document and its score.
Course: CSC 483 - Text Retrieval and Web Search
 */

package edu.arizona.cs;
import org.apache.lucene.document.Document;

public class ResultClass {
    Document docName;
    double docScore = 0;

    /*
    This method provides a string representation for this instance.
     */
    public String toString() {
        return docName.toString() + " score: " + docScore;
    }
}
