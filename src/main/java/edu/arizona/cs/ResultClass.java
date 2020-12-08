package edu.arizona.cs;
import org.apache.lucene.document.Document;

public class ResultClass {
    Document docName;
    double docScore = 0;

    public String toString() {
        return docName.toString() + " score: " + docScore;
    }
}
