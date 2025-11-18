package com.kira.userservice.service;

import com.kira.userservice.entity.Cart;
import com.kira.userservice.entity.Customer;
import com.kira.userservice.exception.ResourceNotFoundException;
import com.kira.userservice.repository.CartItemRepository;
import com.kira.userservice.repository.CartRepository;
import com.kira.userservice.repository.CustomerRepository;
import com.kira.userservice.dto.CustomerRequestDTO;
import com.kira.userservice.dto.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CartService cartService;  // To create a cart when customer registers
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    // 🔹 1️⃣ Register customer
    @Override
    public String registerCustomer(CustomerRequestDTO request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Encrypt password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create customer
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(hashedPassword)
                .role("CUSTOMER")
                .active(true)
                .build();

        // Save customer
        Customer saved = customerRepository.save(customer);

        // IMPORTANT: create a cart for customer
        Cart cart = Cart.builder()
                .customer(saved)
                .items(new ArrayList<>())   // prevents null issues
                .totalAmount(0.0)
                .build();

        Cart savedCart = cartRepository.save(cart);

        // attach cart to customer
        saved.setCart(savedCart);
        customerRepository.save(saved);

        return saved.getId();
    }


    // 🔹 2️⃣ Get customer by ID (returns DTO)
    @Override
    public CustomerResponseDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        return mapToResponseDTO(customer);
    }

    // 🔹 3️⃣ Get all customers (returns DTO list)
    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // 🔹 4️⃣ Update customer
    @Override
    public void updateCustomer(String id, CustomerRequestDTO request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setPhoneNumber(request.getPhoneNumber());

        // Optional password update
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        customerRepository.save(existing);
    }

    // 🔹 5️⃣ Delete customer (cascade delete cart + items)
    @Override
    public void deleteCustomer(String id) {

        // 1️⃣ Find Customer
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // 2️⃣ Get Cart ID
        if (customer.getCart() != null) {

            String cartId = customer.getCart().getId();

            // 2a. Delete all cart items
            cartItemRepository.deleteAllByCart_Id(cartId);

            // 2b. Delete the cart itself
            cartRepository.deleteById(cartId);
        }

        // 3️⃣ Delete customer
        customerRepository.delete(customer);
    }


    // 🔹 Utility Mapper (Entity ➜ DTO)
    private CustomerResponseDTO mapToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
