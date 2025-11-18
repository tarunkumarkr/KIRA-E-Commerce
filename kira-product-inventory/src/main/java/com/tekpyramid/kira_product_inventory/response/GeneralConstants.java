package com.tekpyramid.kira_product_inventory.response;

/**
 * Centralized application-level string constants.
 * Add / extend as needed.
 */
public final class GeneralConstants {

    private GeneralConstants() { /* prevent instantiation */ }

    // Common validation / request messages
    public static final String PRODUCT_PAYLOAD_REQUIRED = "Product payload is required";
    public static final String VENDOR_ID_MISSING = "vendorId is required";
    public static final String PRODUCT_NAME_REQUIRED = "Product name is required";
    public static final String CATEGORY_ID_REQUIRED = "categoryId is required";
    public static final String QUANTITY_MUST_BE_PROVIDED = "Quantity must be provided and >= 1";

    // Vendor related
    public static final String VENDOR_CURRENTLY_INACTIVE = "Vendor Currently Inactive";

    // Duplicate / conflict
    public static final String PRODUCT_EXISTS_FOR_VENDOR = "Product with same name already exists for this vendor";
    public static final String DUPLICATE_PRODUCT_NAMES_IN_REQUEST = "Duplicate product names in request";
    public static final String SOME_PRODUCTS_ALREADY_EXIST = "Some products already exist for this vendor";

    // Bulk create
    public static final String NO_PRODUCTS_PROVIDED = "No products provided";
    public static final String VALIDATION_ERRORS = "Validation errors";
    public static final String PRODUCTS_INVENTORIES_CATEGORIES_UPDATED = "Products, Inventories and Categories updated";
    public static final String BULK_CREATE_FAILED = "Bulk create failed: ";

    // Category / resource
    public static final String CATEGORY_NOT_FOUND_PREFIX = "Category not found: ";
    public static final String CATEGORY_NOT_FOUND_BY_ID = "Category not found with id: ";

    // Product CRUD
    public static final String PRODUCT_AND_INVENTORY_CREATED = "Product and Inventory created successfully";
    public static final String FAILED_TO_CREATE_PRODUCT_PREFIX = "Failed to create product: ";
    public static final String FAILED_TO_UPDATE_PRODUCT_PREFIX = "Failed to update product: ";

    // Delete
    public static final String INVALID_PRODUCT_ID_FORMAT = "Invalid product ID format";
    public static final String PRODUCT_NOT_FOUND_PREFIX = "Product not found: ";
    public static final String ALL_PRODUCTS_DELETED_FOR_VENDOR_PREFIX = "All products deleted for vendor: ";
    public static final String DELETED_COUNT_PREFIX = "Deleted Count: ";

    // Database
    public static final String DATABASE_ERROR_WHILE_CREATING = "Database error while creating products";
    public static final String DATABASE_ERROR = "Database error";

    // Misc
    public static final String UNEXPECTED_ERROR_PREFIX = "Unexpected error: ";
}

