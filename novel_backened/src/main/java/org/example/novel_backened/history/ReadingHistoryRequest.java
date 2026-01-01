package org.example.novel_backened.history;

public class ReadingHistoryRequest {
    private String novelId;
    private Integer chapterNumber;
    private String chapterTitle;

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
}

