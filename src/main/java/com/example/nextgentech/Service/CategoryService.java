package com.example.nextgentech.Service;

import com.example.nextgentech.Model.CategoryPost;
import com.example.nextgentech.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryPost> findAll(){
        return categoryRepository.findAll();
    }

    public Optional<CategoryPost> findById(Long id){
        return categoryRepository.findById(id);
    }

    //Hiển thị sản phẩm theo danh mục
    public CategoryPost findCategoryByCategorySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    public String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.toSlug(title); // Tạo slug cơ bản từ tiêu đề
        String categorySlug = baseSlug;
        int count = 1;

        // Kiểm tra xem slug đã tồn tại trong cơ sở dữ liệu chưa
        while (categoryRepository.existsBySlug(categorySlug)) {
            categorySlug = baseSlug + "-" + count; // Thêm số đằng sau
            count++;
        }

        return categorySlug;
    }

    public CategoryPost save(CategoryPost category) {
        // Tạo slug duy nhất
        String uniqueSlug = generateUniqueSlug(category.getName());
        category.setSlug(uniqueSlug);
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

}
