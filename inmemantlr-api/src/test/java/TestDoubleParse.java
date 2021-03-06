/**
 * Inmemantlr - In memory compiler for Antlr 4
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

import org.junit.Before;
import org.junit.Test;
import org.snt.inmemantlr.GenericParser;
import org.snt.inmemantlr.exceptions.CompilationException;
import org.snt.inmemantlr.exceptions.IllegalWorkflowException;
import org.snt.inmemantlr.exceptions.ParsingException;
import org.snt.inmemantlr.listener.DefaultTreeListener;
import org.snt.inmemantlr.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.snt.inmemantlr.utils.FileUtils.getStringFromStream;

public class TestDoubleParse {

    String sgrammarcontent = "";
    String s1 = "", s2 = "";

    @Before
    public void init() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream sgrammar = classLoader.getResourceAsStream
                ("inmemantlr/Java.g4");
             InputStream sfile1 = classLoader.getResourceAsStream("inmemantlr//HelloWorld.java");
             InputStream sfile2 = classLoader.getResourceAsStream("inmemantlr/HelloUniverse.java")) {
            sgrammarcontent = getStringFromStream(sgrammar);
            s1 = getStringFromStream(sfile1);
            s2 = getStringFromStream(sfile2);
        }
    }

    @Test
    public void testProcessor() {
        GenericParser gp1 = new GenericParser(sgrammarcontent);
        boolean compile;
        try {
            gp1.compile();
            compile = true;
        } catch (CompilationException e) {
            compile = false;
        }

        assertTrue(compile);

        GenericParser gp2 = new GenericParser(sgrammarcontent);

        try {
            gp2.compile();
            compile = true;
        } catch (CompilationException e) {
            compile = false;
        }

        assertTrue(compile);

        DefaultTreeListener l1 = new DefaultTreeListener();
        DefaultTreeListener l2 = new DefaultTreeListener();

        assertFalse(l1.toString() == null);
        assertEquals(l1.toString(), "");

        gp1.setListener(l1);
        gp2.setListener(l2);

        try {
            gp1.parse(s1);
            gp2.parse(s2);
        } catch (IllegalWorkflowException | ParsingException e) {
            e.printStackTrace();
        }

        ParseTree out1 = l1.getParseTree();
        ParseTree out2 = l2.getParseTree();
        assertFalse(out1.equals(out2));
    }
}
