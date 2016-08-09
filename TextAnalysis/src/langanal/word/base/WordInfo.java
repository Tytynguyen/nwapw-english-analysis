package langanal.word.base;

import javax.json.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import langanal.UserInterface.GUI;

public class WordInfo {
	private static final String thesaurusApiKey = "Jcglr2EapVZhu3ucPZsc"; //Api key used for online thesaurus resource

	/*
	 * Gets info from dictionary and thesaurus to return a list of words
	 * that are returned from searching the dictionary for the word inputted
	 * @param Word to find dictionary entries for
	 */
	public static LinkedList<Word> getFullInfoWords(String word) {
		LinkedList<Word> words = getDictionaryWords(word);
		LinkedList<Word> theWords = new LinkedList<Word>();
		for(Word w : words){
			if (w.getValue().equals(word)){
				theWords.add(w);
			}
		}
		addThesaurusInfo(theWords);

		return words;
	}

	/*
	 * Gets info from just dictionary to return a list of words
	 * that are returned from searching the dictionary for the word inputted
	 * @param Word to find dictionary entries for
	 */
	public static LinkedList<Word> getDictionaryWords(String word) {
		LinkedList<Word> words = new LinkedList<>();

		//Receiving info from server
		URL url = null;
		try {
			url = new URL("http://api.pearson.com/v2/dictionaries/ldoce5/entries?headword="+word);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try(InputStream in = url.openStream();
			JsonReader reader = Json.createReader(in)) {

			JsonObject obj = reader.readObject();
			JsonArray results = obj.getJsonArray("results");

			//going through each word result
			for(JsonObject result : results.getValuesAs(JsonObject.class)){
				LinkedList<String> definitions = new LinkedList();
				String headword = "";
				String partOfSpeech = "";
				String exampleSentence = "";


				//extracts headword
				headword = result.getJsonString("headword").toString();
				headword = headword.substring(1,headword.length()-1);

				//extracts part of speech
				if(result.containsKey("part_of_speech")){
					partOfSpeech = result.getJsonString("part_of_speech").toString();
					partOfSpeech = partOfSpeech.substring(1, partOfSpeech.length()-1);
				}



				//checks if result has "senses" entry, which contains definition and examples
				if(!result.containsValue(JsonObject.NULL)){

					JsonObject senses = result.getJsonArray("senses").getJsonObject(0);

					//checks if result has definitions
					if(senses.containsKey("definition")){
						//getting all definitions
						JsonArray defArr = senses.getJsonArray("definition");
						for(JsonValue j : defArr){
							String str = j.toString();
							str = str.substring(1,str.length()-1);
							definitions.add(str);
						}

						//checks if entry has example sentences
						if(senses.containsKey("examples")){
							JsonObject examples = senses.getJsonArray("examples").getJsonObject(0);
							if(examples.containsKey("text")){
								//extracts example sentence
								exampleSentence = examples.getJsonString("text").toString();
								exampleSentence = exampleSentence.substring(1, exampleSentence.length()-1);
							}
						}
					}
				}

				//adding new word with info gathered to return list
				words.add(new Word(headword,partOfSpeech,definitions,exampleSentence));
			}
		} catch(Exception e){//Issue w/ dictionary
			e.printStackTrace();
			GUI error = new GUI();
			error.errorDialogue();
		}
		return words;
	}

	//adds synonyms and antonyms for all words inputted
	private static void addThesaurusInfo(LinkedList<Word> words){

		//Receiving info from server
		URL url = null;
		try {
			url = new URL("http://thesaurus.altervista.org/thesaurus/v1?output=json&language=en_US&key="+thesaurusApiKey+"&word="+words.getFirst().getValue());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try(InputStream in = url.openStream();
			JsonReader reader = Json.createReader(in)) {

			JsonObject obj = reader.readObject();
			JsonArray results = obj.getJsonArray("response");

			//loops through all words to add thesaurus info
			for(Word word: words){
				LinkedList<String> synonyms = new LinkedList<>();
				LinkedList<String> antonyms = new LinkedList<>();

				//loops through results
				for(JsonObject result: results.getValuesAs(JsonObject.class)){
					JsonObject list = result.getJsonObject("list");
					String pos = list.getJsonString("category").toString();
					pos = pos.substring(2, pos.length()-2);

					//checks if result is relevant to current word, if it is, then adds synonyms and antonyms to word
					if(word.getPOS().contains(pos)){
						String data = list.getJsonString("synonyms").toString();
						data = data.substring(1, data.length()-1);
						String[] dataArr = data.split("\\|");
						for(String str : dataArr){
							//processing words and sorting between synonyms and antoynyms
							str = str.replace(" (related term)", "");
							str = str.replace(" (similar term)", "");
							if(str.contains(" (antonym)")){
								str = str.replace(" (antonym)", "");
								antonyms.add(str);
							} else {
								synonyms.add(str);
							}
						}
					}
				}
				word.setAntonyms(antonyms);
				word.setSynonyms(synonyms);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}