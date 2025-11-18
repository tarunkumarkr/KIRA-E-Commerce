package com.tekpyramid.kira_product_inventory.exception;

public class VendorCurrentlyInactive extends RuntimeException {
    public VendorCurrentlyInactive(String message) {
        super(message);
    }
}