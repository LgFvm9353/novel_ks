package org.example.novel_backened.vote;

import org.example.novel_backened.author.Author;
import org.example.novel_backened.author.AuthorMapper;
import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.novel.NovelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VoteService {
    private final VoteMapper voteMapper;
    private final NovelMapper novelMapper;
    private final AuthorMapper authorMapper;

    public VoteService(VoteMapper voteMapper, NovelMapper novelMapper, AuthorMapper authorMapper) {
        this.voteMapper = voteMapper;
        this.novelMapper = novelMapper;
        this.authorMapper = authorMapper;
    }

    public ApiResponse<Void> vote(String novelId, String userId) {
        // Check if user is the author of the novel
        String authorId = novelMapper.findAuthorIdById(novelId);
        if (authorId != null) {
            Author author = authorMapper.findByUserId(userId);
            if (author != null && author.getId().equals(authorId)) {
                return ApiResponse.error("作者不能给自己的作品点赞");
            }
        }

        int count = voteMapper.countUserVote(novelId, userId);
        if (count > 0) {
            return ApiResponse.error("已投过票");
        }
        String id = UUID.randomUUID().toString();
        voteMapper.insertVote(id, userId, novelId);
        voteMapper.increaseVote(novelId);
        return ApiResponse.success(null);
    }

    public ApiResponse<Boolean> checkVoteStatus(String novelId, String userId) {
        int count = voteMapper.countUserVote(novelId, userId);
        return ApiResponse.success(count > 0);
    }
}
