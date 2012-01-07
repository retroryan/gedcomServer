package controllers;

import models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.parser.ModelParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;

public class Files extends Controller {

    public static void uploadForm() {
        render();
    }

    public static void parsedGedcom(File file) throws IOException, SAXException {

        Gedcom2Json gedcom2Json = new Gedcom2Json();
        ParsedDocument parsedDocument = gedcom2Json.parseGedcom(file);
        render(parsedDocument);

    }


}
