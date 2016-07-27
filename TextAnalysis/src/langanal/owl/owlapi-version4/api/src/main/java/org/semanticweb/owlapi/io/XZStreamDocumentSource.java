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
package org.semanticweb.owlapi.io;

import java.io.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.tukaani.xz.XZInputStream;

/**
 * An ontology document source which can read from a XZ stream.
 * 
 * @author ignazio
 * @since 3.4.8
 */
public class XZStreamDocumentSource extends OWLOntologyDocumentSourceBase {

    @Nonnull
    private final IRI documentIRI;
    private byte[] buffer;

    /**
     * Constructs an input source which will read an ontology from a
     * representation from the specified file.
     *
     * @param is
     *        The stream that the ontology representation will be read from.
     */
    public XZStreamDocumentSource(@Nonnull InputStream is) {
        this(is, getNextDocumentIRI("xzinputstream:ontology"), null, null);
    }

    /**
     * Constructs an input source which will read an ontology from a
     * representation from the specified stream.
     *
     * @param stream
     *        The stream that the ontology representation will be read from.
     * @param documentIRI
     *        The document IRI
     * @param format
     *        ontology format
     * @param mime
     *        mime type
     */
    public XZStreamDocumentSource(@Nonnull InputStream stream,
        @Nonnull IRI documentIRI, @Nullable OWLDocumentFormat format,
        @Nullable String mime) {
        super(format, mime);
        this.documentIRI = documentIRI;
        readIntoBuffer(stream);
    }

    private void readIntoBuffer(@Nonnull InputStream reader) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length = 100000;
            byte[] tempBuffer = new byte[length];
            int read = 0;
            do {
                read = reader.read(tempBuffer, 0, length);
                if (read > 0) {
                    bos.write(tempBuffer, 0, read);
                }
            } while (read > 0);
            buffer = bos.toByteArray();
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @Override
    public boolean isInputStreamAvailable() {
        return buffer != null;
    }

    @Override
    public InputStream getInputStream() {
        if (buffer == null) {
            throw new OWLOntologyInputSourceException(
                "Stream not found - check that the file is available before calling this method.");
        }
        try {
            return new XZInputStream(new ByteArrayInputStream(buffer));
        } catch (IOException e) {
            throw new OWLOntologyInputSourceException(e);
        }
    }

    @Override
    public IRI getDocumentIRI() {
        return documentIRI;
    }

    @Override
    public Reader getReader() {
        try {
            return new InputStreamReader(wrap(getInputStream()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // will never happen though - UTF-8 is always supported
            throw new OWLOntologyInputSourceException(e);
        }
    }

    @Override
    public boolean isReaderAvailable() {
        return isInputStreamAvailable();
    }
}
