package search.company;

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


//http://web.cecs.pdx.edu/~maier/cs510iri/HW2010/Project_I_answer_key.pdf

public class DylanAnalyzerWithoutStopWord extends Analyzer{
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer stdTokenizer = new StandardTokenizer();
        TokenStream NewTokenStream = new StandardFilter(stdTokenizer);
        NewTokenStream = new LowerCaseFilter(NewTokenStream);
        return new TokenStreamComponents(stdTokenizer, NewTokenStream);
    }
}
