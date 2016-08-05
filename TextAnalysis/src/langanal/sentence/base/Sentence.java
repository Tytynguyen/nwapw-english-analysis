package langanal.sentence.base;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import langanal.word.base.Word;
import langanal.word.base.WordInfo;

import java.util.LinkedList;
import java.util.List;


public class Sentence {
	LinkedList<NounPhrase> nouns = new LinkedList<NounPhrase>();
	LinkedList<VerbPhrase> verbs = new LinkedList<VerbPhrase>();
	LinkedList<LinkedList<Word>> words = new LinkedList<>();//why is this data type?

	public LinkedList<NounPhrase> getNouns(){return nouns;};
	public LinkedList<VerbPhrase> getVerbs(){return verbs;};
	public LinkedList<LinkedList<Word>> getWords(){return words;}

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
