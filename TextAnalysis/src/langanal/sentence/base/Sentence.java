package langanal.sentence.base;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import langanal.word.base.Word;
import langanal.word.base.WordInfo;

import java.util.LinkedList;
import java.util.List;


public class Sentence {
	LinkedList<NounPhrase> nouns = new LinkedList<>();
	LinkedList<VerbPhrase> verbs = new LinkedList<>();
	LinkedList<LinkedList<Word>> words = new LinkedList<>();
	
	public LinkedList<NounPhrase> getNouns(){return nouns;};
	public LinkedList<VerbPhrase> getVerbs(){return verbs;};
	public LinkedList<LinkedList<Word>> getWords(){return words;}

	/*
	 * Creates a new Sentence with all data filled in for comparison with other sentences
	 * @param String inputted to create a sentence of
	 */
	public Sentence(String str){
		//gets nlp CoreMap of sentence, includes all nlp info
		CoreMap cm = SentenceParser.getCoreMap(str);
		//seperate tagged words
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

		//get sentence dependencies for grammatical relations
		TypedDependency[] dependencies = SentenceParser.getDependencies(cm);

		//loop through all words
		for(CoreLabel cl : tokens){
			IndexedWord indexedWord = new IndexedWord(cl);
			
			//find verbs
			if(nlpPOStoPOS(indexedWord.get(PartOfSpeechAnnotation.class)).equals("verb")){
				//add to verb list
				verbs.add(new VerbPhrase(indexedWord,new LinkedList<>(),this));
				LinkedList<TypedDependency> toCheck = new LinkedList<>();
				
				for(TypedDependency dependency : dependencies){
					//if word is same as word with dependency and dependency is a modifier, adds dependency word to modifiers
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						if(dependency.reln().toString().contains("mod")){
							verbs.getLast().addMod(dependency.dep());
							toCheck.add(dependency);
						}
					} 
				}
				//checks all modifiers for modifiers
				while(!toCheck.isEmpty()){
					LinkedList<TypedDependency> addList = new LinkedList<>();
					for(TypedDependency check : toCheck){
						for(TypedDependency dependency : dependencies){
							//if finds modifiers of modifiers adds to word's modifiers and adds to check list for it to be checked
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
				//add to noun list
				nouns.add(new NounPhrase(indexedWord,new LinkedList<>(),this));
				LinkedList<TypedDependency> toCheck = new LinkedList<>();
				
				for(TypedDependency dependency : dependencies){
					//if word is same as word with dependency and dependency is a modifier, adds dependency word to modifiers
					if(indexedWord.pseudoPosition() == dependency.gov().pseudoPosition()){
						if(dependency.reln().toString().contains("mod")){
							nouns.getLast().addMod(dependency.dep());
							toCheck.add(dependency);
						}
					} 
				}
				//checks all modifiers for modifiers
				while(!toCheck.isEmpty()){
					LinkedList<TypedDependency> addList = new LinkedList<>();
					for(TypedDependency check : toCheck){
						for(TypedDependency dependency : dependencies){
							//if finds modifiers of modifiers adds to word's modifiers and adds to check list for it to be checked
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

	/*
	 * Translates nlpPOS tag to POS tag that can be used in dictionary api
	 * @param nlpPOS tag to translate
	 * @param Translated POS tag
	 */
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
