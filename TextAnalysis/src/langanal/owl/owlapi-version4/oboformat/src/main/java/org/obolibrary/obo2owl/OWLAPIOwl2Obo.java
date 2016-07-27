package org.obolibrary.obo2owl;

import static org.semanticweb.owlapi.search.EntitySearcher.getAnnotationObjects;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary;
import org.obolibrary.obo2owl.OwlStringTools.OwlStringException;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.QualifierValue;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * The Class OWLAPIOwl2Obo.
 */
public class OWLAPIOwl2Obo {

    @Nonnull
    private static final String TOP_BOTTOM_NONTRANSLATEABLE = "Assertions using owl:Thing or owl:Nothing are not translateable OBO";
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OWLAPIOwl2Obo.class);
    private static final String IRI_CLASS_SYNONYMTYPEDEF = Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_synonymtypedef";
    private static final String IRI_CLASS_SUBSETDEF = Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_subsetdef";
    /**
     * The absoulte url pattern.
     */
    protected final Pattern absoulteURLPattern = Pattern.compile("<\\s*http.*?>");
    private static final Set<String> SKIPPED_QUALIFIERS = Sets.newHashSet("gci_relation", "gci_filler", "cardinality",
        "minCardinality", "maxCardinality", "all_some", "all_only");
    /**
     * The manager.
     */
    @Nonnull
    protected OWLOntologyManager manager;
    /**
     * The owl ontology.
     */
    protected OWLOntology owlOntology;
    /**
     * The fac.
     */
    protected OWLDataFactory fac;
    /**
     * The obodoc.
     */
    protected OBODoc obodoc;
    /**
     * The untranslatable axioms.
     */
    protected Set<OWLAxiom> untranslatableAxioms;
    /**
     * The id space map.
     */
    protected Map<String, String> idSpaceMap;
    /**
     * The annotation property map.
     */
    @Nonnull
    public static final Map<String, String> ANNOTATIONPROPERTYMAP = initAnnotationPropertyMap();
    /**
     * The ap to declare.
     */
    protected Set<OWLAnnotationProperty> apToDeclare;
    /**
     * The ontology id.
     */
    protected String ontologyId;
    /**
     * The strict conversion.
     */
    protected boolean strictConversion;
    /**
     * The discard untranslatable.
     */
    protected boolean discardUntranslatable = false;
    /**
     * mute untranslatable axiom warnings
     */
    private boolean muteUntranslatableAxioms = false;

    protected final void init() {
        idSpaceMap = new HashMap<>();
        // legacy:
        idSpaceMap.put("http://www.obofoundry.org/ro/ro.owl#", "OBO_REL");
        untranslatableAxioms = new HashSet<>();
        fac = manager.getOWLDataFactory();
        apToDeclare = new HashSet<>();
    }

    /**
     * Instantiates a new oWLAPI owl2 obo.
     * 
     * @param translationManager
     *        the translation manager
     */
    public OWLAPIOwl2Obo(@Nonnull OWLOntologyManager translationManager) {
        manager = translationManager;
        init();
    }

    /**
     * Inits the annotation property map.
     * 
     * @return the hash map
     */
    @Nonnull
    protected static Map<String, String> initAnnotationPropertyMap() {
        Map<String, String> map = new HashMap<>();
        for (String key : OWLAPIObo2Owl.ANNOTATIONPROPERTYMAP.keySet()) {
            IRI propIRI = OWLAPIObo2Owl.ANNOTATIONPROPERTYMAP.get(key);
            map.put(propIRI.toString(), key);
        }
        return map;
    }

    /**
     * Sets the strict conversion.
     * 
     * @param b
     *        the new strict conversion
     */
    public void setStrictConversion(boolean b) {
        strictConversion = b;
    }

    /**
     * Gets the strict conversion.
     * 
     * @return the strict conversion
     */
    public boolean getStrictConversion() {
        return strictConversion;
    }

    /**
     * Checks if is discard untranslatable.
     * 
     * @return the discardUntranslatable
     */
    public boolean isDiscardUntranslatable() {
        return discardUntranslatable;
    }

    /**
     * Sets the discard untranslatable.
     * 
     * @param discardUntranslatable
     *        the discardUntranslatable to set
     */
    public void setDiscardUntranslatable(boolean discardUntranslatable) {
        this.discardUntranslatable = discardUntranslatable;
    }

    /**
     * Gets the manager.
     * 
     * @return the manager
     */
    public OWLOntologyManager getManager() {
        return manager;
    }

    /**
     * Sets the manager.
     * 
     * @param manager
     *        the new manager
     */
    public void setManager(@Nonnull OWLOntologyManager manager) {
        this.manager = manager;
    }

    /**
     * Gets the obodoc.
     * 
     * @return the obodoc
     */
    @Nonnull
    public OBODoc getObodoc() {
        return verifyNotNull(obodoc);
    }

    /**
     * Sets the obodoc.
     * 
     * @param obodoc
     *        the new obodoc
     */
    public void setObodoc(@Nonnull OBODoc obodoc) {
        this.obodoc = obodoc;
    }

    /**
     * Convert.
     * 
     * @param ont
     *        the ont
     * @return the oBO doc
     */
    @Nonnull
    public OBODoc convert(@Nonnull OWLOntology ont) {
        owlOntology = ont;
        ontologyId = getOntologyId(ont);
        init();
        return tr();
    }

    @Nonnull
    protected OWLOntology getOWLOntology() {
        return verifyNotNull(owlOntology);
    }

    /**
     * Gets the untranslatable axioms.
     * 
     * @return the untranslatableAxioms
     */
    public Collection<OWLAxiom> getUntranslatableAxioms() {
        return untranslatableAxioms;
    }

    /**
     * Tr.
     * 
     * @return the oBO doc
     */
    @Nonnull
    protected OBODoc tr() {
        setObodoc(new OBODoc());
        preProcess();
        tr(getOWLOntology());
        for (OWLAxiom ax : getOWLOntology().getAxioms()) {
            if (ax instanceof OWLDeclarationAxiom) {
                tr((OWLDeclarationAxiom) ax);
            } else if (ax instanceof OWLSubClassOfAxiom) {
                tr((OWLSubClassOfAxiom) ax);
            } else if (ax instanceof OWLDisjointClassesAxiom) {
                tr((OWLDisjointClassesAxiom) ax);
            } else if (ax instanceof OWLEquivalentClassesAxiom) {
                tr((OWLEquivalentClassesAxiom) ax);
            } else if (ax instanceof OWLClassAssertionAxiom) {
                tr((OWLClassAssertionAxiom) ax);
            } else if (ax instanceof OWLEquivalentObjectPropertiesAxiom) {
                tr((OWLEquivalentObjectPropertiesAxiom) ax);
            } else if (ax instanceof OWLSubAnnotationPropertyOfAxiom) {
                tr((OWLSubAnnotationPropertyOfAxiom) ax);
            } else if (ax instanceof OWLSubObjectPropertyOfAxiom) {
                tr((OWLSubObjectPropertyOfAxiom) ax);
            } else if (ax instanceof OWLObjectPropertyRangeAxiom) {
                tr((OWLObjectPropertyRangeAxiom) ax);
            } else if (ax instanceof OWLFunctionalObjectPropertyAxiom) {
                tr((OWLFunctionalObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLSymmetricObjectPropertyAxiom) {
                tr((OWLSymmetricObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLAsymmetricObjectPropertyAxiom) {
                tr((OWLAsymmetricObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLObjectPropertyDomainAxiom) {
                tr((OWLObjectPropertyDomainAxiom) ax);
            } else if (ax instanceof OWLInverseFunctionalObjectPropertyAxiom) {
                tr((OWLInverseFunctionalObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLInverseObjectPropertiesAxiom) {
                tr((OWLInverseObjectPropertiesAxiom) ax);
            } else if (ax instanceof OWLDisjointObjectPropertiesAxiom) {
                tr((OWLDisjointObjectPropertiesAxiom) ax);
            } else if (ax instanceof OWLReflexiveObjectPropertyAxiom) {
                tr((OWLReflexiveObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLTransitiveObjectPropertyAxiom) {
                tr((OWLTransitiveObjectPropertyAxiom) ax);
            } else if (ax instanceof OWLSubPropertyChainOfAxiom) {
                tr((OWLSubPropertyChainOfAxiom) ax);
            } else {
                if (!(ax instanceof OWLAnnotationAssertionAxiom)) {
                    error(ax, false);
                } else {
                    // we presume this has been processed
                }
            }
        }
        if (!untranslatableAxioms.isEmpty() && !discardUntranslatable) {
            try {
                String axiomString = OwlStringTools.translate(untranslatableAxioms, manager);
                if (axiomString != null) {
                    Frame headerFrame = getObodoc().getHeaderFrame();
                    if (headerFrame == null) {
                        headerFrame = new Frame(FrameType.HEADER);
                        getObodoc().setHeaderFrame(headerFrame);
                    }
                    headerFrame.addClause(new Clause(OboFormatTag.TAG_OWL_AXIOMS, axiomString));
                }
            } catch (OwlStringException e) {
                throw new OWLRuntimeException(e);
            }
        }
        return getObodoc();
    }

    /**
     * Pre process.
     */
    @SuppressWarnings("null")
    protected void preProcess() {
        // converse of postProcess in obo2owl
        String viewRel = null;
        for (OWLAnnotation ann : getOWLOntology().getAnnotations()) {
            if (ann.getProperty().getIRI().equals(Obo2OWLVocabulary.IRI_OIO_LogicalDefinitionViewRelation.getIRI())) {
                OWLAnnotationValue v = ann.getValue();
                if (v instanceof OWLLiteral) {
                    viewRel = ((OWLLiteral) v).getLiteral();
                } else {
                    viewRel = getIdentifier((IRI) v);
                }
                break;
            }
        }
        if (viewRel != null) {
            // OWLObjectProperty vp = fac.getOWLObjectProperty(pIRI);
            Set<OWLAxiom> rmAxioms = new HashSet<>();
            Set<OWLAxiom> newAxioms = new HashSet<>();
            for (OWLEquivalentClassesAxiom eca : getOWLOntology().getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
                int numNamed = 0;
                Set<OWLClassExpression> xs = new HashSet<>();
                for (OWLClassExpression x : eca.getClassExpressions()) {
                    if (x instanceof OWLClass) {
                        xs.add(x);
                        numNamed++;
                        continue;
                    } else if (x instanceof OWLObjectSomeValuesFrom) {
                        OWLObjectProperty p = (OWLObjectProperty) ((OWLObjectSomeValuesFrom) x).getProperty();
                        if (!getIdentifier(p).equals(viewRel)) {
                            LOG.error("Expected: {} got: {} in {}", viewRel, p, eca);
                        }
                        xs.add(((OWLObjectSomeValuesFrom) x).getFiller());
                    } else {
                        LOG.error("Unexpected: {}", eca);
                    }
                }
                if (numNamed == 1) {
                    rmAxioms.add(eca);
                    newAxioms.add(fac.getOWLEquivalentClassesAxiom(xs));
                } else {
                    LOG.error("ECA did not fit expected pattern: {}", eca);
                }
            }
            manager.removeAxioms(getOWLOntology(), rmAxioms);
            manager.addAxioms(getOWLOntology(), newAxioms);
        }
    }

    protected void add(@Nullable Frame f) {
        if (f != null) {
            try {
                getObodoc().addFrame(f);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Tr object property.
     * 
     * @param prop
     *        the prop
     * @param tag
     *        the tag
     * @param value
     *        the value
     * @param annotations
     *        the annotations
     * @return true, if successful
     */
    @SuppressWarnings("null")
    protected boolean trObjectProperty(@Nullable OWLObjectProperty prop, @Nullable String tag, @Nullable String value,
        @Nonnull Set<OWLAnnotation> annotations) {
        if (prop == null || value == null) {
            return false;
        }
        Frame f = getTypedefFrame(prop);
        Clause clause;
        if (OboFormatTag.TAG_ID.getTag().equals(tag)) {
            clause = f.getClause(tag);
            if (tag != null) {
                clause.setValue(value);
            } else {
                clause = new Clause(tag, value);
                f.addClause(clause);
            }
        } else {
            clause = new Clause(tag, value);
            f.addClause(clause);
        }
        addQualifiers(clause, annotations);
        return true;
    }

    /**
     * Tr object property.
     * 
     * @param prop
     *        the prop
     * @param tag
     *        the tag
     * @param value
     *        the value
     * @param annotations
     *        the annotations
     * @return true, if successful
     */
    protected boolean trObjectProperty(@Nullable OWLObjectProperty prop, String tag, @Nullable Boolean value,
        @Nonnull Set<OWLAnnotation> annotations) {
        if (prop == null || value == null) {
            return false;
        }
        Frame f = getTypedefFrame(prop);
        Clause clause = new Clause(tag);
        clause.addValue(value);
        f.addClause(clause);
        addQualifiers(clause, annotations);
        return true;
    }

    /**
     * Tr nary property axiom.
     * 
     * @param ax
     *        the ax
     * @param tag
     *        the tag
     */
    protected void trNaryPropertyAxiom(@Nonnull OWLNaryPropertyAxiom<OWLObjectPropertyExpression> ax, String tag) {
        Set<OWLObjectPropertyExpression> set = ax.getProperties();
        if (set.size() > 1) {
            boolean first = true;
            OWLObjectProperty prop = null;
            String disjointFrom = null;
            for (OWLObjectPropertyExpression ex : set) {
                if (ex.isBottomEntity() || ex.isTopEntity()) {
                    error(tag + " using Top or Bottom entities are not supported in OBO.", ax, false);
                    return;
                }
                if (first) {
                    first = false;
                    if (ex instanceof OWLObjectProperty) {
                        prop = (OWLObjectProperty) ex;
                    }
                } else {
                    disjointFrom = getIdentifier(ex); // getIdentifier(ex);
                }
            }
            if (trObjectProperty(prop, tag, disjointFrom, ax.getAnnotations())) {
                return;
            }
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLSubPropertyChainOfAxiom ax) {
        OWLObjectPropertyExpression pEx = ax.getSuperProperty();
        if (pEx.isAnonymous()) {
            error(ax, false);
            return;
        }
        OWLObjectProperty p = pEx.asOWLObjectProperty();
        Frame f = getTypedefFrame(p);
        if (p.isBottomEntity() || p.isTopEntity()) {
            error("Property chains using Top or Bottom entities are not supported in OBO.", ax, false);
            return;
        }
        List<OWLObjectPropertyExpression> list = ax.getPropertyChain();
        if (list.size() != 2) {
            error(ax, false);
            return;
        }
        OWLObjectPropertyExpression exp1 = list.get(0);
        OWLObjectPropertyExpression exp2 = list.get(1);
        if (exp1.isBottomEntity() || exp1.isTopEntity() || exp2.isBottomEntity() || exp2.isTopEntity()) {
            error("Property chains using Top or Bottom entities are not supported in OBO.", ax, false);
            return;
        }
        String rel1 = getIdentifier(exp1);
        String rel2 = getIdentifier(exp2);
        if (rel1 == null || rel2 == null) {
            error(ax, false);
            return;
        }
        Clause clause;
        // set of unprocessed annotations
        Set<OWLAnnotation> unprocessedAnnotations = new HashSet<>(ax.getAnnotations());
        if (rel1.equals(f.getId())) {
            clause = new Clause(OboFormatTag.TAG_TRANSITIVE_OVER, rel2);
        } else {
            OboFormatTag tag = OboFormatTag.TAG_HOLDS_OVER_CHAIN;
            for (OWLAnnotation ann : ax.getAnnotations()) {
                if (OWLAPIObo2Owl.IRI_PROP_ISREVERSIBLEPROPERTYCHAIN.equals(ann.getProperty().getIRI().toString())) {
                    tag = OboFormatTag.TAG_EQUIVALENT_TO_CHAIN;
                    // remove annotation from unprocessed set.
                    unprocessedAnnotations.remove(ann);
                    break;
                }
            }
            clause = new Clause(tag);
            clause.addValue(rel1);
            clause.addValue(rel2);
        }
        f.addClause(clause);
        addQualifiers(clause, unprocessedAnnotations);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLEquivalentObjectPropertiesAxiom ax) {
        trNaryPropertyAxiom(ax, OboFormatTag.TAG_EQUIVALENT_TO.getTag());
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLTransitiveObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_TRANSITIVE.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLDisjointObjectPropertiesAxiom ax) {
        trNaryPropertyAxiom(ax, OboFormatTag.TAG_DISJOINT_FROM.getTag());
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLReflexiveObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_REFLEXIVE.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLInverseFunctionalObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_INVERSE_FUNCTIONAL.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLInverseObjectPropertiesAxiom ax) {
        OWLObjectPropertyExpression prop1 = ax.getFirstProperty();
        OWLObjectPropertyExpression prop2 = ax.getSecondProperty();
        if (prop1 instanceof OWLObjectProperty && prop2 instanceof OWLObjectProperty && trObjectProperty(
            (OWLObjectProperty) prop1, OboFormatTag.TAG_INVERSE_OF.getTag(), getIdentifier(prop2), ax
                .getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLObjectPropertyDomainAxiom ax) {
        OWLClassExpression domain = ax.getDomain();
        OWLObjectPropertyExpression propEx = ax.getProperty();
        if (propEx.isAnonymous()) {
            error(ax, true);
            return;
        }
        OWLObjectProperty prop = propEx.asOWLObjectProperty();
        if (domain.isBottomEntity() || domain.isTopEntity()) {
            // at least get the type def frame
            getTypedefFrame(prop);
            // now throw the error
            error("domains using top or bottom entities are not translatable to OBO.", ax, false);
            return;
        }
        String range = getIdentifier(domain);
        if (range != null) {
            if (trObjectProperty(prop, OboFormatTag.TAG_DOMAIN.getTag(), range, ax.getAnnotations())) {
                return;
            } else {
                error("trObjectProperty failed for " + prop, ax, true);
            }
        } else {
            error("no range translatable for " + ax, false);
        }
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLAsymmetricObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_ASYMMETRIC.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLSymmetricObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_SYMMETRIC.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLFunctionalObjectPropertyAxiom ax) {
        OWLObjectPropertyExpression prop = ax.getProperty();
        if (prop instanceof OWLObjectProperty && trObjectProperty((OWLObjectProperty) prop,
            OboFormatTag.TAG_IS_FUNCTIONAL.getTag(), Boolean.TRUE, ax.getAnnotations())) {
            return;
        }
        error(ax, true);
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLObjectPropertyRangeAxiom ax) {
        OWLClassExpression owlRange = ax.getRange();
        OWLObjectPropertyExpression propEx = ax.getProperty();
        if (propEx.isAnonymous()) {
            error(ax, false);
        }
        OWLObjectProperty prop = propEx.asOWLObjectProperty();
        if (owlRange.isBottomEntity() || owlRange.isTopEntity()) {
            // at least create the property frame
            getTypedefFrame(prop);
            // error message
            error("ranges using top or bottom entities are not translatable to OBO.", ax, false);
            return;
        }
        String range = getIdentifier(owlRange); // getIdentifier(ax.getRange());
        if (range != null && trObjectProperty(prop, OboFormatTag.TAG_RANGE.getTag(), range, ax.getAnnotations())) {
            return;
        }
        error(ax, false);
    }

    @SuppressWarnings("null")
    protected void tr(@Nonnull OWLSubObjectPropertyOfAxiom ax) {
        OWLObjectPropertyExpression sup = ax.getSuperProperty();
        OWLObjectPropertyExpression sub = ax.getSubProperty();
        if (sub.isBottomEntity() || sub.isTopEntity() || sup.isBottomEntity() || sup.isTopEntity()) {
            error("SubProperties using Top or Bottom entites are not supported in OBO.", false);
            return;
        }
        if (sub instanceof OWLObjectProperty && sup instanceof OWLObjectProperty) {
            String supId = getIdentifier(sup);
            if (supId.startsWith("owl:")) {
                return;
            }
            Frame f = getTypedefFrame((OWLObjectProperty) sub);
            Clause clause = new Clause(OboFormatTag.TAG_IS_A, supId);
            f.addClause(clause);
            addQualifiers(clause, ax.getAnnotations());
        } else {
            error(ax, true);
        }
    }

    @SuppressWarnings("null")
    protected void tr(@Nonnull OWLSubAnnotationPropertyOfAxiom ax) {
        OWLAnnotationProperty sup = ax.getSuperProperty();
        OWLAnnotationProperty sub = ax.getSubProperty();
        if (sub.isBottomEntity() || sub.isTopEntity() || sup.isBottomEntity() || sup.isTopEntity()) {
            error("SubAnnotationProperties using Top or Bottom entites are not supported in OBO.", false);
            return;
        }
        String tagObject = owlObjectToTag(sup);
        if (OboFormatTag.TAG_SYNONYMTYPEDEF.getTag().equals(tagObject)) {
            String name = "";
            String scope = null;
            for (OWLAnnotationAssertionAxiom axiom : getOWLOntology().getAnnotationAssertionAxioms(sub.getIRI())) {
                String tg = owlObjectToTag(axiom.getProperty());
                if (OboFormatTag.TAG_NAME.getTag().equals(tg)) {
                    name = ((OWLLiteral) axiom.getValue()).getLiteral();
                } else if (OboFormatTag.TAG_SCOPE.getTag().equals(tg)) {
                    scope = owlObjectToTag(axiom.getValue());
                }
            }
            Frame hf = getObodoc().getHeaderFrame();
            Clause clause = new Clause(OboFormatTag.TAG_SYNONYMTYPEDEF);
            clause.addValue(getIdentifier(sub));
            clause.addValue(name);
            if (scope != null) {
                clause.addValue(scope);
            }
            addQualifiers(clause, ax.getAnnotations());
            if (!hf.getClauses().contains(clause)) {
                hf.addClause(clause);
            } else {
                LOG.error("duplicate clause: {} in header", clause);
            }
            return;
        } else if (OboFormatTag.TAG_SUBSETDEF.getTag().equals(tagObject)) {
            String comment = "";
            for (OWLAnnotationAssertionAxiom axiom : getOWLOntology().getAnnotationAssertionAxioms(sub.getIRI())) {
                String tg = owlObjectToTag(axiom.getProperty());
                if (OboFormatTag.TAG_COMMENT.getTag().equals(tg)) {
                    comment = ((OWLLiteral) axiom.getValue()).getLiteral();
                    break;
                }
            }
            Frame hf = getObodoc().getHeaderFrame();
            Clause clause = new Clause(OboFormatTag.TAG_SUBSETDEF);
            clause.addValue(getIdentifier(sub));
            clause.addValue(comment);
            if (!hf.getClauses().contains(clause)) {
                hf.addClause(clause);
            } else {
                LOG.error("duplicate clause: {} in header", clause);
            }
            addQualifiers(clause, ax.getAnnotations());
            return;
        }
        if (sub instanceof OWLObjectProperty && sup instanceof OWLObjectProperty) {
            String supId = getIdentifier(sup); // getIdentifier(sup);
            if (supId.startsWith("owl:")) {
                return;
            }
            Frame f = getTypedefFrame(sub);
            Clause clause = new Clause(OboFormatTag.TAG_IS_A, supId);
            f.addClause(clause);
            addQualifiers(clause, ax.getAnnotations());
        } else {
            error(ax, true);
        }
    }

    /**
     * Tr.
     * 
     * @param aanAx
     *        the aan ax
     * @param frame
     *        the frame
     */
    protected void tr(@Nonnull OWLAnnotationAssertionAxiom aanAx, @Nonnull Frame frame) {
        boolean success = tr(aanAx.getProperty(), aanAx.getValue(), aanAx.getAnnotations(), frame);
        if (!success) {
            untranslatableAxioms.add(aanAx);
        }
    }

    /**
     * Tr.
     * 
     * @param prop
     *        the prop
     * @param annVal
     *        the ann val
     * @param qualifiers
     *        the qualifiers
     * @param frame
     *        the frame
     * @return true, if successful
     */
    @SuppressWarnings("null")
    protected boolean tr(OWLAnnotationProperty prop, @Nonnull OWLAnnotationValue annVal,
        @Nonnull Set<OWLAnnotation> qualifiers, @Nonnull Frame frame) {
        String tagString = owlObjectToTag(prop);
        OboFormatTag tag = null;
        if (tagString != null) {
            tag = OBOFormatConstants.getTag(tagString);
        }
        if (tag == null) {
            if (annVal instanceof IRI && FrameType.TERM.equals(frame.getType()) && isMetadataTag(prop)) {
                String propId = this.getIdentifier(prop);
                if (propId != null) {
                    Clause clause = new Clause(OboFormatTag.TAG_RELATIONSHIP);
                    clause.addValue(propId);
                    clause.addValue(getIdentifier((IRI) annVal));
                    addQualifiers(clause, qualifiers);
                    frame.addClause(clause);
                    return true;
                }
            }
            // annotation property does not correspond to a mapping to a tag in
            // the OBO syntax -
            // use the property_value tag
            return trGenericPropertyValue(prop, annVal, qualifiers, frame);
        }
        String value = getValue(annVal, tagString);
        if (!value.trim().isEmpty()) {
            if (tag == OboFormatTag.TAG_ID) {
                if (!frame.getId().equals(value)) {
                    warn("Conflicting id definitions: 1) " + frame.getId() + "  2)" + value);
                    return false;
                }
                return true;
            }
            Clause clause = new Clause(tag);
            if (tag == OboFormatTag.TAG_DATE) {
                try {
                    clause.addValue(OBOFormatConstants.headerDateFormat().parseObject(value));
                } catch (ParseException e) {
                    error("Could not parse date string: " + value, true);
                    return false;
                }
            } else {
                clause.addValue(value);
            }
            Set<OWLAnnotation> unprocessedQualifiers = new HashSet<>(qualifiers);
            if (tag == OboFormatTag.TAG_DEF) {
                for (OWLAnnotation aan : qualifiers) {
                    String propId = owlObjectToTag(aan.getProperty());
                    if ("xref".equals(propId)) {
                        OWLAnnotationValue v = aan.getValue();
                        String xrefValue;
                        if (v instanceof IRI) {
                            xrefValue = v.toString();
                        } else {
                            xrefValue = ((OWLLiteral) v).getLiteral();
                        }
                        Xref xref = new Xref(xrefValue);
                        clause.addXref(xref);
                        unprocessedQualifiers.remove(aan);
                    }
                }
            } else if (tag == OboFormatTag.TAG_XREF) {
                Xref xref = new Xref(value);
                for (OWLAnnotation annotation : qualifiers) {
                    if (fac.getRDFSLabel().equals(annotation.getProperty())) {
                        OWLAnnotationValue owlAnnotationValue = annotation.getValue();
                        if (owlAnnotationValue instanceof OWLLiteral) {
                            unprocessedQualifiers.remove(annotation);
                            String xrefAnnotation = ((OWLLiteral) owlAnnotationValue).getLiteral();
                            xrefAnnotation = xrefAnnotation.trim();
                            if (!xrefAnnotation.isEmpty()) {
                                xref.setAnnotation(xrefAnnotation);
                            }
                        }
                    }
                }
                clause.setValue(xref);
            } else if (tag == OboFormatTag.TAG_EXACT || tag == OboFormatTag.TAG_NARROW || tag == OboFormatTag.TAG_BROAD
                || tag == OboFormatTag.TAG_RELATED) {
                handleSynonym(qualifiers, tag.getTag(), clause, unprocessedQualifiers);
            } else if (tag == OboFormatTag.TAG_SYNONYM) {
                // This should never happen.
                // All synonyms need to be qualified with a type.
                String synonymType = null;
                handleSynonym(qualifiers, synonymType, clause, unprocessedQualifiers);
            }
            addQualifiers(clause, unprocessedQualifiers);
            // before adding the clause check for redundant clauses
            boolean redundant = false;
            for (Clause frameClause : frame.getClauses()) {
                if (clause.equals(frameClause)) {
                    redundant = handleDuplicateClause(frame, frameClause);
                }
            }
            if (!redundant) {
                frame.addClause(clause);
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean isMetadataTag(OWLAnnotationProperty p) {
        final IRI metadataTagIRI = IRI.create(Obo2OWLConstants.OIOVOCAB_IRI_PREFIX + OboFormatTag.TAG_IS_METADATA_TAG
            .getTag());
        Set<OWLAnnotationAssertionAxiom> axioms = owlOntology.getAnnotationAssertionAxioms(p.getIRI());
        for (OWLAnnotationAssertionAxiom ax : axioms) {
            if (metadataTagIRI.equals(ax.getProperty().getIRI())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle synonym.
     * 
     * @param qualifiers
     *        the qualifiers
     * @param scope
     *        the scope
     * @param clause
     *        the clause
     * @param unprocessedQualifiers
     *        the unprocessed qualifiers
     */
    protected void handleSynonym(@Nonnull Set<OWLAnnotation> qualifiers, @Nullable String scope, @Nonnull Clause clause,
        @Nonnull Set<OWLAnnotation> unprocessedQualifiers) {
        clause.setTag(OboFormatTag.TAG_SYNONYM.getTag());
        String type = null;
        clause.setXrefs(new ArrayList<Xref>());
        for (OWLAnnotation aan : qualifiers) {
            String propId = owlObjectToTag(aan.getProperty());
            if (OboFormatTag.TAG_XREF.getTag().equals(propId)) {
                OWLAnnotationValue v = aan.getValue();
                String xrefValue;
                if (v instanceof IRI) {
                    xrefValue = v.toString();
                } else {
                    xrefValue = ((OWLLiteral) v).getLiteral();
                }
                Xref xref = new Xref(xrefValue);
                clause.addXref(xref);
                unprocessedQualifiers.remove(aan);
            } else if (OboFormatTag.TAG_HAS_SYNONYM_TYPE.getTag().equals(propId)) {
                type = getIdentifier(aan.getValue());
                unprocessedQualifiers.remove(aan);
            }
        }
        if (scope != null) {
            clause.addValue(scope);
            if (type != null) {
                clause.addValue(type);
            }
        }
    }

    /**
     * Handle a duplicate clause in a frame during translation.
     * 
     * @param frame
     *        the frame
     * @param clause
     *        the clause
     * @return true if the clause is to be marked as redundant and will not be
     *         added to the
     */
    protected boolean handleDuplicateClause(@Nonnull Frame frame, Clause clause) {
        // default is to report it via the logger and remove it.
        LOG.error("Duplicate clause '{}' generated in frame: {}", clause, frame.getId());
        return true;
    }

    /**
     * Tr generic property value.
     * 
     * @param prop
     *        the prop
     * @param annVal
     *        the ann val
     * @param qualifiers
     *        the qualifiers
     * @param frame
     *        the frame
     * @return true, if successful
     */
    @SuppressWarnings("null")
    protected boolean trGenericPropertyValue(OWLAnnotationProperty prop, OWLAnnotationValue annVal,
        @Nonnull Set<OWLAnnotation> qualifiers, @Nonnull Frame frame) {
        // no built-in obo tag for this: use the generic property_value tag
        Clause clause = new Clause(OboFormatTag.TAG_PROPERTY_VALUE.getTag());
        String propId = getIdentifier(prop);
        addQualifiers(clause, qualifiers);
        if (!propId.equals("shorthand")) {
            clause.addValue(propId);
            if (annVal instanceof OWLLiteral) {
                OWLLiteral owlLiteral = (OWLLiteral) annVal;
                clause.addValue(owlLiteral.getLiteral());
                OWLDatatype datatype = owlLiteral.getDatatype();
                IRI dataTypeIri = datatype.getIRI();
                if (!OWL2Datatype.isBuiltIn(dataTypeIri)) {
                    error("Untranslatable axiom due to unknown data type: " + annVal, true);
                    return false;
                }
                if (Namespaces.XSD.inNamespace(dataTypeIri)) {
                    clause.addValue(dataTypeIri.prefixedBy("xsd:"));
                } else if (dataTypeIri.isPlainLiteral()) {
                    clause.addValue("xsd:string");
                } else {
                    clause.addValue(dataTypeIri.toString());
                }
            } else if (annVal instanceof IRI) {
                clause.addValue(getIdentifier((IRI) annVal));
            }
            frame.addClause(clause);
        }
        return true;
    }

    /**
     * Gets the value.
     * 
     * @param annVal
     *        the ann val
     * @param tag
     *        the tag
     * @return the value
     */
    @SuppressWarnings("null")
    @Nullable
    protected String getValue(@Nonnull OWLAnnotationValue annVal, String tag) {
        String value = annVal.toString();
        if (annVal instanceof OWLLiteral) {
            value = ((OWLLiteral) annVal).getLiteral();
        } else if (annVal instanceof IRI) {
            value = getIdentifier((IRI) annVal);
        }
        if (OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(tag)) {
            Matcher matcher = absoulteURLPattern.matcher(value);
            while (matcher.find()) {
                String m = matcher.group();
                m = m.replace("<", "");
                m = m.replace(">", "");
                int i = m.lastIndexOf('/');
                m = m.substring(i + 1);
                value = value.replace(matcher.group(), m);
            }
        }
        return value;
    }

    /**
     * Adds the qualifiers.
     * 
     * @param c
     *        the c
     * @param qualifiers
     *        the qualifiers
     */
    protected static void addQualifiers(@Nonnull Clause c, @Nonnull Set<OWLAnnotation> qualifiers) {
        for (OWLAnnotation ann : qualifiers) {
            String prop = owlObjectToTag(ann.getProperty());
            if (prop == null) {
                prop = ann.getProperty().getIRI().toString();
            }
            if (SKIPPED_QUALIFIERS.contains(prop)) {
                continue;
            }
            String value = ann.getValue().toString();
            if (ann.getValue() instanceof OWLLiteral) {
                value = ((OWLLiteral) ann.getValue()).getLiteral();
            } else if (ann.getValue() instanceof IRI) {
                value = getIdentifier((IRI) ann.getValue());
            }
            assert value != null;
            QualifierValue qv = new QualifierValue(prop, value);
            c.addQualifierValue(qv);
        }
    }

    /**
     * E.g. http://purl.obolibrary.org/obo/go.owl to "go"<br>
     * if does not match this pattern, then retain original IRI
     * 
     * @param ontology
     *        the ontology
     * @return The OBO ID of the ontology
     */
    public static String getOntologyId(@Nonnull OWLOntology ontology) {
        return getOntologyId(ontology.getOntologyID().getOntologyIRI().get());
    }

    /**
     * Gets the ontology id.
     * 
     * @param iriObj
     *        the iri obj
     * @return the ontology id
     */
    public static String getOntologyId(@Nonnull IRI iriObj) {
        // String id = getIdentifier(ontology.getOntologyID().getOntologyIRI());
        String iri = iriObj.toString();
        String id;
        if (iri.startsWith("http://purl.obolibrary.org/obo/")) {
            id = iri.replace("http://purl.obolibrary.org/obo/", "");
            if (id.endsWith(".owl")) {
                id = id.replaceFirst(".owl$", "");
            }
        } else {
            id = iri;
        }
        // int index = iri.lastIndexOf("/");
        // id = iri.substring(index+1);
        // index = id.lastIndexOf(".owl");
        // if(index>0){
        // id = id.substring(0, index);
        // }
        return id;
    }

    /**
     * Gets the data version.
     * 
     * @param ontology
     *        the ontology
     * @return the data version
     */
    @Nullable
    public static String getDataVersion(@Nonnull OWLOntology ontology) {
        String oid = getOntologyId(ontology);
        Optional<IRI> v = ontology.getOntologyID().getVersionIRI();
        if (v.isPresent()) {
            String vs = v.get().toString().replace("http://purl.obolibrary.org/obo/", "");
            vs = vs.replaceFirst(oid + '/', "");
            vs = vs.replace('/' + oid + ".owl", "");
            return vs;
        }
        return null;
    }

    /**
     * Tr.
     * 
     * @param ontology
     *        the ontology
     */
    protected void tr(@Nonnull OWLOntology ontology) {
        Frame f = new Frame(FrameType.HEADER);
        getObodoc().setHeaderFrame(f);
        for (IRI iri : ontology.getDirectImportsDocuments()) {
            Clause c = new Clause(OboFormatTag.TAG_IMPORT.getTag());
            // c.setValue(getOntologyId(iri));
            c.setValue(iri.toString());
            f.addClause(c);
        }
        String id = getOntologyId(ontology);
        Clause c = new Clause(OboFormatTag.TAG_ONTOLOGY.getTag());
        c.setValue(id);
        f.addClause(c);
        String vid = getDataVersion(ontology);
        if (vid != null) {
            Clause c2 = new Clause(OboFormatTag.TAG_DATA_VERSION.getTag());
            c2.setValue(vid);
            f.addClause(c2);
        }
        for (OWLAnnotation ann : ontology.getAnnotations()) {
            OWLAnnotationProperty property = ann.getProperty();
            String tagString = owlObjectToTag(property);
            if (OboFormatTag.TAG_COMMENT.getTag().equals(tagString)) {
                property = fac.getOWLAnnotationProperty(OWLAPIObo2Owl.trTagToIRI(OboFormatTag.TAG_REMARK.getTag()));
            }
            tr(property, ann.getValue(), ann.getAnnotations(), f);
        }
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLEquivalentClassesAxiom ax) {
        /*
         * Assumption: the underlying data structure is a set The order is not
         * guaranteed to be preserved.
         */
        Set<OWLClassExpression> expressions = ax.getClassExpressions();
        // handle expression list with size other than two elements as error
        if (expressions.size() != 2) {
            error(ax, false);
            return;
        }
        Iterator<OWLClassExpression> it = expressions.iterator();
        OWLClassExpression ce1 = it.next();
        OWLClassExpression ce2 = it.next();
        if (ce1.isBottomEntity() || ce1.isTopEntity() || ce2.isBottomEntity() || ce2.isTopEntity()) {
            error("Equivalent classes axioms using Top or Bottom entities are not supported in OBO.", ax, false);
            return;
        }
        if (!(ce1 instanceof OWLClass)) {
            // check whether ce2 is the actual OWLEntity
            if (ce2 instanceof OWLClass) {
                // three way exchange
                OWLClassExpression temp = ce2;
                ce2 = ce1;
                ce1 = temp;
            } else {
                // this might happen for some GCI axioms, which are not
                // expressible in OBO
                error("GCI axioms are not expressible in OBO.", ax, false);
                return;
            }
        }
        Frame f = getTermFrame(ce1.asOWLClass());
        if (f == null) {
            error(ax, false);
            return;
        }
        boolean isUntranslateable = false;
        List<Clause> equivalenceAxiomClauses = new ArrayList<>();
        String cls2 = getIdentifier(ce2);
        if (cls2 != null) {
            Clause c = new Clause(OboFormatTag.TAG_EQUIVALENT_TO.getTag());
            c.setValue(cls2);
            f.addClause(c);
            addQualifiers(c, ax.getAnnotations());
        } else if (ce2 instanceof OWLObjectUnionOf) {
            List<OWLClassExpression> list2 = ((OWLObjectUnionOf) ce2).getOperandsAsList();
            for (OWLClassExpression oce : list2) {
                String id = getIdentifier(oce);
                if (id == null) {
                    isUntranslateable = true;
                    error(ax, true);
                    return;
                }
                Clause c = new Clause(OboFormatTag.TAG_UNION_OF.getTag());
                c.setValue(id);
                equivalenceAxiomClauses.add(c);
                addQualifiers(c, ax.getAnnotations());
            }
        } else if (ce2 instanceof OWLObjectIntersectionOf) {
            List<OWLClassExpression> list2 = ((OWLObjectIntersectionOf) ce2).getOperandsAsList();
            for (OWLClassExpression ce : list2) {
                String r = null;
                cls2 = getIdentifier(ce);
                Integer exact = null; // cardinality
                Integer min = null; // minCardinality
                Integer max = null; // maxCardinality
                Boolean allSome = null; // all_some
                Boolean allOnly = null; // all_only
                if (ce instanceof OWLObjectSomeValuesFrom) {
                    OWLObjectSomeValuesFrom ristriction = (OWLObjectSomeValuesFrom) ce;
                    r = getIdentifier(ristriction.getProperty());
                    cls2 = getIdentifier(ristriction.getFiller());
                } else if (ce instanceof OWLObjectExactCardinality) {
                    OWLObjectExactCardinality card = (OWLObjectExactCardinality) ce;
                    r = getIdentifier(card.getProperty());
                    cls2 = getIdentifier(card.getFiller());
                    exact = card.getCardinality();
                } else if (ce instanceof OWLObjectMinCardinality) {
                    OWLObjectMinCardinality card = (OWLObjectMinCardinality) ce;
                    r = getIdentifier(card.getProperty());
                    cls2 = getIdentifier(card.getFiller());
                    min = card.getCardinality();
                } else if (ce instanceof OWLObjectMaxCardinality) {
                    OWLObjectMaxCardinality card = (OWLObjectMaxCardinality) ce;
                    r = getIdentifier(card.getProperty());
                    cls2 = getIdentifier(card.getFiller());
                    max = card.getCardinality();
                } else if (ce instanceof OWLObjectAllValuesFrom) {
                    OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) ce;
                    OWLClassExpression filler = all.getFiller();
                    if (filler instanceof OWLClass) {
                        r = getIdentifier(all.getProperty());
                        cls2 = getIdentifier(filler);
                        allOnly = Boolean.TRUE;
                    } else if (filler instanceof OWLObjectComplementOf) {
                        OWLObjectComplementOf restriction = (OWLObjectComplementOf) filler;
                        r = getIdentifier(all.getProperty());
                        cls2 = getIdentifier(restriction.getOperand());
                        exact = 0;
                    }
                } else if (ce instanceof OWLObjectIntersectionOf) {
                    // either a min-max or a some-all combination
                    Set<OWLClassExpression> operands = ((OWLObjectIntersectionOf) ce).getOperands();
                    if (operands.size() == 2) {
                        for (OWLClassExpression operand : operands) {
                            if (operand instanceof OWLObjectMinCardinality) {
                                OWLObjectMinCardinality card = (OWLObjectMinCardinality) operand;
                                r = getIdentifier(card.getProperty());
                                cls2 = getIdentifier(card.getFiller());
                                min = card.getCardinality();
                            } else if (operand instanceof OWLObjectMaxCardinality) {
                                OWLObjectMaxCardinality card = (OWLObjectMaxCardinality) operand;
                                r = getIdentifier(card.getProperty());
                                cls2 = getIdentifier(card.getFiller());
                                max = card.getCardinality();
                            } else if (operand instanceof OWLObjectAllValuesFrom) {
                                OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) operand;
                                r = getIdentifier(all.getProperty());
                                cls2 = getIdentifier(all.getFiller());
                                allOnly = Boolean.TRUE;
                            } else if (operand instanceof OWLObjectSomeValuesFrom) {
                                OWLObjectSomeValuesFrom all = (OWLObjectSomeValuesFrom) operand;
                                r = getIdentifier(all.getProperty());
                                cls2 = getIdentifier(all.getFiller());
                                allSome = Boolean.TRUE;
                            }
                        }
                    }
                }
                if (cls2 != null) {
                    Clause c = new Clause(OboFormatTag.TAG_INTERSECTION_OF.getTag());
                    if (r != null) {
                        c.addValue(r);
                    }
                    c.addValue(cls2);
                    equivalenceAxiomClauses.add(c);
                    if (exact != null) {
                        String string = exact.toString();
                        assert string != null;
                        c.addQualifierValue(new QualifierValue("cardinality", string));
                    }
                    if (min != null) {
                        String string = min.toString();
                        assert string != null;
                        c.addQualifierValue(new QualifierValue("minCardinality", string));
                    }
                    if (max != null) {
                        String string = max.toString();
                        assert string != null;
                        c.addQualifierValue(new QualifierValue("maxCardinality", string));
                    }
                    if (allSome != null) {
                        String string = allSome.toString();
                        assert string != null;
                        c.addQualifierValue(new QualifierValue("all_some", string));
                    }
                    if (allOnly != null) {
                        String string = allOnly.toString();
                        assert string != null;
                        c.addQualifierValue(new QualifierValue("all_only", string));
                    }
                    addQualifiers(c, ax.getAnnotations());
                } else if (!f.getClauses(OboFormatTag.TAG_INTERSECTION_OF).isEmpty()) {
                    error("The axiom is not translated (maximimum one IntersectionOf EquivalenceAxiom)", ax, false);
                } else {
                    isUntranslateable = true;
                    error(ax, false);
                }
            }
        } else {
            isUntranslateable = true;
            error(ax, false);
        }
        // Only add clauses if the *entire* equivalence axiom can be translated
        if (!isUntranslateable) {
            for (Clause c : equivalenceAxiomClauses) {
                f.addClause(c);
            }
        }
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    protected void tr(@Nonnull OWLDisjointClassesAxiom ax) {
        // use set, the OWL-API does not provide an order
        Set<OWLClassExpression> set = ax.getClassExpressions();
        if (set.size() != 2) {
            error("Expected two classes in a disjoin classes axiom.", ax, false);
        }
        Iterator<OWLClassExpression> it = set.iterator();
        OWLClassExpression ce1 = it.next();
        OWLClassExpression ce2 = it.next();
        if (ce1.isBottomEntity() || ce1.isTopEntity() || ce2.isBottomEntity() || ce2.isTopEntity()) {
            error("Disjoint classes axiom using Top or Bottom entities are not supported.", ax, false);
        }
        String cls2 = getIdentifier(ce2);
        if (cls2 == null) {
            error(ax, true);
            return;
        }
        if (ce1.isAnonymous()) {
            error(ax, false);
            return;
        }
        OWLClass cls1 = ce1.asOWLClass();
        Frame f = getTermFrame(cls1);
        Clause c = new Clause(OboFormatTag.TAG_DISJOINT_FROM.getTag());
        c.setValue(cls2);
        f.addClause(c);
        addQualifiers(c, ax.getAnnotations());
    }

    /**
     * Tr.
     * 
     * @param axiom
     *        the axiom
     */
    protected void tr(@Nonnull OWLDeclarationAxiom axiom) {
        OWLEntity entity = axiom.getEntity();
        if (entity.isBottomEntity() || entity.isTopEntity()) {
            return;
        }
        Set<OWLAnnotationAssertionAxiom> set = owlOntology.getAnnotationAssertionAxioms(entity.getIRI());
        if (set.isEmpty()) {
            return;
        }
        boolean isClass = entity.isOWLClass();
        boolean isObjectProperty = entity.isOWLObjectProperty();
        boolean isAnnotationProperty = entity.isOWLAnnotationProperty();
        // check whether the entity is an alt_id
        Optional<OboAltIdCheckResult> altIdOptional = checkForOboAltId(set);
        if (altIdOptional.isPresent()) {
            // the entity will not be translated
            // instead create the appropriate alt_id in the replaced_by frame
            String currentId = getIdentifier(entity.getIRI());
            addAltId(altIdOptional.get().replacedBy, currentId, isClass, isObjectProperty);
            // add unrelated annotations to untranslatableAxioms axioms
            untranslatableAxioms.addAll(altIdOptional.get().unrelated);
            return;
        }
        // translate
        Frame f = null;
        if (isClass) {
            f = getTermFrame(entity.asOWLClass());
        } else if (isObjectProperty) {
            f = getTypedefFrame(entity.asOWLObjectProperty());
        } else if (isAnnotationProperty) {
            for (OWLAxiom a : set) {
                OWLAnnotationAssertionAxiom ax = (OWLAnnotationAssertionAxiom) a;
                OWLAnnotationProperty prop = ax.getProperty();
                String tag = owlObjectToTag(prop);
                if (OboFormatTag.TAG_IS_METADATA_TAG.getTag().equals(tag)) {
                    f = getTypedefFrame(entity);
                    break;
                }
            }
        }
        if (f != null) {
            for (OWLAnnotationAssertionAxiom a : set) {
                assert a != null;
                tr(a, f);
            }
            add(f);
        }
    }

    private void addAltId(@Nonnull String replacedBy, @Nonnull String altId, boolean isClass, boolean isProperty) {
        Frame replacedByFrame = null;
        if (isClass) {
            replacedByFrame = getTermFrame(replacedBy);
        } else if (isProperty) {
            replacedByFrame = getTypedefFrame(replacedBy);
        }
        if (replacedByFrame != null) {
            boolean addClause = true;
            // check existing alt_ids to avoid duplicate clauses
            Collection<Clause> existing = replacedByFrame.getClauses(OboFormatTag.TAG_ALT_ID);
            for (Clause clause : existing) {
                if (altId.equals(clause.getValue(String.class))) {
                    addClause = false;
                }
            }
            if (addClause) {
                replacedByFrame.addClause(new Clause(OboFormatTag.TAG_ALT_ID, altId));
            }
        }
    }

    /**
     * Helper class: allow to return two values for the alt id check.
     */
    private static class OboAltIdCheckResult {

        final String replacedBy;
        final Set<OWLAnnotationAssertionAxiom> unrelated;

        OboAltIdCheckResult(@Nonnull String replacedBy, @Nonnull Set<OWLAnnotationAssertionAxiom> unrelated) {
            this.replacedBy = replacedBy;
            this.unrelated = unrelated;
        }
    }

    /**
     * Check the entity annotations for axioms declaring it to be an obsolete
     * entity, with 'obsolescence reason' being 'term merge', and a non-empty
     * 'replaced by' literal. This corresponds to an OBO alternate identifier.
     * Track non related annotations.
     * 
     * @param annotations
     *        set of annotations for the entity @return replaced_by if it is an
     *        alt_id
     * @return optional check result
     */
    @Nonnull
    private static Optional<OboAltIdCheckResult> checkForOboAltId(Set<OWLAnnotationAssertionAxiom> annotations) {
        String replacedBy = null;
        boolean isMerged = false;
        boolean isDeprecated = false;
        final Set<OWLAnnotationAssertionAxiom> unrelatedAxioms = new HashSet<>();
        for (OWLAnnotationAssertionAxiom axiom : annotations) {
            OWLAnnotationProperty prop = axiom.getProperty();
            if (prop.isDeprecated()) {
                isDeprecated = true;
            } else if (Obo2OWLConstants.IRI_IAO_0000231.equals(prop.getIRI())) {
                OWLAnnotationValue value = axiom.getValue();
                Optional<IRI> asIRI = value.asIRI();
                if (asIRI.isPresent()) {
                    isMerged = Obo2OWLConstants.IRI_IAO_0000227.equals(asIRI.get());
                } else {
                    unrelatedAxioms.add(axiom);
                }
            } else if (Obo2OWLVocabulary.IRI_IAO_0100001.iri.equals(prop.getIRI())) {
                OWLAnnotationValue value = axiom.getValue();
                Optional<OWLLiteral> asLiteral = value.asLiteral();
                if (asLiteral.isPresent()) {
                    replacedBy = asLiteral.get().getLiteral();
                } else {
                    // fallback: also check for an IRI
                    Optional<IRI> asIRI = value.asIRI();
                    if (asIRI.isPresent()) {
                        // translate IRI to OBO style ID
                        replacedBy = getIdentifier(asIRI.get());
                    } else {
                        unrelatedAxioms.add(axiom);
                    }
                }
            } else {
                unrelatedAxioms.add(axiom);
            }
        }
        Optional<OboAltIdCheckResult> result;
        if (replacedBy != null && isMerged && isDeprecated) {
            result = Optional.of(new OboAltIdCheckResult(replacedBy, unrelatedAxioms));
        } else {
            result = Optional.absent();
        }
        return result;
    }

    /**
     * Gets the identifier.
     * 
     * @param obj
     *        the obj
     * @return the identifier
     */
    @Nullable
    public String getIdentifier(OWLObject obj) {
        try {
            return getIdentifierFromObject(obj, getOWLOntology());
        } catch (UntranslatableAxiomException e) {
            error(e.getMessage(), true);
        }
        return null;
    }

    /**
     * @return true if untranslatable axioms should not be logged
     */
    public boolean isMuteUntranslatableAxioms() {
        return muteUntranslatableAxioms;
    }

    /**
     * @param muteUntranslatableAxioms
     *        true disables logging
     */
    public void setMuteUntranslatableAxioms(boolean muteUntranslatableAxioms) {
        this.muteUntranslatableAxioms = muteUntranslatableAxioms;
    }

    /**
     * The Class UntranslatableAxiomException.
     */
    public static class UntranslatableAxiomException extends Exception {

        // generated
        private static final long serialVersionUID = 40000L;

        /**
         * Instantiates a new untranslatable axiom exception.
         * 
         * @param message
         *        the message
         * @param cause
         *        the cause
         */
        public UntranslatableAxiomException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Instantiates a new untranslatable axiom exception.
         * 
         * @param message
         *        the message
         */
        public UntranslatableAxiomException(String message) {
            super(message);
        }
    }

    /**
     * Retrieve the identifier for a given {@link OWLObject}. This methods uses
     * also shorthand hints to resolve the identifier. Should the translation
     * process encounter a problem or not find an identifier the defaultValue is
     * returned.
     * 
     * @param obj
     *        the {@link OWLObject} to resolve
     * @param ont
     *        the target ontology
     * @param defaultValue
     *        the value to return in case of an error or no id
     * @return identifier or the default value
     */
    @Nonnull
    public static String getIdentifierFromObject(@Nonnull OWLObject obj, @Nonnull OWLOntology ont,
        @Nonnull String defaultValue) {
        String id = defaultValue;
        try {
            id = getIdentifierFromObject(obj, ont);
            if (id == null) {
                id = defaultValue;
            }
        } catch (UntranslatableAxiomException e) {
            LOG.error(e.getMessage(), e);
        }
        return id;
    }

    /**
     * Retrieve the identifier for a given {@link OWLObject}. This methods uses
     * also shorthand hints to resolve the identifier. Should the translation
     * process encounter an unexpected axiom an
     * 
     * @param obj
     *        the {@link OWLObject} to resolve
     * @param ont
     *        the target ontology
     * @return identifier or null
     * @throws UntranslatableAxiomException
     *         the untranslatable axiom exception
     *         {@link UntranslatableAxiomException} is thrown.
     */
    @SuppressWarnings("null")
    @Nullable
    public static String getIdentifierFromObject(OWLObject obj, @Nonnull OWLOntology ont)
        throws UntranslatableAxiomException {
        if (obj instanceof OWLObjectProperty || obj instanceof OWLAnnotationProperty) {
            OWLEntity entity = (OWLEntity) obj;
            Set<OWLAnnotationAssertionAxiom> axioms = ont.getAnnotationAssertionAxioms(entity.getIRI());
            for (OWLAnnotationAssertionAxiom ax : axioms) {
                String propId = getIdentifierFromObject(ax.getProperty().getIRI(), ont);
                // see BFOROXrefTest
                // 5.9.3. Special Rules for Relations
                if (propId.equals("shorthand")) {
                    OWLAnnotationValue value = ax.getValue();
                    if (value instanceof OWLLiteral) {
                        return ((OWLLiteral) value).getLiteral();
                    }
                    throw new UntranslatableAxiomException("Untranslatable axiom, expected literal value, but was: "
                        + value + " in axiom: " + ax);
                }
            }
        }
        if (obj instanceof OWLEntity) {
            return getIdentifier(((OWLEntity) obj).getIRI());
        }
        if (obj instanceof IRI) {
            return getIdentifier((IRI) obj);
        }
        return null;
    }

    /**
     * See table 5.9.2. Translation of identifiers
     * 
     * @param iriId
     *        the iri id
     * @return obo identifier or null
     */
    @Nullable
    public static String getIdentifier(@Nullable IRI iriId) {
        if (iriId == null) {
            return null;
        }
        String iri = iriId.toString();
        // canonical IRIs
        // if (iri.startsWith("http://purl.obolibrary.org/obo/")) {
        // String canonicalId = iri.replace("http://purl.obolibrary.org/obo/",
        // "");
        // }
        int indexSlash = iri.lastIndexOf('/');
        String id = null;
        if (indexSlash > -1) {
            id = iri.substring(indexSlash + 1);
        } else {
            id = iri;
        }
        String[] s = id.split("#_");
        // table 5.9.2 row 2 - NonCanonical-Prefixed-ID
        if (s.length > 1) {
            return s[0] + ':' + s[1];
        }
        // row 3 - Unprefixed-ID
        s = id.split("#");
        if (s.length > 1) {
            // prefixURI = prefixURI + s[0] + "#";
            // if(!(s[1].contains("#") || s[1].contains("_"))){
            String prefix = "";
            if ("owl".equals(s[0]) || "rdf".equals(s[0]) || "rdfs".equals(s[0])) {
                prefix = s[0] + ':';
            }
            // TODO: the following implements behavior in current spec, but this
            // leads to undesirable results
            // else if (baseOntology != null) {
            // String oid = getOntologyId(baseOntology); // OBO-style ID
            // if (oid.equals(s[0]))
            // prefix = "";
            // else {
            // return iri;
            // }
            // //prefix = s[0];
            // }
            return prefix + s[1];
        }
        // row 1 - Canonical-Prefixed-ID
        s = id.split("_");
        if (s.length == 2 && !id.contains("#") && !s[1].contains("_")) {
            String localId;
            try {
                localId = java.net.URLDecoder.decode(s[1], "UTF-8");
                return s[0] + ':' + localId;
            } catch (UnsupportedEncodingException e) {
                throw new OWLRuntimeException("UTF-8 not supported, JRE corrupted?", e);
            }
        }
        if (s.length > 2 && !id.contains("#") && s[s.length - 1].replaceAll("[0-9]", "").isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length; i++) {
                if (i > 0) {
                    if (i == s.length - 1) {
                        sb.append(':');
                    } else {
                        sb.append('_');
                    }
                }
                sb.append(s[i]);
            }
            return sb.toString();
        }
        return iri;
    }

    /**
     * Owl object to tag.
     * 
     * @param obj
     *        the obj
     * @return the string
     */
    @Nullable
    public static String owlObjectToTag(OWLObject obj) {
        IRI iriObj = null;
        if (obj instanceof OWLNamedObject) {
            iriObj = ((OWLNamedObject) obj).getIRI();
        } else if (obj instanceof IRI) {
            iriObj = (IRI) obj;
        }
        if (iriObj == null) {
            return null;
        }
        String iri = iriObj.toString();
        String tag = ANNOTATIONPROPERTYMAP.get(iri);
        if (tag == null) {
            // hardcoded values for legacy annotation properties: (TEMPORARY)
            if (iri.startsWith(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_")) {
                String legacyId = iri.replace(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "");
                if (legacyId.equals("IAO_xref")) {
                    return OboFormatTag.TAG_XREF.getTag();
                }
                if (legacyId.equals("IAO_id")) {
                    return OboFormatTag.TAG_ID.getTag();
                }
                if (legacyId.equals("IAO_namespace")) {
                    return OboFormatTag.TAG_NAMESPACE.getTag();
                }
            }
            String prefix = Obo2OWLConstants.OIOVOCAB_IRI_PREFIX;
            if (iri.startsWith(prefix)) {
                tag = iri.substring(prefix.length());
            }
        }
        return tag;
    }

    /**
     * Gets the term frame.
     * 
     * @param entity
     *        the entity
     * @return the term frame
     */
    protected Frame getTermFrame(@Nonnull OWLClass entity) {
        String id = getIdentifier(entity.getIRI());
        return getTermFrame(id);
    }

    private Frame getTermFrame(@Nonnull String id) {
        Frame f = getObodoc().getTermFrame(id);
        if (f == null) {
            f = new Frame(FrameType.TERM);
            f.setId(id);
            f.addClause(new Clause(OboFormatTag.TAG_ID, id));
            add(f);
        }
        return f;
    }

    /**
     * Gets the typedef frame.
     * 
     * @param entity
     *        the entity
     * @return the typedef frame
     */
    protected Frame getTypedefFrame(@Nonnull OWLEntity entity) {
        String id = getIdentifier(entity);
        return getTypedefFrame(id);
    }

    private Frame getTypedefFrame(@Nonnull String id) {
        Frame f = getObodoc().getTypedefFrame(id);
        if (f == null) {
            f = new Frame(FrameType.TYPEDEF);
            f.setId(id);
            f.addClause(new Clause(OboFormatTag.TAG_ID, id));
            add(f);
        }
        return f;
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    @SuppressWarnings("null")
    protected void tr(@Nonnull OWLClassAssertionAxiom ax) {
        OWLObject cls = ax.getClassExpression();
        if (!(cls instanceof OWLClass)) {
            return;
        }
        String clsIRI = ((OWLClass) cls).getIRI().toString();
        OWLAnnotationProperty labelProperty = manager.getOWLDataFactory().getRDFSLabel();
        if (IRI_CLASS_SYNONYMTYPEDEF.equals(clsIRI)) {
            Frame f = getObodoc().getHeaderFrame();
            Clause c = new Clause(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag());
            OWLNamedIndividual indv = (OWLNamedIndividual) ax.getIndividual();
            String indvId = getIdentifier(indv);
            // TODO: full specify this in the spec document.
            // we may want to allow full IDs for subsets in future.
            // here we would have a convention that an unprefixed
            // subsetdef/synonymtypedef
            // gets placed in a temp ID space, and only this id space is
            // stripped
            indvId = indvId.replaceFirst(".*:", "");
            c.addValue(indvId);
            c.addValue(indvId);
            String nameValue = "";
            String scopeValue = null;
            for (OWLAnnotation ann : getAnnotationObjects(indv, getOWLOntology(), null)) {
                String value = ((OWLLiteral) ann.getValue()).getLiteral();
                if (ann.getProperty().equals(labelProperty)) {
                    nameValue = '"' + value + '"';
                } else {
                    scopeValue = value;
                }
            }
            c.addValue(nameValue);
            if (scopeValue != null) {
                c.addValue(scopeValue);
            }
            f.addClause(c);
        } else if (IRI_CLASS_SUBSETDEF.equals(clsIRI)) {
            Frame f = getObodoc().getHeaderFrame();
            Clause c = new Clause(OboFormatTag.TAG_SUBSETDEF.getTag());
            OWLNamedIndividual indv = (OWLNamedIndividual) ax.getIndividual();
            String indvId = getIdentifier(indv);
            // TODO: full specify this in the spec document.
            // we may want to allow full IDs for subsets in future.
            // here we would have a convention that an unprefixed
            // subsetdef/synonymtypedef
            // gets placed in a temp ID space, and only this id space is
            // stripped
            indvId = indvId.replaceFirst(".*:", "");
            c.addValue(indvId);
            String nameValue = "";
            for (OWLAnnotation ann : getAnnotationObjects(indv, getOWLOntology(), null)) {
                String value = ((OWLLiteral) ann.getValue()).getLiteral();
                if (ann.getProperty().equals(labelProperty)) {
                    nameValue = '"' + value + '"';
                }
            }
            c.addValue(nameValue);
            f.addClause(c);
        } else {
            // TODO: individual
        }
    }

    /**
     * Tr.
     * 
     * @param ax
     *        the ax
     */
    @SuppressWarnings("null")
    protected void tr(@Nonnull OWLSubClassOfAxiom ax) {
        OWLClassExpression sub = ax.getSubClass();
        OWLClassExpression sup = ax.getSuperClass();
        Set<QualifierValue> qvs = new HashSet<>();
        if (sub.isOWLNothing() || sub.isTopEntity() || sup.isTopEntity() || sup.isOWLNothing()) {
            error(TOP_BOTTOM_NONTRANSLATEABLE, ax, false);
            return;
        }
        // 5.2.2
        if (sub instanceof OWLObjectIntersectionOf) {
            Set<OWLClassExpression> xs = ((OWLObjectIntersectionOf) sub).getOperands();
            // obo-format is limited to very restricted GCIs - the LHS of the
            // axiom
            // must correspond to ObjectIntersectionOf(cls
            // ObjectSomeValuesFrom(p filler))
            if (xs.size() == 2) {
                OWLClass c = null;
                OWLObjectSomeValuesFrom r = null;
                OWLObjectProperty p = null;
                OWLClass filler = null;
                for (OWLClassExpression x : xs) {
                    if (x instanceof OWLClass) {
                        c = (OWLClass) x;
                    }
                    if (x instanceof OWLObjectSomeValuesFrom) {
                        r = (OWLObjectSomeValuesFrom) x;
                        if (r.getProperty() instanceof OWLObjectProperty && r.getFiller() instanceof OWLClass) {
                            p = (OWLObjectProperty) r.getProperty();
                            filler = (OWLClass) r.getFiller();
                        }
                    }
                }
                if (c != null && p != null && filler != null) {
                    sub = c;
                    qvs.add(new QualifierValue("gci_relation", getIdentifier(p)));
                    qvs.add(new QualifierValue("gci_filler", getIdentifier(filler)));
                }
            }
        }
        if (sub instanceof OWLClass) {
            Frame f = getTermFrame((OWLClass) sub);
            if (sup instanceof OWLClass) {
                Clause c = new Clause(OboFormatTag.TAG_IS_A.getTag());
                c.setValue(getIdentifier(sup));
                c.setQualifierValues(qvs);
                f.addClause(c);
                addQualifiers(c, ax.getAnnotations());
            } else if (sup instanceof OWLObjectCardinalityRestriction) {
                // OWLObjectExactCardinality
                // OWLObjectMinCardinality
                // OWLObjectMaxCardinality
                OWLObjectCardinalityRestriction cardinality = (OWLObjectCardinalityRestriction) sup;
                OWLClassExpression filler = cardinality.getFiller();
                if (filler.isBottomEntity() || filler.isTopEntity()) {
                    error(TOP_BOTTOM_NONTRANSLATEABLE, ax, false);
                    return;
                }
                String fillerId = getIdentifier(filler);
                if (fillerId == null) {
                    error(ax, true);
                    return;
                }
                f.addClause(createRelationshipClauseWithCardinality(cardinality, fillerId, qvs, ax));
            } else if (sup instanceof OWLQuantifiedObjectRestriction) {
                // OWLObjectSomeValuesFrom
                // OWLObjectAllValuesFrom
                OWLQuantifiedObjectRestriction r = (OWLQuantifiedObjectRestriction) sup;
                OWLClassExpression filler = r.getFiller();
                if (filler.isBottomEntity() || filler.isTopEntity()) {
                    error(TOP_BOTTOM_NONTRANSLATEABLE, ax, false);
                    return;
                }
                String fillerId = getIdentifier(filler);
                if (fillerId == null) {
                    error(ax, true);
                    return;
                }
                f.addClause(createRelationshipClauseWithRestrictions(r, fillerId, qvs, ax));
            } else if (sup instanceof OWLObjectIntersectionOf) {
                OWLObjectIntersectionOf i = (OWLObjectIntersectionOf) sup;
                List<Clause> clauses = new ArrayList<>();
                for (OWLClassExpression operand : i.getOperands()) {
                    if (operand instanceof OWLObjectCardinalityRestriction) {
                        OWLObjectCardinalityRestriction restriction = (OWLObjectCardinalityRestriction) operand;
                        OWLClassExpression filler = restriction.getFiller();
                        if (filler.isBottomEntity() || filler.isTopEntity()) {
                            error(TOP_BOTTOM_NONTRANSLATEABLE, ax, false);
                            return;
                        }
                        String fillerId = getIdentifier(filler);
                        if (fillerId == null) {
                            error(ax, true);
                            return;
                        }
                        clauses.add(createRelationshipClauseWithCardinality(restriction, fillerId, new HashSet<>(qvs),
                            ax));
                    } else if (operand instanceof OWLQuantifiedObjectRestriction) {
                        OWLQuantifiedObjectRestriction restriction = (OWLQuantifiedObjectRestriction) operand;
                        OWLClassExpression filler = restriction.getFiller();
                        if (filler.isBottomEntity() || filler.isTopEntity()) {
                            error(TOP_BOTTOM_NONTRANSLATEABLE, ax, false);
                            return;
                        }
                        String fillerId = getIdentifier(filler);
                        if (fillerId == null) {
                            error(ax, true);
                            return;
                        }
                        clauses.add(createRelationshipClauseWithRestrictions(restriction, fillerId, new HashSet<>(qvs),
                            ax));
                    } else {
                        error(ax, true);
                        return;
                    }
                }
                if (clauses.isEmpty()) {
                    error(ax, true);
                    return;
                }
                clauses = normalizeRelationshipClauses(clauses);
                for (Clause clause : clauses) {
                    f.addClause(clause);
                }
            } else {
                error(ax, true);
                return;
            }
        } else {
            error(ax, true);
            return;
        }
    }

    /**
     * Creates the relationship clause with restrictions.
     * 
     * @param r
     *        the r
     * @param fillerId
     *        the filler id
     * @param qvs
     *        the qvs
     * @param ax
     *        the ax
     * @return the clause
     */
    @Nonnull
    protected Clause createRelationshipClauseWithRestrictions(@Nonnull OWLQuantifiedObjectRestriction r,
        String fillerId, @Nonnull Set<QualifierValue> qvs, @Nonnull OWLSubClassOfAxiom ax) {
        Clause c = new Clause(OboFormatTag.TAG_RELATIONSHIP.getTag());
        c.addValue(getIdentifier(r.getProperty()));
        c.addValue(fillerId);
        c.setQualifierValues(qvs);
        addQualifiers(c, ax.getAnnotations());
        return c;
    }

    /**
     * Creates the relationship clause with cardinality.
     * 
     * @param restriction
     *        the restriction
     * @param fillerId
     *        the filler id
     * @param qvs
     *        the qvs
     * @param ax
     *        the ax
     * @return the clause
     */
    @Nonnull
    protected Clause createRelationshipClauseWithCardinality(@Nonnull OWLObjectCardinalityRestriction restriction,
        String fillerId, @Nonnull Set<QualifierValue> qvs, @Nonnull OWLSubClassOfAxiom ax) {
        Clause c = new Clause(OboFormatTag.TAG_RELATIONSHIP.getTag());
        c.addValue(getIdentifier(restriction.getProperty()));
        c.addValue(fillerId);
        c.setQualifierValues(qvs);
        String q = "cardinality";
        if (restriction instanceof OWLObjectMinCardinality) {
            q = "minCardinality";
        } else if (restriction instanceof OWLObjectMaxCardinality) {
            q = "maxCardinality";
        }
        c.addQualifierValue(new QualifierValue(q, Integer.toString(restriction.getCardinality())));
        addQualifiers(c, ax.getAnnotations());
        return c;
    }

    /**
     * Join clauses and its {@link QualifierValue} which have the same
     * relationship type and target. Try to resolve conflicts for multiple
     * statements. E.g., min=2 and min=3 is resolved to min=2, or max=2 and
     * max=4 is resolved to max=4. It will not merge conflicting exact
     * cardinality statements. TODO How to merge "all_some", and "all_only"?
     * 
     * @param clauses
     *        the clauses
     * @return normalized list of {@link Clause}
     */
    @Nonnull
    public static List<Clause> normalizeRelationshipClauses(@Nonnull List<Clause> clauses) {
        List<Clause> normalized = new ArrayList<>();
        while (!clauses.isEmpty()) {
            Clause target = clauses.remove(0);
            assert target != null;
            List<Clause> similar = findSimilarClauses(clauses, target);
            normalized.add(target);
            mergeSimilarIntoTarget(target, similar);
        }
        return normalized;
    }

    /**
     * Find similar clauses.
     * 
     * @param clauses
     *        the clauses
     * @param target
     *        the target
     * @return the list
     */
    @SuppressWarnings("null")
    @Nonnull
    static List<Clause> findSimilarClauses(@Nonnull List<Clause> clauses, @Nonnull Clause target) {
        String targetTag = target.getTag();
        Object targetValue = target.getValue();
        Object targetValue2 = target.getValue2();
        List<Clause> similar = new ArrayList<>();
        Iterator<Clause> iterator = clauses.iterator();
        while (iterator.hasNext()) {
            Clause current = iterator.next();
            Object currentValue = current.getValue();
            Object currentValue2 = current.getValue2();
            if (targetTag.equals(current.getTag()) && targetValue.equals(currentValue)) {
                if (targetValue2 == null) {
                    if (currentValue2 == null) {
                        similar.add(current);
                        iterator.remove();
                    }
                } else if (targetValue2.equals(currentValue2)) {
                    similar.add(current);
                    iterator.remove();
                }
            }
        }
        return similar;
    }

    /**
     * Merge similar into target.
     * 
     * @param target
     *        the target
     * @param similar
     *        the similar
     */
    static void mergeSimilarIntoTarget(@Nonnull Clause target, @Nonnull List<Clause> similar) {
        if (similar.isEmpty()) {
            return;
        }
        Collection<QualifierValue> targetQVs = target.getQualifierValues();
        for (Clause current : similar) {
            Collection<QualifierValue> newQVs = current.getQualifierValues();
            for (QualifierValue newQV : newQVs) {
                String newQualifier = newQV.getQualifier();
                // if min or max cardinality check for possible merges
                if ("minCardinality".equals(newQualifier) || "maxCardinality".equals(newQualifier)) {
                    QualifierValue match = findMatchingQualifierValue(newQV, targetQVs);
                    if (match != null) {
                        mergeQualifierValues(match, newQV);
                    } else {
                        target.addQualifierValue(newQV);
                    }
                } else {
                    target.addQualifierValue(newQV);
                }
            }
        }
    }

    /**
     * Find matching qualifier value.
     * 
     * @param query
     *        the query
     * @param list
     *        the list
     * @return the qualifier value
     */
    @Nullable
    static QualifierValue findMatchingQualifierValue(@Nonnull QualifierValue query,
        @Nonnull Collection<QualifierValue> list) {
        String queryQualifier = query.getQualifier();
        for (QualifierValue qv : list) {
            if (queryQualifier.equals(qv.getQualifier())) {
                return qv;
            }
        }
        return null;
    }

    /**
     * Merge qualifier values.
     * 
     * @param target
     *        the target
     * @param newQV
     *        the new qv
     */
    static void mergeQualifierValues(@Nonnull QualifierValue target, @Nonnull QualifierValue newQV) {
        // do nothing, if they are equal
        if (!target.getValue().equals(newQV.getValue())) {
            if ("minCardinality".equals(target.getQualifier())) {
                // try to merge, parse as integers
                int currentValue = Integer.parseInt(target.getValue().toString());
                int newValue = Integer.parseInt(newQV.getValue().toString());
                int mergedValue = Math.min(currentValue, newValue);
                target.setValue(Integer.toString(mergedValue));
            } else if ("maxCardinality".equals(target.getQualifier())) {
                // try to merge, parse as integers
                int currentValue = Integer.parseInt(target.getValue().toString());
                int newValue = Integer.parseInt(newQV.getValue().toString());
                int mergedValue = Math.max(currentValue, newValue);
                target.setValue(Integer.toString(mergedValue));
            }
        }
    }

    protected void error(String message, OWLAxiom ax, boolean shouldLogComplaint) {
        untranslatableAxioms.add(ax);
        error(message + ax, shouldLogComplaint);
    }

    protected void error(OWLAxiom ax, boolean shouldLogComplaint) {
        untranslatableAxioms.add(ax);
        error("the axiom is not translated : " + ax, shouldLogComplaint);
    }

    protected void error(String message, boolean shouldLogComplaint) {
        if (strictConversion) {
            throw new OWLRuntimeException("The conversion is halted: " + message);
        } else {
            if (!muteUntranslatableAxioms && shouldLogComplaint) {
                LOG.error("MASKING ERROR «{}»", message, new Exception());
            }
        }
    }

    protected void warn(String message) {
        if (strictConversion) {
            throw new OWLRuntimeException("The conversion is halted: " + message);
        } else {
            LOG.warn("MASKING ERROR «{}»", message);
        }
    }
}
