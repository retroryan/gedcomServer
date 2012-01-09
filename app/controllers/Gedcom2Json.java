package controllers;

import models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.parser.ErrorHandler;
import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.parser.ModelParser;
import org.folg.gedcom.tools.CountsCollector;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Gedcom2Json implements ErrorHandler {

    private List<String> warnings;
    private List<String> errors;

    public void warning(String message, int lineNumber) {
        warnings.add(message+" @ "+lineNumber);
    }

    public void error(String message, int lineNumber) {
        errors.add(message+" @ "+lineNumber);
    }

    public void fatalError(String message, int lineNumber) {
        // handle below
    }


    public ParsedDocument parseGedcom(File file) throws SAXParseException, IOException {

        warnings = new ArrayList<String>();
        errors = new ArrayList<String>();

        ParsedDocument parsedDocument = new ParsedDocument();
        JsonParser jsonParser = new JsonParser();

        ModelParser modelParser = new ModelParser();
        modelParser.setErrorHandler(this);
        try {
            Gedcom gedcom = modelParser.parseGedcom(file);
            parsedDocument.json = jsonParser.toJson(gedcom);

            // add each error and warning
            StringBuilder buf = new StringBuilder();
            for (String error : errors) {
                buf.append(error);
                buf.append("\n");
            }
            for (String warning : warnings) {
                buf.append(warning);
                buf.append("\n");
            }
            parsedDocument.warnings = buf.toString();

            // add line numbers to the original gedcom
            buf.setLength(0);
            int lineNumber = 0;
            for (String line : FileUtils.readLines(file)) {
                lineNumber++;
                buf.append(String.format("%-5s", Integer.toString(lineNumber)));
                buf.append(" ");
                buf.append(line);
                buf.append("\n");
            }
            parsedDocument.originalGedcom = buf.toString();
            //parsedDocument.originalGedcom = FileUtils.readFileToString(file);
        }
        catch (SAXParseException e) {
            parsedDocument.warnings = e.getMessage();
        }
        catch (IOException e) {
            parsedDocument.warnings = e.getMessage();
        }

        return parsedDocument;
    }

}
