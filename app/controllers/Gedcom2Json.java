package controllers;

import models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.parser.ErrorHandler;
import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.parser.ModelParser;
import org.folg.gedcom.tools.CountsCollector;
import org.folg.gedcom.visitors.GedcomWriter;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Gedcom2Json implements ErrorHandler {

    private Set<String> warnings;
    private Set<String> errors;

    public void warning(String message, int lineNumber) {
        warnings.add(message);
    }

    public void error(String message, int lineNumber) {
        errors.add(message);
    }

    public void fatalError(String message, int lineNumber) {
        // we need to add error handling and return error message to client
    }


    public ParsedDocument parseGedcom(File file) throws SAXParseException, IOException {

        warnings = new HashSet<String>();
        errors = new HashSet<String>();

        ParsedDocument parsedDocument = new ParsedDocument();
        JsonParser jsonParser = new JsonParser();

        ModelParser modelParser = new ModelParser();
        modelParser.setErrorHandler(this);
        Gedcom gedcom = modelParser.parseGedcom(file);
        parsedDocument.json = jsonParser.toJson(gedcom);

        CountsCollector ccWarnings = new CountsCollector();
        CountsCollector ccErrors = new CountsCollector();

        for (String warning : warnings) {
            ccWarnings.add(warning);
        }
        for (String error : errors) {
            ccErrors.add(error);
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ccWarnings.writeSorted(false, 1, printWriter);
        ccErrors.writeSorted(false, 1, printWriter);
        parsedDocument.warnings = stringWriter.toString();


        parsedDocument.originalGedcom = FileUtils.readFileToString(file);

        GedcomWriter writer = new GedcomWriter();
        OutputStream out = new ByteArrayOutputStream();
        writer.write(gedcom, out);
        parsedDocument.reconstituedGedcom = out.toString();

        return parsedDocument;
    }

}
