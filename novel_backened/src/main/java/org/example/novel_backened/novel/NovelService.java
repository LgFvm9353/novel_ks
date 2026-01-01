package org.example.novel_backened.novel;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NovelService {
    private final NovelMapper novelMapper;

    public NovelService(NovelMapper novelMapper) {
        this.novelMapper = novelMapper;
    }

    public ApiResponse<Map<String, Object>> list(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size <= 0 || size > 100) {
            size = 10;
        }
        int offset = (page - 1) * size;
        List<NovelListItemDto> items = novelMapper.findList(size, offset);
        long total = novelMapper.countAll();
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return ApiResponse.success(result);
    }

    public ApiResponse<List<NovelListItemDto>> ranking(int limit) {
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }
        List<NovelListItemDto> items = novelMapper.findRanking(limit);
        return ApiResponse.success(items);
    }

    public ApiResponse<NovelDetailDto> detail(String id) {
        NovelDetailDto detail = novelMapper.findDetailById(id);
        if (detail == null) {
            return ApiResponse.error("小说不存在");
        }
        return ApiResponse.success(detail);
    }
}

