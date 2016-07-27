package lang.anal.word;

import javax.json.*;
import java.util.LinkedList;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WordInfo {
	//API KEY: Jcglr2EapVZhu3ucPZsc
	private static final String word = "throw";

	public static void main(String[] args) throws Exception{
		getWordData(word);
	}
	

	public static void getWordData(String word) throws Exception {
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
					System.out.println("Word: " + headword);
					partOfSpeech = result.getJsonString("part_of_speech").toString();
					partOfSpeech = partOfSpeech.substring(1, partOfSpeech.length()-1);
					System.out.println(partOfSpeech);
					JsonArray defArr = result.getJsonArray("senses").getJsonObject(0).getJsonArray("definition");
					if(defArr!=null){
						for(JsonValue j : defArr){
							String str = j.toString();
							str = str.substring(1,str.length()-1);
							definitions.add(str);
							System.out.println(str);
						}
					}
				}
			}
		}
	}
}
