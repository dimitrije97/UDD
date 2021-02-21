package com.example.boilerplate.handlers;

import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.lucene.document.DateTools;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHandler {

    public IndexUnit getIndexUnit(File file) {
        IndexUnit retVal = new IndexUnit();
        try {
            PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
            parser.parse();
            String content = getText(parser);
            retVal.setContent(content);

            PDDocument pdf = parser.getPDDocument();
            PDDocumentInformation info = pdf.getDocumentInformation();

            String title = "" + info.getTitle();
            retVal.setTitle(title);

            String keywords = "" + info.getKeywords();
            retVal.setKeywords(keywords);

            retVal.setFilename(file.getCanonicalPath());

            //TODO: file date

            pdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    public String getText(File file) {
        try {
            PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
            parser.parse();
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(parser.getPDDocument());
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getText(PDFParser parser) {
        try {
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(parser.getPDDocument());
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
