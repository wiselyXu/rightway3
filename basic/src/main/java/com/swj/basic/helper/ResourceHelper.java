package com.swj.basic.helper;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.swj.basic.SwjConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * com.swj.basic.helper
 *
 * @autor Chenjw
 * @since 2018/5/21
 **/
public class ResourceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResourceHelper.class);
    private static Map<String, AmazonS3> amazonS3Map = new HashMap<>();
    private static final String ZKACCESSKEY = "3dapiCephKey";
    private static final String READ_URL = "AWSReadUrl";
    private static final String REGION_CN = "cn-north-1";
    private static final String ACCESSKEY = "accessKey";
    private static final String SECRETKEY = "secretKey";
    private static final String ENDPOINT = "endPoint";
    private static final String BUCKETNAME = "bucketName";
    private static ObjectMetadata metadata;

    public static Map<String, String> CONTENTTYPE = new HashMap<>();


    static {
        CONTENTTYPE.put("pdf", "application/pdf; charset=utf-8");
        CONTENTTYPE.put("png", "image/png; charset=utf-8");
        CONTENTTYPE.put("doc", "application/msword; charset=utf-8");
        CONTENTTYPE.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=utf-8");
        CONTENTTYPE.put("xls", "application/vnd.ms-excel; charset=utf-8");
        CONTENTTYPE.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=utf-8");
        CONTENTTYPE.put("jpeg", "image/jpeg; charset=utf-8");
        CONTENTTYPE.put("jpg", "image/jpeg; charset=utf-8");
    }

    static {
        String key = SwjConfig.get(ZKACCESSKEY);
        if (key != null) {
            String[] keys = key.split(";");
            for (String k : keys) {
                String[] kk = k.split(",");
                Map<String, String> map = new HashMap<>();
                for (String re : kk) {
                    String[] strings = re.split("=");
                    map.put(strings[0], strings[1]);
                }
                BasicAWSCredentials credentials = new BasicAWSCredentials(map.get(ACCESSKEY), map.get(SECRETKEY));
                AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(map.get(ENDPOINT), REGION_CN))
                        .withPathStyleAccessEnabled(true)
                        .build();
                amazonS3Map.put(map.get(BUCKETNAME), amazonS3);
            }
            metadata = new ObjectMetadata();
        }
    }

    /**
     * 上传文件对象
     *
     * @author Chenjw
     * @since 2018/5/21
     **/
    public static void putObject(String content, String key, String bucketName) {
        putObject(content.getBytes(), key, bucketName);
    }


    public static void putObject(byte[] content, String key, String bucketName) {
        AmazonS3 s3 = amazonS3Map.get(bucketName);
        if (s3 != null) {
            String contentType = CONTENTTYPE.get(getContentType(key));
            if (!StringHelper.isNullOrEmpty(contentType))  metadata.setContentType(contentType.toLowerCase());
            PutObjectRequest request = new PutObjectRequest(bucketName, getKey(key), new ByteArrayInputStream(content), metadata);
            request.setCannedAcl(CannedAccessControlList.AuthenticatedRead);
            s3.putObject(request);
        }
    }

    public static String getContentType(String key) {
        String contentType = null;
        if (!StringHelper.isNullOrEmpty(key)) {
            contentType = key.substring(key.lastIndexOf(".") + 1, key.length());
        }
        return contentType;
    }

    /**
     * 删除文件对象
     *
     * @author Chenjw
     * @since 2018/5/29
     **/
    public static void deleteObject(String key, String bucketName) {
        AmazonS3 s3 = amazonS3Map.get(bucketName);
        if (s3 != null) {
            s3.deleteObject(bucketName, getKey(key));
        }
    }

    /**
     * 遍历 某路径下的文件 获取相应的文件
     *
     * @author Chenjw
     * @since 2018/5/29
     **/
    public static List<S3ObjectSummary> getListInfo(String prefix, String bucketName) {
        List<S3ObjectSummary> result = new ArrayList<>();
        AmazonS3 s3 = amazonS3Map.get(bucketName);
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(getKey(prefix));
            ObjectListing objectListing;
            do {
                objectListing = s3.listObjects(listObjectsRequest);
                result = objectListing.getObjectSummaries();
                listObjectsRequest.setMarker(objectListing.getMarker());
            } while (objectListing.isTruncated());
        } catch (AmazonS3Exception ase) {
            logger.error("Get file info from ceph occur a problem error{}", ase.getMessage());
        }
        return result;
    }

    /**
     * 获取单个文件
     *
     * @author Chenjw
     * @since 2018/5/29
     **/
    public static String getObject(String bucketName, String key) {
        AmazonS3 s3 = amazonS3Map.get(bucketName);
        StringBuffer sb = new StringBuffer();
        S3ObjectInputStream objectContent = null;
        try {
            S3Object s3Object = s3.getObject(bucketName, getKey(key));
            objectContent = s3Object.getObjectContent();
            int len = 0;
            byte[] buf = new byte[1024 * 8];
            while ((len = objectContent.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
        } catch (AmazonS3Exception e) {
            logger.error("AmazonS3Exception error:{}", e.getMessage());
        } catch (IOException e) {
            logger.error("Get Resource from ceph occur a IOException !", e);
        } finally {
            try {
                if (objectContent != null)
                    objectContent.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * key 保持永远是 xx/xx/xx.xx
     *
     * @author Chenjw
     * @since 2018/5/21
     **/
    private static String getKey(String key) {
        String newKey = key;
        if (key.startsWith("/")) {
            newKey = key.substring(1, key.length());
        }
        return newKey;
    }

    /**
     * 判断key 是否存在
     *
     * @author Chenjw
     * @since 2018/5/29
     **/
    public static boolean doesExist(String bucketName, String key) {
        AmazonS3 s3 = amazonS3Map.get(bucketName);
        return s3.doesObjectExist(bucketName, getKey(key));
    }

    /**
     * 手工添加 新的ceph 对象
     *
     * @author Chenjw
     * @since 2018/5/24
     **/
    public static void createNewAmazonS3(String bucketName, String accessKey, String secretKey, String endPoint) throws Exception {
        if (StringHelper.isNullOrEmpty(bucketName) || StringHelper.isNullOrEmpty(accessKey) || StringHelper.isNullOrEmpty(secretKey) || StringHelper.isNullOrEmpty(endPoint)) {
            throw new Exception("bucketName,accessKey,secretKey,endPoint must be not null ! please check the parameter");
        }
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, REGION_CN))
                .withPathStyleAccessEnabled(true)
                .build();
        amazonS3Map.put(bucketName, amazonS3);
    }
}
