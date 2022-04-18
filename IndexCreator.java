package com.example;

import java.io.FileReader;
import org.json.JSONObject;
import org.junit.Test;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;


public class IndexCreator {
    @Test
    public void createIndex() throws Exception {
        String file = "src\\merge_test.json";
//        String js = FileUtils.readFileToString(new File(file),"UTF8");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        List<Document> docList = new ArrayList();

        while(sc.hasNextLine()){
            try{
                Document doc = new Document();

                String inline = sc.nextLine().trim();
                JSONObject jObj = new JSONObject(inline);

//                String bid = jObj.getString("business_id");
                String name = jObj.getString("name");
                String address = jObj.getString("address");
                String city = jObj.getString("city");
                String state = jObj.getString("state");
//                String attributes = jObj.getString("attributes");
                String postal = jObj.getString("postal_code");
                Double latitude = jObj.getDouble("latitude");
                Double longitude = jObj.getDouble("longitude");
                Double stars = jObj.getDouble("stars");
                String categories = jObj.getString("categories");
                String tip = jObj.getString("tip");
                String review = jObj.getString("review");


                doc.add(new StringField("postal", postal, Field.Store.YES));
                doc.add(new StringField("city", city, Field.Store.YES));
                doc.add(new StringField("state", state, Field.Store.YES));

                doc.add(new LatLonPoint("latlon", latitude, longitude));

                doc.add(new DoublePoint("latitude", latitude));
                doc.add(new StoredField("latitude", latitude));

                doc.add(new DoublePoint("longitude", longitude));
                doc.add(new StoredField("longitude", longitude));

                doc.add(new DoublePoint("stars", stars));
                doc.add(new StoredField("stars", stars));

                doc.add(new TextField("name", name, Field.Store.YES));
                doc.add(new TextField("categories", categories, Field.Store.YES));
                doc.add(new TextField("address", address, Field.Store.YES));
                doc.add(new TextField("tip", tip, Field.Store.YES));
                doc.add(new TextField("review", review, Field.Store.YES));

                docList.add(doc);

           }catch(Exception e){}
        }

        Analyzer analyzer = new StandardAnalyzer();

        Directory dir = FSDirectory.open(Paths.get("E:\\Projects\\Information_retrieval\\final_project\\LuceneProject\\LuceneDir"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, config);
        for (Document document : docList) {
            indexWriter.addDocument(document);
        }

        indexWriter.close();

    }
}
