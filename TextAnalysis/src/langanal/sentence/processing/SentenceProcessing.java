package langanal.sentence.processing;

import langanal.sentence.base.NounPhrase;
import langanal.sentence.base.Sentence;
import langanal.sentence.base.VerbPhrase;
import langanal.word.base.Word;
import langanal.word.processing.WordProcessing;

import java.util.LinkedList;
import java.util.List;

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
		/*LinkedList<LinkedList<Word>> nouns1 = NPhToWord(sen1.getNouns(), sen1.getWords());
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
		}*/
		
		//float relevancy = nounRelevancy + nModRelevancy + verbRelevancy + vModRelevancy;
		 //relevancy = (float) (200*(1/(1+Math.pow(Math.E,-(relevancy/9.5)))-0.5));
		//relevancy = (float) Math.min(100,Math.pow(relevancy, 2)/25f);
		
		float noun = compareNounPhrases(sen1.getNouns(),sen2.getNouns());
		float verb = compareVerbPhrases(sen1.getVerbs(),sen2.getVerbs());
		return verb*0.35f + noun*.65f;
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

	private static float compareVerbPhrases(List<VerbPhrase> vl1, List<VerbPhrase> vl2){
		for(VerbPhrase vp1: vl1){
			for(VerbPhrase vp2: vl2){
				float value = 0;
				if(!vp1.getModifiers().isEmpty() && !vp2.getModifiers().isEmpty()){
					float modComparisons = 0;
					for(LinkedList<Word> mod1 : vp1.getModifiers()){
						for(LinkedList<Word> mod2 : vp2.getModifiers()){
							value += WordProcessing.compareWords(mod1,mod2);
							modComparisons++;
						}
					}
					value /= modComparisons;
					value *= 0.2f;
					value += 0.8f*WordProcessing.compareWords(vp1.getVerb(), vp2.getVerb());
				} else {
					value = WordProcessing.compareWords(vp1.getVerb(), vp2.getVerb());
				}
				
				vp1.addComparison(value);
				if(value>vp1.getBestComparison()){
					vp1.setBestComparison(value);
				}
				vp2.addComparison(value);
				if(value>vp2.getBestComparison()){
					vp2.setBestComparison(value);
				}
			}
		}
		float verbComparisonsAvg = 0;
		float verbTotalComparisons = 0;
		for(VerbPhrase vp1: vl1){
			if(vp1.getComparisons().size()>1){
				float comparisonsAvg = 0;
				for(Float f : vp1.getComparisons()){
					if(!f.equals(vp1.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				float percentWeight = vp1.getBestComparison()/100;
				comparisonsAvg /= vp1.getComparisons().size()-1;
				comparisonsAvg *= (1-percentWeight);
				comparisonsAvg += (percentWeight*vp1.getBestComparison());
				verbComparisonsAvg += comparisonsAvg;
				verbTotalComparisons++;
			} else {
				verbComparisonsAvg += vp1.getBestComparison();
				verbTotalComparisons++;
			}
		}
		
		for(VerbPhrase vp2: vl2){
			if(vp2.getComparisons().size()>1){
				float comparisonsAvg = 0;
				for(Float f : vp2.getComparisons()){
					if(!f.equals(vp2.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				float percentWeight = vp2.getBestComparison()/100;
				comparisonsAvg /= vp2.getComparisons().size()-1;
				comparisonsAvg *= (1-percentWeight);
				comparisonsAvg += (percentWeight*vp2.getBestComparison());
				verbComparisonsAvg += comparisonsAvg;
				verbTotalComparisons++;
			} else {
				verbComparisonsAvg += vp2.getBestComparison();
				verbTotalComparisons++;
			}
		}
		
		System.out.println("Verb Average " +verbComparisonsAvg/verbTotalComparisons);
		return verbComparisonsAvg/verbTotalComparisons;
	}
	
	private static float compareNounPhrases(List<NounPhrase> nl1, List<NounPhrase> nl2){
		for(NounPhrase np1: nl1){
			for(NounPhrase np2: nl2){
				float value = 0;
				if(!np1.getModifiers().isEmpty() && !np2.getModifiers().isEmpty()){
					float modComparisons = 0;
					for(LinkedList<Word> mod1 : np1.getModifiers()){
						for(LinkedList<Word> mod2 : np2.getModifiers()){
							value += WordProcessing.compareWords(mod1,mod2);
							modComparisons++;
						}
					}
					value /= modComparisons;
					value *= 0.2f;
					value += 0.8f*WordProcessing.compareWords(np1.getNoun(), np2.getNoun());
				} else {
					value = WordProcessing.compareWords(np1.getNoun(), np2.getNoun());
				}
				
				np1.addComparison(value);
				if(value>np1.getBestComparison()){
					np1.setBestComparison(value);
				}
				np2.addComparison(value);
				if(value>np2.getBestComparison()){
					np2.setBestComparison(value);
				}
			}
		}
		float nounComparisonsAvg = 0;
		float nounTotalComparisons = 0;
		for(NounPhrase np1: nl1){
			if(np1.getComparisons().size()>1){
				float comparisonsAvg = 0;
				for(Float f : np1.getComparisons()){
					if(!f.equals(np1.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				float percentWeight = np1.getBestComparison()/100;
				comparisonsAvg /= np1.getComparisons().size()-1;
				comparisonsAvg *= (1-percentWeight);
				comparisonsAvg += (percentWeight*np1.getBestComparison());
				nounComparisonsAvg += comparisonsAvg;
				nounTotalComparisons++;
			} else {
				nounComparisonsAvg += np1.getBestComparison();
				nounTotalComparisons++;
			}
		}
		
		for(NounPhrase np2: nl2){
			if(np2.getComparisons().size()>1){
				float comparisonsAvg = 0;
				for(Float f : np2.getComparisons()){
					if(!f.equals(np2.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				float percentWeight = np2.getBestComparison()/100;
				comparisonsAvg /= np2.getComparisons().size()-1;
				comparisonsAvg *= (1-percentWeight);
				comparisonsAvg += (percentWeight*np2.getBestComparison());
				nounComparisonsAvg += comparisonsAvg;
				nounTotalComparisons++;
			} else {
				nounComparisonsAvg += np2.getBestComparison();
				nounTotalComparisons++;
			}
		}
		
		System.out.println("Noun Average " +nounComparisonsAvg/nounTotalComparisons);
		return nounComparisonsAvg/nounTotalComparisons;
	}
}