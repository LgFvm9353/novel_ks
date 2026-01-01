package org.example.novel_backened.comment;

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
@RequestMapping("/api/novels/{novelId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<List<CommentDto>> list(@PathVariable("novelId") String novelId) {
        return commentService.list(novelId);
    }

    @PostMapping
    public ApiResponse<Void> add(@PathVariable("novelId") String novelId,
                                 @RequestAttribute("userId") String userId,
                                 @RequestBody CommentRequest request) {
        return commentService.add(novelId, userId, request.getContent());
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(@PathVariable("commentId") String commentId,
                                    @RequestAttribute("userId") String userId) {
        return commentService.delete(commentId, userId);
    }
}

