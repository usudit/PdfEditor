package com.example.demo.repository.impl;

import com.example.demo.repository.MongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class MongoDbRepositoryImpl implements MongoDbRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, String> findById(String formId, Class<Map> mapClass, String pdfFields) {
        // Fetch values from MongoDB using the form ID
        // Fetch values from MongoDB using the form ID (file name as key)
        return mongoTemplate.findById(formId, Map.class, "pdfFields");
    }
}
