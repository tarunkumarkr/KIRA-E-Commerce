package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.CategoryDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.entity.Category;
import com.tekpyramid.kira_product_inventory.entity.Product;
import com.tekpyramid.kira_product_inventory.exception.ResourceNotFoundException;
import com.tekpyramid.kira_product_inventory.repository.CategoryRepository;
import com.tekpyramid.kira_product_inventory.repository.InventoryRepository;
import com.tekpyramid.kira_product_inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseDet createCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            log.warn("createCategory - request body is null");
            return new ResponseDet("Category payload is required", true, HttpStatus.BAD_REQUEST, null);
        }

        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            log.warn("createCategory - category name is missing");
            return new ResponseDet("Category name is required", true, HttpStatus.BAD_REQUEST, null);
        }

        try {
            // Check if category name already exists
            Optional<Category> byName = categoryRepository.findByName(categoryDto.getName());
            if (byName.isPresent()) {
                log.info("createCategory - category exists with name={}", categoryDto.getName());
                return new ResponseDet(
                        "Category with name '" + categoryDto.getName() + "' already exists",
                        true,
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            // Convert DTO to entity
            Category category = dtoToEntity(categoryDto);
            category.setCategoryId(new ObjectId().toHexString());

            Category saved = categoryRepository.save(category);

            // Convert back
            CategoryDto respDto = entityToDto(saved);

            return new ResponseDet("Category created successfully", false, HttpStatus.CREATED, respDto);

        } catch (DataAccessException dae) {
            log.error("createCategory - database error while creating category '{}': {}", categoryDto.getName(), dae.getMessage(), dae);
            return new ResponseDet("Database error while creating category", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        } catch (Exception ex) {
            log.error("createCategory - unexpected error while creating category '{}': {}", categoryDto.getName(), ex.getMessage(), ex);
            return new ResponseDet("Unexpected error while creating category", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public ResponseDet createCategories(List<CategoryDto> categories) {
        log.info("createCategories - payload size: {}", categories == null ? 0 : categories.size());

        if (categories == null || categories.isEmpty()) {
            log.warn("createCategories - No categories provided");
            return new ResponseDet("No categories provided", true, HttpStatus.BAD_REQUEST, null);
        }

        // Validate duplicates in request body
        Set<String> seenNames = new HashSet<>();
        for (CategoryDto dto : categories) {
            if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
                log.warn("createCategories - invalid category DTO in request: {}", dto);
                return new ResponseDet("Each category must have a non-empty name", true, HttpStatus.BAD_REQUEST, null);
            }
            if (!seenNames.add(dto.getName())) {
                log.warn("createCategories - duplicate name in request: {}", dto.getName());
                return new ResponseDet(
                        "Duplicate category name found in request: " + dto.getName(),
                        true,
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }
        }

        try {
            // Validate duplicates with DB
            for (CategoryDto dto : categories) {
                Optional<Category> exists = categoryRepository.findByName(dto.getName());
                if (exists.isPresent()) {
                    log.info("createCategories - category already exists in db: {}", dto.getName());
                    return new ResponseDet(
                            "Category with name '" + dto.getName() + "' already exists",
                            true,
                            HttpStatus.BAD_REQUEST,
                            null
                    );
                }
            }

            // Map DTOs → Entities
            List<Category> toSave = new ArrayList<>();
            for (CategoryDto dto : categories) {
                Category c = dtoToEntity(dto);
                c.setCategoryId(new ObjectId().toHexString());
                if (c.getProductIds() == null) c.setProductIds(new ArrayList<>());
                toSave.add(c);
            }

            // Save all categories
            List<Category> saved = categoryRepository.saveAll(toSave);

            // Convert to DTOs
            List<CategoryDto> respDtos = saved.stream()
                    .map(this::entityToDto)
                    .collect(Collectors.toList());

            return new ResponseDet("Categories created successfully", false, HttpStatus.CREATED, respDtos);

        } catch (DataAccessException dae) {
            log.error("createCategories - database error: {}", dae.getMessage(), dae);
            return new ResponseDet("Database error while creating categories", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        } catch (Exception ex) {
            log.error("createCategories - unexpected error: {}", ex.getMessage(), ex);
            return new ResponseDet("Unexpected error while creating categories", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public ResponseDet updateCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            log.warn("updateCategory - payload is null");
            return new ResponseDet("Category payload is required", true, HttpStatus.BAD_REQUEST, null);
        }

        if (categoryDto.getCategoryId() == null || categoryDto.getCategoryId().isBlank()) {
            log.warn("updateCategory - categoryId missing");
            return new ResponseDet("categoryId is required for update", true, HttpStatus.BAD_REQUEST, null);
        }

        try {
            Optional<Category> existingOpt = categoryRepository.findByCategoryId(categoryDto.getCategoryId());
            if (existingOpt.isEmpty()) {
                log.info("updateCategory - category not found: {}", categoryDto.getCategoryId());
                return new ResponseDet("Category not found with id: " + categoryDto.getCategoryId(), true, HttpStatus.NOT_FOUND, null);
            }

            Category existing = existingOpt.get();

            // Business updates
            existing.setName(categoryDto.getName() == null ? existing.getName() : categoryDto.getName());
            existing.setDescription(categoryDto.getDescription() == null ? existing.getDescription() : categoryDto.getDescription());

            Category updated = categoryRepository.save(existing);
            CategoryDto respDto = entityToDto(updated);
            return new ResponseDet("Category updated successfully", false, HttpStatus.OK, respDto);

        } catch (DataAccessException dae) {
            log.error("updateCategory - database error for id {}: {}", categoryDto.getCategoryId(), dae.getMessage(), dae);
            return new ResponseDet("Database error while updating category", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        } catch (Exception ex) {
            log.error("updateCategory - unexpected error for id {}: {}", categoryDto.getCategoryId(), ex.getMessage(), ex);
            return new ResponseDet("Unexpected error while updating category", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    // ---------- mapping helpers ----------
    private Category dtoToEntity(CategoryDto dto) {
        if (dto == null) return null;
        Category c = new Category();
        c.setCategoryId(dto.getCategoryId());
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        // productIds and BaseEntity fields are left untouched here
        return c;
    }

    private CategoryDto entityToDto(Category entity) {
        if (entity == null) return null;
        return CategoryDto.builder()
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    // ================Aravind Api ==========================

    //  API 8
    @Override
    public ResponseDet deleteCategoryById(String categoryId) {
        try {
            // 1. Validate categoryId as ObjectId
            ObjectId catObjId;
            try {
                catObjId = new ObjectId(categoryId);
            } catch (Exception e) {
                return new ResponseDet(
                        "Invalid category ID format",
                        true,
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            // 2. Check category exists
            Category category = mongoTemplate.findById(catObjId, Category.class);
            if (category == null) {
                throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
            }

            // 3. Get all products under this category
            List<Product> products = productRepository.findByCategoryId(categoryId);

            int deletedProducts = 0;

            for (Product product : products) {

                String productId = product.getProductId();

                // 3A. Delete inventory
                try {
                    inventoryRepository.deleteByProductId(productId);
                } catch (Exception ex) {
                    System.out.println("Warning: Failed to delete inventory for product: " + productId);
                }

                // 3B. Delete product
                try {
                    ObjectId prodObj = new ObjectId(productId);

                    mongoTemplate.remove(
                            Query.query(Criteria.where("_id").is(prodObj)),
                            Product.class
                    );

                    deletedProducts++;

                } catch (Exception e) {
                    return new ResponseDet(
                            "Invalid product ID format inside category: " + productId,
                            true,
                            HttpStatus.BAD_REQUEST,
                            null
                    );
                }
            }

            // 4. Delete category
            mongoTemplate.remove(
                    Query.query(Criteria.where("_id").is(catObjId)),
                    Category.class
            );

            return new ResponseDet(
                    "Category deleted successfully along with its products & inventory",
                    false,
                    HttpStatus.OK,
                    "Deleted Products: " + deletedProducts
            );

        } catch (ResourceNotFoundException ex) {
            throw ex;  // handled by GlobalExceptionHandler

        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete category: " + ex.getMessage());
        }
    }


    // API 9
    @Override
    public ResponseDet getAllCategories(Pageable pageable) {

        try {
            // 1. Fetch paged categories
            Page<Category> page = categoryRepository.findAll(pageable);

            if (page.isEmpty()) {
                return new ResponseDet(
                        "No categories found",
                        true,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            // 2. Convert Entity → DTO
            List<CategoryDto> dtoList = page.getContent()
                    .stream()
                    .map(cat -> {
                        CategoryDto dto = new CategoryDto();
                        dto.setCategoryId(cat.getCategoryId());
                        dto.setName(cat.getName());
                        dto.setDescription(cat.getDescription());
                        dto.setProductIds(cat.getProductIds());

                        //  Correct BaseEntityDto mapping (LocalDateTime → String)
                        dto.setCreatedAt(
                                cat.getCreatedAt() != null ? cat.getCreatedAt().toString() : null
                        );

                        dto.setUpdatedAt(
                                cat.getUpdatedAt() != null ? cat.getUpdatedAt().toString() : null
                        );

                        dto.setCreatedBy(cat.getCreatedBy());
                        dto.setUpdatedBy(cat.getUpdatedBy());

                        return dto;
                    })
                    .collect(Collectors.toList());

            return new ResponseDet(
                    "Categories fetched successfully",
                    false,
                    HttpStatus.OK,
                    dtoList
            );

        } catch (Exception ex) {
            return new ResponseDet(
                    "Failed to fetch categories: " + ex.getMessage(),
                    true,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }
}
