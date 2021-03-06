package com.holidaysomething.holidaysomething.JDragon.repositoryTest;

import com.holidaysomething.holidaysomething.domain.Product;
import com.holidaysomething.holidaysomething.domain.ProductDetail;
import com.holidaysomething.holidaysomething.repository.ProductDetailRepository;
import com.holidaysomething.holidaysomething.repository.ProductRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class ProductTest {

  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
      .getLogger(ProductTest.class);

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductDetailRepository productDetailRepository;


  @Test
  public void 전체상품갯수조회하기() {
    int count = productRepository.countAll();
    log.info("========================= " + count);
  }


  @Test
  public void 상품등록하깃() {
    ProductDetail pd = new ProductDetail("");
    pd.setDescription("이거 방탄 펜슬이야!!");
    ProductDetail pds = productDetailRepository.save(pd);
    Product p = new Product();

    p.setName("애플펜슬");
    p.setManufacturer("삼성");
    p.setCode("ajax1234");
    p.setOriginalPrice(500);
    p.setSellingPrice(10000000);
    p.setManufacturingPrice(100);
    p.setShippingPrice(50000);
    p.setProductDetail(pds);
    // 상품설명과 카테고리는 fk
    LocalDateTime ldt1 = LocalDateTime.of(2018, 11, 01, 00, 00, 00);
    LocalDateTime ldt2 = LocalDateTime.of(2018, 11, 25, 00, 00, 00);

    p.setManufactureDate(ldt1);
    p.setReleaseDate(ldt2);

    Product pp = productRepository.save(p);

    log.info("================================");
    log.info("상품설명 id : " + pds.getId());
    log.info("상품 id : " + pp.getId());
    log.info("상품의 productDetail().getId() : " + pp.getProductDetail().getId());
  }

  @Test
  public void 날짜테스트하기() {
    LocalDateTime ldt = LocalDateTime.now();
    log.info("======================= ldt : " + ldt);

    ldt = LocalDateTime.now(ZoneId.systemDefault());
    log.info("======================= ldt : " + ldt);
  }


  // 상품 한개 조회하기
  @Test
  public void 상품한개조회하기() {
    Product product = productRepository.getOne(1L);
    log.info("============= Id" + product.getId());
    log.info("============= Name" + product.getName());
  }

  @Test
  public void 상품카테고리와이름으로조회하기() {
    Product product = productRepository.findByProductCategoryIdAndId(2L, 3L);
    System.out.println(product.getName());
  }

}
