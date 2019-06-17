package com.lyf.foodie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableWebMvc
public class VideoConfig implements WebMvcConfigurer {

    @Value("${video.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*if(basePath.equals("") || basePath.equals("${video.base-path}")){
            String path = VideoConfig.class.getClassLoader().getResource("").getPath();
            log.info("1.上传配置类imagesPath==" + path + "\n");
            if(path.indexOf(".jar")>0){
                path = path.substring(0, path.indexOf(".jar"));
            }else if(path.indexOf("classes")>0){
                path = "file:"+path.substring(0, path.indexOf("classes"));
            }
            path = path.substring(0, path.lastIndexOf("/"))+"/images/";
            basePath = path;
        }*/
        log.info("begin addResourceHandlers.");
        registry
                .addResourceHandler("/videos/**")
                .addResourceLocations("file:" + basePath);
    }
}
