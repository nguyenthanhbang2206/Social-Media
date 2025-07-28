package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g WHERE g.name LIKE CONCAT('%', :keyword, '%') or g.description LIKE CONCAT('%', :keyword, '%')")
    List<Group> search(@Param("keyword") String keyword);

}
