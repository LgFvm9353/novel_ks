package org.example.novel_backened.novel;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novels")
public class NovelController {
    private final NovelService novelService;

    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return novelService.list(page, size);
    }

    @GetMapping("/ranking")
    public ApiResponse<List<NovelListItemDto>> ranking(@RequestParam(defaultValue = "10") int limit) {
        return novelService.ranking(limit);
    }

    @GetMapping("/{id}")
    public ApiResponse<NovelDetailDto> detail(@PathVariable("id") String id) {
        return novelService.detail(id);
    }
}

