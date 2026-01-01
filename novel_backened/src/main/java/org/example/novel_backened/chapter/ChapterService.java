package org.example.novel_backened.chapter;

import org.example.novel_backened.author.Author;
import org.example.novel_backened.author.AuthorMapper;
import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.history.ReadingHistoryMapper;
import org.example.novel_backened.novel.NovelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChapterService {
    private final ChapterMapper chapterMapper;
    private final NovelMapper novelMapper;
    private final AuthorMapper authorMapper;
    private final ReadingHistoryMapper readingHistoryMapper;

    public ChapterService(ChapterMapper chapterMapper, NovelMapper novelMapper, AuthorMapper authorMapper, ReadingHistoryMapper readingHistoryMapper) {
        this.chapterMapper = chapterMapper;
        this.novelMapper = novelMapper;
        this.authorMapper = authorMapper;
        this.readingHistoryMapper = readingHistoryMapper;
    }

    public ApiResponse<List<ChapterSummaryDto>> listChapters(String novelId) {
        List<ChapterSummaryDto> list = chapterMapper.findByNovelId(novelId);
        return ApiResponse.success(list);
    }

    public ApiResponse<ChapterDetailDto> getChapter(String novelId, int chapterNumber) {
        ChapterDetailDto detail = chapterMapper.findDetail(novelId, chapterNumber);
        if (detail == null) {
            return ApiResponse.error("章节不存在");
        }
        return ApiResponse.success(detail);
    }

    public ApiResponse<List<ChapterSummaryDto>> listChaptersForAuthor(String userId, String role, String novelId) {
        if (!canEditNovel(userId, role, novelId)) {
            return ApiResponse.error("没有权限查看该小说章节");
        }
        return listChapters(novelId);
    }

    public ApiResponse<Void> createChapter(String userId, String role, String novelId, String title, String content) {
        if (!canEditNovel(userId, role, novelId)) {
            return ApiResponse.error("没有权限操作该小说章节");
        }
        if (title == null || title.trim().isEmpty()) {
            return ApiResponse.error("章节标题不能为空");
        }
        int maxNumber = chapterMapper.findMaxChapterNumber(novelId);
        ChapterRecord record = new ChapterRecord();
        record.setId(UUID.randomUUID().toString());
        record.setNovelId(novelId);
        record.setChapterNumber(maxNumber + 1);
        record.setTitle(title.trim());
        record.setContent(content);
        chapterMapper.insert(record);
        return ApiResponse.success(null);
    }

    public ApiResponse<Void> updateChapter(String userId, String role, String novelId, int chapterNumber, String title, String content) {
        if (!canEditNovel(userId, role, novelId)) {
            return ApiResponse.error("没有权限操作该小说章节");
        }
        if (title == null || title.trim().isEmpty()) {
            return ApiResponse.error("章节标题不能为空");
        }
        ChapterRecord record = new ChapterRecord();
        record.setNovelId(novelId);
        record.setChapterNumber(chapterNumber);
        record.setTitle(title.trim());
        record.setContent(content);
        int rows = chapterMapper.update(record);
        if (rows == 0) {
            return ApiResponse.error("章节不存在");
        }
        return ApiResponse.success(null);
    }

    @Transactional
    public ApiResponse<Void> deleteChapter(String userId, String role, String novelId, int chapterNumber) {
        if (!canEditNovel(userId, role, novelId)) {
            return ApiResponse.error("没有权限操作该小说章节");
        }
        
        // Delete related reading history first
        readingHistoryMapper.deleteByChapter(novelId, chapterNumber);
        
        int rows = chapterMapper.delete(novelId, chapterNumber);
        if (rows == 0) {
            return ApiResponse.error("章节不存在");
        }
        return ApiResponse.success(null);
    }

    private boolean canEditNovel(String userId, String role, String novelId) {
        String ownerAuthorId = novelMapper.findAuthorIdById(novelId);
        if (ownerAuthorId == null) {
            return false;
        }
        if (!"author".equals(role)) {
            return false;
        }
        if (userId == null) {
            return false;
        }
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return false;
        }
        return ownerAuthorId.equals(author.getId());
    }
}
