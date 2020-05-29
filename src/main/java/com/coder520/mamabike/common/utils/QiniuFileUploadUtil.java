package com.coder520.mamabike.common.utils;

import com.coder520.mamabike.common.constants.Constants;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/4.
 */
public class QiniuFileUploadUtil {

    public static String uploadHeadImg(MultipartFile file) throws IOException {

        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(Constants.QINIU_ACCESS_KEY, Constants.QINIU_SECRET_KEY);
        String upToken = auth.uploadToken(Constants.QINIU_HEAD_IMG_BUCKET_NAME);
        Response response = uploadManager.put(file.getBytes(),null, upToken);
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }


//    public static void main(String[] args) throws IOException {
//        uploadHeadImg(new File("C:\\Users\\Administrator\\Desktop\\FncDEhwErCW8DdZOIAY0cz-sc1TV"),"FncDEhwErCW8DdZOIAY0cz-sc1TV");
//    }

}
