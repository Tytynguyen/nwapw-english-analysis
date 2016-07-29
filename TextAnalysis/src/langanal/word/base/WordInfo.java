package langanal.word.base;

import javax.json.*;
import java.util.LinkedList;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WordInfo {
	private static final String thesaurusApiKey = "Jcglr2EapVZhu3ucPZsc"; //Api key used for online thesaurus resource
	
	
	//Takes a String, outputs list of Words with that spelling which have definition, part of speech, and synonyms and antonyms filled in
	public static LinkedList<Word> getFullInfoWords(String word){

		LinkedList<Word> words;
		try {
			words = getDictionaryWords(word);
			addThesaurusInfo(words);
			return words;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new LinkedList<Word>();
	}
	
	//Takes a String, outputs list of Words with that spelling which have definition and part of speech filled in
	public static LinkedList<Word> getDictionaryWords(String word) throws Exception {
		LinkedList<Word> words = new LinkedList<>();
		
		//Receiving info from server
		URL url = new URL("http://api.pearson.com/v2/dictionaries/ldoce5/entries?headword="+word);
		try(InputStream in = url.openStream();
				JsonReader reader = Json.createReader(in)) {

			JsonObject obj = reader.readObject();
			JsonArray results = obj.getJsonArray("results");

			//going through each word result
			for(JsonObject result : results.getValuesAs(JsonObject.class)){
				LinkedList<String> definitions = new LinkedList();
				String headword;
				String partOfSpeech;

				headword = result.getJsonString("headword").toString();
				headword = headword.substring(1,headword.length()-1);

				//checking if result is exact same spelling as inputted word
				if(headword.equals(word)){
					partOfSpeech = result.getJsonString("part_of_speech").toString();
					partOfSpeech = partOfSpeech.substring(1, partOfSpeech.length()-1);

					//getting all definitions if there are any
					JsonArray defArr = result.getJsonArray("senses").getJsonObject(0).getJsonArray("definition");
					if(defArr!=null){
						for(JsonValue j : defArr){
							String str = j.toString();
							str = str.substring(1,str.length()-1);
							definitions.add(str);
						}
					}
					//adding new word with info gathered to return list
					words.add(new Word(headword,partOfSpeech,definitions));
				}
			}
		}
		return words;
	}
	
	//adds synonyms and antonyms for all words inputted
	private static void addThesaurusInfo(LinkedList<Word> words) throws Exception{
		
		//Receiving info from server
		URL url = new URL("http://thesaurus.altervista.org/thesaurus/v1?output=json&language=en_US&key="+thesaurusApiKey+"&word="+words.getFirst().getValue());
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
		}	
	}
}
