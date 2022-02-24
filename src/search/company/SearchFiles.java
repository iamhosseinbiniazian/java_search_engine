package search.company;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.PrintWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.AxiomaticF2EXP;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import java.util.*;

/** Simple command-line based search demo. */
public class SearchFiles {


    public static String outputs = "D:\\IdeaProject\\JavaSearchEngine\\src\\search\\company\\cran\\outputs.txt";
    private SearchFiles() {}

    public static void main(String[] args) throws Exception {
        String index = "index";
        String queries = "D:\\IdeaProject\\JavaSearchEngine\\src\\search\\company\\cran\\cran.qry";
        String queryString = null;
        int hitsPerPage = 10;
        int scoringType = 4;


        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        if(scoringType == 0) searcher.setSimilarity(new BooleanSimilarity());
        if(scoringType == 1) searcher.setSimilarity(new ClassicSimilarity());
        if(scoringType == 2) searcher.setSimilarity(new BM25Similarity());
        if(scoringType == 3) searcher.setSimilarity(new AxiomaticF2EXP());
        if(scoringType == 4) searcher.setSimilarity(new LMDirichletSimilarity());

        Analyzer analyzer = new DylansAnalyzer();
        BufferedReader in = null;
        if (queries != null) {
            in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
        } else {
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
        HashMap<String, Float> boostedScores = new HashMap<String, Float>();
        boostedScores.put("Title", 0.65f);
        boostedScores.put("Author", 0.04f);
        boostedScores.put("Bibliography", 0.02f);
        boostedScores.put("Words", 0.35f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser (
                new String[]{"Title", "Author", "Bibliography", "Words"},
                analyzer, boostedScores);

        String line=in.readLine();
        String nextLine ="";
        int queryNumber = 1;
        PrintWriter writer = new PrintWriter(outputs, "UTF-8");
        while (true) {
            if (queries == null && queryString == null) {                        // prompt the user
                System.out.println("Enter query: ");
            }
            if (line == null || line.length() == -1) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                break;
            }

            if( line.substring(0,2).equals(".I") ){
                line = in.readLine();
                if( line.equals(".W") ){
                    line = in.readLine();
                }
                nextLine = "";
                while( !line.substring(0,2).equals(".I") ){
                    nextLine = nextLine + " " + line;
                    line = in.readLine();
                    if( line == null ) break;
                }
            }

            Query query = parser.parse( QueryParser.escape( nextLine.trim() ) );

            doPagingSearch(queryNumber, in, searcher, query, hitsPerPage, queries == null && queryString == null, writer);
            queryNumber++;
            if (queryString != null) {
                break;
            }
        }
        writer.close();
        reader.close();
    }

    public static void doPagingSearch(int queryNumber, BufferedReader in, IndexSearcher searcher, Query query,
                                      int hitsPerPage, boolean interactive, PrintWriter writer) throws IOException {
        TopDocs results = searcher.search(query, hitsPerPage);
//        int numTotalHits = Math.toIntExact(results.totalHits);
//        results = searcher.search(query, numTotalHits);
        //System.out.println(numTotalHits +" "+ results.totalHits);
        ScoreDoc[] hits = results.scoreDocs;
//        System.out.println(numTotalHits + " total matching documents");
        int start = 0;
        int end =hitsPerPage;

        while (true) {
            end = Math.min(hits.length, start + hitsPerPage);
            for (int i = start; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                String path = doc.get("path");
                if (path != null) {
                    System.out.println(queryNumber + "\t0\t" + path.replace(".I ","") + "\t" +(i+1)+ "\t" + hits[i].score);
                    writer.println(queryNumber + "\t0\t" + path.replace(".I ","") + "\t" +(i+1)+ "\t" + hits[i].score+"\tEXP");
                }
            }
            if (!interactive || end == 0) {
                break;
            }
        }
    }
}