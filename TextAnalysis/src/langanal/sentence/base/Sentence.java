package langanal.sentence.base;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import edu.stanford.nlp.*;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.*;
import langanal.word.base.Word;
import langanal.word.base.WordInfo;


public class Sentence {
	LinkedList<Clause> clauses = new LinkedList<>();
	LinkedList<LinkedList<Word>> words = new LinkedList<>();

	Sentence(String str){
		CoreMap cm = SentenceParser.getCoreMap(str);
		List<CoreLabel> tokens = cm.get(TokensAnnotation.class);
		for(CoreLabel token : tokens){
			LinkedList<Word> dictWords = WordInfo.getFullInfoWords(token.get(TextAnnotation.class));
			words.addLast(new LinkedList<>());
			String posTag = token.get(PartOfSpeechAnnotation.class);
			String pos = "";
			if(posTag.length()>=2){
				posTag = posTag.substring(0,2);
			}
			switch(posTag){
			case "JJ":
				pos = "adjective";
				break;
			case "MD":
				pos = "verb";
				break;
			case "NN":
				pos = "noun";
				break;
			case "RB":
				pos = "adverb";
				break;
			case "VB":
				pos = "verb";
				break;
			case "CC":
				pos = "conjunction";
				break;
			case "DT":
				pos = "determiner";
				break;
			case "CD":
				pos = "number";
				break;
			case "PR":
				pos = "pronoun";
				break;
			}
			
			for(Word w : dictWords){
				if(w.getPOS().equals(pos)){
					words.getLast().addLast(w);
				}
			}
			
			if(words.getLast().size()==0){
				for(Word w : dictWords){
					words.getLast().addLast(w);
				}
			}
		}
		for(LinkedList<Word> l: words){
			for(Word w: l){
				System.out.println(w.getValue()+ " " + words.indexOf(l));
			}
		}
		
	}
}
