package langanal.sentence.base;

import java.util.LinkedList;

import edu.stanford.nlp.ling.IndexedWord;
import langanal.word.base.Word;

public class NounPhrase {
	IndexedWord noun;
	LinkedList<IndexedWord> modifiers;
	Sentence sentence;
	LinkedList<Float> comparisons = new LinkedList<Float>();
	float bestComparison;
	
	NounPhrase(IndexedWord verb, LinkedList<IndexedWord> modifiers, Sentence sentence){
		this.noun = verb;
		this.modifiers = modifiers;
		this.sentence = sentence;
	}

	/**
	 *
	 * @param modifier to be added to the given NounPhrase
	 * @return true if the modifier didn't already exist and was added, false otherwise
	 */
	public boolean addMod(IndexedWord modifier){
		if(!modifiers.contains(modifier)){
			modifiers.add(modifier);
			return true;
		}
		return false;
	}
	
	public String toString(){
		String returnString = "Noun: " + noun;
		for(IndexedWord iw : modifiers){
			returnString += '\n' + "    " + iw.toString();
		}
		returnString += '\n';
		return returnString;
	}
	
	public int getIndex(){return (int)noun.pseudoPosition()-1;}

	public int[] getModIndex() {
		int[] ModIndex = new int[modifiers.size()];
		int i = 0;
		for (IndexedWord mod : modifiers) {
			ModIndex[i] = (int) mod.pseudoPosition() - 1;
		}
		return ModIndex;
	}

	/**
	 * @return a List of modifiers as Words for the given NounPhrase
	 */
	public LinkedList<LinkedList<Word>> getModifiers(){
		LinkedList<LinkedList<Word>> returnList = new LinkedList<>();
		for(IndexedWord modifier:modifiers){
			returnList.add(sentence.words.get((int) (modifier.pseudoPosition()-1)));
		}
		return returnList;
	}
	
	public LinkedList<Word> getNoun(){
		return sentence.words.get((int) (noun.pseudoPosition()-1));
	}
	
	public LinkedList<Float> getComparisons(){
		return comparisons;
	}
	
	public void addComparison(float comparison){
		this.comparisons.addLast(comparison);
	}
	
	public float getBestComparison(){
		return this.bestComparison;
	}
	
	public void setBestComparison(float bestComp){
		this.bestComparison = bestComp;
	}
	
	public IndexedWord getIndexedWord(){
		return noun;
	}
}
