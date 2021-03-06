package ices.fashion.service.collaborate.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import ices.fashion.entity.collaborate.ColVersion;
import ices.fashion.entity.collaborate.ColWorkDescription;
import ices.fashion.mapper.collaborate.ColVersionMapper;
import ices.fashion.service.collaborate.ColVersionService;
import ices.fashion.service.collaborate.dto.ColVersionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ColVersionServiceImpl implements ColVersionService {

    @Autowired
    private ColVersionMapper colVersionMapper;

    @Override
    public List<ColVersionDto> getVersion(int pid){
        QueryWrapper<ColVersion> colVersionQueryWrapper = new QueryWrapper<>();
        colVersionQueryWrapper.eq("pid",pid);
        List<ColVersion> colVersionList = colVersionMapper.selectList(colVersionQueryWrapper);
        List<ColVersionDto> res = new ArrayList<>();

        //根据parent和children的关系创建

        HashMap<Integer,Integer> mp = new HashMap<>();
        res.add(new ColVersionDto());
        int cnt = 1;
        for(ColVersion colVersion : colVersionList){
            int id= colVersion.getId();
            int parent = colVersion.getParentVersion();
            //传给前端的数据只保留到天
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(colVersion.getCreateTime());
            ColVersionDto dto = new ColVersionDto(id,colVersion.getCanvas(),colVersion.getBackCanvas(),colVersion.getImage(),colVersion.getBackImage(),parent,date,colVersion.getSaved(),new ArrayList<>());
            if(id == parent){
                res.set(0,dto);
                mp.put(id,0);
            }
            else{
                res.add(dto);
                mp.put(id,cnt);
                cnt++;
            }
        }

        for(ColVersion colVersion : colVersionList){
            int id= colVersion.getId();
            int parent = colVersion.getParentVersion();
            int index = mp.get(id);
//            System.out.println(id);
            int parent_index = mp.get(parent);
            if(id!=parent)
                res.get(parent_index).getChildrenIndex().add(index);
        }

        return res;
    }

    @Override
    public void insertVersion(int pid, String canvas, String image, int parent){
        ColVersion colVersion = new ColVersion();
        colVersion.setPid(pid);
        colVersion.setCanvas(canvas);
        colVersion.setImage(image);
        colVersion.setParentVersion(parent);
        colVersion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        colVersion.setSaved(0);

        Integer result = colVersionMapper.insert(colVersion);

        if(parent == -1){
            int id = colVersion.getId();
            colVersion.setParentVersion(id);
            colVersionMapper.updateById(colVersion);
        }

//        System.out.println(colVersion);
    }

    @Override
    public void insertVersionDouble(int pid,String canvas,String backCanvas,String frontImage,String backImage,int parent){
        ColVersion colVersion = new ColVersion();
        colVersion.setPid(pid);
        colVersion.setCanvas(canvas);
        colVersion.setBackCanvas(backCanvas);
        colVersion.setImage(frontImage);
        colVersion.setBackImage(backImage);
        colVersion.setParentVersion(parent);
        colVersion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        colVersion.setSaved(0);

        Integer result = colVersionMapper.insert(colVersion);

        if(parent == -1){
            int id = colVersion.getId();
            colVersion.setParentVersion(id);
            colVersionMapper.updateById(colVersion);
        }
    }

    @Override
    public void  updateSaved(int vid, int saved){
        ColVersion version = new ColVersion();
        version.setId(vid);
        version.setSaved(saved);

        colVersionMapper.updateById(version);
    }

    @Override
    public void updateSaved(String str,int saved){
        Gson gson = new Gson();
        ColWorkDescription workDescription = gson.fromJson(str,ColWorkDescription.class);
        ColVersion version = workDescription.getVersion();
        int id = version.getId();
        System.out.println("update saved: " + id);

        updateSaved(id,saved);

    }
}
