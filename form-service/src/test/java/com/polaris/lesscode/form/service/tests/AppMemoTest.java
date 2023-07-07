package com.polaris.lesscode.form.service.tests;

import com.polaris.lesscode.form.resp.AppMemoResp;
import com.polaris.lesscode.form.util.PdfUtil;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Liu.B.J
 * @data: 2020/10/29 12:47
 * @description:
 */
public class AppMemoTest extends BaseTest {

    public void testDownloadPdf(HttpServletResponse response) throws Exception {
        String filePath = "E:\\testDownload\\备忘录_2020.pdf";
        String fileName = "备忘录_2020.pdf";
        // url = pdfLocalDomain + fileName;
        PdfUtil.html2Pdf(PdfUtil.content2Html("LBJ", "这是内容"), filePath);
        File file = new File(filePath);
        if(file.exists()){
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download  successfully!");

            } catch (Exception e) {
                System.out.println("Download  failed!");
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

}
