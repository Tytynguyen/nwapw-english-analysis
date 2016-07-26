package lang.anal.word;

import java.util.LinkedList;

public class Word {

	private String value; //The word itself
	private String definition;
	private String POS; //Part of speech 'verb' , 'noun' , 'adjective', etc.
	private LinkedList<String> synonyms,antonyms;
	
	
	public Word(String value, String definitions, LinkedList<String> synonyms, LinkedList<String> antonyms){
		this.value = value;
		this.definition = definitions;
		this.synonyms = synonyms;
		this.antonyms = antonyms;
	}
	
	public String getValue(){	return this.value; 	}
	public String getDef(){	return this.definition;	}
	public String getPOS(){	return this.POS;	}
	public LinkedList<String> getSynonyms(){	return this.synonyms;	}
	public LinkedList<String> getAntonyms(){	return this.antonyms;	}
	
	@Override
	public String toString(){
		return "Word: " + this.getValue() + " | "
				+ "definition: " + this.getDef() + " | "
				+ "synonyms: " + this.getSynonyms() + " | "
				+ "antonyms: " + this.getAntonyms();
	}
	
	
}
