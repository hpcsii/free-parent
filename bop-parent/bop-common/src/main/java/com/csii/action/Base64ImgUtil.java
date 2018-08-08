/**
 * ModifiedBy:   hepeng
 * 图片文件转base64字符串 and base64字符串转图片文件
 * Date:     2018/8/7 下午5:00
 */
package com.csii.action;


import com.jcraft.jsch.ChannelSftp;
import sun.misc.BASE64Decoder;

import java.io.*;

public class Base64ImgUtil {

    public static void main(String[] args) {
        //图片转base64 str
        String str = getImgStr("/Users/hepeng/Desktop/彭.jpg");
        System.out.println(str);
        //base64 str转图片
        GenerateImg(str, "/Users/hepeng/Desktop/zhu.jpg");
        ChannelSftp csf = new ChannelSftp();
    }

    public static String getImgStr(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        }
        FileInputStream fin = null;
        byte[] data = null;
        try {
            fin = new FileInputStream(filePath);
            int count = -1;
            while (count == -1) {//in.available可能为0，所以不能用0作默认值，以防死循环
                count = fin.available();
            }
            if (count <= 0) {
                return null;
            }
            data = new byte[count];
            fin.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //对字节数组进行base64编码
        return Base64Encoder.encode(data);
    }

    public static boolean GenerateImg(String imgStr, String outFilePath) {
        if (imgStr == null || "".equals(imgStr) || outFilePath == null || "".equals(outFilePath)) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bos = null;
        try {
            //base64解码
            byte[] data = decoder.decodeBuffer(imgStr);
            fileOutputStream = new FileOutputStream(outFilePath);
            bos = new BufferedOutputStream(fileOutputStream);
            //使用缓冲区写二进制字节数据
            bos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
