package ices.fashion.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ices.fashion.entity.recommendation.TBaseMaterial;
import ices.fashion.service.recommendation.dto.MaterialPageCriteria;
import ices.fashion.service.recommendation.dto.MaterialPageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TBaseMaterialMapper extends BaseMapper<TBaseMaterial> {

    Integer deleteMaterials(@Param("ids") List<Integer> ids);
    Integer recoverMaterials(@Param("ids") List<Integer> ids);
    List<MaterialPageDto> selectMaterialDtoByIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);
    List<MaterialPageDto> selectMaterialDtoByImgUrls(@Param("imgUrls") List<String> imgUrls);
    //传入了列表则查询，注意判断非空；未传入则不查询该条件；注意xml不要在sql中间插注解啊！
//    如果传入status则按status查询；如果没有就按status=1查询
    List<MaterialPageDto> selectMaterialPage(Page page, MaterialPageCriteria materialPageCriteria);

}
