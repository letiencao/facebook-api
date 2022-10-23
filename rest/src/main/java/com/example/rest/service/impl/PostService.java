package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.entity.File;
import com.example.rest.model.entity.Post;
import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.model.response.post.GetPostResponse;
import com.example.rest.repository.*;
import com.example.rest.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PostService implements IPostService {
    @Autowired
    private CommonService commonService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

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
            //truyền image/video sai định dạng || nội dung bài viết quá 500 từ
            if (!checkImageFileTypeValid(image) || !checkVideoFileTypeValid(video.getOriginalFilename()) || commonService.countWordInString(described) > 500) {
                return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
            }
            //Check quá dung lượng của image và video
            if (getImageFileSize(image) * Constant.CONVERSION_TO_MB > 4 || video.getSize() * Constant.CONVERSION_TO_MB > 10) {
                return new CommonResponse(Constant.FILE_SIZE_IS_TOO_BIG_CODE, Constant.FILE_SIZE_IS_TOO_BIG_MESSAGE, null);

            }
            //Save post -> Save File
            createDirectoryIfItDoesntExist(Constant.ROOT_DIRECTORY);
            //Get user Id from token
            int userId = Integer.parseInt(commonService.getUserIdFromToken(token).getData().get(0).getId());
            if (userId > 0) {

                Post post = postRepository.save(setCommonPostInfo(userId, described));
                if (post != null && post.getId() > 0) {
                    if (image.length > 0 && image.length < 4) { //xem lại
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
    public CommonResponse<GetPostResponse> getPost(String token, String postId) {
        //Check các trường bắt buộc
//        commonService.checkCommonValidate(token,postId);
//
//        int userId = Integer.parseInt(commonService.getUserIdFromToken(token).getData().get(0).getId());
//        Post post = postRepository.findById(Integer.parseInt(postId));
//        if(post != null){
//
//        }
//        //Output
//        List<GetPostResponse> list = new ArrayList<>();
//        GetPostResponse getPostResponse = new GetPostResponse();
//        getPostResponse.setId(String.valueOf(post.getId()));
//        getPostResponse.setDescribed(post.getContent());
//        getPostResponse.setCreated(String.valueOf(post.getCreatedDate()));
//        getPostResponse.setModified(String.valueOf(post.getModifiedDate()));
//        getPostResponse.setLike(String.valueOf(likesRepository.findByPostId(post.getId()).size()));
//        getPostResponse.setComment(String.valueOf(commentRepository.findByPostId(post.getId()).size()));
//        getPostResponse.setIsLiked(likesRepository.findByUserIdAndPostId(userId,post.getId()) != null ? "true" : "false");
//        getPostResponse.setImage();
//        getPostResponse.setVideo();
//        User user = userRepository.findById(userId);
//        List<Author> authors = new ArrayList<>();
//        Author author = new Author();
//        author.setAvatar(user.getLinkAvatar());
//        author.setName(user.getName());
//        author.setOnline("true");
//        authors.add(author);
//
//        getPostResponse.setAuthor(authors);
//        getPostResponse.setState("deleted = " + post.isDeleted());
//        getPostResponse.setIsBlocked("false");
//        getPostResponse.setCanEdit();
//        getPostResponse.setBanned();
//        getPostResponse.setCanComment();
//        getPostResponse.setUrl("");
//        getPostResponse.setMessages(null);


        return null;
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

    //Check định dạng image file phải dạng .png || .jpg || .jpeg
    private boolean checkImageFileTypeValid(MultipartFile[] image) {
        List<String> fileNames = new ArrayList<>();
        //Add file name into List to trim
        for (int i = 0; i < image.length; i++) {
            fileNames.add(image[i].getOriginalFilename());
        }
        //check file type valid
        for (String fileName : fileNames) {
            int length = fileName.length();
            String imageNameTrim = fileName.substring(fileName.indexOf(".") + 1, length);
            if (!imageNameTrim.equalsIgnoreCase(Constant.PNG) && !imageNameTrim.equalsIgnoreCase(Constant.JPG) && !imageNameTrim.equalsIgnoreCase(Constant.JPEG)) {
                return false;
            }
        }
        return true;
    }

    //check định dạng video file phải ở dạng .mp4 || .flv
    private boolean checkVideoFileTypeValid(String fileName) {
        int length = fileName.length();
        String videoNameTrim = fileName.substring(fileName.indexOf(".") + 1, length);
        if(videoNameTrim.equalsIgnoreCase(Constant.MP4) || videoNameTrim.equalsIgnoreCase(Constant.FLV)){
            return true;
        }
        return false;
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


}
