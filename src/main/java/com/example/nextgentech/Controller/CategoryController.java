package com.example.nextgentech.Controller;

import org.springframework.ui.Model;
import com.example.nextgentech.Model.CategoryPost;
import com.example.nextgentech.Service.CategoryService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/categories-post")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String categoryPost(Model model){
        List<CategoryPost> categoryPostList = categoryService.findAll();
        model.addAttribute("categoryPostList", categoryPostList);
     //   model.addAttribute("content", "admin/categories/list"); // Thêm dòng này
        return "admin/my-account";
    }
    // Xử lý thêm danh mục từ modal
    @PostMapping("/add")
    public String addCategory(@RequestParam("name") String name,
                              @RequestParam("description") String description) {
        CategoryPost category = new CategoryPost();
        category.setName(name);
        category.setDescription(description);

        // Lưu danh mục sử dụng service
        categoryService.save(category);

        return "redirect:/categories-post"; // Chuyển hướng về danh sách danh mục
    }
    // Trang sửa danh mục
    @GetMapping("/edit/{id}")
    @ResponseBody
    public CategoryPost getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id).orElse(null); // Trả về danh mục theo ID
    }

    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, @ModelAttribute("category") CategoryPost updatedCategory) {
        CategoryPost category = categoryService.findById(id).orElse(null);
        if (category != null) {
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            categoryService.save(category);
        }
        return "redirect:/categories"; // Chuyển hướng về danh sách danh mục sau khi cập nhật
    }


}
