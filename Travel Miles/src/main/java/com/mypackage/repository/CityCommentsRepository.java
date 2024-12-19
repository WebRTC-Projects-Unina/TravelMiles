package com.mypackage.repository;

import com.mypackage.entity.CityComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityCommentsRepository extends JpaRepository<CityComments, Integer> {

    //Trova tutti i commenti associati a una città specifica
    List<CityComments> findByCity_CityName(String cityName);

    //trova i commenti principali di una città
    @Query("SELECT c FROM CityComments c WHERE c.city.cityName = :cityName AND c.parentComment IS NULL")
    List<CityComments> findMainCommentsByCity(@Param("cityName") String cityName);
    
    @Query("SELECT c FROM CityComments c WHERE c.city.cityName = :cityName AND c.parentComment IS NOT NULL")
    List<CityComments> findRepliesByParentCommentId(@Param("cityName") String cityName);

    //Trova risposte ad un commento
    @Query("SELECT c FROM CityComments c WHERE c.parentComment.commentId = :parentCommentId")
    List<CityComments> findRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);
}
