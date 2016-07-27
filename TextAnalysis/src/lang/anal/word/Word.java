package lang.anal.word;

import java.util.LinkedList;

public class Word {

	private String value; //The word itself
	private LinkedList<String> definitions;
	private String POS; //Part of speech 'verb' , 'noun' , 'adjective', etc.
	private LinkedList<String> synonyms,antonyms;


	public Word(String value, String POS, LinkedList<String> definitions){
		this.value = value;
		this.POS = POS;
		this.definitions = definitions;
	}

	public String getValue(){	return this.value; 	}
	public LinkedList<String> getDefinitions(){	return this.definitions;	}
	public String getPOS(){	return this.POS;	}
	public LinkedList<String> getSynonyms(){	return this.synonyms;	}
	public LinkedList<String> getAntonyms(){	return this.antonyms;	}
	
	public void setDefinitions(LinkedList<String> definitions){
		this.definitions = definitions;
	}
	
	public void setPOS(String POS){
		this.POS = POS;
	}
	
	public void setSynonyms(LinkedList<String> synonyms){
		this.synonyms = synonyms;
	}
	
	public void setAntonyms(LinkedList<String> antonyms){
		this.antonyms = antonyms;
	}

	@Override
	public String toString(){
		return "Word: " + this.getValue() + " | "
				+ "definition: " + this.getDefinitions() + " | "
				+ "synonyms: " + this.getSynonyms() + " | "
				+ "antonyms: " + this.getAntonyms();
	}


}
