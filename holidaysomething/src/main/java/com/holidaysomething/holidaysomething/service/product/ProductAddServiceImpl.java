package com.holidaysomething.holidaysomething.service.product;

import com.holidaysomething.holidaysomething.domain.Product;
import com.holidaysomething.holidaysomething.domain.ProductCategory;
import com.holidaysomething.holidaysomething.domain.ProductDetail;
import com.holidaysomething.holidaysomething.dto.ProductAddDto;
import com.holidaysomething.holidaysomething.repository.ProductCategoryRepository;
import com.holidaysomething.holidaysomething.repository.ProductDetailRepository;
import com.holidaysomething.holidaysomething.repository.ProductRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductAddServiceImpl implements ProductAddService {

  private final ProductCategoryRepository productCategoryRepository;
  private final ProductDetailRepository productDetailRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductCategory> productCategoryList(Long parentId) {
    return productCategoryRepository.findCategory(parentId);
  }

  /**
   * @author JDragon
   * DTO 객체를 Product(도메인) 객체로 바꿔주는 메소드.
   */
  @Override
  @Transactional
  public Product productDtoToProduct(ProductAddDto productDto, ProductDetail productDetail) {

    Product product = new Product();

    product.setProductDetail(productDetail);
    product.setName(productDto.getName());
    product.setOriginalPrice(productDto.getOriginalPrice());
    product.setSellingPrice(productDto.getSellingPrice());
    product.setManufacturingPrice(productDto.getManufacturingPrice());
    product.setCode(productDto.getCode());
    product.setManufacturer(productDto.getManufacturer());
    product.setShippingPrice(productDto.getShippingPrice());
    product.setSellingQuantity(productDto.getSellingQuantity());
    product.setMileage(productDto.getMileage());
    product.setOptionalPriceText(productDto.getOptionalPriceText());
    product.setRegDate(LocalDateTime.now());

    String manufactureDateStr = productDto.getManufactureDate() + ":00";
    String releaseDateStr = productDto.getReleaseDate() + ":00";

    LocalDateTime manufactureDate = LocalDateTime
        .parse(manufactureDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime releaseDate = LocalDateTime
        .parse(releaseDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    product.setManufactureDate(manufactureDate);
    product.setReleaseDate(releaseDate);

    // null 이면
    if (productDto.getDisplay() == null) {
      product.setDisplay(false);
    } else {
      product.setDisplay(productDto.getDisplay());
    }

    return product;
  }


  @Override
  @Transactional
  public Product productRegister(ProductAddDto productAddDto) {

    ProductDetail productDetail = new ProductDetail();
    productDetail.setDescription(productAddDto.getDescription());
    ProductDetail savedProductDetail = productDetailRepository.save(productDetail);

    // id를 이용해서 상품에 넣어야 하는 카테고리 인스턴스를 생성해야해!
    // 왜? 카테고리가 fk 를 가지고 있어!. 근데 이 fk 를 등록하려면 카테고리 인스턴스가 필요해!
    ProductCategory productCategory = productCategoryRepository.findByIdContaining(
        productAddDto.getProductCategoryId());

    // 상품 등록하기 위해 Dto 객체를 Product(Domain) 객체로 바꿔주기~
    Product product = productDtoToProduct(productAddDto, savedProductDetail);

    // 상품에 set 해줘버리기~
    product.setProductDetail(productDetail);
    product.setProductCategory(productCategory);

    productRepository.save(product);

    return product;
  }
}