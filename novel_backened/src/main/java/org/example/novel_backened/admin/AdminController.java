package org.example.novel_backened.admin;

import org.example.novel_backened.author.AuthorAdminDto;
import org.example.novel_backened.author.AuthorMapper;
import org.example.novel_backened.author.Author;
import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.novel.NovelMapper;
import org.example.novel_backened.user.User;
import org.example.novel_backened.user.UserMapper;
import org.example.novel_backened.user.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserMapper userMapper;
    private final NovelMapper novelMapper;
    private final AuthorMapper authorMapper;
    private final UserService userService;

    public AdminController(UserMapper userMapper, NovelMapper novelMapper, AuthorMapper authorMapper, UserService userService) {
        this.userMapper = userMapper;
        this.novelMapper = novelMapper;
        this.authorMapper = authorMapper;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public ApiResponse<AdminStatsDto> stats(@RequestAttribute("role") String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        AdminStatsDto dto = new AdminStatsDto();
        dto.setTotalUsers(userMapper.countAll());
        dto.setTotalAuthors(userMapper.countByRole("author"));
        dto.setTotalNovels(novelMapper.countAll());
        return ApiResponse.success(dto);
    }

    @GetMapping("/authors")
    public ApiResponse<List<AuthorAdminDto>> authors(@RequestAttribute("role") String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        List<AuthorAdminDto> list = authorMapper.findAllWithUser();
        for (AuthorAdminDto dto : list) {
            if ("author".equals(dto.getRole())) {
                dto.setStatus("approved");
            } else {
                dto.setStatus("pending");
            }
        }
        return ApiResponse.success(list);
    }

    @PostMapping("/authors/{id}/approve")
    public ApiResponse<Void> approveAuthor(@RequestAttribute("role") String role,
                                           @PathVariable("id") String id) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        Author author = authorMapper.findById(id);
        if (author == null) {
            return ApiResponse.error("作者不存在");
        }
        User user = userMapper.findById(author.getUserId());
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            return ApiResponse.error("管理员不能成为作者");
        }
        userMapper.updateRole(user.getId(), "author");
        return ApiResponse.success(null);
    }

    @PostMapping("/authors/{id}/reject")
    public ApiResponse<Void> rejectAuthor(@RequestAttribute("role") String role,
                                          @PathVariable("id") String id) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        Author author = authorMapper.findById(id);
        if (author == null) {
            return ApiResponse.error("作者不存在");
        }
        User user = userMapper.findById(author.getUserId());
        if (user != null && "author".equals(user.getRole())) {
            return ApiResponse.error("已经是作者，不能驳回");
        }
        authorMapper.deleteById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/users")
    public ApiResponse<List<UserAdminDto>> users(@RequestAttribute("role") String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        List<UserAdminDto> list = userMapper.findAllForAdmin();
        return ApiResponse.success(list);
    }

    @PostMapping("/users/{id}/role")
    public ApiResponse<Void> updateUserRole(@RequestAttribute("role") String role,
                                            @PathVariable("id") String id,
                                            @RequestParam("role") String newRole) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        if (!"reader".equals(newRole) && !"author".equals(newRole) && !"admin".equals(newRole)) {
            return ApiResponse.error("非法角色");
        }
        User user = userMapper.findById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        userMapper.updateRole(id, newRole);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@RequestAttribute("role") String role,
                                        @PathVariable("id") String id) {
        if (!"admin".equals(role)) {
            return ApiResponse.error("无权限");
        }
        User user = userMapper.findById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            return ApiResponse.error("不能删除管理员");
        }
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }
}
