# Watson

This project attempts to replicate the groundbreaking IBM Watson by indexing wikipedia articles and using Jeopardy questions to query the system using different parameters, then comparing results and stating the best model.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.
* Clone this git repository to your local machine
* Download and unzip the index files from [Dropbox](https://arizona.box.com/s/mqtkkho9myv5lbz40ei6mku3fiawrcmx)
* Place index files in src/main/resources/index

### Prerequisites

* Java 8
* git
* Maven


### Installing

cd into project directory
```
cd .../Watson
```

Run the following Maven command

```
mvn verify
```

This will trigger a test to run and it will show all the different results obtained using different indices and parameters.

## Running the tests

The test can be run manually using:
```
mvn test
```
### Break down into tests

For each index, the program will print out all the different scores obtained using the different similarity functions.
```
========CALCULATING INDEX src/main/resources/index/stem========
                --------Similarity Function: NONE
        P@1: 20/100 = 0.2
        MMR: 0.2
                --------Similarity Function: org.apache.lucene.search.similarities.BM25Similarity
        P@1: 20/100 = 0.2
        MMR: 0.2
                --------Similarity Function: org.apache.lucene.search.similarities.ClassicSimilarity
        P@1: 0/100 = 0.0
        MMR: 0.0
                --------Similarity Function: org.apache.lucene.search.similarities.BooleanSimilarity
        P@1: 13/100 = 0.13
        MMR: 0.13
                --------Similarity Function: org.apache.lucene.search.similarities.DFISimilarity
        P@1: 31/100 = 0.31
        MMR: 0.31
                --------Similarity Function: org.apache.lucene.search.similarities.DFRSimilarity
        P@1: 18/100 = 0.18
        MMR: 0.18
                --------Similarity Function: org.apache.lucene.search.similarities.LMDirichletSimilarity
        P@1: 34/100 = 0.34
        MMR: 0.34
                --------Similarity Function: org.apache.lucene.search.similarities.LMJelinekMercerSimilarity
        P@1: 23/100 = 0.23
        MMR: 0.23

```


## Built With

* [Java](https://docs.oracle.com/javase/8/docs/api/) - programming language
* [Maven](https://maven.apache.org/) - Dependency Management
* [Lucene](https://lucene.apache.org/) - Used to generate indices

## Authors

* **Mauricio Herrera** - [Git](https://github.com/mauherrerag)

## Acknowledgments

* This project was assigned as the Final Project for CSC 483 - Text Retrieval and Web Search at the University of Arizona
