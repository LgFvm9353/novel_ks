package org.example.novel_backened.author;

import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.novel.NovelListItemDto;
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

@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/become")
    public ApiResponse<AuthorDto> become(@RequestAttribute("userId") String userId,
                                         @RequestAttribute("role") String role,
                                         @RequestBody AuthorRequest request) {
        return authorService.becomeAuthor(userId, role, request.getName(), request.getBio());
    }

    @GetMapping("/me")
    public ApiResponse<AuthorDto> me(@RequestAttribute("userId") String userId,
                                     @RequestAttribute("role") String role) {
        return authorService.getCurrentAuthor(userId, role);
    }

    @GetMapping("/novels")
    public ApiResponse<List<NovelListItemDto>> myNovels(@RequestAttribute("userId") String userId,
                                                        @RequestAttribute("role") String role) {
        return authorService.myNovels(userId, role);
    }

    @PostMapping("/novels")
    public ApiResponse<String> createNovel(@RequestAttribute("userId") String userId,
                                           @RequestAttribute("role") String role,
                                           @RequestBody NovelEditRequest request) {
        return authorService.createNovel(userId, role, request);
    }

    @PutMapping("/novels/{id}")
    public ApiResponse<Void> updateNovel(@RequestAttribute("userId") String userId,
                                         @RequestAttribute("role") String role,
                                         @PathVariable("id") String id,
                                         @RequestBody NovelEditRequest request) {
        return authorService.updateNovel(userId, role, id, request);
    }

    @DeleteMapping("/novels/{id}")
    public ApiResponse<Void> deleteNovel(@RequestAttribute("userId") String userId,
                                         @RequestAttribute("role") String role,
                                         @PathVariable("id") String id) {
        return authorService.deleteNovel(userId, role, id);
    }
}
