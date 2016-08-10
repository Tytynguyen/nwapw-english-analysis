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
		float noun = compareNounPhrases(sen1.getNouns(),sen2.getNouns());
		float verb = compareVerbPhrases(sen1.getVerbs(),sen2.getVerbs());
		return verb*0.35f + noun*.65f;
	}
	

	/**
	 * @param verbList1
	 * @param verbList2
	 * @return relevancy between two lists of verbs
	 * Takes in two lists of VerbPhrases and returns the relevancy between them
	 */
	private static float compareVerbPhrases(List<VerbPhrase> vl1, List<VerbPhrase> vl2){
		//loops through all phrases
		for(VerbPhrase vp1: vl1){
			for(VerbPhrase vp2: vl2){
				float value = 0; //relevancy value
				//if has modifiers, compares
				if(!vp1.getModifiers().isEmpty() && !vp2.getModifiers().isEmpty()){
					float modComparisons = 0;
					//calculates modifier relevancy
					for(LinkedList<Word> mod1 : vp1.getModifiers()){
						for(LinkedList<Word> mod2 : vp2.getModifiers()){
							value += WordProcessing.compareWords(mod1,mod2);
							modComparisons++;
						}
					}
					value /= modComparisons;
					value *= 0.2f; //modifiers weighted 20%
					value += 0.8f*WordProcessing.compareWords(vp1.getVerb(), vp2.getVerb()); // word wieghted 80%
				} else {
					//if no modifiers word weighted 100%
					value = WordProcessing.compareWords(vp1.getVerb(), vp2.getVerb());
				}
				//record comparison values
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
		//calculates relevancy based of recorded comparisons
		float verbComparisonsAvg = 0;
		float verbTotalComparisons = 0;
		//loops through all comparisons in first list
		for(VerbPhrase vp1: vl1){
			if(vp1.getComparisons().size()>1){
				float comparisonsAvg = 0;
				//finds average of all but best comparison
				for(Float f : vp1.getComparisons()){
					if(!f.equals(vp1.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				//calculates comparison for this phrase with best comparison weighted based on its own value
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
		//does same for second list
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
	
	
	/**
	 * @param nounList1
	 * @param nounList2
	 * @return relevancy between two lists of nouns
	 * Takes in two lists of VerbPhrases and returns the relevancy between them
	 */
	private static float compareNounPhrases(List<NounPhrase> nl1, List<NounPhrase> nl2){
		//loops through all phrases
		for(NounPhrase np1: nl1){
			for(NounPhrase np2: nl2){
				float value = 0;//relevancy value
				//if has modifiers, compares
				if(!np1.getModifiers().isEmpty() && !np2.getModifiers().isEmpty()){
					float modComparisons = 0;
					//calculates modifier relevancy
					for(LinkedList<Word> mod1 : np1.getModifiers()){
						for(LinkedList<Word> mod2 : np2.getModifiers()){
							value += WordProcessing.compareWords(mod1,mod2);
							modComparisons++;
						}
					}
					value /= modComparisons;
					value *= 0.2f;//modifiers weighted 20%
					value += 0.8f*WordProcessing.compareWords(np1.getNoun(), np2.getNoun());//word weighted 80%
				} else {
					//if no modifiers word weighted 100%
					value = WordProcessing.compareWords(np1.getNoun(), np2.getNoun());
				}
				//records comparison values
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
		//calculates relevancy based of recorded comparisons
		float nounComparisonsAvg = 0;
		float nounTotalComparisons = 0;
		//loops through first list
		for(NounPhrase np1: nl1){
			if(np1.getComparisons().size()>1){
				//finds average of all but best comparison
				float comparisonsAvg = 0;
				for(Float f : np1.getComparisons()){
					if(!f.equals(np1.getBestComparison())){
						comparisonsAvg += f;
					}
				}
				//calculates comparison for this phrase with best comparison weighted based on its own value
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
		//does same for second list
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