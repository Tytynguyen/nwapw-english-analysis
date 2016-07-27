package lang.anal.word;

import javax.json.*;
import java.util.LinkedList;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WordInfo {
	private static final String thesaurusApiKey = "Jcglr2EapVZhu3ucPZsc";
	private static final String word = "drive";

	public static void main(String[] args) throws Exception{
		LinkedList<String> list = getFullInfoWords(word).getFirst().getSynonyms();
		for(String s: list){
			System.out.println(s);
		}
	}
	
	public static LinkedList<Word> getFullInfoWords(String word) throws Exception{
		LinkedList<Word> words = getDictionaryWords(word);
		addThesaurusInfo(words);
		return words;
	}

	public static LinkedList<Word> getDictionaryWords(String word) throws Exception {
		LinkedList<Word> words = new LinkedList<>();
		URL url = new URL("http://api.pearson.com/v2/dictionaries/ldoce5/entries?headword="+word);
		try(InputStream in = url.openStream();
				JsonReader reader = Json.createReader(in)) {

			JsonObject obj = reader.readObject();
			JsonArray results = obj.getJsonArray("results");

			for(JsonObject result : results.getValuesAs(JsonObject.class)){
				LinkedList<String> definitions = new LinkedList();
				String headword;
				String partOfSpeech;

				headword = result.getJsonString("headword").toString();
				headword = headword.substring(1,headword.length()-1);


				if(headword.equals(word)){
					partOfSpeech = result.getJsonString("part_of_speech").toString();
					partOfSpeech = partOfSpeech.substring(1, partOfSpeech.length()-1);

					JsonArray defArr = result.getJsonArray("senses").getJsonObject(0).getJsonArray("definition");
					if(defArr!=null){
						for(JsonValue j : defArr){
							String str = j.toString();
							str = str.substring(1,str.length()-1);
							definitions.add(str);
						}
						words.add(new Word(headword,partOfSpeech,definitions));
					}
				}
			}
		}
		return words;
	}
	
	private static void addThesaurusInfo(LinkedList<Word> words) throws Exception{
		URL url = new URL("http://thesaurus.altervista.org/thesaurus/v1?output=json&language=en_US&key="+thesaurusApiKey+"&word="+words.getFirst().getValue());
		try(InputStream in = url.openStream();
				JsonReader reader = Json.createReader(in)) {
			
			JsonObject obj = reader.readObject();
			JsonArray results = obj.getJsonArray("response");
			for(Word word: words){
				LinkedList<String> synonyms = new LinkedList<>();
				LinkedList<String> antonyms = new LinkedList<>();
				
				for(JsonObject result: results.getValuesAs(JsonObject.class)){
					JsonObject list = result.getJsonObject("list");
					String pos = list.getJsonString("category").toString();
					pos = pos.substring(2, pos.length()-2);
					
					if(pos.equals(word.getPOS())){
						String data = list.getJsonString("synonyms").toString();
						data = data.substring(1, data.length()-1);
						String[] dataArr = data.split("|");
						for(String str : dataArr){
							str = str.replace(" (related term)", "");
							if(str.contains(" (antonym")){
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
