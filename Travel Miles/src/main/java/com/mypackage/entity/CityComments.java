package com.mypackage.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "citycomments")
public class CityComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Generazione automatica dell'ID
    @Column(name = "comment_id")
    private Integer commentId; //ID univoco per ogni commento

    @ManyToOne
    @JoinColumn(name = "city_name", referencedColumnName = "cityName", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_city_name"))
    private City city; //Riferimento all'entità City

    @Column(name = "comment", nullable = false)
    private String commentText;

    @Column(name = "username", nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id", 
                foreignKey = @ForeignKey(name = "fk_parent_comment"))
    private CityComments parentComment; //Riferimento al commento principale

   
    public CityComments() {
    }

    
    public CityComments(City city, String commentText, String username, CityComments parentComment) {
        this.city = city;
        this.commentText = commentText;
        this.username = username;
        this.parentComment = parentComment;
    }

    
    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CityComments getParentComment() {
        return parentComment;
    }

    public void setParentComment(CityComments parentComment) {
        this.parentComment = parentComment;
    }
}
