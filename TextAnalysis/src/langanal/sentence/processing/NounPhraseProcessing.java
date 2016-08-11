package langanal.sentence.processing;

import langanal.sentence.base.NounPhrase;
import langanal.word.base.Word;

import java.util.LinkedList;

/**
 * Created by SteinJac.ao on 8/4/2016.
 */
public class NounPhraseProcessing {

    public NounPhraseProcessing(){}

    /**
     * NPh == NounPhrase
     * @param nouns, takes in a list of NounPhrases
     * @return a LinkedList of the verbs with as the Word class
     */
    public static LinkedList<LinkedList<Word>> NPhToWord(LinkedList<NounPhrase> nouns, LinkedList<LinkedList<Word>> sentence){
        LinkedList<LinkedList<Word>> n = new LinkedList<>();
        int i = 0;
        for (NounPhrase noun: nouns){
            n.add(i, sentence.get(noun.getIndex()));
            i++;
        }
        return n;
    }

    public static LinkedList<LinkedList<LinkedList<Word>>> ModToWord(LinkedList<NounPhrase> mods, LinkedList<LinkedList<Word>> sentence){
        LinkedList<LinkedList<LinkedList<Word>>> mod = new LinkedList<>();
        for(int j = 0; j<mods.size(); j++){

            LinkedList<LinkedList<Word>> n = new LinkedList<>();
            for(NounPhrase noun: mods){

                int[] ModIndex = noun.getModIndex();
                for (int i = 0; i < ModIndex.length; i++){
                    n.add(i,sentence.get(ModIndex[i]));
                }
            }
            mod.addLast(n);
            
        }
        return mod;
    }
}