package com.lyf.foodie.controller;

import com.lyf.foodie.VO.VideoVO;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @PostMapping("/files")
    public String upload(@RequestParam("video") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        InputStream ins = file.getInputStream();
        String contentType = file.getContentType();
        ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType);
        return objectId.toString();
    }

    @GetMapping("/files")
    public List<VideoVO> getFiles() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "uploadDate"));
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(query);

        List<VideoVO> videos = new ArrayList<>();
        for (GridFSFile gridFSFile : gridFSFiles) {
            videos.add(VideoVO.builder().id(gridFSFile.getId().toString()).name(gridFSFile.getFilename()).build());
        }
        return videos;
    }

    @GetMapping("/files/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> getById(@PathVariable(name = "fileId") String fileId) {
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

    @DeleteMapping("/files/{fileId}")
    public void delete(@PathVariable(name = "fileId") String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if(gridFSFile == null || gridFSFile.getMetadata() == null) {
            return;
        }
        gridFsTemplate.delete(query);
    }
}
