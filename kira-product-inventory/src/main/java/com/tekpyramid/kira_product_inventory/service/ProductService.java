package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.ProductDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    // ProductService
    ResponseDet createProduct(ProductDto productDto, String vendorId);
    ResponseDet updateProduct(ProductDto productDto, String vendorId);

    ResponseDet createProducts(List<ProductDto> productDtos, String vendorId);

    // Aravind Api
    ResponseDet getProductById(String productId);

    ResponseDet getProductsByVendorId(String vendorId, Pageable pageable);

    ResponseDet getAllProducts(Pageable pageable);

    ResponseDet getProductsByCategoryId(String categoryId, Pageable pageable);

    ResponseDet searchProductsByName(String keyword, Pageable pageable);

    ResponseDet deleteProductById(String productId);

    ResponseDet deleteProductsByVendorId(String vendorId);
}
