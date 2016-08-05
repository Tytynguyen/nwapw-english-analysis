package langanal.sentence.processing;

import langanal.sentence.base.VerbPhrase;
import langanal.word.base.Word;

import java.util.LinkedList;

/**
 * Created by SteinJac.ao on 8/4/2016.
 */
public class VerbPhraseProcessing {

    VerbPhraseProcessing(){}

    /**
     * VPh == VerbPhrase
     * @param verbs, takes in a list of VerbPhrases
     * @return a LinkedList of the verbs with as the Word class
     */
    public static LinkedList<LinkedList<Word>> VPhToWord(LinkedList<VerbPhrase> verbs, LinkedList<LinkedList<Word>> sentence){
        LinkedList<LinkedList<Word>> v = new LinkedList<>();
        int i = 0;
        for (VerbPhrase verb: verbs){
            v.add(i, sentence.get(verb.getIndex()));
            i++;
        }
        return v;
    }

    public static LinkedList<LinkedList<LinkedList<Word>>> ModToWord(LinkedList<VerbPhrase> mods, LinkedList<LinkedList<Word>> sentence){
        LinkedList<LinkedList<LinkedList<Word>>> mod = new LinkedList<>();
        for(int j = 0; j<mods.size(); j++){

            LinkedList<LinkedList<Word>> n = new LinkedList<>();
            for(VerbPhrase verb: mods){

                int[] ModIndex = verb.getModIndex();
                for (int i = 0; i < ModIndex.length; i++){
                    n.add(i,sentence.get(ModIndex[i]));
                }
            }
            mod.add(j,n);
            j++;
        }
        return mod;
    }
}


