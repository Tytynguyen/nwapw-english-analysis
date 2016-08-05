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

		TypedDependency[] dependencies = SentenceParser.getDependencies(cm);

		for(CoreLabel cl : tokens){
			IndexedWord indexedWord = new IndexedWord(cl);
			if(nlpPOStoPOS(indexedWord.get(PartOfSpeechAnnotation.class)).equals("verb")) {
				verbs.add(new VerbPhrase(indexedWord,new LinkedList<>()));
				LinkedList<TypedDependency> toCheck = new LinkedList<>();
				for(TypedDependency dependency : dependencies){
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						if(dependency.reln().toString().contains("mod")){
							verbs.getLast().addMod(dependency.dep());
							toCheck.add(dependency);
						}
					} 
				}
				while(!toCheck.isEmpty()){
					LinkedList<TypedDependency> addList = new LinkedList<>();
					for(TypedDependency check : toCheck){
						
						for(TypedDependency dependency : dependencies){
							if(check.dep().pseudoPosition() == dependency.gov().pseudoPosition()){
								if(dependency.reln().toString().startsWith("conj") || dependency.reln().toString().contains("mod")){
									verbs.getLast().addMod(dependency.dep());
									addList.add(dependency);
								}
							}
						}
					}
					toCheck.clear();
					toCheck.addAll(addList);
				}
			} else if(nlpPOStoPOS(indexedWord.get(PartOfSpeechAnnotation.class)).equals("noun")){
				nouns.add(new NounPhrase(indexedWord,new LinkedList<>()));
				LinkedList<TypedDependency> toCheck = new LinkedList<>();
				for(TypedDependency dependency : dependencies){
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						if(dependency.reln().toString().contains("mod")){
							nouns.getLast().addMod(dependency.dep());
							toCheck.add(dependency);
						}
					} 
				}
				while(!toCheck.isEmpty()){
					LinkedList<TypedDependency> addList = new LinkedList<>();
					for(TypedDependency check : toCheck){
						for(TypedDependency dependency : dependencies){
							if(check.dep().pseudoPosition() == dependency.gov().pseudoPosition()){
								if(dependency.reln().toString().startsWith("conj") || dependency.reln().toString().contains("mod")){
									nouns.getLast().addMod(dependency.dep());
									addList.add(dependency);
								}
							}
						}
					}
					toCheck.clear();
					toCheck.addAll(addList);
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
