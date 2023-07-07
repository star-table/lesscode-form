package com.polaris.lesscode.form.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.vo.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * PDF转换工具
 *
 * @author: Liu.B.J
 * @data: 2020/10/26 19:52
 * @modified:
 */
public class PdfUtil {

    /**
     * html转换成pdf文件
     *
     * @param htmlContent
     * @throws Exception
     */
    public static void html2Pdf(String htmlContent, String filePath) throws Exception {
        File dir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File pdfFile = new File(filePath);
        //1 打开文件流
        Document document = new Document();
        FileOutputStream fos = new FileOutputStream(pdfFile);
        InputStream is = new ByteArrayInputStream(htmlContent.getBytes(Charset.forName("UTF-8")));
        InputStream cssIs = new ByteArrayInputStream(getCssFile());
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, fos);
            //3. 设置字体
            XMLWorkerFontProvider fontProvider1 = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
            ClassPathResource pathResource = new ClassPathResource("static/simsun.ttc");
            fontProvider1.register(pathResource.getPath());
            // Thread.currentThread().getContextClassLoader().getResource("").getPath()+"static/simsun.ttc"

            //3 打开文档
            document.open();
            //4 html转为pdf
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is, cssIs, Charset.forName("UTF-8"), fontProvider1);

        } catch (DocumentException | IOException e) {
            throw new BusinessException(ResultCode.MEMO_PDF_DOWNLOAD_FAIL);
        } finally {
            if (null != writer) {
                writer.flush();
            }
            //5 关闭文档
            document.close();
            fos.close();
            cssIs.close();
            is.close();
            writer.close();
        }
    }

    /**
     * 获取html
     *
     * @return
     */
    public static String content2Html(String title, String content) {
        title = "<h2 align=\"center\">" + title + "</h2><br/>";
        content = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><link href=\"static/css/editor.css\" type=\"text/css\" rel=\"stylesheet\" /></head><body style=\"font-family: SimSun;\">" +
                title +
                content +
                "</body></html>";
        return content.replace("<p></p>", "<br/>");
    }

    /**
     * 获取样式文件
     *
     * @return
     * @throws Exception
     */
    private static byte[] getCssFile() throws Exception {
        //String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"static/css/editor.css";
        Resource resource = new ClassPathResource("static/css/editor.css");
        BufferedInputStream inputStream = new BufferedInputStream (resource.getInputStream());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inputStream.close();
        return outStream.toByteArray();
    }

}
