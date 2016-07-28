package langanal.word.processing;

import java.util.ArrayList;

import langanal.word.base.Word;

public class WordProcessing {

	/**
	 * Counts the repetition of words in each definition
	 * 
	 * @param word1
	 * @param word2
	 * @return The number of repetitions
	 */
	public static int checkDefinition(Word word1, Word word2){
		ArrayList<String> definitionWords1 = new ArrayList<String>();
		ArrayList<String> definitionWords2 = new ArrayList<String>();

		int definitionRepeats = 0;

		//Fills definitionWords1
		for(String fullDefW1 : word1.getDefinitions()){
			ArrayList<String> defWordList1 = definitionToWords(fullDefW1);
			for(String curWord : defWordList1){
				definitionWords1.add(curWord);
			}
		}

		System.out.println("Words 1: " + definitionWords1);
		//Fills definitionWords2
		for(String fullDefW2 : word2.getDefinitions()){
			ArrayList<String> defWordList2 = definitionToWords(fullDefW2);

			for(String curWord : defWordList2){
				definitionWords2.add(curWord);
			}
		}
		System.out.println("Words 2: " + definitionWords2);

		//Checks for repetitions

		for(String persistWord : definitionWords1){
			for(String checkingWord : definitionWords2){
				if(persistWord.equals(checkingWord)){
					definitionRepeats++;
				}
			}
		}

		return definitionRepeats;
	}

	/**
	 * Returns an ArrayList of words that are formatted for checkDefinition()
	 * @param definition
	 * @return
	 */
	private static ArrayList<String> definitionToWords(String definition){
		ArrayList<String> returnList = new ArrayList<String>();

		for(String word : definition.replaceAll("[^a-zA-Z ]", "").toLowerCase().trim().split(" ")){
			returnList.add(word.trim());
		}
		return returnList;
	}

	/**
	 * Checks to see if the POS is the same between two words
	 * 
	 * @param word1
	 * @param word2
	 * @return 1 if they share the same POS, 0 if they do not share the same POS
	 */
	public static int checkPOS(Word word1, Word word2){
		if(word1.getPOS().equals(word2.getPOS())){
			return 1;
		}else{
			return 0;
		}
	}

	/**
	 * Checks to see if two words share any synonyms
	 * @param word1
	 * @param word2
	 * @return The # of shared synonyms
	 */
	public static int checkSynonyms(Word word1, Word word2){
		ArrayList<String> synonymsWord1 = new ArrayList<String>(word1.getSynonyms().size());
		ArrayList<String> synonymsWord2 = new ArrayList<String>(word2.getSynonyms().size());

		int repeatedSynonyms = 0; //holds the # of repeated pairs of similarity words

		for(String s : word1.getSynonyms()){
			synonymsWord1.add(s);
		}
		for(String s : word2.getSynonyms()){
			synonymsWord2.add(s);
		}

		for(String curSynonymWord1 : synonymsWord1){
			for(String curSynonymWord2 : synonymsWord2) {
				if(curSynonymWord1.equals(curSynonymWord2)){
					repeatedSynonyms++;
				}
			}
		}

		return repeatedSynonyms;
	}

	/**
	 * Checks to see if two words share any antonyms
	 * @param word1
	 * @param word2
	 * @return The # of shared antonyms
	 */
	public static int checkAntonyms(Word word1, Word word2){
		ArrayList<String> antonymsWord1 = new ArrayList<String>(word1.getAntonyms().size());
		ArrayList<String> antonymsWord2 = new ArrayList<String>(word2.getAntonyms().size());

		int repeatedAntonyms = 0; //holds the # of repeated pairs of similarity words

		for(String s : word1.getAntonyms()){
			antonymsWord1.add(s);
		}
		for(String s : word2.getAntonyms()){
			antonymsWord2.add(s);
		}

		for(String curWord1 : antonymsWord1){
			for(String curWord2 : antonymsWord2) {
				if(curWord1.equals(curWord2)){
					repeatedAntonyms++;
				}
			}
		}

		return repeatedAntonyms;
	}
}
