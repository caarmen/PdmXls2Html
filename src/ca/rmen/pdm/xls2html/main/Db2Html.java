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
import com.sun.webkit.WebPage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
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
        if(args.length != 9) {
            System.err.println("Usage: Db2Html <db file> <breveria start> <breveria end> <sonnet start> <sonnet end> <page title> <prev page> <next page> <template file>");
            System.err.println("This program will generate an HTML file in the same folder as the template file");
            System.exit(1);
        }
        String dbPath = args[i++];
        int breveriaStart = Integer.parseInt(args[i++]);
        int breveriaEnd = Integer.parseInt(args[i++]);
        int sonnetStart = Integer.parseInt(args[i++]);
        int sonnetEnd = Integer.parseInt(args[i++]);
        String pageTitle = args[i++];
        String prevPageNumber = args[i++];
        String nextPageNumber = args[i++];
        String templatePath = args[i++];
        Webpage document = readDBFile(dbPath, breveriaStart, breveriaEnd, sonnetStart, sonnetEnd);
        document.setPageNumber(pageTitle);
        document.setPrevPageNumber(prevPageNumber);
        document.setNextPageNumber(nextPageNumber);
        String htmlPath = templatePath.replaceAll(".ftl$", ".html");
        writeWebpage(document, templatePath, htmlPath);
    }

    private static Connection getDbConnection(String dbPath) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    /**
     * Read the DB file and return a list of Webpages which we can transform into HTML files. The first Webpage is the index.
     */
    private static Webpage readDBFile(String filePath, int breveriaStart, int breveriaEnd, int sonnetStart, int sonnetEnd) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(filePath);
        Webpage webpage = new Webpage("");
        PreparedStatement statement = connection.prepareStatement("SELECT poem_number, content FROM breverias WHERE CAST(poem_number AS INTEGER) >= ? and CAST(poem_number AS INTEGER) <= ? ORDER BY CAST(poem_number AS INTEGER)");
        statement.setInt(1, breveriaStart);
        statement.setInt(2, breveriaEnd);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String poemNumber = resultSet.getString("poem_number");
            String content = resultSet.getString("content");
            webpage.addBreveria(new Poem(poemNumber, poemNumber, poemNumber, null, content, null, null, null));
        }
        statement.close();
        statement = connection.prepareStatement("SELECT poem_number, title, pre_content, content, location, year, month, day FROM sonetos WHERE CAST(poem_number AS INTEGER) >= ? and CAST(poem_number AS INTEGER) <= ? ORDER BY CAST(poem_number AS INTEGER)");
        statement.setInt(1, sonnetStart);
        statement.setInt(2, sonnetEnd);
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
        statement.close();
        return webpage;
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
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(calendar.getTime());
    }

    /**
     * Create one HTML file for the given Webpage.  
     */
    private static void writeWebpage(Object webpage, String inputTemplatePath, String outputHTMLPath) throws Throwable {
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

}
