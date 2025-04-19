package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {



    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);



    void update(CategoryDTO categoryDTO);

    /**
     * 启用或禁用状态
     * @param status
     * @param id
     */
    void changeStatus(Integer status, long id);




    void setCategory(CategoryDTO categoryDTO);

    void delete(Long id);

    List<Category> getByType(Integer type);

}
