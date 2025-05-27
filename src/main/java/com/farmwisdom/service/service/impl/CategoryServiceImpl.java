package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmwisdom.entity.Category;
import com.farmwisdom.exception.ResourceNotFoundException;
import com.farmwisdom.mapper.CategoryMapper;
import com.farmwisdom.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getId));
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        if (categoryMapper.updateById(category) == 0) {
            throw new ResourceNotFoundException("Category", "id", category.getId());
        }
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (categoryMapper.deleteById(id) == 0) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
    }
}