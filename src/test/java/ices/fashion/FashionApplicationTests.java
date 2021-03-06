package ices.fashion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import ices.fashion.constant.ApiResult;
import ices.fashion.constant.CollaborateConst;
import ices.fashion.entity.collaborate.ColProject;
import ices.fashion.entity.collaborate.TCollaborateMaterial;
import ices.fashion.entity.space.TShare;
import ices.fashion.entity.space.TWork;
import ices.fashion.entity.tools.TMmc;
import ices.fashion.entity.tools.TOutfitShoes;
import ices.fashion.entity.tools.TRender;
import ices.fashion.mapper.collaborate.ColCommentMapper;
import ices.fashion.mapper.collaborate.ColProjectMapper;
import ices.fashion.mapper.collaborate.CollaborateMaterialMapper;
import ices.fashion.mapper.space.ShareMapper;
import ices.fashion.mapper.space.WorkMapper;
import ices.fashion.mapper.tools.*;
import ices.fashion.service.collaborate.ColCommentService;
import ices.fashion.service.collaborate.ColProjectService;
import ices.fashion.service.collaborate.ColUserService;
import ices.fashion.service.collaborate.ColVersionService;
import ices.fashion.service.collaborate.dto.ColCommentDto;
import ices.fashion.service.collaborate.dto.ColProjectDto;
import ices.fashion.service.collaborate.dto.ColVersionDto;
import ices.fashion.service.*;
import ices.fashion.service.space.CommentService;
import ices.fashion.service.space.ShareService;
import ices.fashion.service.space.WorkService;
import ices.fashion.service.space.dto.ShareWorkCriteria;
import ices.fashion.service.space.dto.ShowDto;
import ices.fashion.service.space.dto.WorkDetailDto;
import ices.fashion.service.tools.MMCGANService;
import ices.fashion.service.tools.OutfitGANService;
import ices.fashion.service.tools.RenderGANService;
import ices.fashion.service.tools.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.io.IOException;

@SpringBootTest
class FashionApplicationTests {

    @Autowired
    private MMCGANService mmcganService;

    @Autowired
    private OutfitGANService outfitGANService;

    @Autowired
    private RenderGANService renderGANService;

    @Autowired
    private ColCommentMapper colCommentMapper;

    @Autowired
    private ColProjectMapper colProjectMapper;

    @Autowired
    protected ColProjectService colProjectService;

    @Autowired
    private ColVersionService colVersionService;

    @Autowired
    private MMCGANMapper mmcganMapper;

    @Autowired
    private TOutfitLowerMapper tOutfitLowerMapper;

    @Autowired
    private TOutfitBagMapper tOutfitBagMapper;

    @Autowired
    private TOutfitShoesMapper tOutfitShoesMapper;

    @Autowired
    private TOutfitUpperMapper tOutfitUpperMapper;

    @Autowired
    private RenderMapper renderMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    private WorkService workService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollaborateMaterialMapper collaborateMaterialMapper;

    @Autowired
    private ShareMapper shareMapper;

    @Test
    void contextLoads() {
    }

    @Autowired
    ColCommentService colCommentService;

    @Test
    void testColComment() {
        List<ColCommentDto> colCommentList = colCommentService.getCommentbyVersion(1);
        for (ColCommentDto colComment : colCommentList) {
            System.out.println(colComment);
        }
    }

    @Autowired
    private UploadTokenService uploadTokenService;

    @Test
    void testProject(){
        Integer n = new Integer(1);
        List<ColProjectDto> colProjectDtoList = colProjectMapper.selectProjectbyMemberId(n);
        for(ColProjectDto colProjectDto : colProjectDtoList){
            System.out.println(colProjectDto);
        }
    }

    @Test
    void testProjectbyId(){
        ColProjectDto colProjectDto = colProjectService.getProjectbyId(1);
        System.out.print(colProjectDto);
    }

    @Test
    void testInsertProject(){
        ColProject project = colProjectService.insertProject(2,"123","123");
        System.out.println(project);
    }

    @Test
    void testVersion(){
        List<ColVersionDto> colVersionDtoList = colVersionService.getVersion(1);
        for(ColVersionDto colVersionDto : colVersionDtoList){
            System.out.println(colVersionDto);
        }
    }

    @Test
    void testInsertVersion(){
        colVersionService.insertVersion(1,"a","aa",-1);
    }


    @Test
    void testGson() throws IOException {
        MMCGANCriteria mmcganCriteria = new MMCGANCriteria();
        mmcganCriteria.setId(1);
        mmcganCriteria.setOriginalText("Womens Fashion Outerwear Jackets Balenciaga jackets Balenciaga Cropped " +
                "embellished woolblend tweed jacket");
        mmcganCriteria.setTargetCategory("Skirts");
        mmcganCriteria.setTargetText("Womens Fashion Skirts Mini Skirts Helmut Lang mini skirts Helmut Lang " +
                "Layered Skirt");
        mmcganCriteria.setFileName("e8fd2028d0462196fdd3fbc9b27bbbcc.jpg");
        mmcganService.doMMCGAN(mmcganCriteria);
    }

