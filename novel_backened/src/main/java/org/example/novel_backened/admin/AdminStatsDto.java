package org.example.novel_backened.admin;

public class AdminStatsDto {
    private long totalUsers;
    private long totalAuthors;
    private long totalNovels;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalAuthors() {
        return totalAuthors;
    }

    public void setTotalAuthors(long totalAuthors) {
        this.totalAuthors = totalAuthors;
    }

    public long getTotalNovels() {
        return totalNovels;
    }

    public void setTotalNovels(long totalNovels) {
        this.totalNovels = totalNovels;
    }
}

