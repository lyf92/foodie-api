package com.lyf.foodie.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @PostMapping("/files")
    public String upload(@RequestParam("video") MultipartFile file) throws IOException {
        String fileName = file.getName();
        InputStream ins = file.getInputStream();
        String contentType = file.getContentType();
        ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType);
        return objectId.toString();
    }

    @GetMapping("/files/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> fetch(@PathVariable(name = "fileId") String fileId) throws UnsupportedEncodingException {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if(gridFSFile == null || gridFSFile.getMetadata() == null) {
            return null;
        }
        return ResponseEntity.ok()
                .contentLength(gridFSFile.getLength())
                .contentType(MediaType.parseMediaType(gridFSFile.getMetadata().get("_contentType").toString()))
                .body(gridFsTemplate.getResource(gridFSFile));
    }
}
