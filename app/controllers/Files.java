package controllers;

import models.Document;
import models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.GedcomTag;
import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.parser.ModelParser;
import org.folg.gedcom.parser.TreeParser;
import org.xml.sax.SAXException;
import play.libs.MimeTypes;
import play.mvc.Controller;

import java.io.*;
import java.util.List;

public class Files extends Controller {

    public static void uploadForm() {
        render();
    }

    public static void parsedGedcom(File file, boolean useTreeParser) throws IOException, SAXException {

/*
        final Document doc = new Document();
        doc.fileName = file.getName();
        doc.contentType = MimeTypes.getContentType(file.getName());
        doc.save();
*/

        ParsedDocument parsedDocument = new ParsedDocument();
        JsonParser jsonParser = new JsonParser();

        if (useTreeParser) {
            TreeParser treeParser = new TreeParser();
            List<GedcomTag> gedcomTags = treeParser.parseGedcom(file);
            parsedDocument.json = jsonParser.toJson(gedcomTags);
        } else {
            ModelParser modelParser = new ModelParser();
            Gedcom gedcom = modelParser.parseGedcom(file);
            parsedDocument.json = jsonParser.toJson(gedcom);
        }

        parsedDocument.originalGedcom = FileUtils.readFileToString(file);
        render(parsedDocument);

    }

    public static void downloadFile(long id) throws FileNotFoundException {
        final Document doc = Document.findById(id);
        notFoundIfNull(doc);
        response.setContentTypeIfNotSet(doc.contentType);

        File file = new File(doc.fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        renderBinary(bufferedInputStream);
    }


}
