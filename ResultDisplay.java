package com.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "ResultDisplay", value = "/result-display")
public class ResultDisplay extends HttpServlet {

    String postName;
    String postCity;
    String postCategories;
    String postTip;
    String postReview;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // load index
        Directory dir = FSDirectory.open(new File("E:\\Projects\\Information_retrieval\\final_project\\LuceneProject\\LuceneDir").toPath());
        IndexReader indexreader = DirectoryReader.open(dir);

        // comparison between two retrieval models
        IndexSearcher indexSearcher_1 = new IndexSearcher(indexreader);
        indexSearcher_1.setSimilarity(new BM25Similarity(1.2f,0.75f));

        IndexSearcher indexSearcher_2 = new IndexSearcher(indexreader);
        indexSearcher_2.setSimilarity(new LMDirichletSimilarity());

        String q = "categories:\"Tobacco Shops\" AND Shopping";
        Analyzer analyzer = new StandardAnalyzer();
        String[] fields = {"name","categories","tip","review"};

        MultiFieldQueryParser multiQueryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query_1 = null;
        try {
            query_1 = multiQueryParser.parse(q);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for search in specific city
        Query query_2 = new PhraseQuery.Builder()
                .add(new Term("city", "Glen Mills")).build();

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query_1, BooleanClause.Occur.MUST);
        booleanQuery.add(query_2, BooleanClause.Occur.MUST);


        // highlighter
        Formatter formatter = new SimpleHTMLFormatter("<font color='red'><strong>","</strong></font>");
//        Scorer fragmentscorer = new QueryScorer(booleanQuery.build());
        Scorer fragmentscorer = new QueryScorer(query_1);
        Highlighter highlighter = new Highlighter(formatter, fragmentscorer);
        // display the most relevant part with maximum number of preset words
        Fragmenter fragmenter = new SimpleFragmenter(500);
        highlighter.setTextFragmenter(fragmenter);


        PrintWriter out = response.getWriter();
        out.println("<h3><center><strong>" + "Query: " + "\"" + q + "\"" + "</strong></center></h3>");
        out.println("<br>");
        out.println("<div style=\"display:block; width:100%;\">");
        out.println("<div style=\"width:50%; float: left; display: inline-block;\"><center>");
        out.println("<h3>" + "BM25" + "</h3>");
        out.println("</center></div>");
        out.println("<div style=\"display:block; width:100%;\">");
        out.println("<div style=\"width:50%; float: right; display: inline-block;\"><center>");
        out.println("<h3>" + "LM(Dirichlet)" + "</h3>");
        out.println("</center></div>");
        out.println("</div>");


        ArrayList<ArrayList<String>> model_bm25 = null;
        ArrayList<ArrayList<String>> model_lm = null;

