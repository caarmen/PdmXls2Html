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

import ca.rmen.pdm.xls2html.model.PageCollection;
import ca.rmen.pdm.xls2html.model.Poem;
import ca.rmen.pdm.xls2html.model.Webpage;
import ca.rmen.pdm.xls2html.model.Page;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Db2Html {

    public static void main(String[] args) throws Throwable {
        int i = 0;
        if (args.length != 5) {
            System.err.println("Usage: Db2Html <db file> <collection ids csv> <template file> <index template file> <DESC|ASC>");
            System.err.println("This program will generate an HTML file in the same folder as the template file");
            System.exit(1);
        }
        String dbPath = args[i++];
        String[] collectionIds = args[i++].split(",");
        String templatePath = args[i++];
        String indexTemplatePath = args[i++];
        String sortOrder = args[i++];

        List<PageCollection> collections = readCollections(dbPath, collectionIds, sortOrder);
        Map<PageCollection, List<Page>> pagesPerCollection = readCollectionsPages(dbPath, collections, sortOrder);
        writeIndexPages(pagesPerCollection, indexTemplatePath);
        for (int cid = 0; cid < collections.size(); cid++) {
            PageCollection collection = collections.get(cid);
            List<Page> pages = pagesPerCollection.get(collection);
            for (int pid = 0; pid < pages.size(); pid++) {
                Page page = pages.get(pid);
                File outputRootDir = new File(templatePath).getParentFile();
                File outputDir = new File(outputRootDir, String.valueOf(collection.getId()));
                if (!outputDir.exists()) outputDir.mkdirs();
                File outputFile = new File(outputDir, page.getId() + ".html");
                Webpage document = readDBFile(dbPath, page);
                String nextPageId = getNextPageId(collections, pagesPerCollection, cid, pid);
                String prevPageId = getPrevPageId(collections, pagesPerCollection, cid, pid);
                if (nextPageId != null) {
                    if ("ASC".equals(sortOrder)) {
                        document.setNextPageNumber(nextPageId);
                    } else {
                        document.setPrevPageNumber(nextPageId);
                    }
                }
                if (prevPageId != null) {
                    if ("ASC".equals(sortOrder)) {
                        document.setPrevPageNumber(prevPageId);
                    } else {
                        document.setNextPageNumber(prevPageId);
                    }
                }
                document.setPageNumber(page.getTitle());
                writeWebpage(collection, document, templatePath, outputFile.getAbsolutePath());
            }
        }
    }

    private static String getPrevPageId(List<PageCollection> collections, Map<PageCollection, List<Page>> pagesPerCollection, int collectionIndex, int pageIndex) {
        PageCollection currentCollection = collections.get(collectionIndex);
        List<Page> pagesInCurrentCollection = pagesPerCollection.get(currentCollection);
        if (pageIndex > 0) {
            Page prevPage = pagesInCurrentCollection.get(pageIndex - 1);
            return "/" + currentCollection.getId()+ "/" + prevPage.getId() + ".html";
        }
        if (collectionIndex > 0) {
            PageCollection prevCollection = collections.get(collectionIndex - 1);
            List<Page> pagesInPrevCollection = pagesPerCollection.get(prevCollection);
            Page prevPage = pagesInPrevCollection.get(pagesInPrevCollection.size() - 1);
            return "/" + prevCollection.getId() + "/" + prevPage.getId() + ".html";
        }
        return null; // This is the first page
    }

    private static String getNextPageId(List<PageCollection> collections, Map<PageCollection, List<Page>> pagesPerCollection, int collectionIndex, int pageIndex) {
        PageCollection currentCollection = collections.get(collectionIndex);
        List<Page> pagesInCurrentCollection = pagesPerCollection.get(currentCollection);
        if (pageIndex < pagesInCurrentCollection.size() - 1) {
            Page nextPage = pagesInCurrentCollection.get(pageIndex + 1);
            return "/" + currentCollection.getId()+ "/" + nextPage.getId() + ".html";
        }
        if (collectionIndex < collections.size() - 1) {
            PageCollection nextCollection = collections.get(collectionIndex + 1);
            List<Page> pagesInNextCollection = pagesPerCollection.get(nextCollection);
            Page nextPage = pagesInNextCollection.get(0);
            return "/" + nextCollection.getId()+ "/" + nextPage.getId() + ".html";
        }
        return null; // This is the last page
    }

    private static Connection getDbConnection(String dbPath) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private static List<PageCollection> readCollections(String dbPath, String[] collectionIds, String sortOrder) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement("SELECT collection_id, title FROM collections WHERE collection_id IN " + buildInClause(collectionIds.length)
                + " ORDER BY CAST(collection_id AS INTEGER)" + sortOrder + ", collection_id " + sortOrder);
        for (int i = 0; i < collectionIds.length; i++) {
            statement.setString(i + 1, collectionIds[i]);
        }
        List<PageCollection> collections = new ArrayList<PageCollection>();
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String id = resultSet.getString("collection_id");
            String collectionTitle = resultSet.getString("title");
            collections.add(new PageCollection(id, collectionTitle));
        }
        resultSet.close();
        connection.close();
        return collections;
    }

    private static String buildInClause(int count) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (int i = 0; i < count - 1; i++) {
            stringBuilder.append("?,");
        }
        stringBuilder.append("?)");
        return stringBuilder.toString();
    }

    private static Map<PageCollection, List<Page>> readCollectionsPages(String dbPath, List<PageCollection> collections, String sortOrder) throws SQLException, ClassNotFoundException {
        Map<PageCollection, List<Page>> collectionsPages = new LinkedHashMap<PageCollection, List<Page>>();
        for (PageCollection collection : collections) {
            List pages = readPages(dbPath, collection, sortOrder);
            collectionsPages.put(collection, pages);
        }
        return collectionsPages;
    }

    private static List<Page> readPages(String dbPath, PageCollection collection, String sortOrder) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement(
                "SELECT pages.page_id AS page_id, pages.title AS page_title "
                        + "FROM collection_pages JOIN pages on collection_pages.page_id = pages.page_id "
                        + "WHERE collection_pages.collection_id = ? "
                        + "ORDER BY CAST(pages.page_id as INTEGER) " + sortOrder + ", pages.page_id " + sortOrder);
        statement.setString(1, collection.getId());
        List<Page> pages = new ArrayList<Page>();
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String pageId = resultSet.getString("page_id");
            String title = resultSet.getString("page_title");
            pages.add(new Page(pageId, title));
        }
        resultSet.close();
        connection.close();
        return pages;
    }

    private static List<Poem> readBreverias(String dbPath, String pageId) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement(
                "SELECT poem_number, content FROM breverias WHERE poem_number IN "
                        + "(SELECT poem_id FROM page_poems WHERE poem_type='breveria' AND page_id = ? ) "
                        + "ORDER BY CAST(poem_number AS INTEGER) ASC");
        statement.setString(1, pageId);
        ResultSet resultSet = statement.executeQuery();
        List<Poem> breverias = new ArrayList<Poem>();
        while (resultSet.next()) {
            String poemNumber = resultSet.getString("poem_number");
            String content = resultSet.getString("content");
            breverias.add(new Poem(poemNumber, poemNumber, poemNumber, null, content, null, null, null));
        }
        resultSet.close();
        connection.close();
        return breverias;
    }

    private static List<Poem> readSonnets(String dbPath, String pageId) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement(
                "SELECT poem_number, CAST(poem_number AS INTEGER) as poem_number_int, title, pre_content, content, location, year, month, day FROM sonetos WHERE poem_number IN "
                        + "(SELECT poem_id FROM page_poems WHERE poem_type='soneto' AND page_id = ? ) "
                        + " UNION SELECT poem_number, CAST(poem_number AS INTEGER) as poem_number_int, title, pre_content, content, location, year, month, day FROM luminarias_mitos WHERE poem_number IN "
                        + "(SELECT poem_id FROM page_poems WHERE poem_type='LM' AND page_id = ? ) "
                        + "ORDER BY poem_number_int ASC, poem_number ASC");
        statement.setString(1, pageId);
        statement.setString(2, pageId);
        ResultSet resultSet = statement.executeQuery();
        List<Poem> sonetos = new ArrayList<Poem>();
        while (resultSet.next()) {
            String poemNumber = resultSet.getString("poem_number");
            String title = resultSet.getString("title");
            String preContent = resultSet.getString("pre_content");
            String content = resultSet.getString("content");
            int year = resultSet.getInt("year");
            int month = resultSet.getInt("month");
            int day = resultSet.getInt("day");
            String location = resultSet.getString("location");
            sonetos.add(new Poem(title, Poem.PoemType.SONNET.name(), poemNumber, preContent, content,
                    formatLocationDate(year, month, day, location), null, null)
            );
        }
        resultSet.close();
        connection.close();
        return sonetos;
    }

    private static List<Poem> readOtherPoems(String dbPath, String pageId) throws SQLException, ClassNotFoundException {
        Connection connection = getDbConnection(dbPath);
        PreparedStatement statement = connection.prepareStatement("SELECT title, pre_content, content, location, year, month, day FROM otros_poemas WHERE poem_number IN "
                + "(SELECT poem_id FROM page_poems WHERE poem_type='otro' AND page_id = ? ) "
                + "ORDER BY CAST(otros_poemas.year AS INTEGER) ASC, CAST(otros_poemas.month AS INTEGER) ASC, CAST(otros_poemas.day AS INTEGER) ASC");
        statement.setString(1, pageId);
        ResultSet resultSet = statement.executeQuery();
        List<Poem> poemas = new ArrayList<Poem>();
        while (resultSet.next()) {
            String title = resultSet.getString("title");
            String preContent = resultSet.getString("pre_content");
            String content = resultSet.getString("content");
            int year = resultSet.getInt("year");
            int month = resultSet.getInt("month");
            int day = resultSet.getInt("day");
            String location = resultSet.getString("location");
            poemas.add(new Poem(title, Poem.PoemType.OTHER.name(), null, preContent, content,
                    formatLocationDate(year, month, day, location), null, null)
            );
        }
        resultSet.close();
        connection.close();
        return poemas;
    }

    /**
     * Read the DB file and return a Webpage which we can transform into an HTML file.
     */
    private static Webpage readDBFile(String dbPath, Page page) throws SQLException, ClassNotFoundException {
        Webpage webpage = new Webpage(page.getId());
        List<Poem> breverias = readBreverias(dbPath, page.getId());
        for (Poem breveria : breverias) {
            webpage.addBreveria(breveria);
        }
        List<Poem> sonnets = readSonnets(dbPath, page.getId());
        for (Poem sonnet : sonnets) {
            webpage.addSonnet(sonnet);
        }
        List<Poem> otherPoems = readOtherPoems(dbPath, page.getId());
        for (Poem poem : otherPoems) {
            webpage.addOtherPoem(poem);
        }
        return webpage;
    }

    private static void writeIndexPages(Map<PageCollection, List<Page>> collectionsPages, String templatePath) throws IOException, TemplateException {
        for (PageCollection collection : collectionsPages.keySet()) {
            writeIndexPage(collection, collectionsPages.get(collection), templatePath);
        }
    }

    private static void writeIndexPage(PageCollection collection, List<Page> pages, String templatePath) throws IOException, TemplateException {
        File outputRootDir = new File(templatePath).getParentFile();
        File outputDir = new File(outputRootDir, collection.getId());
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File outputFile = new File(outputDir, "index.html");
        System.out.println("Writing " + outputFile);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("pages", pages);
        root.put("collection", collection);
        PageCreator.createPage(root, templatePath, outputFile.getAbsolutePath());
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
    private static void writeWebpage(PageCollection collection, Webpage webpage, String inputTemplatePath, String outputHTMLPath) throws Throwable {
        System.out.println("Writing " + outputHTMLPath);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("webpage", webpage);
        root.put("collection", collection);
        PageCreator.createPage(root, inputTemplatePath, outputHTMLPath);
    }

}
