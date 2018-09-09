package com.hnust.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hnust.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "eqsIGqSR6RA6bCvkD1Qm1YSAQdt9wxkTMbU86C7o";
    String SECRET_KEY = "2wDHQ3he9A_g7s0OVSkwPIZ_3mOaxfpJ0bq-TC5_";
    //要上传的空间
    String bucketname = "toutiao";
    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();
    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    String upToken = auth.uploadToken(bucketname);

    /**
     * 保存图片
     */
    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");   ////获取文件名中最后一个点的位置
            if (dotPos < 0) {   //格式不符合
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();    //文件的后缀名fileExt
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {  //文件格式不符合，不允许上传到七牛云
                return null;
            }

            //构造一个带指定Zone对象的配置类
            //Configuration cfg = new Configuration(Zone.zone2());

            //UploadManager uploadManager = new UploadManager((Recorder) cfg);
            //设置图片上传到七牛后保存的文件名
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //String fileName = "G:\\upload\\wp.jpg";
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, upToken);
            //打印返回的信息
            System.out.println(res.toString());       //输出 Response 对象中的信息
            //解析上传成功的结果
//            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
            if (res.isOK() && res.isJson()) {
                String key = JSONObject.parseObject(res.bodyString()).get("key").toString();    //解析出文件名
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX + key;   //返回完整的文件访问路径
            } else {
                logger.error("七牛异常:" + res.bodyString());   //打印异常，bodyString() 实际上是一个 json 串
                return null;
            }
        } catch (QiniuException e) {
            Response r = e.response;
            System.err.println(r.toString());
            // 请求失败时打印的异常的信息
            logger.error("七牛异常:" + e.getMessage());
            return null;
        }
    }
}
