package org.example.novel_backened.author;

import org.example.novel_backened.chapter.ChapterMapper;
import org.example.novel_backened.comment.CommentMapper;
import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.history.ReadingHistoryMapper;
import org.example.novel_backened.novel.Category;
import org.example.novel_backened.novel.CategoryMapper;
import org.example.novel_backened.novel.Novel;
import org.example.novel_backened.novel.NovelListItemDto;
import org.example.novel_backened.novel.NovelMapper;
import org.example.novel_backened.user.User;
import org.example.novel_backened.user.UserMapper;
import org.example.novel_backened.vote.VoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorService {
    private final AuthorMapper authorMapper;
    private final UserMapper userMapper;
    private final NovelMapper novelMapper;
    private final CategoryMapper categoryMapper;
    private final ChapterMapper chapterMapper;
    private final CommentMapper commentMapper;
    private final ReadingHistoryMapper readingHistoryMapper;
    private final VoteMapper voteMapper;

    public AuthorService(AuthorMapper authorMapper, UserMapper userMapper, NovelMapper novelMapper,
                         CategoryMapper categoryMapper, ChapterMapper chapterMapper,
                         CommentMapper commentMapper, ReadingHistoryMapper readingHistoryMapper,
                         VoteMapper voteMapper) {
        this.authorMapper = authorMapper;
        this.userMapper = userMapper;
        this.novelMapper = novelMapper;
        this.categoryMapper = categoryMapper;
        this.chapterMapper = chapterMapper;
        this.commentMapper = commentMapper;
        this.readingHistoryMapper = readingHistoryMapper;
        this.voteMapper = voteMapper;
    }

    public ApiResponse<AuthorDto> becomeAuthor(String userId, String role, String name, String bio) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        if ("admin".equals(role)) {
            return ApiResponse.error("管理员不能申请作者");
        }
        if ("author".equals(role)) {
            return ApiResponse.error("已经是作者");
        }
        Author existing = authorMapper.findByUserId(userId);
        if (existing != null) {
            return ApiResponse.error("申请已提交，正在审核中");
        }
        Author author = new Author();
        author.setId(UUID.randomUUID().toString());
        author.setUserId(userId);
        if (name == null || name.trim().isEmpty()) {
            author.setName(user.getUsername());
        } else {
            author.setName(name.trim());
        }
        author.setBio(bio);
        authorMapper.insert(author);
        AuthorDto dto = toDto(author);
        return ApiResponse.success(dto);
    }

    public ApiResponse<AuthorDto> getCurrentAuthor(String userId, String role) {
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return ApiResponse.error("还不是作者");
        }
        if (!"author".equals(role)) {
            return ApiResponse.error("申请待审核");
        }
        return ApiResponse.success(toDto(author));
    }

    public ApiResponse<List<NovelListItemDto>> myNovels(String userId, String role) {
        if (!"author".equals(role)) {
            return ApiResponse.error("还不是作者");
        }
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return ApiResponse.error("还不是作者");
        }
        List<NovelListItemDto> list = novelMapper.findByAuthor(author.getId());
        return ApiResponse.success(list);
    }

    public ApiResponse<String> createNovel(String userId, String role, NovelEditRequest request) {
        if (!"author".equals(role)) {
            return ApiResponse.error("还不是作者");
        }
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return ApiResponse.error("还不是作者");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ApiResponse.error("标题不能为空");
        }
        if (request.getCategoryId() == null || request.getCategoryId().trim().isEmpty()) {
            return ApiResponse.error("分类不能为空");
        }
        Category category = categoryMapper.findById(request.getCategoryId());
        if (category == null) {
            return ApiResponse.error("分类不存在");
        }
        Novel novel = new Novel();
        novel.setId(UUID.randomUUID().toString());
        novel.setTitle(request.getTitle().trim());
        novel.setAuthorId(author.getId());
        novel.setCategoryId(request.getCategoryId());
        novel.setDescription(request.getDescription());
        novel.setCoverImage(request.getCoverImage());
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            novel.setStatus("连载中");
        } else {
            novel.setStatus(request.getStatus().trim());
        }
        novel.setTotalChapters(0);
        novel.setTotalPages(0);
        novel.setVoteCount(0);
        try {
            novelMapper.insert(novel);
        } catch (Exception e) {
            return ApiResponse.error("创建小说失败: " + e.getMessage());
        }
        return ApiResponse.success(novel.getId());
    }

    @Transactional
    public ApiResponse<Void> deleteNovel(String userId, String role, String novelId) {
        if (!"author".equals(role)) {
            return ApiResponse.error("没有权限删除该小说");
        }
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return ApiResponse.error("没有权限删除该小说");
        }
        String ownerAuthorId = novelMapper.findAuthorIdById(novelId);
        if (ownerAuthorId == null || !ownerAuthorId.equals(author.getId())) {
            return ApiResponse.error("没有权限删除该小说");
        }
        
        // Delete related data first
        chapterMapper.deleteByNovelId(novelId);
        commentMapper.deleteByNovelId(novelId);
        readingHistoryMapper.deleteByNovelId(novelId);
        voteMapper.deleteByNovelId(novelId);
        
        novelMapper.deleteById(novelId);
        return ApiResponse.success(null);
    }

    public ApiResponse<Void> updateNovel(String userId, String role, String novelId, NovelEditRequest request) {
        if (!"author".equals(role)) {
            return ApiResponse.error("没有权限编辑该小说");
        }
        Author author = authorMapper.findByUserId(userId);
        String ownerAuthorId = novelMapper.findAuthorIdById(novelId);
        if (ownerAuthorId == null) {
            return ApiResponse.error("小说不存在");
        }
        if (author == null || !ownerAuthorId.equals(author.getId())) {
            return ApiResponse.error("没有权限编辑该小说");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ApiResponse.error("标题不能为空");
        }
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setTitle(request.getTitle().trim());
        novel.setCategoryId(request.getCategoryId());
        novel.setDescription(request.getDescription());
        novel.setCoverImage(request.getCoverImage());
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            novel.setStatus("连载中");
        } else {
            novel.setStatus(request.getStatus().trim());
        }
        novelMapper.update(novel);
        return ApiResponse.success(null);
    }

    private AuthorDto toDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBio(author.getBio());
        return dto;
    }
}
