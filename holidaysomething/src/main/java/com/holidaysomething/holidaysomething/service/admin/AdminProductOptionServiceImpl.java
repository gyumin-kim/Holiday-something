package com.holidaysomething.holidaysomething.service.admin;

import com.holidaysomething.holidaysomething.domain.Product;
import com.holidaysomething.holidaysomething.domain.ProductOption;
import com.holidaysomething.holidaysomething.domain.ProductOptionCommand;
import com.holidaysomething.holidaysomething.repository.ProductOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminProductOptionServiceImpl implements AdminProductOptionService {

  private final ProductOptionRepository productOptionRepository;

  @Override
  @Transactional
  public List<ProductOption> fromProductOptionCommandToProductOptionList(ProductOptionCommand poc) {
    List<ProductOption> productOptions = poc.getProductOptions();
    return productOptions;
  }

  @Override
  @Transactional
  public void save(List<ProductOption> productOptions, Long productId) {
    Product product = new Product();
    product.setId(productId);

    for (ProductOption productOption : productOptions) {
      productOption.setProduct(product);
      productOptionRepository.save(productOption);
    }
  }

}
