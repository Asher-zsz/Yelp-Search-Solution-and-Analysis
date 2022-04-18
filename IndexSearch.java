package com.example;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.junit.Before;
import org.junit.Test;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexSearch {

    private IndexReader indexreader;
    private IndexSearcher indexsearcher;

    @Before
    public void init() throws Exception{
        Directory dir = FSDirectory.open(new File("E:\\Projects\\Information_retrieval\\final_project\\LuceneProject\\LuceneDir").toPath());
        indexreader = DirectoryReader.open(dir);
        indexsearcher = new IndexSearcher(indexreader);
        //Default: BM25
//        indexsearcher.setSimilarity(new BM25Similarity(1.2f,0.75f));
        //TF-IDF
        indexsearcher.setSimilarity(new ClassicSimilarity());
    }

    @Test
    public void booleanQuery() throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        String[] fields = {"name", "categories"};
        MultiFieldQueryParser multiQueryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query = multiQueryParser.parse("Diners AND Breakfast AND Brunch");
        printResult(query);
    }

    @Test
    public void rangeQuery() throws Exception{
        Query query = DoublePoint.newRangeQuery("stars",3.5,5.0);
        printResult(query);
    }

    @Test
    public void geospatialSearch() throws Exception{
        //search within 5km from the given location
        Query distQuery = LatLonPoint.newDistanceQuery("latlon", 40.0, -75.0, 10 * 1000);
        printResult(distQuery);
    }

    @Test
    public void combinedQuery() throws Exception{
        Analyzer analyzer = new StandardAnalyzer();
        String[] fields = {"name", "categories","tip","review"};
        MultiFieldQueryParser multiQueryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query1 = multiQueryParser.parse("Good Breakfast with cafe");
        Query query2 = DoublePoint.newRangeQuery("stars",4.0,5.0);
        Query distQuery = LatLonPoint.newDistanceQuery("latlon", 40.0, -75.0, 5 * 1000);

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query1, BooleanClause.Occur.MUST);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);
        booleanQuery.add(distQuery, BooleanClause.Occur.MUST);

        printResult(booleanQuery.build());
    }

    @Test
    public void QueryBoost() throws Exception{
        Analyzer analyzer = new StandardAnalyzer();
        String[] fields = {"name", "categories", "tip", "review"};
        // set parameters related to relevance ranking
        HashMap<String, Float> boost = new HashMap<>();
        boost.put("categories", 10000f);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer, boost);
        Query query = multiFieldQueryParser.parse("breakfast");
        printResult(query);

    }

    private void printResult(Query query) throws IOException {
        TopDocs topDocs = indexsearcher.search(query, 20);

        System.out.println("Total Records:" + topDocs.totalHits + "\n");
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        if (scoreDocs != null) {
            for (ScoreDoc scoreDoc : scoreDocs) {
                //获取文档的唯一ID
                int DocID = scoreDoc.doc;
                //根据文档ID获取文档内容
                Document doc = indexsearcher.doc(DocID);
                float score = scoreDoc.score;

                System.out.println("score:" + score);
                System.out.println("name:" + doc.get("name"));
                System.out.println("stars:" + doc.get("stars"));
                System.out.println("categories:" + doc.get("categories"));
                System.out.println("tip:" + doc.get("tip"));
                System.out.println();
            }
        }
        indexreader.close();
    }
}
