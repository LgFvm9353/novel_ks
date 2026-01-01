package org.example.novel_backened.novel;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ApiResponse<List<Category>> listAll() {
        List<Category> list = categoryMapper.findAll();
        if (list == null || list.isEmpty()) {
            initDefaultCategories();
            list = categoryMapper.findAll();
        }
        return ApiResponse.success(list);
    }

    private void initDefaultCategories() {
        List<String> names = new ArrayList<>();
        names.add("玄幻");
        names.add("都市");
        names.add("言情");
        names.add("科幻");
        names.add("悬疑");
        for (String name : names) {
            Category category = new Category();
            category.setId(UUID.randomUUID().toString());
            category.setName(name);
            categoryMapper.insert(category);
        }
    }
}
