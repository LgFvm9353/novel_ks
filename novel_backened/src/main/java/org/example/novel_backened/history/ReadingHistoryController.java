package org.example.novel_backened.history;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reader/history")
public class ReadingHistoryController {
    private final ReadingHistoryService readingHistoryService;

    public ReadingHistoryController(ReadingHistoryService readingHistoryService) {
        this.readingHistoryService = readingHistoryService;
    }

    @GetMapping
    public ApiResponse<List<ReadingHistoryDto>> list(@RequestAttribute("userId") String userId) {
        return readingHistoryService.list(userId);
    }

    @PostMapping
    public ApiResponse<Void> save(@RequestAttribute("userId") String userId,
                                  @RequestBody ReadingHistoryRequest request) {
        return readingHistoryService.save(userId, request.getNovelId(), request.getChapterNumber(), request.getChapterTitle());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestAttribute("userId") String userId,
                                    @PathVariable("id") String id) {
        return readingHistoryService.delete(userId, id);
    }

    @DeleteMapping
    public ApiResponse<Void> clear(@RequestAttribute("userId") String userId) {
        return readingHistoryService.clear(userId);
    }
}

