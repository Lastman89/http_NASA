package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;



public class Main {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        String urlRequest = "https://api.nasa.gov/planetary/apod?api_key=dx8UWRnUW0ZU9v6mAdtAo3UkSvi7XWSVbkiyfeAN";
        httpClient(httpClient(urlRequest));
    }

    public static String httpClient(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        // создание объекта запроса с произвольными заголовками
        HttpGet request = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (url.contains(".jpg")) {
            String name = null;
            String[] namePic = url.split("/");
            for (int i=0; i<namePic.length; i++){
                if (namePic[i].contains(".jpg")) {
                    name = namePic[i];
                }
            }
            savePicture(url, name);
            return name;
        } else {
            // чтение тела ответа
            Post posts = null;
            try {
                posts = mapper.readValue(response.getEntity().getContent(), new TypeReference<Post>() {
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return posts.getUrl();
        }

    }

    public static void savePicture (String urlPic, String fileName) {
        try {
            BufferedImage img = ImageIO.read(new URL(urlPic));//читаем картинку в буффер
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();//создаем файл
            }
            ImageIO.write(img, "jpg", file);//сохраняем считанную картинку
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}