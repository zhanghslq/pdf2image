package com.zhs.pdf2image.controller;

import com.aspose.pdf.*;
import com.aspose.pdf.devices.JpegDevice;
import com.aspose.pdf.devices.Resolution;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.zhs.pdf2image.model.ImageBO;
import com.zhs.pdf2image.model.Result;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
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
        JpegDevice jpegDevice = new JpegDevice(85);
        long timeMillis = System.currentTimeMillis();
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int monthValue = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();
        Document pdfDocument = new Document(pdfPath);
        PageCollection pages = pdfDocument.getPages();
        int imageIndex= 0;
        for (Page page : pages) {
            XImageCollection images = page.getResources().getImages();
            for (XImage image : images) {
                String imageUrl = BASE_IMAGE_PATH+"/" + year+"/"+monthValue+"/"+dayOfMonth+"/"+timeMillis + "/item-image" + "/" + (imageIndex++ + 1) + ".jpg";
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
        String base = BASE_IMAGE_PATH + "/" + year+"/"+monthValue+"/"+dayOfMonth+"/"+timeMillis + "/";
        for (int i = 0; i < size; i++) {
            String imageUrl =  base + (i + 1) + ".jpg";
            File file = new File(imageUrl);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            try {
                jpegDevice.process(pages.get_Item(i + 1), fos);
                fos.close();
                // 转换
                File parentDirectory = new File(base);
                if(parentDirectory.exists()){
                    File[] files = parentDirectory.listFiles();
                    for (File fileItem : files) {
                        if(fileItem.isDirectory()){
                            continue;
                        }
                        updateImage(fileItem,base+"/1050/"+(i+1)+".jpg",1050);
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                fos.close();
            }
        }
        return base;
    }

    public static void updateImage(File file,String outPath,int wid) throws IOException{

        InputStream inputStream = new FileInputStream(file);

        BufferedImage bufferedImage = ImageIO.read(inputStream);

        int height = bufferedImage.getHeight(); //图片的高
        int width = bufferedImage.getWidth();  //图片的宽

        int newHeight;
        int newWidth;
        double s = wid*1.0/width;
        newHeight = (int) (height *s);
        newWidth = (int) (width *s);

        File outFile = new File(outPath);
        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).forceSize(newWidth, newHeight).toFile(outPath);

        inputStream.close();
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
