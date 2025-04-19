package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO)
    {

    categoryService.update(categoryDTO);

    return Result.success();
    }


    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改分类状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result changeStatus( @PathVariable Integer status,@RequestParam Long id)
    {

            categoryService.changeStatus(status,id);


            return Result.success();

    }


    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result setCategory(@RequestBody CategoryDTO categoryDTO)
    {

            categoryService.setCategory(categoryDTO);



         return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(@RequestParam Long id)
    {

            categoryService.delete(id);

        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result getByType(@RequestParam Integer type)
    {
        List<Category>list=categoryService.getByType(type);


        return Result.success(list);
    }

}