    @Test
    void testOutfitGAN() throws IOException {
        OutfitGANCriteria outfitGANCriteria = new OutfitGANCriteria();
        outfitGANCriteria.setUpperFileName("001aeb1dc1adbcb6a36060961f92843e.jpg");
        outfitGANCriteria.setShoesFileName("001aeb1dc1adbcb6a36060961f92843e_shoes.jpg");
//        outfitGANCriteria.setLowerFileName("001aeb1dc1adbcb6a36060961f92843e_lower.jpg");
        outfitGANCriteria.setBagFileName("001aeb1dc1adbcb6a36060961f92843e_bag.jpg");
        try{
            outfitGANService.doOutfitGAN(outfitGANCriteria);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testRender() throws IOException {
        RenderGANCriteria renderGANCriteria = new RenderGANCriteria();
        renderGANCriteria.setColorFileName("render_1.jpg");
        renderGANCriteria.setSketchFileName("000000221f00000b30e99368f4b72eed.jpg");
        renderGANService.doRenderGenerate(renderGANCriteria);
    }

    @Test
    void testUploadToken() {
        String token = uploadTokenService.getUploadToken(true);
        System.out.println(token);
    }

    @Test
    void testMMCGANInit() throws IOException {
        ApiResult<MMCGANInitDto> res =  mmcganService.init();
        System.out.println(res.getData());
        QueryWrapper<TMmc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", "e8fd2028d0462196fdd3fbc9b27bbbcc.jpg");
        TMmc cur = mmcganMapper.selectOne(queryWrapper);
        System.out.println(cur);
    }

    @Test
    void testOutfitGANInit() throws IOException {
        ApiResult<OutfitGANInitDto> res = outfitGANService.init();
        System.out.println(res.getData());
    }

    @Test
    void testSplit() {
        String s = "fashion/outfit-gan/upper/001eeda1267459a4c5dfe924c8f0468e.jpg";
        String[] ss = s.split("/");
        System.out.println(ss[ss.length - 1]);
    }

    @Autowired
    ColUserService colUserService;

    @Test
    void testColLogin(){
        String phone = "18218746467";
        String userName="zhangsan";
        int id = colUserService.login(phone,userName);
        System.out.println("colLogin "+id);
    }

    @Test
    void modifyOutfitData() {
        String dir = "C:\\Users\\81481\\Documents\\WeChat Files\\wxid_pdslqg4qx9g632\\FileStorage\\File\\2022-05\\outfitGANimage\\shoes-seg";
        File file = new File(dir);
        File[] files = file.listFiles();
        int i = 31;
        for (File f : files) {
            System.out.println(f.getName());
            TOutfitShoes tmp = new TOutfitShoes();
            tmp.setFileName("fashion/outfit-gan/shoes/" + f.getName());
            tmp.setDeleted(0);
            tmp.setId(i);
            tOutfitShoesMapper.insert(tmp);
            i++;
        }
    }

    @Test
    void modifyRenderData() {
        String[] dirList = {"C:\\Users\\HIT\\Documents\\WeChat Files\\wxid_pdslqg4qx9g632\\FileStorage\\File\\2022-05\\renderimage\\real",
        "C:\\Users\\HIT\\Documents\\WeChat Files\\wxid_pdslqg4qx9g632\\FileStorage\\File\\2022-05\\renderimage\\sketch"};
        String[] typeList = {"origin", "sketch"};
        int i = 161;
        for (int idx = 0; idx < 2; idx++) {
            File file = new File(dirList[idx]);
            String type = typeList[idx];
            File[] files = file.listFiles();
            for (File f : files) {
                System.out.println(f.getName());
                TRender tmp = new TRender();
                tmp.setFileName("fashion/render/" + type + "/" + f.getName());
                tmp.setDeleted(0);
                tmp.setId(i);
                tmp.setType(type);
                tmp.setCategory("top");
                renderMapper.insert(tmp);
                i++;
            }
        }
    }

    @Test
    void redisTest() {
        userService.getUserInfo();
    }

    @Test
    void testWorkTable() {
        TWork work = new TWork();
        work.setCategory("test3");
        work.setUserName("test3");
        work.setPhone("test3");
        System.out.println(workMapper.insert(work));
//        workMapper.insert(work);

    }

    @Test
    void testDesign() {
        ApiResult<ShowDto> res = workService.getUserDesign("test2", false);
        ShowDto data = res.getData();
        System.out.println(data);
    }

    @Test
    void testDetail() {
        ApiResult<WorkDetailDto> res = workService.getWorkDetail(5, "test");
        WorkDetailDto data = res.getData();
        System.out.println(data);
    }

    @Test
    void testInsert() {
        TWork work = new TWork();
        work.setCategory("test3");
        work.setUserName("test3");
        work.setPhone("test3");
        ApiResult res = workService.saveOneWork(work);
    }

    @Test
    void testShareWork() {
        ShareWorkCriteria shareWorkCriteria = new ShareWorkCriteria();
        shareWorkCriteria.setId(5);
        workService.shareWork(shareWorkCriteria);
    }

    @Test
    void testGetAllShareWork() {
        ApiResult<ShowDto> res = workService.getAllShareWork();
        System.out.println(res.getData());
    }

    @Test
    void testSaveShare() {
        TShare share = new TShare();
        share.setCreatorPhone("test");
        share.setCreatorCoverUrl("test");
        share.setCreatorUserName("test");
        share.setWid(3);
        share.setPhone("test");
        share.setUserName("test");
        share.setWorkCategory("vton");
        shareService.saveOneShare(share);
    }

    @Test
    void testGetUserShare() {
        System.out.println(shareService.getUserShare("test").getData());
    }

    @Test
    void testUpdateVersionShared(){
        colVersionService.updateSaved(59,1);
    }

    @Test
    void testUpdateVersionSharedStr(){
        String str = "{\n" +
                "    backImg: \"\",\n" +
                "    frontImg: \"\",\n" +
                "    introduce: \"\",\n" +
                "    version: {\n" +
                "        \"id\": 140,\n" +
                "        \"canvas\": \"1\",\n" +
                "        \"backCanvas\":\"1\",\n" +
                "        \"image\": \"1\",\n" +
                "        \"backImage\":\"1\",\n" +
                "        \"parent_version\": 134,\n" +
                "        \"create_time\": \"2022-05-30\",\n" +
                "        \"childrenIndex\": []\n" +
                "    }\n" +
                "}\n";
        colVersionService.updateSaved(str,1);
    }

    @Test
    void insertIconsMaterial(){
        String dir = "D:\\HIT\\5\\fashion\\web\\FashionAI\\static\\svg\\icons";
        File file = new File(dir);
        File[] files = file.listFiles();
        for (File f : files) {
            TCollaborateMaterial tmp = new TCollaborateMaterial();
            tmp.setFileName("fashion/collaborate/icons/" + f.getName());
            tmp.setCategory(CollaborateConst.ICONS);
            collaborateMaterialMapper.insert(tmp);
        }
    }

    @Test
    void insertClothMaterial(){
        String dir = "C:\\Users\\HIT\\Documents\\WeChat Files\\wxid_pdslqg4qx9g632\\FileStorage\\File\\2022-06\\cloth";
        File file = new File(dir);
        File[] files = file.listFiles();
        for (File f : files) {
            TCollaborateMaterial tmp = new TCollaborateMaterial();
            tmp.setFileName("fashion/collaborate/cloth/" + f.getName());
            tmp.setCategory(CollaborateConst.CLOTH);
            collaborateMaterialMapper.insert(tmp);
        }
    }

    @Test
    void insertTemplateMaterial(){
        String dir = "D:\\HIT\\5\\fashion\\web\\FashionAI\\static\\template";
        File file = new File(dir);
        File[] files = file.listFiles();
        for (File f : files) {
            TCollaborateMaterial tmp = new TCollaborateMaterial();
            tmp.setFileName("fashion/collaborate/template/" + f.getName());
            tmp.setCategory(CollaborateConst.TEMPLATE);
            collaborateMaterialMapper.insert(tmp);
        }
    }

    @Test
    void testSelectOne() {
        QueryWrapper<TShare> shareQueryWrapper = new QueryWrapper<>();
        shareQueryWrapper.eq("id", 100);
        TShare share = shareMapper.selectOne(shareQueryWrapper);
        System.out.println(share);
    }

    @Test
    void testMMC() throws IOException {
        MMCGANCriteria mmcganCriteria = new MMCGANCriteria();
        mmcganCriteria.setFileName("fashion/mmc-gan/jacket/fc81dde479776d6082eb30d3c75a5053.jpg");
        mmcganCriteria.setTargetCategory("Clothing Jeans");
        mmcganCriteria.setTargetText("Womens Fashion Clothing Jeans Black Skinny Jeans Trousers ");
        System.out.println(mmcganService.doMMCGAN(mmcganCriteria).getData().fileUrl);
    }

    @Test
    void testOutfit() throws IOException {
        OutfitGANCriteria outfitGANCriteria = new OutfitGANCriteria();
        outfitGANCriteria.setUpperFileName("fashion/outfit-gan/upper/13cb0fd8d0454a9dcc258c8d039bed54.jpg");
        outfitGANCriteria.setLowerFileName("fashion/outfit-gan/lower/0a45edf011d699b7667ada914e1be34d.jpg");
        outfitGANCriteria.setShoesFileName("fashion/outfit-gan/shoes/1eb68c4e0070698bb9832e0d75325b70.jpg");
        outfitGANCriteria.setBagFileName("fashion/outfit-gan/bag/1b42dd87b1d9a91f170e42d37e0a5d9e.jpg");
        System.out.println(outfitGANService.doOutfitGAN(outfitGANCriteria).getData());
    }
}
