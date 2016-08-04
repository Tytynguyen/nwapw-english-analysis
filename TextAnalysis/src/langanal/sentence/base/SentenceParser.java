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
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference  
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "Jon walked, slowly and carefully fed, and helped the hungry, energetic dog quickly when he saw a squirrel on the hill";
		new Sentence(text);
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				//System.out.println("Word: " + word + " POS: " + pos + " NER: " + ne);
				System.out.print(word + " (" + pos + ") ");
			}
			System.out.println();

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);
			// Get dependency tree
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			System.out.println();

			Object[] list = td.toArray();
			System.out.println(list.length);
			TypedDependency typedDependency;
			for (Object object : list) {
				typedDependency = (TypedDependency) object;
				System.out.println("Dependency Name " + typedDependency.toString() + 
						" :: " + "Node " + typedDependency.reln() +
						" :: " + "Dep " + typedDependency.dep() +
						" :: " + "Gov " + typedDependency.gov().pseudoPosition());

				if (typedDependency.reln().getShortName().equals("")) {
				}
			}
			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			System.out.println(dependencies.getFirstRoot());
			for(IndexedWord id: dependencies.getRoots()){
				System.out.println(id);
			}

			// This is the coreference link graph
			// Each chain stores a set of mentions that link to each other,
			// along with a method for getting the most representative mention
			// Both sentence and token offsets start at 1!
			Map<Integer, edu.stanford.nlp.dcoref.CorefChain> graph = 
					document.get(CorefChainAnnotation.class);
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
		Tree tree = cm.get(TreeAnnotation.class);
		
		// Get dependency tree
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		// Get dependencies
		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		System.out.println();

		Object[] list = td.toArray();
		
		return (TypedDependency[]) list;
	}
}
