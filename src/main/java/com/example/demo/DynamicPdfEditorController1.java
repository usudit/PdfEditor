package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class DynamicPdfEditorController1 {

    @PostMapping(value = "/editDynamic1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(
            @RequestParam("pdf") MultipartFile pdfFile,
            @RequestParam("json") MultipartFile jsonFile
    ) throws DocumentException {
        try {
            // Read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, String>> pdfData = objectMapper.readValue(jsonFile.getInputStream(), Map.class);

            // Identify the PDF type
            String pdfName = pdfFile.getOriginalFilename();
            if (pdfName == null || !pdfData.containsKey(pdfName)) {
                return ResponseEntity.badRequest().body(null);
            }
            Map<String, String> fieldValues = pdfData.get(pdfName);

            // Process the PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfReader pdfReader = new PdfReader(pdfFile.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            AcroFields form = pdfStamper.getAcroFields();

            // Fill the form fields
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                if (form.getFields().containsKey(fieldName)) {
                    form.setField(fieldName, fieldValue);
                }
            }

            // Do not flatten fields (fields remain editable if needed)
            pdfStamper.close();
            pdfReader.close();

            // Return the edited PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_" + pdfName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}

