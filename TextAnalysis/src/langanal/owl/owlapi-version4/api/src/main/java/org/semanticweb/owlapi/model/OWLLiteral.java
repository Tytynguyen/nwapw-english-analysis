/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.model;

import javax.annotation.Nonnull;

/**
 * Represents a <a
 * href="http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Literals"
 * >Literal</a> in the OWL 2 Specification.
 * <p>
 * Each literal consists of a lexical form, which is a string, and a datatype. A
 * literal consisting of a lexical form {@code "abc"} and a datatype identified
 * by the IRI {@code datatypeIRI} is written as {@code "abc"^^datatypeIRI}.
 * <p>
 * Note that literals whose datatype is {@code rdf:PlainLiteral} can be
 * abbreviated. For example, literals of the form
 * {@code "abc@"^^rdf:PlainLiteral} can be abbreviated in the functional-style
 * syntax, and other concrete syntaxes to "abc". Literals of the form
 * "abc@langTag"^^rdf:PlainLiteral where "langTag" is not empty are abbreviated
 * in functional-style syntax documents (and other concrete syntaxes) to
 * "abc"@langTag whenever possible.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public interface OWLLiteral extends OWLObject, OWLAnnotationObject,
        OWLAnnotationValue, OWLPropertyAssertionObject, OWLPrimitive, HasLang {

    /**
     * Determines if the datatype of this literal is {@code rdf:PlainLiteral}.
     * Note that literals that are abbreviated in the functional syntax (and
     * other concrete syntaxes) and are of the form {@code "abc"} or
     * {@code "abc"@langTag} will be of the type {@code rdf:PlainLiteral} after
     * parsing.
     * 
     * @return {@code true} if the datatype of this literal is rdf:PlainLiteral,
     *         otherwise {@code false}.
     */
    boolean isRDFPlainLiteral();

    /**
     * Gets the lexical value of this literal. Note that if the datatype is
     * {@code rdf:PlainLiteral} then the abbreviated lexical form will be
     * returned. That is, the language tag is not included.
     * 
     * @return The lexical value of this literal. If the datatype is
     *         {@code rdf:PlainLiteral} then the return values are as follows:
     *         If the literal is of the form {@code "abc@"^^rdf:PlainLiteral}
     *         then the return value will be "abc" (without the language tag
     *         included). If the literal is of the form
     *         {@code "abc@langTag"^^rdf:PlainLiteral} then the return value
     *         will be "abc" (without the language tag included).
     */
    @Nonnull
    String getLiteral();

    /**
     * Gets the {@code OWLDatatype} which types this literal.
     * 
     * @return The {@code OWLDatatype} that types this literal. Note that for
     *         strings with language tag (previously considered to be untyped
     *         literals) the datatype will be rdf:PlainLiteral. The return value
     *         is never {@code null}.
     */
    @Nonnull
    OWLDatatype getDatatype();

    /**
     * Determines if this literal has a language tag.
     * 
     * @return {@code true} if this literal has a non-empty language tag,
     *         otherwise {@code false}
     */
    boolean hasLang();

    /**
     * Gets the language tag of this literal.
     * 
     * @return The language tag of this literal. If the literal does not have a
     *         language tag, because it is not of the type
     *         {@code rdf:PlainLiteral}, or because its language tag is empty,
     *         then the empty string will be returned. (The {@code null} value
     *         is never returned).
     */
    @Nonnull
    @Override
    String getLang();

    /**
     * Determines if this {@code OWLLiteral} has a particular language tag.
     * 
     * @param lang
     *        The specific lang to test for. The tag will be normalised - white
     *        space will be trimmed from the end and it will be converted to
     *        lower case.
     * @return {@code true} if this literal has a language tag equal to
     *         {@code lang}, otherwise {@code false}.
     */
    boolean hasLang(String lang);

    /**
     * Determines if this literal is typed with a datatype that has an IRI that
     * is {@code "http://www.w3.org/2001/XMLSchema#"integer}.
     * 
     * @return {@code true} if this literal is typed with
     *         {@code "http://www.w3.org/2001/XMLSchema#"integer}, i.e. this
     *         literal represents an integer, otherwise {@code false}.
     */
    boolean isInteger();

    /**
     * Parses the lexical value of this literal into an integer. The lexical
     * value of this literal should be in the lexical space of the integer
     * datatype ({@code "http://www.w3.org/2001/XMLSchema#"integer})
     * 
     * @return An integer value that is represented by this literal.
     * @throws NumberFormatException
     *         if the lexical form could not be parsed into an integer because
     *         it is not in the lexical space of the integer datatype.
     */
    int parseInteger();

    /**
     * Determines if this literal is typed with a datatype that has an IRI that
     * is {@code "http://www.w3.org/2001/XMLSchema#"boolean}.
     * 
     * @return {@code true} if this literal is typed with
     *         {@code "http://www.w3.org/2001/XMLSchema#"boolean}, i.e. this
     *         literal represents a boolean, otherwise {@code false}.
     */
    boolean isBoolean();

    /**
     * Parses the lexical value of this literal into a boolean. The lexical
     * value of this literal should be in the lexical space of the boolean
     * datatype ({@code "http://www.w3.org/2001/XMLSchema#"boolean}).
     * 
     * @return A boolean value that is represented by this literal.
     * @throws NumberFormatException
     *         if the lexical form could not be parsed into a boolean because it
     *         is not in the lexical space of the boolean datatype.
     */
    boolean parseBoolean();

    /**
     * Determines if this literal is typed with a datatype that has an IRI that
     * is {@code "http://www.w3.org/2001/XMLSchema#"double}.
     * 
     * @return {@code true} if this literal is typed with
     *         {@code "http://www.w3.org/2001/XMLSchema#"double}, i.e. this
     *         literal represents a double, otherwise {@code false}.
     */
    boolean isDouble();

    /**
     * Parses the lexical value of this literal into a double. The lexical value
     * of this literal should be in the lexical space of the double datatype (
     * {@code "http://www.w3.org/2001/XMLSchema#"double}).
     * 
     * @return A double value that is represented by this literal.
     * @throws NumberFormatException
     *         if the lexical form could not be parsed into a double because it
     *         is not in the lexical space of the double datatype.
     */
    double parseDouble();

    /**
     * Determines if this literal is typed with a datatype that has an IRI that
     * is {@code "http://www.w3.org/2001/XMLSchema#"float}.
     * 
     * @return {@code true} if this literal is typed with
     *         {@code "http://www.w3.org/2001/XMLSchema#"float}, i.e. this
     *         literal represents a float, otherwise {@code false}.
     */
    boolean isFloat();

    /**
     * Parses the lexical value of this literal into a float. The lexical value
     * of this literal should be in the lexical space of the float datatype (
     * {@code "http://www.w3.org/2001/XMLSchema#"float}).
     * 
     * @return A float value that is represented by this literal.
     * @throws NumberFormatException
     *         if the lexical form could not be parsed into a float because it
     *         is not in the lexical space of the float datatype.
     */
    float parseFloat();

    /**
     * @param visitor
     *        visitor
     */
    void accept(@Nonnull OWLDataVisitor visitor);

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor return type
     * @return visitor return value
     */
    @Nonnull
    <O> O accept(@Nonnull OWLDataVisitorEx<O> visitor);
}
