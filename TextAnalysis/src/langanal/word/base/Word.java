package langanal.word.base;

import java.io.Serializable;
import java.util.LinkedList;

public class Word implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String value; //The word itself
	private LinkedList<String> definitions;
	private String POS; //Part of speech 'verb' , 'noun' , 'adjective', etc.
	private LinkedList<String> synonyms,antonyms;
	private String exampleSentence;


	public Word(String value, String POS, LinkedList<String> definitions, String exampleSentence){
		this.value = value;
		this.POS = POS;
		this.definitions = definitions;
		this.exampleSentence = exampleSentence;
		this.synonyms = new LinkedList<>();
		this.antonyms = new LinkedList<>();
	}

	public String getValue(){	return this.value; 	}
	public LinkedList<String> getDefinitions(){	return this.definitions;	}
	public String getPOS(){	return this.POS;	}
	public LinkedList<String> getSynonyms(){	return this.synonyms;	}
	public LinkedList<String> getAntonyms(){	return this.antonyms;	}
	public String getExample(){		return this.exampleSentence;	}
	
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
				+ "antonyms: " + this.getAntonyms() + " | "
				+ "part of speech: "+ this.getPOS();
	}


}