        try {
//            model_bm25 = modelResult(analyzer,indexSearcher_1,booleanQuery.build(),highlighter);
            model_bm25 = modelResult(analyzer,indexSearcher_1,query_1,highlighter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            model_lm = modelResult(analyzer,indexSearcher_2,booleanQuery.build(),highlighter);
            model_lm = modelResult(analyzer,indexSearcher_2,query_1,highlighter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        int n = 0;
        for (int i = 0; i < 10; i++) {

            n+=1;
            ArrayList<String> bm25_score = model_bm25.get(0);
            ArrayList<String> bm25_name = model_bm25.get(1);
            ArrayList<String> bm25_city = model_bm25.get(2);
            ArrayList<String> bm25_stars = model_bm25.get(3);
            ArrayList<String> bm25_categories = model_bm25.get(4);
            ArrayList<String> bm25_tip = model_bm25.get(5);
            ArrayList<String> bm25_review = model_bm25.get(6);

            ArrayList<String> lm_score = model_lm.get(0);
            ArrayList<String> lm_name = model_lm.get(1);
            ArrayList<String> lm_city = model_lm.get(2);
            ArrayList<String> lm_stars = model_lm.get(3);
            ArrayList<String> lm_categories = model_lm.get(4);
            ArrayList<String> lm_tip = model_lm.get(5);
            ArrayList<String> lm_review = model_lm.get(6);


//            out.println("<div style=\"display:block; width:100%;\">");
//            out.println("<div style=\"width:47%; float: left; padding-right:2%; display: inline-block;\">");
//            out.println("<p>" + "Ranking: " + n + "</p>");
//            out.println("<p>" + "Ranking Score: " + bm25_score.get(i) + "</p>");
//            out.println("<p>" + "Name: " + bm25_name.get(i) + "</p>");
//            out.println("<p>" + "Stars: " + bm25_stars.get(i) + "</p>");
//            out.println("<p>" + "Categories: " + bm25_categories.get(i) + "</p>");
//            out.println("<p align='justify'>" + "Tip: " + bm25_tip.get(i) + "</p>");
//            out.println("<p align='justify'>" + "Review: " + bm25_review.get(i) + "</p>");
//            out.println("<br>");
//            out.println("</div>");
//
//            out.println("<div style=\"width:47%; float: right; padding-right:2%; display: inline-block;\">");
//            out.println("<p>" + "Ranking: " + n + "</p>");
//            out.println("<p>" + "Ranking Score: " + lm_score.get(i) + "</p>");
//            out.println("<p>" + "Name: " + lm_name.get(i) + "</p>");
//            out.println("<p>" + "Stars: " + lm_stars.get(i) + "</p>");
//            out.println("<p>" + "Categories: " + lm_categories.get(i) + "</p>");
//            out.println("<p align='justify'>" + "Tip: " + lm_tip.get(i) + "</p>");
//            out.println("<p align='justify'>" + "Review: " + lm_review.get(i) + "</p>");
//            out.println("<br>");
//            out.println("</div>");
//            out.println("</div>");

//            out.println("<table style=\"width:100%\"><tr>");
//            out.println("<th style=\"width:50%\">" + "BM25" + "</th>");
//            out.println("<th>" + "LM" + "</th>");
//            out.println("</tr>");

            out.println("<table style=\"width:100%\", CELLPADDING=10>");
            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Ranking: " + n + "</td>");
            out.println("<td align='justify'>" + "Ranking: " + n + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Ranking Score: " + bm25_score.get(i) + "</td>");
            out.println("<td align='justify'>" + "Ranking Score: " + lm_score.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Name: " + bm25_name.get(i) + "</td>");
            out.println("<td align='justify'>" + "Name: " + lm_name.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "City: " + bm25_city.get(i) + "</td>");
            out.println("<td align='justify'>" + "City: " + lm_city.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Stars: " + bm25_stars.get(i) + "</td>");
            out.println("<td align='justify'>" + "Stars: " + lm_stars.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Categories: " + bm25_categories.get(i) + "</td>");
            out.println("<td align='justify'>" + "Categories: " + lm_categories.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Tip: " + bm25_tip.get(i) + "</td>");
            out.println("<td align='justify'>" + "Tip: " + lm_tip.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align='justify', style=\"width:50%\">" + "Review: " + bm25_review.get(i) + "</td>");
            out.println("<td align='justify'>" + "Review: " + lm_review.get(i) + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<br>");;
            out.println("<br>");;
            out.println("</tr>");
            out.println("</table>");
        }

        indexreader.close();
    }

    public ArrayList<ArrayList<String>> modelResult(Analyzer analyzer, IndexSearcher indexSearcher, Query query, Highlighter highlighter) throws Exception{

        ArrayList<String> list_score = new ArrayList<String>();
        ArrayList<String> list_name = new ArrayList<String>();
        ArrayList<String> list_city = new ArrayList<String>();
        ArrayList<String> list_stars = new ArrayList<String>();
        ArrayList<String> list_categories = new ArrayList<String>();
        ArrayList<String> list_tip = new ArrayList<String>();
        ArrayList<String> list_review = new ArrayList<String>();

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        if (scoreDocs != null) {
            for (ScoreDoc scoreDoc : scoreDocs) {
                //获取文档的唯一ID
                int DocID = scoreDoc.doc;
                //根据文档ID获取文档内容
                Document doc = indexSearcher.doc(DocID);
                float score = scoreDoc.score;

                postName = highlighter.getBestFragment(analyzer, "name:", doc.get("name"));
                if (postName == null){
                    postName = doc.get("name");
                }
                postCity = highlighter.getBestFragment(analyzer, "city:", doc.get("city"));
                if (postCity == null){
                    postCity = doc.get("city");
                }
                postCategories = highlighter.getBestFragment(analyzer, "categories:", doc.get("categories"));
                if (postCategories == null){
                    postCategories = doc.get("categories");
                }
                postTip = highlighter.getBestFragment(analyzer, "tip:", doc.get("tip"));
                if (postTip == null){
                    postTip = doc.get("tip");
                }
                postReview = highlighter.getBestFragment(analyzer, "review:", doc.get("review"));
                if (postReview == null){
                    postReview = doc.get("review");
                }

                list_score.add(Float.toString(score));
                list_name.add(postName);
                list_city.add(postCity);
                list_stars.add(doc.get("stars"));
                list_categories.add(postCategories);
                list_tip.add(postTip);
                list_review.add(postReview);
            }
        }

        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(list_score);
        arrayList.add(list_name);
        arrayList.add(list_city);
        arrayList.add(list_stars);
        arrayList.add(list_categories);
        arrayList.add(list_tip);
        arrayList.add(list_review);

        return arrayList;
    }


    public void destroy() {
    }
}