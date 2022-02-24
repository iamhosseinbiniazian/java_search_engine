package search.company;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.Arrays;
import java.util.List;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.PorterStemFilter;



public class DylansAnalyzerWithSteming extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer stdTokenizer = new StandardTokenizer();
        TokenStream NewTokenStream = new StandardFilter(stdTokenizer);
        NewTokenStream = new LowerCaseFilter(NewTokenStream);
        //from https://github.com/Yoast/YoastSEO.js/blob/develop/src/config/stopwords.js
        //stop filer needs to have a charArraySet argument of the stopwords
        //from https://stackoverflow.com/questions/31957986/how-to-combine-analyzer-instances-for-stop-word-removal-and-stemming-in-lucene
        NewTokenStream = new PorterStemFilter(NewTokenStream);
        return new TokenStreamComponents(stdTokenizer, NewTokenStream);
    }
}


