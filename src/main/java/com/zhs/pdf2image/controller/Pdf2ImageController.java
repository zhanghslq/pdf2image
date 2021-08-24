package com.zhs.pdf2image.controller;

import com.aspose.pdf.*;
import com.aspose.pdf.devices.JpegDevice;
import com.zhs.pdf2image.model.ImageBO;
import com.zhs.pdf2image.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: zhs
 * @date: 2021/8/24 22:45
 */
@Slf4j
@RestController
public class Pdf2ImageController {
    @Value("${pdf.image.basepath}")
    private String BASE_IMAGE_PATH;



    @PostMapping("pdf_to_image")
    public Result<String> pdfToImage(@RequestParam String path){
        String imagePath=null;
        try {
            imagePath = pdf2Image(path);
        } catch (Exception e) {
            log.error("转换失败：",e);
            return Result.fail(-1,"转换失败",null);
        }
        return Result.success(imagePath);
    }

    public  String pdf2Image(String pdfPath) throws IOException {
        getpdfLicense();
        JpegDevice jpegDevice = new JpegDevice();
        long timeMillis = System.currentTimeMillis();
        Document pdfDocument = new Document(pdfPath);
        PageCollection pages = pdfDocument.getPages();
        int imageIndex= 0;
        for (Page page : pages) {
            XImageCollection images = page.getResources().getImages();
            for (XImage image : images) {
                String imageUrl = BASE_IMAGE_PATH+"/" + timeMillis + "/item-image" + "/" + (imageIndex++ + 1) + ".jpg";
                File file = new File(imageUrl);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    image.save(fos);
                    fos.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    fos.close();
                }
            }
        }

        int size = pages.size();
        for (int i = 0; i < size; i++) {
            String imageUrl = BASE_IMAGE_PATH + "/" + timeMillis + "/" + (i + 1) + ".jpg";
            File file = new File(imageUrl);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            try {
                jpegDevice.process(pages.get_Item(i + 1), fos);
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                fos.close();
            }
        }
        return BASE_IMAGE_PATH + "/" + timeMillis + "/";
    }

    public  boolean getpdfLicense() {
        boolean result2 = false;
        try {
            String license2 = "<License>\n"
                    + "  <Data>\n"
                    + "    <Products>\n"
                    + "      <Product>Aspose.Total for Java</Product>\n"
                    + "      <Product>Aspose.Words for Java</Product>\n"
                    + "    </Products>\n"
                    + "    <EditionType>Enterprise</EditionType>\n"
                    + "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n"
                    + "    <LicenseExpiry>20991231</LicenseExpiry>\n"
                    + "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n"
                    + "  </Data>\n"
                    + "  <Signature>111</Signature>\n"
                    + "</License>";
            InputStream is2 = new ByteArrayInputStream(
                    license2.getBytes(StandardCharsets.UTF_8));
            com.aspose.pdf.License asposeLic2 = new com.aspose.pdf.License();
            asposeLic2.setLicense(is2);
            result2 = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result2;
    }

}
