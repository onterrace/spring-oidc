package com.jirepos.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

/**
 * 클래스패스 경로에 있느 파일을 읽기 위한 유틸리티 클래스이다. 
 */
public class ClassPathFileUtil {

    /**
     * 경로에 있는 파일을 읽어서 파일의 내용을 반환한다.
     * 
     * @param path 클래스패스의 파일경로 - "com/sanghyun/aaa.txt" 와 같이 경로를 설정
     * @return
     *         파일 내용
     * @throws Exception
     */
    public static String readFile(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        FileReader fr = new FileReader(resource.getFile());
        String line = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }// :

    /**
     * 클래스 패스의 파일의 InputStream 객체를 반환한다.
     * 
     * @param path 클래스패스의 파일경로 - "com/sanghyun/aaa.txt" 와 같이 경로를 설정
     * @return
     *         InputStream 인스턴스
     * @throws IOException
     */
    public static InputStream readFileToInputStream(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getInputStream();
    }// :

    /**
     * 클래스패스의 파일객체를 생성하고 반환한다.
     * 
     * @param path
     *             클래스패스의 파일경로 - "com/sanghyun/aaa.txt" 와 같이 경로를 설정
     * @return
     *         파일 객체
     * @throws IOException
     */
    public static File getFileObject(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getFile();
    }// :

}
