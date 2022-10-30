package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.entity.File;
import com.example.rest.model.entity.Post;
import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.repository.*;
import com.example.rest.service.IPostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService implements IPostService {
    private final CommonService commonService;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    //tạo folder trong project để lưu file nếu chưa có
    protected static void createDirectoryIfItDoesntExist(String dir) {
        final Path path = Paths.get(dir);

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ie) {
                ie.getMessage();
            }
        }
    }

    @Override
    public CommonResponse<AddPostResponse> addPost(String token, MultipartFile[] image, MultipartFile video, String described, String status) throws Exception {
        try {
            //Check validate token (token bắt buộc)
            commonService.checkCommonValidate(token);
            checkConstraintOfFile(described, image, video);
            //Save post -> Save File
            createDirectoryIfItDoesntExist(Constant.ROOT_DIRECTORY);
            //Get user Id from token
            int userId = Integer.parseInt(commonService.getUserIdFromToken(token).getData().get(0).getId());
            if (userId > 0) {

                Post post = postRepository.save(setCommonPostInfo(userId, described));
                if (post != null && post.getId() > 0) {
                    if (image.length > 0 && image.length <= 4) { //xem lại
                        for (int i = 0; i < image.length; i++) {
                            saveFile(image[i], Paths.get(Constant.ROOT_DIRECTORY));
                            fileRepository.save(setCommonFileInfo(image[i].getOriginalFilename(), post.getId()));
                        }
                    } else if (!video.isEmpty()) {
                        saveFile(video, Paths.get(Constant.ROOT_DIRECTORY));
                        fileRepository.save(setCommonFileInfo(video.getOriginalFilename(), post.getId()));
                    }
                }
                //For response OK status
                List<AddPostResponse> list = new ArrayList<>();
                AddPostResponse addPostResponse = new AddPostResponse();
                addPostResponse.setId(String.valueOf(post.getId()));
                addPostResponse.setUrl("");
                list.add(addPostResponse);
                return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, list);
            }
        } catch (Exception e) {
            return new CommonResponse(Constant.EXCEPTION_ERROR_CODE, Constant.EXCEPTION_ERROR_MESSAGE, null);
        }
        return new CommonResponse(Constant.COULD_NOT_PUBLISH_THIS_POST_CODE, Constant.COULD_NOT_PUBLISH_THIS_POST_MESSAGE, null);
    }

    @Override
    public CommonResponse deletePost(String token, String postId) throws IOException {
        commonService.checkCommonValidate(token, postId);
        //Lấy user id from token
        String userId = commonService.getUserIdFromToken(token).getData().get(0).getId();
        //Lấy thông tin bài viết
        Post post = postRepository.findById(Integer.parseInt(postId));
        if (post == null) {
            return new CommonResponse(Constant.POST_IS_NOT_EXISTED_CODE, Constant.POST_IS_NOT_EXISTED_MESSAGE, null);
        }

        if (post.getUserId() != Integer.parseInt(userId)) {
            return new CommonResponse(Constant.NOT_ACCESS_CODE, Constant.NOT_ACCESS_MESSAGE, null);
        }

        //Xóa post -> isDeleted = true
        try{
            post.setDeleted(true);
            post.setModifiedDate(System.currentTimeMillis());
            post.setModifiedBy(userId);
            postRepository.save(post);
            //Xóa File của post
            List<File> files = fileRepository.findByPostId(post.getId());
            for(int i = 0;i < files.size();i++){
                File file = files.get(i);
                deleteFile(file.getContent(), Path.of(Constant.ROOT_DIRECTORY));
                file.setDeleted(true);
                file.setModifiedBy(userId);
                file.setModifiedDate(System.currentTimeMillis());
                fileRepository.save(file);
            }
        }catch (Exception e){
            return new CommonResponse(Constant.CAN_NOT_CONNECT_TO_DB_CODE,Constant.CAN_NOT_CONNECT_TO_DB_MESSAGE,null);
        }
        return new CommonResponse(Constant.OK_CODE,Constant.OK_MESSAGE,null);
    }

    @Override
    public CommonResponse editPost(String token, String postId, String described, String status, MultipartFile[] image, List<String> imageIdsDeleted, List<String> imageIdsSort, MultipartFile video, MultipartFile thumb, String autoAccept, String autoBlock) throws Exception {

        commonService.checkCommonValidate(token, postId);
        //Lấy user id from token
        String userId = (commonService.getUserIdFromToken(token).getData().get(0).getId());
//        imageIdsDeleted => phục vụ cho case muốn xóa ảnh
//        image && imageIdsSort => phục vụ cho case update
        checkConstraintOfFile(described, image, video);
        //Case Success
        //Lấy thông tin bài viết
        Post post = postRepository.findById(Integer.parseInt(postId));
        if (post == null) {
            return new CommonResponse(Constant.POST_IS_NOT_EXISTED_CODE, Constant.POST_IS_NOT_EXISTED_MESSAGE, null);
        }
        //Lấy các file của post đó
        List<File> files = fileRepository.findByPostId(post.getId());
        if (files == null || files.size() == 0) {
            return new CommonResponse(Constant.NO_DATA_OR_END_OF_LIST_DATA_CODE, Constant.NO_DATA_OR_END_OF_LIST_DATA_MESSAGE, null);
        }
        if (described.length() > 0) {
            post.setContent(described);
            post.setModifiedBy(userId);
            post.setModifiedDate(System.currentTimeMillis());
            postRepository.save(post);
        }
        //Case xóa ảnh thành công
        //Xóa ảnh ở bài viết có ảnh
        if (imageIdsDeleted != null && imageIdsDeleted.size() > 0 && checkImageFileTypeValid(files.get(0).getContent())) {
            //Check các id ảnh muốn xóa => phải trùng với các id ảnh của bài post
            for (int i = 0; i < imageIdsDeleted.size(); i++) {
                int imageIdDeleted = Integer.parseInt(imageIdsDeleted.get(i));
                for (int j = 0; j < files.size(); j++) {
                    if (imageIdDeleted == files.get(j).getId()) {
                        File file = files.get(j);
                        commonSaveFileIntoDB(file, userId, null, "d");
                        break;
                    }
                }
            }
        } else {
            //Xóa ảnh ở bài viết không có ảnh
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        }
        files = fileRepository.findByPostId(Integer.parseInt(postId));
        //Case update ảnh
        //=> image_sort truyền lên thứ tự < 0 || lớn hơn size - 1
        if(checkImageFileTypeValid(files.get(0).getContent()) && image.length > 0){
            //Có ảnh mới cho sửa ảnh
            for (int i = 0; i < imageIdsSort.size(); i++) {
                int imageIndex = Integer.parseInt(imageIdsSort.get(i));
                if (imageIndex < 4 || imageIndex >= 0) {
                    //Trùng thì update tên file
                    File file = new File();
                    if (imageIndex < files.size()) {
                        file = files.get(imageIndex);
                    } else {
                        file.setPostId(Integer.parseInt(postId));
                        file.setDeleted(false);
                        file.setContent(image[i].getOriginalFilename());
                        file.setCreatedDate(System.currentTimeMillis());
                        file.setCreatedBy(userId);
                    }
                    commonSaveFileIntoDB(file, userId, image[i], "s");

                }
            }
        } else if (checkVideoFileTypeValid(files.get(0).getContent()) && !video.isEmpty()) {
            //Có video mới cho sửa video
            File file = files.get(0);
            commonSaveFileIntoDB(file, userId, video, "s");
        } else {
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        }
        return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, null);
    }


    //Tính dung lượng của mảng image
    private long getImageFileSize(MultipartFile[] image) throws Exception {
        long imageFileSỉze = 0;
        for (MultipartFile file : image) {
            imageFileSỉze += file.getSize();
        }
        return imageFileSỉze;
    }

    //Ghi file vào folder đã tạo
    private CommonResponse saveFile(MultipartFile file, Path rootLocation) throws Exception {

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException ie) {
            return new CommonResponse(Constant.UPLOAD_FILE_FAILED_CODE, Constant.UPLOAD_FILE_FAILED_MESSAGE, null);

        }
        return null;
    }
    //delete file
    private CommonResponse deleteFile(String fileName,Path rootLocation) throws IOException {
        try{
            Files.deleteIfExists(Path.of(rootLocation + "/" + fileName));
        }catch (Exception e){
            return new CommonResponse(Constant.EXCEPTION_ERROR_CODE,Constant.EXCEPTION_ERROR_MESSAGE,null);
        }
        return new CommonResponse(Constant.OK_CODE,Constant.OK_MESSAGE,null);
    }

    //Check định dạng list image files phải dạng .png || .jpg || .jpeg
    private boolean checkListImageFilesTypeValid(List<String> fileNames) {
        //check file type valid
        for (String fileName : fileNames) {
            if (!checkImageFileTypeValid(fileName)) {
                return false;
            }
        }
        return true;
    }

    //Check định dạng Image File phải dạng .png || .jpg || .jpeg
    private boolean checkImageFileTypeValid(String fileName) {
        int length = fileName.length();
        char[] fileChar = fileName.toCharArray();
        int indexDot = 0;
        for (int i = 0; i < fileChar.length; i++) {
            if (fileChar[i] == '.') {
                indexDot = i;
            }
        }
        String imageNameTrim = fileName.substring(indexDot + 1, length);
        return imageNameTrim.equalsIgnoreCase(Constant.PNG) || imageNameTrim.equalsIgnoreCase(Constant.JPG) || imageNameTrim.equalsIgnoreCase(Constant.JPEG);
    }

    //check định dạng video file phải ở dạng .mp4 || .flv
    private boolean checkVideoFileTypeValid(String fileName) {
        int length = fileName.length();
        String videoNameTrim = fileName.substring(fileName.indexOf(".") + 1, length);
        return videoNameTrim.equalsIgnoreCase(Constant.MP4) || videoNameTrim.equalsIgnoreCase(Constant.FLV);
    }

    private File setCommonFileInfo(String content, int postId) {
        File file = new File();
        file.setContent(content);
        file.setCreatedDate(System.currentTimeMillis());
        file.setDeleted(false);
        file.setPostId(postId);
        return file;
    }

    private Post setCommonPostInfo(int userId, String described) {
        Post post = new Post();
        post.setCreatedBy(String.valueOf(userId));
        post.setCreatedDate(System.currentTimeMillis());
        post.setDeleted(false);
        post.setUserId(Integer.parseInt(String.valueOf(userId)));
        post.setContent(described);
        return post;
    }

    //Các case ràng buộc chung khi upload file
    public CommonResponse checkConstraintOfFile(String described, MultipartFile[] image, MultipartFile video) throws Exception {
        //Không truyền described, image và video (y/c 1 trong 3)
        if (described == null && image.length == 0 && video.isEmpty()) {
            return new CommonResponse(Constant.PARAMETER_IS_NOT_ENOUGH_CODE, Constant.PARAMETER_IS_NOT_ENOUGH_MESSAGE, null);
        }
        //truyền cả image và video
        if ((image.length > 0) && !video.isEmpty()) {
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        }
        //Check quá số lượng file
        if ((image.length > 4)) {
            return new CommonResponse(Constant.MAXIMUM_NUMBER_OF_IMAGES_CODE, Constant.MAXIMUM_NUMBER_OF_IMAGES_MESSAGE, null);
        }
        //Add file name into List to trim
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < image.length; i++) {
            fileNames.add(image[i].getOriginalFilename());
        }
        //truyền image/video sai định dạng || nội dung bài viết quá 500 từ
        if ((image.length > 0 && !checkListImageFilesTypeValid(fileNames)) || (!video.isEmpty() && !checkVideoFileTypeValid(video.getOriginalFilename()) || commonService.countWordInString(described) > 500)) {
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        }
        //Check quá dung lượng của image và video
        if (getImageFileSize(image) * Constant.CONVERSION_TO_MB > 4 || video.getSize() * Constant.CONVERSION_TO_MB > 10) {
            return new CommonResponse(Constant.FILE_SIZE_IS_TOO_BIG_CODE, Constant.FILE_SIZE_IS_TOO_BIG_MESSAGE, null);

        }
        return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, null);
    }

    //function = d -> delete
    //function = s -> save
    private void commonSaveFileIntoDB(File file, String userId, MultipartFile savedFile, String function) throws Exception {
        deleteFile(file.getContent(), Path.of(Constant.ROOT_DIRECTORY));
        if (function.equalsIgnoreCase("d")) {
            file.setDeleted(true);
        } else if (function.equalsIgnoreCase("s")) {
            saveFile(savedFile, Path.of(Constant.ROOT_DIRECTORY));
            file.setContent(savedFile.getOriginalFilename());
        }
        file.setModifiedBy(userId);
        file.setModifiedDate(System.currentTimeMillis());
        fileRepository.save(file);
    }


}
