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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Webpage {
    public enum Column {
        PAGE_NUMBER, DATE, PAINTING_CAPTION, SONG_TITLE, SONG_LINK, PREV_PAGE_NUMBER, NEXT_PAGE_NUMBER
    }

    private String pageNumber;
    private String nextPageNumber;
    private String prevPageNumber;
    private String date;
    private String paintingCaption;
    private String songTitle;
    private String songLink;
    private final List<Poem> breverias = new ArrayList<Poem>();
    private final List<Poem> sonnets = new ArrayList<Poem>();
    private final List<Poem> others = new ArrayList<Poem>();

    public Webpage(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber (String pageNumber) {
        this.pageNumber = pageNumber;
    }
    public String getNextPageNumber() {
        return nextPageNumber;
    }

    public String getPrevPageNumber() {
        return prevPageNumber;
    }

    public void setPrevPageNumber(String prevPageNumber) { this.prevPageNumber = prevPageNumber; }

    public void setNextPageNumber(String nextPageNumber) { this.nextPageNumber = nextPageNumber; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaintingCaption() {
        return paintingCaption;
    }

    public void setPaintingCaption(String paintingCaption) {
        this.paintingCaption = paintingCaption;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public void addBreveria(Poem poem) {
        breverias.add(poem);
    }

    public void addSonnet(Poem poem) {
        sonnets.add(poem);
    }

    public void addOtherPoem(Poem poem) {
        others.add(poem);
    }

    public List<Poem> getBreverias() {
        return Collections.unmodifiableList(breverias);
    }

    public List<Poem> getSonnets() {
        return sonnets;
    }

    public List<Poem> getOthers() {
        return others;
    }

    @Override
    public String toString() {
        return breverias.size() + " breverias, " + sonnets.size() + " sonnets, " + others.size() + " others";
    }

}
