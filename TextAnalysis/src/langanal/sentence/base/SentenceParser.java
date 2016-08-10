package langanal.sentence.base;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import java.util.Collection;
import java.util.Properties;

public class SentenceParser {

	/* Parts of speech tags from coreNLP
	 *
	 * CC Coordinating conjunction
	CD Cardinal number
	DT Determiner
	EX Existential there
	FW Foreign word
	IN Preposition or subordinating conjunction
	JJ Adjective
	JJR Adjective, comparative
	JJS Adjective, superlative
	LS List item marker
	MD Modal
	NN Noun, singular or mass
	NNS Noun, plural
	NNP Proper noun, singular
	NNPS Proper noun, plural
	PDT Predeterminer
	POS Possessive ending
	PRP Personal pronoun
	PRP$ Possessive pronoun
	RB Adverb
	RBR Adverb, comparative
	RBS Adverb, superlative
	RP Particle
	SYM Symbol
	TO to
	UH Interjection
	VB Verb, base form
	VBD Verb, past tense
	VBG Verb, gerund or present participle
	VBN Verb, past participle
	VBP Verb, non�3rd person singular present
	VBZ Verb, 3rd person singular present
	WDT Wh�determiner
	WP Wh�pronoun
	WP$ Possessive wh�pronoun
	WRB Wh�adverb*/



    /*
     * Takes a sentence, uses coreNLP's tagging functionality to tag sentence and find dependencies
     * @param sentence to be tagged
     * @return tagged sentence (CoreMap)
     */
    public static CoreMap getCoreMap(String sentence){
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(sentence);

        // run annotator on this text
        pipeline.annotate(document);

        //return CoreMap of sentence
        return document.get(SentencesAnnotation.class).get(0);
    }

    /*
     * Gets dependency array from CoreMap
     * @param CoreMap to get dependency array from
     * @return Dependency array from CoreMap
     */
    public static TypedDependency[] getDependencies(CoreMap cm){
        // get parse tree of sentence
        Tree tree = cm.get(TreeAnnotation.class);
        // Get dependency tree
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

        //get collection of all dependencies
        Collection<TypedDependency> td = gs.typedDependenciesCollapsed();

        //change to array
        Object[] temp = td.toArray();
        TypedDependency[]  dependencies = new TypedDependency[temp.length];
        int index = 0;
        for(Object o : temp){
            dependencies[index] = (TypedDependency) o;
            index++;
        }
        return dependencies;
    }
}
