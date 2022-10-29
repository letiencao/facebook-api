package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.entity.File;
import com.example.rest.model.entity.Post;
import com.example.rest.model.entity.User;
import com.example.rest.model.mapper.PostMapper;
import com.example.rest.model.mapper.UserMapper;
import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.model.response.post.GetPostResponse;
import com.example.rest.model.response.posts.AuthorResponse;
import com.example.rest.model.response.posts.DataResponse;
import com.example.rest.model.response.posts.PostsResponse;
import com.example.rest.model.response.posts.VideoResponse;
import com.example.rest.repository.*;
import com.example.rest.service.IPostService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
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
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private PostsRepository postsRepository;

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

    /**
     * Get list-posts
     *
     * @param token
     * @param user_id
     * @param in_campaign
     * @param campaign_id
     * @param latitude
     * @param longitude
     * @param last_id
     * @param index
     * @param count
     * @return
     */
    @Override
    public CommonResponse<DataResponse> getListPosts(String token, String user_id, String in_campaign, String campaign_id, String latitude, String longitude, String last_id, String index, String count) {
        CommonResponse<DataResponse> commonResponse = new CommonResponse();
        this.validateParamsGetPosts(token, user_id, in_campaign, campaign_id, latitude, longitude, last_id, index, count);

        //last_id of post not found
        if (!StringUtils.isEmpty(last_id)) {
            Post postInDB = postRepository.findById(Integer.parseInt(last_id));
            if (postInDB == null) {
                return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.POST_IS_NOT_EXISTED_MESSAGE, null);
            }
        }

        //validate token and get UserId
        int userId = Integer.parseInt(commonService.getUserIdFromToken(token).getData().get(0).getId());
        if (userId < 0) {
            return new CommonResponse(Constant.PARAMETER_IS_NOT_ENOUGH_CODE, Constant.PARAMETER_TYPE_IS_INVALID_MESSAGE, null);
        }

        if (StringUtils.isEmpty(count)) {
            count = String.valueOf(20);
        }
        if (StringUtils.isEmpty(index)) {
            index = String.valueOf(1);
        }
        //create datas for Client's response
        List<DataResponse> dataResponses = new ArrayList<>();

        //get files
        List<File> files = fileService.findAll();
        if (CollectionUtils.isEmpty(files)) {
            files = files.stream().filter(item -> item.isDeleted() == Constant.IS_NOT_DELETED).collect(Collectors.toList());
        }

        //get authors
        List<User> usersInDB = userRepository.findAll();
        if (!CollectionUtils.isEmpty(usersInDB)) {
            usersInDB = usersInDB.stream().filter(item -> item.isDeleted() == Constant.IS_NOT_DELETED).collect(Collectors.toList());
        }

        //get posts
        //TODO: paging
        List<Post> postsInDB = postsRepository.findPostByAll(last_id, count,index);
        //find post is not deleted
        postsInDB = postsInDB.stream().filter(item -> item.isDeleted() == Constant.IS_NOT_DELETED).collect(Collectors.toList());

        List<PostsResponse> posts = new ArrayList<>();

        //convert Posts to PostResponse
        if (!CollectionUtils.isEmpty(postsInDB)) {
            for (Post post : postsInDB) {
                posts.add(postMapper.toResponse(post));
            }
        }
        //add datas to PostResponses
        if (!CollectionUtils.isEmpty(posts)) {
            for (PostsResponse postsResponse : posts) {
                //get author by postId
                if (!CollectionUtils.isEmpty(usersInDB)) {
                    User userInDb = usersInDB.stream().filter(item -> (item.isDeleted() == Constant.IS_NOT_DELETED) && (item.getId() == Integer.parseInt(postsResponse.getUser_id()))).findAny().orElse(null);
                    if (userInDb != null) {
                        AuthorResponse authorResponse = userMapper.toResponse(userInDb);
                        authorResponse.setOnline("online");
                        //set to data return
                        postsResponse.setAuthor(authorResponse);
                    }
                }

                if (!CollectionUtils.isEmpty(files)) {
                    //get files by postId
                    List<File> fileWithPost = files.stream().filter(item -> item.isDeleted() == Constant.IS_NOT_DELETED && item.getPostId() == (Integer.parseInt(postsResponse.getId()))).collect(Collectors.toList());
                    //convert file to video or images
                    if (!CollectionUtils.isEmpty(fileWithPost)) {
                        List<String> images = new ArrayList<>();
                        List<VideoResponse> videoResponses = new ArrayList<>();
                        for (File file : fileWithPost) {
                            if (!StringUtils.isEmpty(file.getContent()) && this.checkImageFileTypeValid(file.getContent())) {
                                images.add(file.getContent());
                                //set to data return
                                postsResponse.setImage(images);
                                postsResponse.setVideo(null);
                            } else if (!StringUtils.isEmpty(file.getContent()) && this.checkVideoFileTypeValid(file.getContent())) {
                                VideoResponse videoResponse = new VideoResponse();
                                videoResponse.setUrl(file.getContent());
                                videoResponse.setThumb("example thump");
                                //set to data return
                                videoResponses.add(videoResponse);
                                postsResponse.setVideo(videoResponses);
                                postsResponse.setImage(null);
                            } else {
                                //list of Files null -> both image and video are null
                                postsResponse.setImage(null);
                                postsResponse.setVideo(null);
                            }
                        }
                    }
                }

            }
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setPosts(posts);
        dataResponse.setNew_items("test-set-new-item");
        dataResponse.setLast_id("test-set-last-id");
        dataResponses.add(dataResponse);
        //return common-response
        commonResponse.setData(dataResponses);
        commonResponse.setMessage(Constant.OK_MESSAGE);
        commonResponse.setCode(Constant.OK_CODE);
        return commonResponse;
    }

    /**
     * Validate input params
     *
     * @return: null
     */
    private CommonResponse<DataResponse> validateParamsGetPosts(String token, String user_id, String in_campaign, String campaign_id, String latitude, String longitude, String last_id, String index, String count) {

        return null;
    }

    @Override
    public CommonResponse editPost(String token, String postId, String described, String status, MultipartFile[] image, List<String> imageIdsDeleted, List<String> imageIdsSort, MultipartFile video, MultipartFile thumb, String autoAccept, String autoBlock) throws Exception {

        commonService.checkCommonValidate(token, postId);
        //Lấy user id from token
        String userId = (commonService.getUserIdFromToken(token).getData().get(0).getId());

//        //size of list ảnh cần edit, và size of list imageSort, size of list id ảnh cần sửa != (y/c trùng nhau)
//        if(image.length != imageIdsDeleted.size() || image.length != imageIdsSort.size() || imageIdsDeleted.size() != imageIdsSort.size()){
//
//        }
//        imageIdsDeleted => phục vụ cho case muốn xóa ảnh
//        image && imageIdsSort => phục vụ cho case update


        //Case Success
        //Lấy thông tin bài viết
        Post post = postRepository.findById(Integer.parseInt(postId));
        if (post == null) {
            //return post ko tồn tại
        }
        //Lấy các file của post đó
        List<File> files = fileRepository.findByPostId(post.getId());
        //list image sort ,index > size của list ảnh đã đăng
//        for(int i = 0;i < imageIdsSort.size();i++){
//            int idSort = Integer.parseInt(imageIdsSort.get(i));
//            if(idSort > files.size() - 1){
//                //return lỗi
//            }
//        }
        //Case xóa ảnh thành công
        if (imageIdsDeleted != null && imageIdsDeleted.size() > 0) {
            //Check các id ảnh muốn xóa => phải trùng với các id ảnh của bài post
            for (int i = 0; i < imageIdsDeleted.size(); i++) {
                int imageIdDeleted = Integer.parseInt(imageIdsDeleted.get(i));
                for (int j = 0; j < files.size(); j++) {
                    if (imageIdDeleted == files.get(j).getId()) {
                        File file = files.get(j);
                        deleteFile(file.getContent(), Path.of(Constant.ROOT_DIRECTORY));
                        file.setModifiedBy(userId);
                        file.setModifiedDate(System.currentTimeMillis());
                        file.setDeleted(true);
                        fileRepository.save(file);
                        break;
                    }
                }
            }
        }

        //Case update ảnh
        //=> size của image = size của image_sort
        if (image.length != imageIdsSort.size()) {
            //return
        }
        //=> image_sort truyền lên thứ tự < 0 || lớn hơn size - 1
        if (checkImageFileTypeValid(files.get(0).getContent()) && image.length > 0) {
            for (int i = 0; i < imageIdsSort.size(); i++) {
                int imageIndex = Integer.parseInt(imageIdsSort.get(i));
                if (imageIndex < 0 || imageIndex > files.size() - 1) {

                } else {
                    //Trùng thì update tên file
                    File file = files.get(i);
                    deleteFile(file.getContent(), Path.of(Constant.ROOT_DIRECTORY));
                    file.setModifiedBy(userId);
                    file.setModifiedDate(System.currentTimeMillis());
                    file.setDeleted(false);
                    saveFile(image[i], Path.of(Constant.ROOT_DIRECTORY));
                    file.setContent(image[i].getOriginalFilename());
                    fileRepository.save(file);
                }
            }
        } else if (checkVideoFileTypeValid(files.get(0).getContent()) && !video.isEmpty()) {

        }


        //Check các file này là file ảnh
//        if(files == null || files.size() == 0){
//
//        }
//        String theFirstFileName = files.get(0).getContent();
//        if(checkImageFileTypeValid(theFirstFileName)){
        //Nếu là image thì cho thay đổi image

//            //Check các id ảnh muốn chỉnh sửa => phải trùng với các id ảnh của bài post
//            for(int i = 0;i < imageIdsDeleted.size();i++){
//                int imageId = Integer.parseInt(imageIdsDeleted.get(i));
//                for(int j = 0;j < files.size();j++){
//                    if(imageId != files.get(i).getId()){
//                        //input id ảnh muốn sửa, không phải ảnh của bài
//
//                    }
//                }
//            }
// => Size of imageIdsDeleted, imageIdsSort, image == nhau;
        //Update File đúng vị trí
//            [1,2];
//            list<file> : id, tên file
//            check trùng id thì update tên file và saveDB
//            for(int i = 0;i < imageIdsSort.size();i++){
//                int idUpdate = Integer.parseInt(imageIdsSort.get(i));
//                for(int j = 0;j < files.size();j++){
//                    if(idUpdate == files.get(j).getId()){
//                        File file = files.get(j);
//                        saveFile(image[i], Paths.get(Constant.ROOT_DIRECTORY));
//                    }
//                }
//            }
//
//
//
//        }else{
//            //File của bài viết là video
//        }


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

    //delete file
    private CommonResponse deleteFile(String fileName, Path rootLocation) throws IOException {
        try {
            Files.deleteIfExists(Path.of(rootLocation + "/" + fileName));
        } catch (Exception e) {
            return new CommonResponse(Constant.EXCEPTION_ERROR_CODE, Constant.EXCEPTION_ERROR_MESSAGE, null);
        }
        return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, null);
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


}
