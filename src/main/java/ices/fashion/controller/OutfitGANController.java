package ices.fashion.controller;

import ices.fashion.constant.ApiResult;
import ices.fashion.service.OutfitGANService;
import ices.fashion.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/outfit")
public class OutfitGANController {

    @Autowired
    private OutfitGANService outfitGANService;

    @PostMapping("/doOutfitGAN")
    public ApiResult<OutfitGANDto> doOutfitGAN(@RequestBody OutfitGANCriteria outfitGANCriteria) throws IOException {
        try {
            ApiResult<OutfitGANDto> res = outfitGANService.doOutfitGAN(outfitGANCriteria);
            return res;
        }catch (Exception e){
            ApiResult<OutfitGANDto> res = new ApiResult<>(500,e.getMessage());
            return res;
        }
    }

    @GetMapping("/init")
    public ApiResult<OutfitGANInitDto> init() {
        return outfitGANService.init();
    }
}
