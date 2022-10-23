package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.entity.Comment;
import com.example.rest.model.entity.Post;
import com.example.rest.model.entity.User;
import com.example.rest.model.response.CommentResponse;
import com.example.rest.model.response.Poster;
import com.example.rest.repository.CommentRepository;
import com.example.rest.repository.PostRepository;
import com.example.rest.repository.UserRepository;
import com.example.rest.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService implements ICommentService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public CommonResponse<CommentResponse> setComment(String token ,String id, String comment, String index, String count) {
        commonService.checkCommonValidate(id, comment, index, count);


        Post post = postRepository.findById(Integer.parseInt(id));


// truyen vao id bai viet ko ton tai
    if (post == null){
        return new CommonResponse<>(Constant.POST_IS_NOT_EXISTED_CODE, Constant.POST_IS_NOT_EXISTED_MESSAGE, null);
    }

    List<Comment> comments = commentRepository.findByPostId(Integer.parseInt(id));

//    truyen vao sai index va count
    if (Integer.parseInt(index) < 0 || Integer.parseInt(index) > comments.size() - 1 || Integer.parseInt(count) < 0 ){
        return  new CommonResponse<>(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
    }




        int userId = 1; // sua lai sau (lay tu token)


        //Chặn test case trả ra mã lỗi

//    truyen vao nhung khong ket noi duoc database
        try {
            Comment commentDb = commentRepository.save(setCommonCommentInfo(userId, comment, Integer.parseInt(id)));


        }catch (Exception e){
            return new CommonResponse(Constant.CAN_NOT_CONNECT_TO_DB_CODE, Constant.CAN_NOT_CONNECT_TO_DB_MESSAGE, null);
        }





        //Trả ra dữ liệu trường hợp thành công


        List<CommentResponse> list = new ArrayList<>();

        for(int i = 0;i < comments.size();i++){
            Comment commentEntity = comments.get(i);
            int userId1 = commentEntity.getUserId();
            User user = userRepository.findById(userId1); //Thông tin người comment

            //Set comment response
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setComment(commentEntity.getContent());
            commentResponse.setId(String.valueOf(commentEntity.getId()));
            commentResponse.setCreated(String.valueOf(commentEntity.getCreatedDate()));

            List<Poster> posters = new ArrayList<>();
            Poster poster = new Poster();
            poster.setUsername(user.getPhoneNumber());
            poster.setAvatar(user.getLinkAvatar());
            poster.setId(String.valueOf(user.getId()));
            posters.add(poster);

            commentResponse.setPoster(posters);

            list.add(commentResponse);
        }

    return new CommonResponse<CommentResponse>(Constant.OK_CODE,Constant.OK_MESSAGE,list);

    }

    private Comment setCommonCommentInfo(int userId, String comment, int postId){


        Comment commentEntity = new Comment();

        commentEntity.setDeleted(false);
        commentEntity.setCreatedDate(System.currentTimeMillis());
        commentEntity.setCreatedBy(String.valueOf(userId));
        commentEntity.setContent(comment);
        commentEntity.setUserId(userId);
        commentEntity.setPostId(postId);

        return commentEntity;
    }
}
