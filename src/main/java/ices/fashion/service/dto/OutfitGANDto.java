package ices.fashion.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutfitGANDto {

    private String bagUrl;
    private String shoesUrl;
    private String lowerUrl;
}
