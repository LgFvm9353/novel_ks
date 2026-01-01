package org.example.novel_backened.chapter;

public class ChapterDetailDto {
    private String novelId;
    private Integer chapterNumber;
    private String title;
    private String content;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

