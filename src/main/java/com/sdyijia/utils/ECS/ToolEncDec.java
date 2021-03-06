package com.sdyijia.utils.ECS;

import com.sdyijia.utils.tool.ToolFile;
import org.apache.shiro.crypto.hash.SimpleHash;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

public class ToolEncDec {

    /**
     * 使用AES对文件进行加密和解密
     * 
     */
    private static final String TYPE = "AES";

    /**
     * 生成指定字符串的密钥
     * 
     * @param secret
     *            要生成密钥的字符串
     * @return secretKey 生成后的密钥
     * @throws GeneralSecurityException
     */
    private static Key getKey(String secret) throws GeneralSecurityException {
        KeyGenerator kgen = KeyGenerator.getInstance(TYPE);
        kgen.init(128, new SecureRandom(secret.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        return secretKey;
    }

    /**
     * 加密解密流
     * 
     * @param in
     *            加密解密前的流
     * @param out
     *            加密解密后的流
     * @param cipher
     *            加密解密
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static void crypt(InputStream in, OutputStream out, Cipher cipher)
            throws IOException, GeneralSecurityException {
        int blockSize = cipher.getBlockSize() * 1000;
        int outputSize = cipher.getOutputSize(blockSize);

        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];

        int inLength = 0;
        boolean more = true;
        while (more) {
            inLength = in.read(inBytes);
            if (inLength == blockSize) {
                int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
                out.write(outBytes, 0, outLength);
            } else {
                more = false;
            }
        }
        if (inLength > 0)
            outBytes = cipher.doFinal(inBytes, 0, inLength);
        else
            outBytes = cipher.doFinal();
        out.write(outBytes);
    }

    /**
     * 把文件srcFile加密后存储为destFile
     * 
     * @param srcFile
     *            加密前的文件
     * @param destFile
     *            加密后的文件
     * @param privateKey
     *            密钥
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void encrypt(String srcFile, String destFile, String privateKey)
            throws GeneralSecurityException, IOException {
        Key key = getKey(privateKey);
        Cipher cipher = Cipher.getInstance(TYPE + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(ToolFile.mkdirFiles(destFile));

            crypt(fis, fos, cipher);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 把文件srcFile解密后存储为destFile
     * 
     * @param srcFile
     *            解密前的文件
     * @param destFile
     *            解密后的文件
     * @param privateKey
     *            密钥
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void decrypt(String srcFile, String destFile, String privateKey)
            throws GeneralSecurityException, IOException {
        Key key = getKey(privateKey);
        Cipher cipher = Cipher.getInstance(TYPE + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(ToolFile.mkdirFiles(destFile));

            crypt(fis, fos, cipher);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 利用MD5进行加密
     * 
     * @param str
     *            待加密的字符串
     * @return 加密后的字符串
     */
    public static String encoderByMd5(String str, String salt) {
        try {
            String newstr = new SimpleHash("MD5", str, salt, 1024) + "";
//            System.out.println(newstr);
            return newstr;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

}
