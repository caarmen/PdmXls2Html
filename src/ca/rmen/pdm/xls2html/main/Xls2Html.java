/*-
The MIT License (MIT)

Copyright (c) 2015 Carmen Alvarez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package ca.rmen.pdm.xls2html.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import ca.rmen.pdm.xls2html.model.Poem;
import ca.rmen.pdm.xls2html.model.Webpage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class Xls2Html {
    private final static String[] MONTHS = new String[] { "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre",
            "noviembre", "diciembre" };

    public static void main(String[] args) throws Throwable {
        int i = 0;
        if(args.length != 2) {
            System.err.println("Usage: Xls2Html <Excel file> <template file>");
            System.err.println("This program will generate a set of HTML files in the same folder as the template file");
            System.exit(1);;
        }
        String excelPath = args[i++];
        String templatePath = args[i++];
        List<Webpage> documents = readExcelFile(excelPath);
        for (Webpage webpage : documents) {
            String htmlPath = templatePath.replaceAll(".ftl$", webpage.getPageNumber() + ".html");
            writeWebpage(webpage, templatePath, htmlPath);
        }
    }

    /**
     * Read the Excel file and return a list of Webpages which we can transform into HTML files.
     */
    private static List<Webpage> readExcelFile(String filePath) {
        try {
            InputStream is = new FileInputStream(filePath);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setEncoding("iso-8859-1");
            Workbook wb = Workbook.getWorkbook(is, wbSettings);
            return readBook(wb);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }

    /**
     * Read the first row in an Excel sheet and return the list of cell values.
     */
    private static List<String> readColumnNames(Sheet sheet) {
        Cell[] headerRow = sheet.getRow(0);
        List<String> columnNames = new ArrayList<String>();
        for (Cell headerCell : headerRow)
            columnNames.add(headerCell.getContents());
        return columnNames;
    }

    /**
     * Read one row in the given Excel sheet, and return a map of the column names to the cell values in this row.
     */
    private static Map<String, String> readRow(Sheet sheet, int rowId, List<String> columnNames) {
        Map<String, String> values = new HashMap<String, String>();
        int columnCount = sheet.getColumns();
        Cell[] row = sheet.getRow(rowId);
        for (int c = 0; c < columnCount; c++) {
            String columnName = columnNames.get(c);
            if (columnName.startsWith("#"))
                continue;
            String cellData = row[c].getContents();
            cellData = clean(cellData);
            values.put(columnName.toUpperCase(Locale.US), cellData);
        }
        return values;
    }

    /**
     * Read the "page" sheet in the Excel file which contains the data about each page (excluding the poem content).
     */
    private static Map<Integer, Webpage> readPageMeta(Workbook wb) {
        Map<Integer, Webpage> result = new HashMap<Integer, Webpage>();
        Sheet pageSheet = wb.getSheet("page");
        List<String> columnNames = readColumnNames(pageSheet);
        int rowCount = pageSheet.getRows();
        for (int r = 1; r < rowCount; r++) {
            Map<String, String> values = readRow(pageSheet, r, columnNames);
            int pageNumber = Integer.parseInt(values.get(Webpage.Column.PAGE_NUMBER.name()));
            Webpage webpage = new Webpage(pageNumber);
            String date = values.get(Webpage.Column.DATE.name());
            String paintingCaption = values.get(Webpage.Column.PAINTING_CAPTION.name());
            String songTitle = values.get(Webpage.Column.SONG_TITLE.name());
            String songLink = values.get(Webpage.Column.SONG_LINK.name());
            webpage.setDate(date);
            webpage.setPaintingCaption(paintingCaption);
            webpage.setSongTitle(songTitle);
            webpage.setSongLink(songLink);
            result.put(pageNumber, webpage);
        }
        return result;
    }

    /**
     * Read the page and poem sheets of an Excel workbook, and return the list of Webpages which are ready to be given to freemarket to generate HTML files.
     */
    private static List<Webpage> readBook(Workbook wb) {
        Map<Integer, Webpage> documents = readPageMeta(wb);

        Sheet poemSheet = wb.getSheet("poem");
        List<String> columnNames = readColumnNames(poemSheet);

        int rowCount = poemSheet.getRows();
        for (int r = 1; r < rowCount; r++) {
            Map<String, String> values = readRow(poemSheet, r, columnNames);
            String title = values.get(Poem.Column.TITLE.name());
            System.out.println(title);
            String poemTypeName = values.get(Poem.Column.POEM_TYPE.name());
            Poem.PoemType poemType = Poem.PoemType.valueOf(poemTypeName.toUpperCase(Locale.US));
            int pageNumber = Integer.parseInt(values.get(Poem.Column.PAGE_NUMBER.name()));
            String poemNumber = values.get(Poem.Column.POEM_NUMBER.name());
            String preContent = values.get(Poem.Column.PRE_CONTENT.name());
            String content = values.get(Poem.Column.CONTENT.name());
            String year = values.get(Poem.Column.YEAR.name());
            int month = Integer.valueOf(values.get(Poem.Column.MONTH.name()));
            int day = Integer.valueOf(values.get(Poem.Column.DAY.name()));
            String location = values.get(Poem.Column.LOCATION.name());

            String locationDate = day >= 1 ? location + ", " + day + " de " + MONTHS[month - 1] + " de " + year : location + ", " + MONTHS[month - 1] + " de "
                    + year;
            Webpage webpage = documents.get(pageNumber);
            if(webpage == null) 
                continue;
            switch (poemType) {
            case BREVERIA:
                webpage.addBreveria(new Poem(title, "Brevería", poemNumber, preContent, content, locationDate));
                break;
            case SONNET:
                webpage.addSonnet(new Poem(title, "Soneto", poemNumber, preContent, content, locationDate));
                break;
            default:
                webpage.addOtherPoem(new Poem(title, null, poemNumber, preContent, content, locationDate));
                break;
            }
        }

        final List<Webpage> result = new ArrayList<Webpage>();
        SortedSet<Integer> pageNumbers = new TreeSet<Integer>();
        pageNumbers.addAll(documents.keySet());
        for (Integer pageNumber : pageNumbers)
            result.add(documents.get(pageNumber));
        return result;
    }

    /**
     * Create one HTML file for the given Webpage.  
     */
    private static void writeWebpage(Webpage webpage, String inputTemplatePath, String outputHTMLPath) throws Throwable {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("webpage", webpage);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(cfg.getIncompatibleImprovements()).build());
        cfg.setDirectoryForTemplateLoading(new File("."));
        Template template = cfg.getTemplate(inputTemplatePath);
        FileWriter writer = new FileWriter(outputHTMLPath);
        template.process(root, writer);
        writer.flush();
        writer.close();
    }

    /**
     * Fix whitespace in the given string.
     */
    private static String clean(String data) {
        if (data == null)
            return data;
        data = data.trim();
        data = data.replaceAll("\\\\n", "\n");
        if (data.isEmpty())
            data = null;
        return data;
    }

}
