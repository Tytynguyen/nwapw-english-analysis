package langanal.sentence.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import edu.stanford.nlp.*;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.*;

public class SentenceParser {

	/*CC Coordinating conjunction
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
	VBP Verb, non­3rd person singular present
	VBZ Verb, 3rd person singular present
	WDT Wh­determiner
	WP Wh­pronoun
	WP$ Possessive wh­pronoun
	WRB Wh­adverb*/

	public static void main(String[] args) {
		String text = "Jon walked, slowly and carefully fed, and helped the hungry and energetic dog, when he saw a squirrel on the hill.";
		Sentence s = new Sentence(text);
		System.out.println("done");
		for(NounPhrase n : s.nouns){
			System.out.println(n);
		}
		for(VerbPhrase v : s.verbs){
			System.out.println(v);
		}
	}

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

	public static TypedDependency[] getDependencies(CoreMap cm){
		// get parse tree of sentence
		Tree tree = cm.get(TreeAnnotation.class);
		// Get dependency tree
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
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
