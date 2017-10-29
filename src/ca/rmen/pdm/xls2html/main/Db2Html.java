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

import ca.rmen.pdm.xls2html.model.Poem;
import ca.rmen.pdm.xls2html.model.Webpage;
import ca.rmen.pdm.xls2html.model.WebpageId;
import com.sun.webkit.WebPage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Db2Html {

    public static void main(String[] args) throws Throwable {
        int i = 0;
        if(args.length != 3) {
            System.err.println("Usage: Db2Html <db file> <template file> <index template file>");
            System.err.println("This program will generate an HTML file in the same folder as the template file");
            System.exit(1);
        }
        String dbPath = args[i++];
        String templatePath = args[i++];
        String indexTemplatePath = args[i++];
        List<WebpageId> webpageIds = readWebpageIds(dbPath);
        writeIndexPages(webpageIds, indexTemplatePath);
        for (int id = 0; id < webpageIds.size(); id++) {
            WebpageId webpageId = webpageIds.get(id);
            Webpage document = readDBFile(dbPath, webpageId);
            if (id < webpageIds.size() - 1) {
                WebpageId previousPage = webpageIds.get(id + 1);
                document.setPrevPageNumber("/" + previousPage.getYear() + "/poemas" + previousPage.getId() + ".html");
            }
            if (id > 0) {
                WebpageId nextPage = webpageIds.get(id - 1);
                document.setNextPageNumber("/" + nextPage.getYear() + "/poemas" + nextPage.getId() + ".html");
            }
            document.setPageNumber(webpageId.getTitle());
            File outputRootDir = new File(templatePath).getParentFile();
            File outputDir = new File(outputRootDir, String.valueOf(webpageId.getYear()));
            if (!outputDir.exists()) outputDir.mkdirs();
            File outputFile = new File(outputDir, "poemas" + webpageId.getId() + ".html");
            writeWebpage(document, templatePath, outputFile.getAbsolutePath());
        }
    }

    private static Connection getDbConnection(String dbPath) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private static List<WebpageId> readWebpageIds(String dbPath) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement("SELECT web, year, month, title FROM webs ORDER BY CAST(web AS INTEGER) DESC, web DESC");
        List<WebpageId> webpageIds = new ArrayList<WebpageId>();
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String webpageId = resultSet.getString("web");
            int year = resultSet.getInt("year");
            int month = resultSet.getInt("month");
            String title = resultSet.getString("title");
            webpageIds.add(new WebpageId(webpageId, year, month, title));
        }
        resultSet.close();
        connection.close();
        return webpageIds;
    }
    /**
     * Read the DB file and return a Webpage which we can transform into an HTML file.
     */
    private static Webpage readDBFile(String filePath, WebpageId webpageId) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(filePath);
        Webpage webpage = new Webpage("");
        PreparedStatement statement = connection.prepareStatement("SELECT poem_number, content FROM breverias WHERE web = ? ORDER BY CAST(poem_number AS INTEGER)");
        statement.setString(1, webpageId.getId());
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String poemNumber = resultSet.getString("poem_number");
            String content = resultSet.getString("content");
            webpage.addBreveria(new Poem(poemNumber, poemNumber, poemNumber, null, content, null, null, null));
        }
        resultSet.close();
        statement.close();

        statement = connection.prepareStatement("SELECT poem_number, title, pre_content, content, location, year, month, day FROM sonetos WHERE web = ? ORDER BY CAST(poem_number AS INTEGER)");
        statement.setString(1, webpageId.getId());
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String poemNumber = resultSet.getString("poem_number");
            String title = resultSet.getString("title");
            String preContent = resultSet.getString("pre_content");
            String content = resultSet.getString("content");
            int year = resultSet.getInt("year");
            int month = resultSet.getInt("month");
            int day = resultSet.getInt("day");
            String location = resultSet.getString("location");
            webpage.addSonnet(
                    new Poem(title, Poem.PoemType.SONNET.name(), poemNumber, preContent, content,
                            formatLocationDate(year, month, day, location), null, null)
            );
        }
        resultSet.close();
        statement.close();

        statement = connection.prepareStatement("SELECT title, pre_content, content, location, year, month, day FROM otros_poemas WHERE web = ? ORDER BY year, month, day");
        statement.setString(1, webpageId.getId());
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String title = resultSet.getString("title");
            String preContent = resultSet.getString("pre_content");
            String content = resultSet.getString("content");
            int year = resultSet.getInt("year");
            int month = resultSet.getInt("month");
            int day = resultSet.getInt("day");
            String location = resultSet.getString("location");
            webpage.addOtherPoem(
                    new Poem(title, Poem.PoemType.OTHER.name(), null, preContent, content,
                            formatLocationDate(year, month, day, location), null, null)
            );
        }
        resultSet.close();
        statement.close();
        return webpage;
    }

    private static void writeIndexPages(List<WebpageId> webpageIds, String templatePath) throws IOException, TemplateException {
        Map<Integer, List<WebpageId>> webpageIdsPerYear = new HashMap<Integer, List<WebpageId>>();
        for (WebpageId webpageId : webpageIds) {
            List<WebpageId> pages = webpageIdsPerYear.get(webpageId.getYear());
            if (pages == null) {
                pages = new ArrayList<WebpageId>();
                webpageIdsPerYear.put(webpageId.getYear(), pages);
            }
            pages.add(webpageId);
        }
        for (int year : webpageIdsPerYear.keySet()) {
            List<WebpageId> pages = webpageIdsPerYear.get(year);
            File outputRootDir = new File(templatePath).getParentFile();
            File outputDir = new File(outputRootDir, String.valueOf(year));
            File outputFile = new File(outputDir, "index.html");
            writeIndex(year, pages, templatePath, outputFile.getAbsolutePath());
        }
    }
    private static String formatLocationDate(int year, int month, int day, String location) {
        return location + ", " + day + " de " + formatDate(year, month);
    }
    /**
     * @return the given date formatted in the Spanish locale.
     */
    private static String formatDate(int year, int month) {
        Locale localeES = new Locale.Builder().setLanguage("es").setRegion("es").build();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM' de 'yyyy", localeES);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(calendar.getTime());
    }

    /**
     * Create one HTML file for the given Webpage.  
     */
    private static void writeWebpage(Object webpage, String inputTemplatePath, String outputHTMLPath) throws Throwable {
        System.out.println("Writing " + outputHTMLPath);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("webpage", webpage);
        writePage(root, inputTemplatePath, outputHTMLPath);
    }

    private static void writeIndex(int year, List<WebpageId> webpageIds, String inputTemplatePath, String outputHTMLPath) throws IOException, TemplateException {
        System.out.println("Writing " + outputHTMLPath);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("webpageIds", webpageIds);
        root.put("year", year);
        writePage(root, inputTemplatePath, outputHTMLPath);
    }

    private static void writePage(Map<String,Object> root, String inputTemplatePath, String outputHTMLPath) throws IOException, TemplateException {
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

}
