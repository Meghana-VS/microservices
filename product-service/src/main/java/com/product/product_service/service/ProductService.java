package com.product.product_service.service;

import com.product.product_service.dto.ProductRequest;
import com.product.product_service.dto.ProductResponse;
import com.product.product_service.model.Product;
import com.product.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(),
                        product.getDescription(), product.getPrice()))
                .toList();
//        return products.stream().map(this::mapToProductResponse).toList();
    }

//    private ProductResponse mapToProductResponse(Product product){
//        return ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .build();
//    }
}
