package langanal.word.processing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.regex.Pattern;

import langanal.word.base.Word;
import langanal.word.base.WordInfo;

import javax.annotation.processing.SupportedAnnotationTypes;

public class WordProcessing {
	//Weights
	public static float definitionWeight = 1f;
	public static float POSWeight = 0.1f;
	public static float synonymWeight = 2;
	public static float antonymWeight = 2;
	public static float exampleWeight = 3;
	public static float synonymDefWeight = 0;
	public static float antonymDefWeight = 0;
	public static float defDefWeight = 1;

	//compiling the regular expression so it doesn't have to be recompiled every time in definitionToWords
	private static Pattern alphabetic = Pattern.compile("[^a-zA-Z ]");

	//debugging
	public static int increment;
	public static boolean debugging = true;

	//Stores the common "trash" words that shouldn't be used to find similarities
	private static String[] commonWords = new String[]{
			"the","be","to","of","and","a","in","that","have","i","it","for","not","not","on","with",
			"or","is", "was", "were", "being", "been", "they", "are", "when", "if", "which", "something", "from", "you"
	};

	/**
	 * Compares, processes, and returns the relevancy of the two words
	 * 
	 * @param word1
	 * @param word2
	 * @return The % relevancy
	 */
	public static float compareWords(String word1, String word2){
		//debugging
		increment = 0;

		float relevancy = 0;	//in %


			float definitiontValue = 0;
			float POStValue = 0;
			float synonymtValue = 0;
			float antonymtValue = 0;
			float exampletValue = 0;
			float synonymDefTValue = 0;
			float antonymDefTValue = 0;
			float defDefTValue = 0;
		
		
		//Stores the words with the same spelling into one LinkedList
		LinkedList<Word> allWord1 = new LinkedList<Word>();
		LinkedList<Word> allWord2 = new LinkedList<Word>();

		allWord1 = WordInfo.getFullInfoWords(word1);
		allWord2 = WordInfo.getFullInfoWords(word2);

		//Makes sure that the word exists
		if(allWord1.size() != 0 && allWord2.size() != 0){
			for(Word curWord1 : allWord1){
				for(Word curWord2 : allWord2){
					float definitionValue = checkDefinitionSimilarities(curWord1, curWord2)*definitionWeight;
					float POSValue = checkPOSSimilarities(curWord1, curWord2)*POSWeight;
					float synonymValue = checkSynonymSimilarities(curWord1,curWord2)*synonymWeight;
					float antonymValue = checkAntonymSimilarities(curWord1,curWord2)*antonymWeight;
					float exampleValue = checkExampleSimilarities(curWord1, curWord2)*exampleWeight;
					float synonymDefValue = checkSynonymDefinitionSimilarities(curWord1, curWord2)*synonymDefWeight;
					float antonymDefValue = checkAntonymDefinitionSimilarities(curWord1, curWord2)*antonymDefWeight;
					//float synonymDefValue = 0;
					//float antonymDefValue = 0;
					float defDefValue = checkDefinitionsDefinitions(curWord1, curWord2)*defDefWeight;
					//float defDefValue = 0;

					if(debugging){
						System.out.println("Compare:");
						System.out.println("\tdefinition: " + definitionValue);
						System.out.println("\tPOS: " + POSValue);
						System.out.println("\tsynonym: " + synonymValue);
						System.out.println("\tantonym: " + antonymValue);
						System.out.println("\texample: " + exampleValue);
						System.out.println("\tsynDef: " + synonymDefValue);
						System.out.println("\tantDef: " + antonymDefValue);
						System.out.println("\tdefDef: " + defDefValue);
						
						definitiontValue += definitionValue;
						POStValue += POSValue;
						synonymtValue += synonymValue;
						antonymtValue += antonymValue;
						exampletValue += exampleValue;
						synonymDefTValue += synonymValue;
						antonymDefTValue += antonymDefValue;
						defDefTValue += defDefValue;

					}

					relevancy += (definitionValue + POSValue + synonymValue + antonymValue + synonymDefValue + antonymDefValue + defDefValue)/5;

				}
			}
			if(debugging){
				System.out.println();
				System.out.println("\tdefT: " + definitiontValue);
				System.out.println("\tpost: " + POStValue);
				System.out.println("\tsynT: " + antonymtValue);
				System.out.println("\tantT: " + synonymtValue);
				System.out.println("\texaT: " + exampletValue);
				System.out.println("\tsynDT: " + synonymDefTValue);
				System.out.println("\tantDT: " + antonymDefTValue);
				System.out.println("\tdDT: " + defDefTValue);

				System.out.println("\tTotal rel: " + relevancy);
			}
			/*
			 * Plots the rough weighted data from before on a sigmoid function.
			 * This is used to transfer the earlier rough numbers into a percentage number
			 */
			relevancy = (float) (200*(1/(1+Math.pow(Math.E,-(relevancy/5)))-0.5));
		}else{
			if(allWord1.size() == 0){
				System.err.println("ERROR: Word \"" + word1 + "\" was not found in the dictionary or thesaurus. Check your spelling.");
			}
			if(allWord2.size() == 0){
				System.err.println("ERROR: Word \"" + word2 + "\" was not found in the dictionary or thesaurus. Check your spelling.");
			}
		}

		if (debugging) {
			System.out.println("Increment:\t" + increment);
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
	public static int checkDefinitionSimilarities(Word word1, Word word2){
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

		//Checks for repetitions
		for(String persistWord : definitionWords1){
			for(String checkingWord : definitionWords2){
				if(persistWord.equals(checkingWord)){
					if(!isCommonWord(persistWord)){
						System.out.println("\t\t def: " + persistWord);
						definitionRepeats++;
					}
				}
			}
		}

		return definitionRepeats;
	}


	/**
	 *
	 * Checks the relevancy of the definitions of the definitions of words
	 *
	 * @param word1
	 * @param word2
	 * @return
	 */
	public static int checkDefinitionsDefinitions(Word word1, Word word2) {

		int relevancy = 0;

		//though using the string is less accurate, it's much faster
		HashMap<String,Word> ignore = new HashMap<String,Word>();

		//no duplicates
		//value contains number of times that word has been added
		HashMap<Word,Integer> h1 = new HashMap<Word,Integer>();
		HashMap<Word,Integer> h2 = new HashMap<Word,Integer>();

		for (String s1:word1.getDefinitions()) {
			for (String ss1:s1.split(" ")) {
				if (ignore.get(ss1)!=null) {break;}
				for (Word w1 : WordInfo.getDictionaryWords(ss1)) {
					//for speed purposes analysis is only on nouns and verbs
					if (!w1.getPOS().equals("noun") && !w1.getPOS().equals("verb")) {
						//System.out.println("Broke 1 on "+word1.getPOS());
						ignore.put(ss1,w1);
						break;
					} else if (h1.containsKey(w1)) {
						//if the word is already in there increment the value
						h1.replace(w1, h1.get(w1));
					} else {
						//add it
						h1.put(w1, 1);
					}
				}
			}
		}


		for (String s2:word2.getDefinitions()) {
			for (String ss2 : s2.split(" ")) {
				for (Word w2 : WordInfo.getDictionaryWords(ss2)) {
					if (ignore.get(ss2)!=null) {break;}
					//for speed purposes analysis is only on nouns and verbs
					if (!w2.getPOS().equals("noun") && !w2.getPOS().equals("verb")) {
						//System.out.println("Broke 2 on "+word2.getPOS());
						ignore.put(ss2,w2);
						break;
					} else if (h2.containsKey(w2)) {
						//if the word is already in there increment the value
						h2.replace(w2, h2.get(w2));
					} else {
						//add it
						h2.put(w2, 1);
					}
				}
			}
		}

		for (Word currentWord1:h1.keySet()) {
			for (Word currentWord2:h2.keySet()) {
				//System.out.println("\t\t\t" + currentWord1 + "\t\t" + currentWord2);
				relevancy += (checkDefinitionSimilarities(currentWord1,currentWord2) * h1.get(currentWord1) *h2.get(currentWord2));
			}
		}

		return relevancy;
	}

	/**
	 *
	 * Check the similarity of the definitions of the synonyms of words
	 *
	 * @param word1
	 * @param word2
	 * @return int representing similarity
	 */
	private static int checkSynonymDefinitionSimilarities(Word word1, Word word2) {

		int similarity = 0;

		for (String s1:splitThesaurusWords(word1.getSynonyms())) {
			for (String s2 : splitThesaurusWords(word2.getSynonyms())) {
				for (Word w1 : WordInfo.getDictionaryWords(s1)) {
					for (Word w2 : WordInfo.getDictionaryWords(s2)) {
						similarity += checkDefinitionSimilarities(w1, w2);
					}
				}
			}
		}

		return similarity;
	}


	/**
	 *
	 * Check the similarity of the definitions of the antonyms of words
	 *
	 * @param word1
	 * @param word2
	 * @return int representing similarity
	 */
	private static int checkAntonymDefinitionSimilarities(Word word1, Word word2) {

		int similarity = 0;

		for (String s1 : splitThesaurusWords(word1.getAntonyms())) {
			for (String s2 : splitThesaurusWords(word2.getAntonyms())) {
				for (Word w1 : WordInfo.getDictionaryWords(s1)) {
					for (Word w2 : WordInfo.getDictionaryWords(s2)) {
						similarity += checkDefinitionSimilarities(w1, w2);
					}
				}
			}
		}

		return similarity;
	}



	/**
	 * Returns an ArrayList of words that are formatted for checkDefinition()
	 * @param definition
	 * @return
	 */
	private static ArrayList<String> definitionToWords(String definition){

		ArrayList<String> returnList = new ArrayList<String>();

		increment++;
		//removes non-alphabetic characters, makes lower case, trims starting and ending whitespace, then splits by spaces
		for(String word : alphabetic.matcher(definition).replaceAll("").toLowerCase().trim().split(" ")){
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
	public static int checkPOSSimilarities(Word word1, Word word2){
		if(word1.getPOS().equals(word2.getPOS())){
			return 1;
		}else{
			return 0;
		}
	}



	/**
	 * Checks to see if the two words are synonyms
	 * @param word1 to compare
	 * @param word2 to compare
	 * @return if they're synonyms
	 */
	public static boolean checkSynonyms(Word word1, Word word2) {

		for (String s:splitThesaurusWords(word1.getSynonyms())) {
			if (s.equals(word2)) {
				return true;
			}
		}
		for (String s:splitThesaurusWords(word2.getSynonyms())) {
			if (s.equals(word1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the two words are antonyms
	 * @param word1 to compare
	 * @param word2 to compare
	 * @return if they're antonyms
	 */
	public static boolean checkAntonyms(Word word1, Word word2) {

		for (String s:splitThesaurusWords(word1.getAntonyms())) {
			if (s.equals(word2)) {
				return true;
			}
		}
		for (String s:splitThesaurusWords(word2.getAntonyms())) {
			if (s.equals(word1)) {
				return true;
			}
		}
		return false;
	}




	/**
	 * Checks to see if two words share any synonyms
	 * @param word1
	 * @param word2
	 * @return The # of shared synonyms
	 */
	public static int checkSynonymSimilarities(Word word1, Word word2){
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
	public static int checkAntonymSimilarities(Word word1, Word word2){
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

	/**
	 * Finds the number of shared words in their examples
	 * @param word1
	 * @param word2
	 * @return number of similarities
	 */
	private static int checkExampleSimilarities(Word word1, Word word2){
		ArrayList<String> word1Ex = splitExampleWords(word1.getExample());
		ArrayList<String> word2Ex = splitExampleWords(word2.getExample());

		int count = 0;
		if(word1Ex.size()!= 0 && word2Ex.size()!= 0){
			for(String cur1 : word1Ex){
				for(String cur2 : word2Ex){
					if(cur1.equals(cur2)){
						if(!isCommonWord(cur1)){
							count++;
						}

					}
				}
			}
		}
		return count;
	}

	/**
	 * Splits the example up by spaces and cleans the input
	 * @param example
	 * @return ArrayList of words in the example sentence.
	 */
	private static ArrayList<String> splitExampleWords(String example){
		ArrayList<String> returnList = new ArrayList<String>();

		//removes all non alphabetic characters, makes lowercase, remove leading/trailing whitespace and split by spaces
		for(String s : example.replaceAll("[^a-zA-Z ]", "").toLowerCase().trim().split(" ")){
			String add = s.trim();
			if(add.length() != 0){
				returnList.add(add);
			}
		}
		System.out.println(returnList);
		return returnList;
	}
}
