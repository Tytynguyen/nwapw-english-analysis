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
	LinkedList<NounPhrase> nouns = new LinkedList<>();
	LinkedList<VerbPhrase> verbs = new LinkedList<>();
	LinkedList<LinkedList<Word>> words = new LinkedList<>();

	/*
	 * Creates a new Sentence with all data filled in for comparison with other sentences
	 * @param String inputted to create a sentence of
	 */
	Sentence(String str){
		//gets nlp CoreMap of sentence, includes all nlp info
		CoreMap cm = SentenceParser.getCoreMap(str);
		List<CoreLabel> tokens = cm.get(TokensAnnotation.class);

		//loops through all CoreLabels (nlp tagged words)
		for(CoreLabel token : tokens){
			//gets dictionary info of word
			LinkedList<Word> dictWords = WordInfo.getFullInfoWords(token.get(TextAnnotation.class));

			words.addLast(new LinkedList<>());
			//finds nlp pos tag of token
			String posTag = token.get(PartOfSpeechAnnotation.class);
			//translates to dictionary
			String pos = nlpPOStoPOS(posTag);

			//loops through dictionary entries, finds ones with same pos and keeps them in words list
			for(Word w : dictWords){
				if(w.getPOS().equals(pos)){
					words.getLast().addLast(w);
				}
			}

			//if none have same pos, puts all entries in words list
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

		// get parse tree of sentence
		Tree tree = cm.get(TreeAnnotation.class);
		// Get dependency tree
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		Object[] temp = td.toArray();
		TypedDependency[]  dependencies = new TypedDependency[temp.length];
		int index = 0;
		for(Object o : temp){
			dependencies[index] = (TypedDependency) o;
			System.out.println(dependencies[index]);
			index++;
		}
		
		for(CoreLabel cl : tokens){
			IndexedWord indexedWord = new IndexedWord(cl);
			if(nlpPOStoPOS(indexedWord.get(PartOfSpeechAnnotation.class)).equals("verb")) {
				verbs.add(new VerbPhrase(indexedWord,new LinkedList<>()));
				for(TypedDependency dependency : dependencies){
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						
					} else if(indexedWord.pseudoPosition() == dependency.dep().pseudoPosition()){
						
					}
				}
			} else if(nlpPOStoPOS(indexedWord.get(PartOfSpeechAnnotation.class)).equals("noun")){
				for(TypedDependency dependency : dependencies){
					nouns.add(new NounPhrase(indexedWord,new LinkedList<>()));
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						
					} else if(indexedWord.pseudoPosition() == dependency.dep().pseudoPosition()){
						
					}
				}
			}
		}
	}

	private static String nlpPOStoPOS(String nlpPOS){
		//delete extra modifier of pos tag
		if(nlpPOS.length()>=2){
			nlpPOS = nlpPOS.substring(0,2);
		}

		//translate nlp pos tag to dictionary pos
		String pos = "";
		switch(nlpPOS){
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
		case "IN":
			pos = "preposition";
			break;
		}
		//return dictionary pos
		return pos;
	}
}
