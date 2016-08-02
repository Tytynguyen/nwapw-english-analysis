package langanal.word.base;

import javax.json.*;

import langanal.word.base.Word;

import java.util.LinkedList;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WordInfo {
	private static final String thesaurusApiKey = "Jcglr2EapVZhu3ucPZsc"; //Api key used for online thesaurus resource

	public static void main(String[] args){
		for(Word w :getDictionaryWords("big")){
			System.out.println("Word: " + w.getValue());
			System.out.println(w.getDefinitions().getFirst());
			System.out.println(w.getExample());
		}
	}


	//Takes a String, outputs list of Words with that spelling which have definition, part of speech, and synonyms and antonyms filled in
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

	//Takes a String, outputs list of Words with that spelling which have definition and part of speech filled in
	public static LinkedList<Word> getDictionaryWords(String word) {
		LinkedList<Word> words = new LinkedList<>();

		//Receiving info from server
		URL url = null;
		try {
			url = new URL("http://api.pearson.com/v2/dictionaries/ldoce5/entries?headword="+word);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
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
				if(!result.containsValue(JsonObject.NULL)){
					if(result.containsKey("part_of_speech") && result.getJsonArray("senses").getJsonObject(0).containsKey("definition")){

						headword = result.getJsonString("headword").toString();
						headword = headword.substring(1,headword.length()-1);

						partOfSpeech = result.getJsonString("part_of_speech").toString();
						partOfSpeech = partOfSpeech.substring(1, partOfSpeech.length()-1);
						
						JsonObject senses = result.getJsonArray("senses").getJsonObject(0);
						
						//getting all definitions
						JsonArray defArr = senses.getJsonArray("definition");
						for(JsonValue j : defArr){
							String str = j.toString();
							str = str.substring(1,str.length()-1);
							definitions.add(str);
						}
						
						if(senses.containsKey("examples")){
							JsonObject examples = senses.getJsonArray("examples").getJsonObject(0);
							if(examples.containsKey("text")){
								exampleSentence = examples.getJsonString("text").toString();
								exampleSentence = exampleSentence.substring(1, exampleSentence.length()-1);
							}
						}
						//adding new word with info gathered to return list
						words.add(new Word(headword,partOfSpeech,definitions,exampleSentence));
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return words;
	}

	//adds synonyms and antonyms for all words inputted
	private static void addThesaurusInfo(LinkedList<Word> words){

		//Receiving info from server
		URL url = null;
		try {
			url = new URL("http://thesaurus.altervista.org/thesaurus/v1?output=json&language=en_US&key="+thesaurusApiKey+"&word="+words.getFirst().getValue());
		} catch (MalformedURLException e1) {
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
