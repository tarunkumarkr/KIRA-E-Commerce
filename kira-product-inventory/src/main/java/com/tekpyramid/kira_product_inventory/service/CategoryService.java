package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.CategoryDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    // CategoryService
    ResponseDet createCategory(CategoryDto categoryDto);
    ResponseDet updateCategory(CategoryDto categoryDto);

    //Aravind
    ResponseDet createCategories(List<CategoryDto> categories);
    ResponseDet deleteCategoryById(String categoryId);
    ResponseDet getAllCategories(Pageable pageable);

}
