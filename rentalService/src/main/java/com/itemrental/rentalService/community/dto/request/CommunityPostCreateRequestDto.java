package com.itemrental.rentalService.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Setter
public class CommunityPostCreateRequestDto {
  private String title;
  private String content;
  private MultipartFile image;
}
