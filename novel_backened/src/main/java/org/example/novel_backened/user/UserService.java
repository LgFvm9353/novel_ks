package org.example.novel_backened.user;

import org.example.novel_backened.author.Author;
import org.example.novel_backened.author.AuthorMapper;
import org.example.novel_backened.chapter.ChapterMapper;
import org.example.novel_backened.comment.CommentMapper;
import org.example.novel_backened.history.ReadingHistoryMapper;
import org.example.novel_backened.novel.NovelListItemDto;
import org.example.novel_backened.novel.NovelMapper;
import org.example.novel_backened.util.JwtUtils;
import org.example.novel_backened.vote.VoteMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ReadingHistoryMapper readingHistoryMapper;
    private final CommentMapper commentMapper;
    private final VoteMapper voteMapper;
    private final AuthorMapper authorMapper;
    private final NovelMapper novelMapper;
    private final ChapterMapper chapterMapper;

    public UserService(UserMapper userMapper, JwtUtils jwtUtils,
                       ReadingHistoryMapper readingHistoryMapper,
                       CommentMapper commentMapper,
                       VoteMapper voteMapper,
                       AuthorMapper authorMapper,
                       NovelMapper novelMapper,
                       ChapterMapper chapterMapper) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.readingHistoryMapper = readingHistoryMapper;
        this.commentMapper = commentMapper;
        this.voteMapper = voteMapper;
        this.authorMapper = authorMapper;
        this.novelMapper = novelMapper;
        this.chapterMapper = chapterMapper;
    }

    public UserDto register(String username, String rawPassword) {
        User existing = userMapper.findByUsername(username);
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("reader");
        userMapper.insert(user);
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    public LoginResponse login(String username, String rawPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        UserDto profile = new UserDto(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(profile);
        return response;
    }

    public UserDto getCurrentUser(String userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    @Transactional
    public void deleteUser(String userId) {
        // 1. Delete Reading History
        readingHistoryMapper.deleteAllByUser(userId);

        // 2. Delete Comments
        commentMapper.deleteByUserId(userId);

        // 3. Delete Votes
        voteMapper.deleteByUserId(userId);

        // 4. Check if user is Author
        Author author = authorMapper.findByUserId(userId);
        if (author != null) {
            // Get all novels by author
            List<NovelListItemDto> novels = novelMapper.findByAuthor(author.getId());
            for (NovelListItemDto novel : novels) {
                String novelId = novel.getId();
                // Delete Chapters
                chapterMapper.deleteByNovelId(novelId);
                // Delete Comments on Novel
                commentMapper.deleteByNovelId(novelId);
                // Delete ReadingHistory on Novel
                readingHistoryMapper.deleteByNovelId(novelId);
                // Delete Votes on Novel
                voteMapper.deleteByNovelId(novelId);
                // Delete Novel
                novelMapper.deleteById(novelId);
            }
            // Delete Author
            authorMapper.deleteById(author.getId());
        }

        // 5. Delete User
        userMapper.deleteById(userId);
    }
}
