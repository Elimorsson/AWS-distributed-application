import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;
import java.util.Vector;


public class NlpHandler {
    private StanfordCoreNLP NERPipeline;

    public NlpHandler() {
        Properties props1 = new Properties();
        props1.put("annotators", "tokenize , ssplit, pos, lemma, ner");
        NERPipeline =  new StanfordCoreNLP(props1, false);
    }


  Vector<String> getEntities(String review){
    // create an empty Annotation just with the given text
    Annotation document = new Annotation(review);
    Vector<String> ans = new Vector<>();
    // run all Annotators on this text
    NERPipeline.annotate(document);

    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    for(CoreMap sentence: sentences) {
        // traversing the words in the current sentence
        // a CoreLabel is a CoreMap with additional token-specific methods
        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            // this is the text of the token
            String word = token.get(TextAnnotation.class);
            // this is the NER label of the token
            String ne = token.get(NamedEntityTagAnnotation.class);
            ans.add(word);
            ans.add(ne);
        }
    }
    return ans;
}
}
