package langanal.sentence.processing;

import langanal.sentence.base.Sentence;
import langanal.word.base.Word;
import langanal.word.processing.WordProcessing;

import java.util.LinkedList;

import static langanal.sentence.processing.NounPhraseProcessing.ModToWord;
import static langanal.sentence.processing.NounPhraseProcessing.NPhToWord;
import static langanal.sentence.processing.VerbPhraseProcessing.ModToWord;
import static langanal.sentence.processing.VerbPhraseProcessing.VPhToWord;

/**
 * Created by SteinJac.ao on 8/4/2016.
 */
public class SentenceProcessing {


	/**
	 * @param sen1, sentence input
	 * @param sen2, "            "
	 * @return senRelevancy given, the relativity of the two sentences
	 */
	public static float calcRelevancy(Sentence sen1, Sentence sen2){
		//creates all the words as Word classes, stored in the appropriate lists
		LinkedList<LinkedList<Word>> nouns1 = NPhToWord(sen1.getNouns(), sen1.getWords());
		LinkedList<LinkedList<LinkedList<Word>>> nMods1 = ModToWord(sen1.getNouns(),sen1.getWords());
		LinkedList<LinkedList<Word>> verbs1 = VPhToWord(sen1.getVerbs(),sen1.getWords());
		LinkedList<LinkedList<LinkedList<Word>>> vMods1 = ModToWord(sen1.getVerbs(),sen1.getWords());

		LinkedList<LinkedList<Word>> nouns2 = NPhToWord(sen2.getNouns(), sen2.getWords());
		LinkedList<LinkedList<LinkedList<Word>>> nMods2 = ModToWord(sen2.getNouns(), sen2.getWords());
		LinkedList<LinkedList<Word>> verbs2 = VPhToWord(sen2.getVerbs(),sen2.getWords());
		LinkedList<LinkedList<LinkedList<Word>>> vMods2 = ModToWord(sen2.getVerbs(),sen2.getWords());

		//each average is wieghted to add up to a whole
		float nounRelevancy;
		float nModRelevancy;
		float verbRelevancy;
		float vModRelevancy;
		
		if(!nMods1.isEmpty() && !nMods2.isEmpty()){
			nounRelevancy = 0.4f*calcPhRelevancy(nouns1,nouns2);
			nModRelevancy = 0.1f*calcModRelevancy(nMods1,nMods2);
		} else {
			nounRelevancy = 0.5f*calcPhRelevancy(nouns1,nouns2);
			nModRelevancy = 0;
		}
		
		if(!vMods1.isEmpty() && !vMods2.isEmpty()){
			verbRelevancy = 0.4f*calcPhRelevancy(verbs1,verbs2);
			vModRelevancy = 0.1f*calcModRelevancy(vMods1,vMods2);
		} else {
			verbRelevancy = 0.5f*calcPhRelevancy(verbs1,verbs2);
			vModRelevancy = 0;
		}
		
		float relevancy = nounRelevancy + nModRelevancy + verbRelevancy + vModRelevancy;
		relevancy = relevancy = (float) (200*(1/(1+Math.pow(Math.E,-(relevancy/9.5)))-0.5));
		
		return relevancy;
	}



	/**
	 *
	 * @param phraseList1, a converted Verb or Noun Phrase is taken in
	 * @param phraseList2, a converted Verb or Noun Phrase is taken in
	 *                     These must be entered from the same orgin (ie a converted
	 *                     VerbPhrase should go with a converted VerbPhrase)
	 * @return phraseRelevancy is each relevancy computed averaged
	 */
	private static float calcPhRelevancy(LinkedList<LinkedList<Word>> phraseList1, LinkedList<LinkedList<Word>> phraseList2){
		float phraseRelevancy = 0;
		float relevancyCounter = 0;
		for(LinkedList<Word> w1: phraseList1){
			for(LinkedList<Word> w2: phraseList2){
				if(!w1.isEmpty() && !w2.isEmpty()){
					phraseRelevancy += WordProcessing.compareWords(w1, w2);
					relevancyCounter ++;
				}
			}
		}
		if(phraseRelevancy!=0){
			return phraseRelevancy/relevancyCounter;
		} else {
			return 0;
		}
	}


	private static float calcModRelevancy(LinkedList<LinkedList<LinkedList<Word>>> modList1, LinkedList<LinkedList<LinkedList<Word>>> modList2){
		float modRelevancy = 0;
		float relevancyCounter = 0;
		for(LinkedList<LinkedList<Word>> mod1: modList1){
			for(LinkedList<LinkedList<Word>> mod2: modList2){
				if(!mod1.isEmpty() && !mod2.isEmpty()){
					modRelevancy += calcPhRelevancy(mod1, mod2);
					relevancyCounter ++;
				}
			}
		}
		if(modRelevancy!=0){
			return modRelevancy/relevancyCounter;
		} else {
			return 0;
		}
	}

}