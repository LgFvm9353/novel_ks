package org.example.novel_backened.history;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingHistoryService {
    private final ReadingHistoryMapper readingHistoryMapper;

    public ReadingHistoryService(ReadingHistoryMapper readingHistoryMapper) {
        this.readingHistoryMapper = readingHistoryMapper;
    }

    public ApiResponse<List<ReadingHistoryDto>> list(String userId) {
        List<ReadingHistoryDto> list = readingHistoryMapper.findByUserId(userId);
        return ApiResponse.success(list);
    }

    public ApiResponse<Void> save(String userId, String novelId, Integer chapterNumber, String chapterTitle) {
        if (novelId == null || chapterNumber == null) {
            return ApiResponse.error("参数不完整");
        }
        String id = readingHistoryMapper.findIdByUserAndNovel(userId, novelId);
        if (id == null) {
            ReadingHistory history = new ReadingHistory();
            history.setId(UUID.randomUUID().toString());
            history.setUserId(userId);
            history.setNovelId(novelId);
            history.setChapterNumber(chapterNumber);
            history.setChapterTitle(chapterTitle);
            readingHistoryMapper.insert(history);
        } else {
            ReadingHistory history = new ReadingHistory();
            history.setId(id);
            history.setChapterNumber(chapterNumber);
            history.setChapterTitle(chapterTitle);
            readingHistoryMapper.update(history);
        }
        return ApiResponse.success(null);
    }

    public ApiResponse<Void> delete(String userId, String id) {
        int rows = readingHistoryMapper.deleteByIdAndUser(id, userId);
        if (rows == 0) {
            return ApiResponse.error("删除失败");
        }
        return ApiResponse.success(null);
    }

    public ApiResponse<Void> clear(String userId) {
        readingHistoryMapper.deleteAllByUser(userId);
        return ApiResponse.success(null);
    }
}

