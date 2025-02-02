package com.example.demo.service;

import com.itextpdf.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface MultiCitiPdfService {

    public byte[] modifyPdfFieldsWithoutFlattening(MultipartFile pdfFile, Map<String, String> fieldValues) throws IOException, DocumentException;

    public void printAllFieldNames(MultipartFile pdfFile) throws IOException;

    public Map<String, String> fetchFieldValuesFromMongo(String formId);


}
