package langanal.word.processing;

import langanal.word.base.Word;

import java.util.ArrayList;
import java.util.LinkedList;

public class WordProcessing {
	//Weights
	public static float definitionWeight = .5f;
	public static float POSWeight = 0.2f;
	public static float synonymWeight = 1;
	public static float antonymWeight = 1;

	private static String[] commonWords = 
			new String[]{
					"the", "be", "to", "of", "and", "a", "in","that", "have", "i","it","for","not","not","on","with",
					"or", "is"
	};

	/**
	 * Compares, processes, and returns the relevancy of the two words
	 * 
	 * @param allWord1
	 * @param allWord2
	 * @return The % relevancy
	 */
	public static float compareWords(LinkedList<Word> allWord1, LinkedList<Word> allWord2){
		float relevancy = 0;	//in %

		if(allWord1.size() != 0 && allWord2.size() != 0){
			for(Word curWord1 : allWord1){
				for(Word curWord2 : allWord2){
					float definitionValue = checkDefinition(curWord1, curWord2)*definitionWeight;
					float POSValue = checkPOS(curWord1, curWord2)*POSWeight;
					float synonymValue = checkSynonyms(curWord1,curWord2)*synonymWeight;
					float antonymValue = checkAntonyms(curWord1,curWord2)*antonymWeight;

					relevancy += (definitionValue + POSValue + synonymValue + antonymValue);
				}
			}
			/*
			 * Plots the rough weighted data from before on a sigmoid function.
			 * This is used to transfer the earlier rough numbers into a percentage number
			 */
			//relevancy = (float) (200*(1/(1+Math.pow(Math.E,-(relevancy/5)))-0.5));
			relevancy = (float) Math.min(100,Math.pow(relevancy, 2)/25f);
		
		}else{
			System.err.println("ERROR: One of the words was not found in the dictionary or thesaurus. Check your spelling.");
		}
		return relevancy;

	}
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

		//Fills definitionWords2
		for(String fullDefW2 : word2.getDefinitions()){
			ArrayList<String> defWordList2 = definitionToWords(fullDefW2);

			for(String curWord : defWordList2){
				definitionWords2.add(curWord);
			}
		}

		//Clears common words
		for(int i = 0;i<definitionWords1.size();i++){

		}
		//Checks for repetitions
		for(String persistWord : definitionWords1){
			for(String checkingWord : definitionWords2){
				if(persistWord.equals(checkingWord)){
					if(!isCommonWord(persistWord)){
						definitionRepeats++;
					}
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
	 * Determines whether the inputted word is a common one
	 * 
	 * @param word
	 * @return Boolean whether the inputted word is a common word
	 */
	private static Boolean isCommonWord(String word){
		boolean isIn = false;

		for(String s : commonWords){
			if(word.equals(s)){
				isIn = true;
			}
		}

		return isIn;
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

		for(String s : splitThesaurusWords(word1.getSynonyms())){
			synonymsWord1.add(s);
		}
		for(String s : splitThesaurusWords(word2.getSynonyms())){
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

		for(String s : splitThesaurusWords(word1.getAntonyms())){
			antonymsWord1.add(s);
		}
		for(String s : splitThesaurusWords(word2.getAntonyms())){
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
	
	/**
	 * Splits the words in input linkedlist by spaces, meaning that 
	 * each item in the returned linkedlist is one word.
	 * @param list
	 * @return cleaned and formatted list. Each object is a single word with no spaces
	 */
	private static ArrayList<String> splitThesaurusWords(LinkedList<String> list){
		ArrayList<String> returnList = new ArrayList<String>();

		for(String s : list){
			for(String x : s.split(" ")){
				returnList.add(x);
			}
		}
		return returnList;
	}
}
