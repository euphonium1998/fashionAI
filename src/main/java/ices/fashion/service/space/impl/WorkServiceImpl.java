package ices.fashion.service.space.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import ices.fashion.constant.ApiResult;
import ices.fashion.entity.space.TComment;
import ices.fashion.entity.space.TShare;
import ices.fashion.entity.space.TWork;
import ices.fashion.mapper.space.CommentMapper;
import ices.fashion.mapper.space.ShareMapper;
import ices.fashion.mapper.space.WorkMapper;
import ices.fashion.service.collaborate.ColVersionService;
import ices.fashion.service.space.WorkService;
import ices.fashion.service.space.dto.ShareWorkCriteria;
import ices.fashion.service.space.dto.ShowDto;
import ices.fashion.service.space.dto.WorkDetailDto;
import ices.fashion.util.WorkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkServiceImpl implements WorkService {

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    private WorkUtil workUtil;

    @Autowired
    private ColVersionService colVersionService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ShareMapper shareMapper;

    @Override
    //这个id目前的实现形式的phone
    public ApiResult<ShowDto> getUserDesign(String id, Boolean isVisitor) {
        QueryWrapper<TWork> workQueryWrapper = new QueryWrapper<>();
        workQueryWrapper.eq("phone", id).select("id", "cover_url", "user_name", "category");
        if (isVisitor) {
            workQueryWrapper.eq("work_shared", 1);
        }
        List<TWork> workList = workMapper.selectList(workQueryWrapper);

        ShowDto showDto = workUtil.workList2ShowDto(workList);
        ApiResult<ShowDto> res = new ApiResult<>(200, "success");
        res.setData(showDto);
        return res;
    }

    @Override
    //这个uid代表其他游客访问详情页,目前的实现形式的phone
    public ApiResult<WorkDetailDto> getWorkDetail(Integer wid, String uid) {
        QueryWrapper<TWork> workQueryWrapper = new QueryWrapper<>();
        workQueryWrapper.eq("id", wid);
        TWork work = workMapper.selectOne(workQueryWrapper);

        //拿到评论信息
        QueryWrapper<TComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wid", wid).eq("comment_deleted", 0).orderByDesc("create_time");
        List<TComment> commentList = commentMapper.selectList(queryWrapper);

        WorkDetailDto data = new WorkDetailDto(work);
        data.setCommentList(commentList);

        //获取游客是否收藏的信息
        QueryWrapper<TShare> shareQueryWrapper = new QueryWrapper<>();
        shareQueryWrapper.eq("deleted", 0).eq("wid", wid).eq("phone", uid);
        TShare share = shareMapper.selectOne(shareQueryWrapper);
        if (share == null) {
            data.setIsVisitorShared(0);
            data.setShareId(0);
        } else {
            data.setIsVisitorShared(1);
            data.setShareId(share.getId());
        }

        ApiResult<WorkDetailDto> res = new ApiResult<>(200, "success");
        res.setData(data);
        return res;
    }

    @Override
    public ApiResult saveOneWork(TWork work) {
        if(workMapper.insert(work) != 1) {
            return new ApiResult(800, "数据库更新失败");
        }
        System.out.println(work.getCategory());
        if(work.getCategory().equals("collaborate") ){
            String workStr = work.getWorkDescription();
            colVersionService.updateSaved(workStr,1);
        }
        return new ApiResult(200, "success");
    }

    @Override
    /*
    将当前work id
    的作品分享
     */
    public ApiResult shareWork(ShareWorkCriteria shareWorkCriteria) {
        int id = shareWorkCriteria.getId();
        TWork work = workMapper.selectById(id);
        work.setWorkShared(1);
        work.setTitle(shareWorkCriteria.getTitle());
        work.setWordDescription(shareWorkCriteria.getWordDescription());
        if(workMapper.updateById(work) != 1) {
            return new ApiResult(800, "数据库更新失败");
        }
        return new ApiResult(200, "success");
    }

    @Override
    /*
    分享区得到所有分享的作品
     */
    public ApiResult<ShowDto> getAllShareWork() {
        QueryWrapper<TWork> workQueryWrapper = new QueryWrapper<>();
        workQueryWrapper.eq("work_shared", 1).eq("work_deleted",0)
                .select("id", "cover_url", "user_name", "category", "title");
        List<TWork> workList = workMapper.selectList(workQueryWrapper);

        ShowDto showDto = workUtil.workList2ShowDto(workList);
        ApiResult<ShowDto> res = new ApiResult<>(200, "success");
        res.setData(showDto);
        return res;
    }

    @Override
    public ApiResult cancelOneWorkShare(Integer wid) {
        TWork work = workMapper.selectById(wid);
        work.setWorkShared(0);
        if(workMapper.updateById(work) != 1) {
            return new ApiResult(800, "数据库更新失败");
        }
        QueryWrapper<TShare> shareQueryWrapper = new QueryWrapper<>();
        shareQueryWrapper.eq("wid", wid).eq("deleted", 0);
        List<TShare> shareList = shareMapper.selectList(shareQueryWrapper);
        for (TShare share : shareList) {
            share.setDeleted(1);
            if (shareMapper.updateById(share) != 1) {
                return new ApiResult(800, "数据库更新失败");
            }
        }
        return new ApiResult(200, "success");
    }


}