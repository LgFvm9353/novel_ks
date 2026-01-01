package org.example.novel_backened.chapter;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/novels/{novelId}/chapters")
public class ChapterController {
    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping
    public ApiResponse<List<ChapterSummaryDto>> list(@PathVariable("novelId") String novelId) {
        return chapterService.listChapters(novelId);
    }

    @GetMapping("/{chapterNumber}")
    public ApiResponse<ChapterDetailDto> detail(@PathVariable("novelId") String novelId,
                                                @PathVariable("chapterNumber") int chapterNumber) {
        return chapterService.getChapter(novelId, chapterNumber);
    }
}

