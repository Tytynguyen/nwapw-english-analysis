package langanal.sentence.base;

public class Clause {
	NounPhrase subject;
	VerbPhrase verb;
	NounPhrase dobject;
	NounPhrase iobject;
	
	Clause(NounPhrase subject,VerbPhrase verb,NounPhrase dobject,NounPhrase iobject){
		this.subject = subject;
		this.verb = verb;
		this.dobject = dobject;
		this.iobject = iobject;
	}

}
