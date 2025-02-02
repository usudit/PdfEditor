package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;

public interface MongoDbRepository {
    public Map<String, String> findById(String formId, Class<Map> mapClass, String pdfFields);

    //public void saveFieldValues(String formId, Map<String, String> fieldValues);
    //public Map<String, String> findById(String formId);
}
