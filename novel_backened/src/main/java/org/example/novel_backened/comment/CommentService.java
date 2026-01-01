package org.example.novel_backened.comment;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public ApiResponse<List<CommentDto>> list(String novelId) {
        List<CommentDto> list = commentMapper.findByNovelId(novelId);
        return ApiResponse.success(list);
    }

    public ApiResponse<Void> add(String novelId, String userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.error("评论内容不能为空");
        }
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setNovelId(novelId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        commentMapper.insert(comment);
        return ApiResponse.success(null);
    }

    public ApiResponse<Void> delete(String id, String userId) {
        int rows = commentMapper.deleteByIdAndUser(id, userId);
        if (rows == 0) {
            return ApiResponse.error("无法删除该评论");
        }
        return ApiResponse.success(null);
    }
}

