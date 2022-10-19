//package com.example.rest.service.impl;
//
//import com.example.rest.common.CommonResponse;
//import com.example.rest.common.CommonService;
//import com.example.rest.model.entity.File;
//import com.example.rest.model.response.AddPostResponse;
//import com.example.rest.service.IPostService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PostService implements IPostService {
//    @Autowired
//    private CommonService commonService;
//    @Override
//    public CommonResponse<AddPostResponse> addPost(String token, MultipartFile[] image, MultipartFile video, String described, String status) {
//
//        commonService.checkCommonValidate(token);
//
//        if((image == null || image.length == 0) && (video == null || video.isEmpty())){
//            //return lỗi
//        }
//        //Add Post
//
//        //Add File
//        try {
//            // Declare empty list for collect the files data
//            // which will come from UI
//            if(image.length > 0){
//                List<File> fileList = new ArrayList<File>();
//                for (MultipartFile file : image) {
//                    String fileName = file.getOriginalFilename();
//                    File file1 = new File();
//                    file1.setContent(fileName);
//
//                    // Adding file into fileList
//                    fileList.add(fileModal);
//                }
//            }
//
//            // Saving all the list item into database
//            fileServiceImplementation.saveAllFilesList(fileList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return null;
//    }
//}
