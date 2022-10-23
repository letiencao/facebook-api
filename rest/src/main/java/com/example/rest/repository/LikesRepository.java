package com.example.rest.repository;

import com.example.rest.model.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Integer> {
    List<Likes> findByPostId(int postId);
    //xem userId đã like bài postId chưa
    Likes findByUserIdAndPostId(int userId,int postId);
}
