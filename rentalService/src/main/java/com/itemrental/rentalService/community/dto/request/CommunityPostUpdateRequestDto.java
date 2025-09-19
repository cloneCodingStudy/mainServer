package com.itemrental.rentalService.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CommunityPostUpdateRequestDto {
  private String title;
  private String content;
  private MultipartFile image;
  private Boolean removeImage;
}
