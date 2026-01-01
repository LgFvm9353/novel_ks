package org.example.novel_backened.comment;

import java.util.Date;

public class Comment {
    private String id;
    private String novelId;
    private String userId;
    private String content;
    private String managementCategory;
    private Double pricePerThousandWords;
    private Integer ranking;
    private String novelStatus;
    private String statusNote;
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getManagementCategory() {
        return managementCategory;
    }

    public void setManagementCategory(String managementCategory) {
        this.managementCategory = managementCategory;
    }

    public Double getPricePerThousandWords() {
        return pricePerThousandWords;
    }

    public void setPricePerThousandWords(Double pricePerThousandWords) {
        this.pricePerThousandWords = pricePerThousandWords;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getNovelStatus() {
        return novelStatus;
    }

    public void setNovelStatus(String novelStatus) {
        this.novelStatus = novelStatus;
    }

    public String getStatusNote() {
        return statusNote;
    }

    public void setStatusNote(String statusNote) {
        this.statusNote = statusNote;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

