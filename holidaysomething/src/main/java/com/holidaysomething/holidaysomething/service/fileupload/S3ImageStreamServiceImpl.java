package com.holidaysomething.holidaysomething.service.fileupload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.holidaysomething.holidaysomething.domain.Product;
import com.holidaysomething.holidaysomething.domain.ProductImage;
import com.holidaysomething.holidaysomething.repository.ProductImageRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 개발 환경에서 S3와의 연동을 테스트할 때에도 사용할 수 있음, TODO ** S3 용량 5G 넘으면 과금입니다!!! **
// program arguments 에 --spring.profiles.active=real 로 설정하여 구동하면 됩니다.
// 또는 VM options 에 -Dspring.profiles.active=real 로 설정하여 구동하면 됩니다.
// VM options 보다 program arguments 의 우선순위가 더 높기 때문에 둘을 다르게 설정하면 program arguments 의 설정으로 구동됩니다.
@Profile("real")
@Service
@Slf4j
@RequiredArgsConstructor
public class S3ImageStreamServiceImpl implements ImageStreamService {

  private final ProductImageRepository productImageRepository;
  private final AmazonS3Client amazonS3Client;

  // S3 버킷의 이름
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  // 파일을 업로드 할 폴더 이름
  @Value("${cloud.aws.s3.bucket.dirName}")
  private String dirName;

  @Override
  public String save(MultipartFile multipartFile, Long productId) {
//        for (MultipartFile multipartFile : multipartFiles) {
    // 멀티플 업로드를 설정하면 파일을 올리지 않아도 쓰레기 파일이 날라와서 걸러내기 위함...
//        if (!multipartFile.getOriginalFilename().isEmpty()) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    objectMetadata.setContentLength(multipartFile.getSize());

    // 일단 해당 폴더 이하에서만 파일을 읽을 수 있도록 있도록 권한을 설정하였음.(다른 곳에 올리면 못 읽어요!!)
    // 근데 다른 곳에 올릴 수는 있어요...
    String path = dirName + "/images/";
    String storedFileName = UUID.randomUUID().toString();

    String fileKey = path + storedFileName;

    try {
      amazonS3Client.putObject(bucket, fileKey, multipartFile.getInputStream(), objectMetadata);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String url = amazonS3Client.getUrl(bucket, fileKey).toString();
    String urlPath = url.substring(0, url.length() - storedFileName.length());

    ProductImage productImage = new ProductImage();
    productImage.setOriginalFileName(multipartFile.getOriginalFilename());
    productImage.setFileType(multipartFile.getContentType());
    productImage.setPath(urlPath);
    productImage.setStoredFileName(storedFileName);
    productImage.setRegDate(LocalDateTime.now());
    productImage.setSize(multipartFile.getSize());

    if (productId != null) {
      Product product = new Product();
      product.setId(productId);
      productImage.setProduct(product);
    }

    // Category 1 = Main Image
    // Category 2 = Sub Image
    // Category 3 = Description Image
    switch (multipartFile.getName()) {
      case "mainImage":
        productImage.setCategory(1L);
        break;
      case "subImages":
        productImage.setCategory(2L);
        break;
      default:
        productImage.setCategory(3L);
        break;
    }
    productImageRepository.save(productImage);

    return productImage.getStoredFileName();
  }

  @Override
  public void readAndWrite(String saveFileName, OutputStream out) {
    String filekey = saveFileName.substring(saveFileName.lastIndexOf(dirName));

    S3Object s3Object = amazonS3Client.getObject(bucket, filekey);

    InputStream in = s3Object.getObjectContent();

    int readCount = 0;
    byte[] buffer = new byte[1024];

    try {
      while ((readCount = in.read(buffer)) != -1) {
        out.write(buffer, 0, readCount);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage());
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
