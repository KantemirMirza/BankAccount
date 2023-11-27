package com.kani.bankaccount.test;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Slf4j
public class BankStatementTest {
    private static final String FILE = "C:\\Users\\Admin\\Documents\\BankStatement.pdf";
    public static void generateStatement() throws FileNotFoundException, DocumentException {
        Rectangle rectangle = new Rectangle(PageSize.A4);

        Document document = new Document(rectangle);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(new Paragraph("Sample text"));
        document.add(new Chunk("The Bank Account Statement"));
        document.add(new Phrase("Enjoy"));

        document.add(new Paragraph(" "));

        Anchor anchor = new Anchor("HAMBURG");
        anchor.setReference("https://github.com/KantemirMirza");

        List orderedList = new List(List.ORDERED);
        orderedList.add(new ListItem("HAMBURG"));
        orderedList.add(new ListItem("HAMBURG"));

        List unOrderedList = new List(List.UNORDERED);
        unOrderedList.add(new ListItem("Testing"));
        unOrderedList.add(new ListItem("Testing2"));

        log.info("List");
        String[] firstNamesArray = {"Hamburg", "Hamburg", "Hamburg"};
        String[] lastNamesArray = {"London", "London", "London"};
        log.info("setting table to 3 columns");
        PdfPTable table = new PdfPTable(3);
        PdfPCell serialNo = new PdfPCell(new Paragraph("H/L"));
        PdfPCell names = new PdfPCell(new Paragraph("FirstName"));
        PdfPCell lastname = new PdfPCell(new Paragraph("Lastname"));

        table.addCell(serialNo);
        table.addCell(names);
        table.addCell(lastname);
        for (int i=0; i< firstNamesArray.length; i++){
            PdfPCell serialNo1 = new PdfPCell(new Paragraph(String.valueOf(i+1)));
            PdfPCell firstName = new PdfPCell(new Paragraph(firstNamesArray[i]));
            PdfPCell lastName = new PdfPCell(new Paragraph(lastNamesArray[i]));


            table.addCell(serialNo1).setBackgroundColor(BaseColor.BLUE);
            table.addCell(firstName);
            table.addCell(lastName);
        }

        document.add(table);
        document.add(orderedList);
        document.add(unOrderedList);

        document.add(anchor);

        document.close();

        log.info("File has been created!");
    }

    public static void main(String[] args) throws DocumentException, FileNotFoundException {
        generateStatement();
    }
}
