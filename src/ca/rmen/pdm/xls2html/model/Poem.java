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
package ca.rmen.pdm.xls2html.model;

import java.text.Normalizer;

public class Poem {
    public enum Column {
        TITLE, POEM_TYPE, POEM_NUMBER, PRE_CONTENT, CONTENT, LOCATION, YEAR, MONTH, DAY, PAGE_NUMBER

    }

    public enum PoemType {
        BREVERIA, SONNET, OTHER
    }

    private final String title;
    private final String poemType;
    private final String poemNumber;
    private final String preContent;
    private final String content;
    private final String locationDate;

    public Poem(String title, String poemType, String poemNumber, String preContent, String content, String locationDate) {
        this.title = title;
        this.poemType = poemType;
        this.poemNumber = poemNumber;
        this.preContent = preContent;
        this.content = content;
        this.locationDate = locationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        StringBuilder buffer = new StringBuilder();
        if (poemType != null)
            buffer.append(poemType);
        if (poemNumber != null)
            buffer.append(poemNumber);
        if (title != null)
            buffer.append(title);
        String result = Normalizer.normalize(buffer.toString(), Normalizer.Form.NFD);
        result = result.replaceAll("[^0-9a-zA-Z]", "");
        result = result.replaceAll("\\p{M}", "");
        result = result.toLowerCase();
        return result;
    }

    public String getPoemType() {
        return poemType;
    }

    public String getPoemNumber() {
        return poemNumber;
    }

    public String getPreContent() {
        return preContent;
    }

    public String getContent() {
        return content;
    }

    public String getLocationDate() {
        return locationDate;
    }

}
