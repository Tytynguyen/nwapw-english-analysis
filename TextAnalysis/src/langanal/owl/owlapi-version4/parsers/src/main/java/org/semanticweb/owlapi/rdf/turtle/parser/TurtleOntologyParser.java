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
package org.semanticweb.owlapi.rdf.turtle.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormatFactory;
import org.semanticweb.owlapi.io.AbstractOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * The Class TurtleOntologyParser.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.2.0
 */
public class TurtleOntologyParser extends AbstractOWLParser {

    private static final long serialVersionUID = 40000L;

    @Nonnull
    @Override
    public String getName() {
        return "TurtleOntologyParser";
    }

    @Override
    public OWLDocumentFormatFactory getSupportedFormat() {
        return new TurtleDocumentFormatFactory();
    }

    @SuppressWarnings("null")
    @Override
    public OWLDocumentFormat parse(OWLOntologyDocumentSource documentSource,
            OWLOntology ontology, OWLOntologyLoaderConfiguration configuration)
            throws IOException {
        Reader reader = null;
        InputStream is = null;
        try {
            TurtleParser parser;
            if (documentSource.isReaderAvailable()) {
                reader = documentSource.getReader();
                parser = new TurtleParser(reader, new ConsoleTripleHandler(),
                        documentSource.getDocumentIRI());
            } else if (documentSource.isInputStreamAvailable()) {
                is = documentSource.getInputStream();
                parser = new TurtleParser(is, new ConsoleTripleHandler(),
                        documentSource.getDocumentIRI());
            } else {
                is = getInputStream(documentSource.getDocumentIRI(),
                        configuration);
                parser = new TurtleParser(is, new ConsoleTripleHandler(),
                        documentSource.getDocumentIRI());
            }
            OWLRDFConsumerAdapter consumer = new OWLRDFConsumerAdapter(
                    ontology, configuration);
            TurtleDocumentFormat format = new TurtleDocumentFormat();
            consumer.setOntologyFormat(format);
            consumer.startModel(documentSource.getDocumentIRI());
            parser.setTripleHandler(consumer);
            parser.parseDocument();
            PrefixManager prefixManager = parser.getPrefixManager();
            for (String prefixName : prefixManager.getPrefixNames()) {
                format.setPrefix(prefixName,
                        prefixManager.getPrefix(prefixName));
            }
            return format;
        } catch (ParseException e) {
            throw new TurtleParserException(e);
        } catch (IOException e) {
            throw new TurtleParserException(e);
        } finally {
            if (is != null) {
                is.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}
