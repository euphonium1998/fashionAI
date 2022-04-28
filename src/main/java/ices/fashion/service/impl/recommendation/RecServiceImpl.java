package ices.fashion.service.impl.recommendation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ices.fashion.constant.RecConst;
import ices.fashion.entity.TBaseMaterial;
import ices.fashion.entity.TBaseMaterialCategory;
import ices.fashion.mapper.TBaseMaterialMapper;
import ices.fashion.service.CategoryService;
import ices.fashion.service.MaterialService;
import ices.fashion.service.RecService;
import ices.fashion.service.dto.RecCriteria;
import ices.fashion.service.dto.RecDto;
import ices.fashion.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecServiceImpl implements RecService {
    @Autowired
    private TBaseMaterialMapper tBaseMaterialMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MaterialService materialService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecServiceImpl.class);

    @Override
    public Map<Integer, String> selectRecCategoryMap() {
        Map<Integer, String> recCategoryMap = new HashMap<>(2);
        List<TBaseMaterialCategory> categories = categoryService.selectAllCategories();
        for (TBaseMaterialCategory category: categories) {
            if(category.getStatus() != 0 && ! category.getRecCategory().equals("invalid")) {
                // 有效种类
                recCategoryMap.put(category.getId(), category.getRecCategory());
            }
        }
        return recCategoryMap;
    }

    @Override
    /**
     * 请求推荐
     */
    public List<TBaseMaterial> reqRecommendations(List<Integer> itemIds, String matchType) throws IOException {
        // 得到图片地址列表
        List<TBaseMaterial> materials = materialService.selectMaterialsByIds(itemIds);
        List<String> urlList = materials.stream().map(TBaseMaterial::getImgUrl).collect(Collectors.toList());
        // 获取对应图片
        List<File> files = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (String url: urlList) {
            String fileName = FileUtil.url2FileName(url);
            File file = FileUtil.download(url, fileName); // 下载到本地
            files.add(file);
            images.add(FileUtil.pictureFileToBase64String(file));
        }
        if(images.size() != 3) return new ArrayList<>();
        RecCriteria recCriteria = new RecCriteria();
        recCriteria.init(images.get(0), images.get(1), images.get(2), matchType);
        // 调用模型
        RecDto recDto = getRecommended(recCriteria);
        // 删除图片
        FileUtil.deleteFile(files.toArray(new File[files.size()]));
        // 根据链接获取对应素材
        if(recDto == null) {
            return new ArrayList<>();
        }
        Map<String, Object> conditions = new HashMap<>();
        List<String> filenames = recDto.getFilename();
        List<String> recUrls = new ArrayList<>();
        for (String filename: filenames) {
            String recUrl = FileUtil.concatUrlwithoutEncoding(filename);

            recUrls.add(recUrl);
        }
        QueryWrapper<TBaseMaterial> tBaseMaterialQueryWrapper = new QueryWrapper<>();
        tBaseMaterialQueryWrapper.in("img_url", recUrls);
        return tBaseMaterialMapper.selectList(tBaseMaterialQueryWrapper);
    }


    private RecDto getRecommended(RecCriteria recCriteria) {
        String recUrl = RecConst.REC_URL;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);
        Gson gson = new Gson();
        String content = '[' + gson.toJson(recCriteria) + ']';
//        System.out.println(content);

        HttpEntity<String> httpEntity = new HttpEntity<>(content, headers);
        long startTime = System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(recUrl, HttpMethod.POST, httpEntity, String.class);
//        LOGGER.info(responseEntity.toString());
        long endTime = System.currentTimeMillis();
        LOGGER.info("Rec Model time: " + (endTime - startTime) / 1000);
        List<RecDto> recDtoList = gson.fromJson(responseEntity.getBody(), new TypeToken<List<RecDto>>(){}.getType());
        return recDtoList.get(0);
    }
}
