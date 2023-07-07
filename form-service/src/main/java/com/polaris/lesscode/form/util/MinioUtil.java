package com.polaris.lesscode.form.util;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * minio utils
 *
 * @author ethanliao
 * @date 2020/12/29
 * @since 1.0.0
 */
public class MinioUtil {
    private MinioUtil() {
    }

    public static MinioClient getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private static String endPoint;
    private static String key;
    private static String secretKey;

    public static void setEndPoint(String endPoint) {
        MinioUtil.endPoint = endPoint;
    }

    public static void setKey(String key) {
        MinioUtil.key = key;
    }

    public static void setSecretKey(String secretKey) {
        MinioUtil.secretKey = secretKey;
    }

    private enum Singleton {
        INSTANCE;
        private MinioClient singleton;

        Singleton() {
            singleton =
                    MinioClient.builder()
//                            .endpoint("https://minio.startable.cn")
//                            .credentials("admin", "runx@123")
//                            .build();
                            .endpoint(endPoint)
                            .credentials(key, secretKey)
                            .build();
        }

        public MinioClient getInstance() {
            return singleton;
        }
    }

    public static void putObject(String bucket, String fileName, MultipartFile file) {
        try {
            boolean found =
                    MinioUtil.getInstance().bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                MinioUtil.getInstance().makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            MinioUtil.getInstance().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static InputStream getObject(String bucket, String fileName) {
        try {
            return MinioUtil.getInstance().getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(new byte[0]);
    }
}
