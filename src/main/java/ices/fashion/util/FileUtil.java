package ices.fashion.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import ices.fashion.constant.QiniuCloudConst;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class FileUtil {

    private static final String sepa = File.separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static File download(String targetUrl, String fileName) {
        String downloadUrl = getDownloadUrl(targetUrl);
        return downloadFile(downloadUrl, fileName);
    }

    public static File downloadFile(String url, String fileName) {
        File file = null;
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request req = new Request.Builder().url(url).build();
        Response resp = null;
        String[] fileNameSplitArray = fileName.split("/");
        fileName = fileNameSplitArray[fileNameSplitArray.length - 1];
        try {
            resp = client.newCall(req).execute();
//            System.out.println(resp.isSuccessful());
            if (resp.isSuccessful()) {
                ResponseBody body = resp.body();
                InputStream is = body.byteStream();
                byte[] data = readInputStream(is);
                String filePath = System.getProperty("user.dir") + sepa + "img" + sepa;
                file = new File(filePath + fileName);
                FileOutputStream fops = new FileOutputStream(file);
                fops.write(data);
                fops.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unexpected code" + resp);
        }
        return file;
    }

    public static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buff  = new byte[1024 * 2];
        int len = 0;
        try {
            while ((len = is.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }



    public static String getDownloadUrl(String targetUrl) {
        Auth auth = Auth.create(QiniuCloudConst.ACCESS_KEY, QiniuCloudConst.SECRET_KEY);
        return auth.privateDownloadUrl(targetUrl);
    }

    public static String concatUrl(String fileName) throws UnsupportedEncodingException {
        String encodeFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        return String.format("%s/%s", QiniuCloudConst.DOMAIN_BUCKET, encodeFileName);
    }

    public static String concatUrlwithoutEncoding(String fileName) {
        String ext = "png";
        return String.format("%s/%s.%s", QiniuCloudConst.DOMAIN_BUCKET, fileName, ext);
    }
    public static String url2FileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * ??? url ????????????
     * @param multipartFile
     * @param url
     * @return
     */
    public static String uploadFile(MultipartFile multipartFile, String url, String name) {
        if (multipartFile == null && StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            return null;
        }
        // ?????? FileSystemResource ??????
        File excelFile = transfer2file(multipartFile);
        FileSystemResource fileSystemResource = new FileSystemResource(excelFile);
        log.info("post filename: " + fileSystemResource.getFilename());
        // ?????? Post ??????
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
        map.add(name, fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        // ?????? Post ??????
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(response.getBody());
//        ????????????????????????????????????
		deleteFile(excelFile);
        return response.getBody();
    }

    private static File transfer2file(MultipartFile multfile) {
        // ???????????????
        String fileName = multfile.getOriginalFilename();
        log.info("original filename: " + fileName);
        // ??????????????????
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // ??????????????????
        File file = createTmpFile(multfile.getOriginalFilename());
        // MultipartFile to File
        try {
//			excelFile = File.createTempFile(fileName, prefix);
            multfile.transferTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
//		log.info("filename: " + excelFile);
        return file;
    }

    private static File createTmpFile(String filename) {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "tmp";
        File dir = new File(path);
        if(!dir.exists()){//????????????????????????
            dir.mkdirs();
        }
        String filepath = path + sepa + filename;
        File file = new File(filepath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("filepath: " + filepath);
        return file;
    }

    //???byte??????????????????
    public static void createFile(String filename, byte[] content) {
        String path = System.getProperty("user.dir") + sepa + "img" + sepa + filename;
        System.out.println(path);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MultipartFile fileToMultipartFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        System.out.println(file.getName());
        String contentType = "image/jpeg";
        int len = file.getName().length();
        if (file.getName().startsWith("png", len - 3)) {
            contentType = "image/png";
        }

        return new MockMultipartFile(file.getName(), file.getName(), contentType, input);
    }

    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean uploadFile2Cloud(String fileName) {
        //????????????????????? Region ??????????????????
        Configuration cfg = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(cfg);

        String filePath = System.getProperty("user.dir") + sepa + "img" + sepa + fileName;
        Auth auth = Auth.create(QiniuCloudConst.ACCESS_KEY, QiniuCloudConst.SECRET_KEY);
        String upToken = auth.uploadToken(QiniuCloudConst.BUCKET);
        try {
            com.qiniu.http.Response response = uploadManager.put(filePath, fileName, upToken);
            //???????????????????????????
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println("key is: " + putRet.key);
//            System.out.println("hash is: " + putRet.hash);
            return true;
        } catch (QiniuException  ex) {
            com.qiniu.http.Response r = ex.response;
            System.err.println(r.toString());
            log.error(r.toString());
            return false;
//            try {
//                System.err.println(r.bodyString());
//            } catch (QiniuException ex2) {
//                //ignore
//                ex2.fillInStackTrace();
//            }
        }
    }

    public static void setExpireTime(String fileName) {
        //????????????????????? Region ??????????????????
        Configuration cfg = new Configuration(Region.huanan());
        Auth auth = Auth.create(QiniuCloudConst.ACCESS_KEY, QiniuCloudConst.SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.deleteAfterDays(QiniuCloudConst.BUCKET, fileName, QiniuCloudConst.EXPIRE_DAYS);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
    }

    public static String pictureFileToBase64String(File picture) throws IOException {
        String res = "";
        try (FileInputStream input = new FileInputStream(picture)) {
            List<Byte> buffer = new ArrayList<>();
            int n;
            while ((n = input.read()) != -1) {
                buffer.add((byte)n);
            }
            byte[] pictureBytes = new byte[buffer.size()];
            int idx = 0;
            for (Byte b : buffer) {
                pictureBytes[idx++] = b;
            }

            //base64????????????utf-8??????????????????
            byte[] tmp = Base64.getEncoder().encode(pictureBytes);
            res = new String(tmp, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static byte[] base64StringToBytes(String s) {
        return Base64.getDecoder().decode(s);
    }

    /**
     * ??????multipartFile????????????
     */
    public static String uploadMultipartFile(MultipartFile multipartFile) {
        if (multipartFile != null && StringUtils.isNotEmpty(multipartFile.getOriginalFilename())) {
            try {

                // ????????????
                Auth auth = Auth.create(QiniuCloudConst.ACCESS_KEY, QiniuCloudConst.SECRET_KEY);
                // ?????????????????????????????????Zone?????????????????????zone2???????????????
                Configuration configuration = new Configuration(Zone.zone2());
                UploadManager uploadManager = new UploadManager(configuration);

                // ????????????yyyyMMdd_timeStamp_OriginalFileName
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = simpleDateFormat.format(new Date());
                String fileName = date + "_" + System.currentTimeMillis() + "_"
                        + Base64Utils.encodeToString(multipartFile.getOriginalFilename().getBytes("UTF-8"));
                String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                if (StringUtils.isEmpty(extension)) {
                    extension = "png";
                }
                String fullName = fileName + "." + extension;

                InputStream inputStream = multipartFile.getInputStream();
                // ??????put????????????
                com.qiniu.http.Response response = uploadManager.put(inputStream, fullName, auth.uploadToken(QiniuCloudConst.BUCKET), null, null);
                String url = QiniuCloudConst.DOMAIN_BUCKET + "/" + fullName;
                LOGGER.info("image url: {}", url);
                return url;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
