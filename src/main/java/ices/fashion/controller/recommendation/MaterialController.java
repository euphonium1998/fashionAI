package ices.fashion.controller.recommendation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ices.fashion.constant.ApiResult;
import ices.fashion.constant.ResultMessage;
import ices.fashion.entity.recommendation.TBaseMaterial;
import ices.fashion.service.recommendation.MaterialService;
import ices.fashion.service.recommendation.dto.MaterialPageCriteria;
import ices.fashion.service.recommendation.dto.MaterialPageDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/recommendation")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    /*
    获取素材分页列表
     */
    @GetMapping("/get-material-page")
    @ApiOperation(value = "根据品牌id、种类id、目标人群、状态、页面大小、当前页数分页查询素材记录")
    @ApiImplicitParams({@ApiImplicitParam(name = "materialPageCriteria", dataType = "MaterialPageCriteria")})
    public ApiResult<IPage<MaterialPageDto>> getMaterialPage(MaterialPageCriteria materialPageCriteria) {
        Page<MaterialPageDto> materials = materialService.selectMaterialsByPage(materialPageCriteria);
        return new ApiResult<>(ResultMessage.RESULT_SUCCESS_1, materials);
    }
    /*
    根据ID获取素材
     */
    @GetMapping("/get-material-by-ids")
    @ApiOperation(value = "根据素材id列表查询素材记录")
    @ApiImplicitParams({@ApiImplicitParam(name = "ids", value = "素材id列表", dataType = "STRING", allowMultiple = true, paramType = "QUERY", required = true), @ApiImplicitParam(name = "status", value = "素材状态", dataType = "INT", paramType = "QUERY")})
    public ApiResult<Map<String, Object>> getMaterialByIds(@RequestParam("ids") List<Integer> ids, @RequestParam(required=false, name="status") Integer status) {
        Map<String, Object> resultMap = new HashMap<>(2);
        List<MaterialPageDto> materials = materialService.selectMaterialDtoByIds(ids, status);
        resultMap.put("materials", materials);
        return new ApiResult<>(ResultMessage.RESULT_SUCCESS_1, resultMap);
    }

    /*
    批量删除素材
     */
    @PostMapping("/delete-materials")
    @ApiOperation(value = "软删除素材")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "ids", value = "素材id列表", required = true, allowMultiple = true, dataType = "INT"),
            }
    )
    public ApiResult deleteMaterials(@RequestParam("ids") List<Integer> ids) throws Exception {
        try {
            return materialService.deleteMaterials(ids);
        } catch (Exception e) {
            return new ApiResult(500, e.getMessage());
        }
    }

    /*
    批量撤销删除
     */
    @PostMapping("/recover-materials")
    @ApiOperation(value = "还原已删除素材")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "ids", value = "素材id列表", required = true, allowMultiple = true, dataType = "INT"),
            }
    )
    public ApiResult recoverMaterials(@RequestParam("ids") List<Integer> ids) throws Exception {
        try {
            return materialService.recoverMaterials(ids);
        } catch (Exception e) {
            return new ApiResult(500, e.getMessage());
        }
    }

    /*
    保存素材
     */
    @PostMapping("/save-material")
    @ApiOperation(value = "新增或者修改素材记录", notes = "新增或者修改素材记录")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "id", dataType = "INT", paramType = "form"),
                    @ApiImplicitParam(name = "categoryId", value = "素材所属分类的id", dataType = "INT", paramType = "form", required = true),
                    @ApiImplicitParam(name = "brandId", value = "素材所属品牌的id", dataType = "INT", paramType = "form", required = true),
                    @ApiImplicitParam(name = "linkUrl", value = "素材来源页面链接", dataType = "STRING", paramType = "form", required = true),
                    @ApiImplicitParam(name = "linkUrl", value = "素材来源页面链接", dataType = "STRING", paramType = "form", required = true),
                    @ApiImplicitParam(name = "price", value = "价格", dataType = "INT", required = true, paramType = "form"),
                    @ApiImplicitParam(name = "targetPopulation", value = "目标人群：F-女，M-男，G-女孩，B-男孩", dataType = "STRING", required = true, paramType = "form"),
                    @ApiImplicitParam(name = "description", value = "描述", dataType = "STRING", paramType = "form"),
                    @ApiImplicitParam(name = "file", value = "图片", dataType = "__file", paramType = "form", required = true),
            }
    )
    public ApiResult<Map<String, Object>> saveMaterial(MultipartHttpServletRequest request) {
        // 解析表单内容
        TBaseMaterial tBaseMaterial = new TBaseMaterial();
        if(request.getParameter("id") != null && !request.getParameter("id").equals("null") && !request.getParameter("id").equals("") && !request.getParameter("id").isEmpty()) {
//            非常奇妙……会直接等于字符串null
            tBaseMaterial.setId(Integer.valueOf(request.getParameter("id")));
        }
        tBaseMaterial.setCategoryId(Integer.valueOf(request.getParameter("categoryId")));
        tBaseMaterial.setBrandId(Integer.valueOf(request.getParameter("brandId")));
        tBaseMaterial.setLinkUrl(request.getParameter("linkUrl"));
        tBaseMaterial.setPrice(new BigDecimal(request.getParameter("price")));
        tBaseMaterial.setStatus(1);
        tBaseMaterial.setDescription(request.getParameter("description"));
        tBaseMaterial.setTargetPopulation(request.getParameter("targetPopulation"));
        MultipartFile multipartFile = request.getFile("file");
        Integer id = materialService.saveMaterial(tBaseMaterial, multipartFile);
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("id", id);
        return new ApiResult<>(ResultMessage.RESULT_SUCCESS_1, resultMap);
    }
}
