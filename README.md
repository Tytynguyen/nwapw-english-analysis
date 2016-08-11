# nwapw-english-analysis

### Installing Dependencies#
----

This project relies on multiple libraries with series of dependencies. Though not required, using [Apache Maven](https://maven.apache.org) to install the required dependencies is highly recommended. Simply [follow the install instructions](https://maven.apache.org/install.html) (Homebrew can also be used on OS X with the command `brew install maven`), then navigate to the TextAnalysis directory and execute the command `mvn clean install`.

If you choose to manually install the required dependencies, the dependency tree is as follows.

```
net.sourceforge.owlapi:owlapi-distribution:4.2.4
  com.fasterxml.jackson.core:jackson-core:2.5.1
  com.fasterxml.jackson.core:jackson-databind:2.5.1
  com.fasterxml.jackson.core:jackson-annotations:2.5.1
  org.tukaani:xz:1.5
  org.openrdf.sesame:sesame-model:2.7.16
    org.openrdf.sesame:sesame-util:2.7.16
    org.openrdf.sesame:sesame-rio-api:2.7.16
  org.openrdf.sesame:sesame-rio-languages:2.7.16
  org.openrdf.sesame:sesame-rio-datatypes:2.7.16
  org.openrdf.sesame:sesame-rio-binary:2.7.16
  org.openrdf.sesame:sesame-rio-n3:2.7.16
  org.openrdf.sesame:sesame-rio-nquads:2.7.16
  org.openrdf.sesame:sesame-rio-ntriples:2.7.16
  org.openrdf.sesame:sesame-rio-rdfjson:2.7.16
  org.openrdf.sesame:sesame-rio-rdfxml:2.7.16
  org.openrdf.sesame:sesame-rio-trix:2.7.16
  org.openrdf.sesame:sesame-rio-turtle:2.7.16
  org.openrdf.sesame:sesame-rio-trig:2.7.16
  org.openrdf.sesame:sesame-rio-api:2.7.16
  org.openrdf.sesame:sesame-rio-languages:2.7.16
  org.openrdf.sesame:sesame-rio-datatypes:2.7.16
  org.openrdf.sesame:sesame-rio-binary:2.7.16
  org.openrdf.sesame:sesame-rio-n3:2.7.16
  org.openrdf.sesame:sesame-rio-nquads:2.7.16
  org.openrdf.sesame:sesame-rio-ntriples:2.7.16
  org.openrdf.sesame:sesame-rio-rdfjson:2.7.16
  org.openrdf.sesame:sesame-rio-rdfxml:2.7.16
  org.openrdf.sesame:sesame-rio-trix:2.7.16
  org.openrdf.sesame:sesame-rio-turtle:2.7.16
  org.openrdf.sesame:sesame-rio-trig:2.7.16
    com.github.jsonld-java:jsonld-java:0.5.0
      org.apache.httpcomponents:httpclient-cache:4.2.5
      org.apache.httpcomponents:httpclient:4.2.5
      org.apache.httpcomponents:httpcore:4.2.4
      commons-codec:commons-codec:1.6
    org.slf4j:jcl-over-slf4j:1.7.7
    org.semarglproject:semargl-sesame:0.6.1
  org.semarglproject:semargl-core:0.6.1
    org.semarglproject:semargl-rdfa:0.6.1
    org.semarglproject:semargl-rdf:0.6.1
      net.sf.trove4j:trove4j:3.0.3
  com.google.guava:guava:18.0
  com.google.inject:guice:4.0
    javax.inject:javax.inject:1
    aopalliance:aopalliance:1.0
  com.google.inject.extensions:guice-assistedinject:4.0
  com.google.inject.extensions:guice-multibindings:4.0
  com.google.code.findbugs:jsr305:2.0.1
  commons-io:commons-io:2.4
com.googlecode.efficient-java-matrix-library:ejml:0.25
joda-time:joda-time:2.9.4
de.jollyday:jollyday:0.5.1
  org.threeten:threeten-extra:0.9
  javax.xml.bind:jaxb-api:2.2.7
com.google.protobuf:protobuf-java:3.0.0
org.slf4j:slf4j-api:1.7.21
org.slf4j:slf4j-simple:1.7.21
edu.stanford.nlp:stanford-corenlp:3.6.0
  com.io7m.xom:xom:1.2.10
    xml-apis:xml-apis:1.3.03
    xerces:xercesImpl:2.8.0
    xalan:xalan:2.7.0
```
