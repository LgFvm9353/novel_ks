package org.example.novel_backened.chapter;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/author/novels/{novelId}/chapters")
public class AuthorChapterController {
    private final ChapterService chapterService;

    public AuthorChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping
    public ApiResponse<List<ChapterSummaryDto>> list(@PathVariable("novelId") String novelId,
                                                     @RequestAttribute("userId") String userId,
                                                     @RequestAttribute("role") String role) {
        return chapterService.listChaptersForAuthor(userId, role, novelId);
    }

    @PostMapping
    public ApiResponse<Void> create(@PathVariable("novelId") String novelId,
                                    @RequestAttribute("userId") String userId,
                                    @RequestAttribute("role") String role,
                                    @RequestBody Map<String, String> body) {
        String title = body.get("title");
        String content = body.get("content");
        return chapterService.createChapter(userId, role, novelId, title, content);
    }

    @PutMapping("/{chapterNumber}")
    public ApiResponse<Void> update(@PathVariable("novelId") String novelId,
                                    @PathVariable("chapterNumber") int chapterNumber,
                                    @RequestAttribute("userId") String userId,
                                    @RequestAttribute("role") String role,
                                    @RequestBody Map<String, String> body) {
        String title = body.get("title");
        String content = body.get("content");
        return chapterService.updateChapter(userId, role, novelId, chapterNumber, title, content);
    }

    @DeleteMapping("/{chapterNumber}")
    public ApiResponse<Void> delete(@PathVariable("novelId") String novelId,
                                    @PathVariable("chapterNumber") int chapterNumber,
                                    @RequestAttribute("userId") String userId,
                                    @RequestAttribute("role") String role) {
        return chapterService.deleteChapter(userId, role, novelId, chapterNumber);
    }
}

