package com.example.rest.repository;

import com.example.rest.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query(value = "SELECT * FROM post WHERE post.id = :id AND user.deleted = false", nativeQuery = true)
    Post findById(int id);

    Post save(Post post);

}
