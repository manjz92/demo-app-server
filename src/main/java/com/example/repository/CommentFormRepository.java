package com.example.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.domain.CommentForm;

public interface CommentFormRepository extends MongoRepository<CommentForm, String> {

}
