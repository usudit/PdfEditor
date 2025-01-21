package com.example.demo;

import com.itextpdf.text.DocumentException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class DynamicCitiPdfControllerDB {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/editCitiDynamicDB")
    public ResponseEntity<byte[]> editPdf(@RequestParam("file") MultipartFile pdfFile) {
        try {
            // Retrieve field values from MongoDB
            Map<String, String> fieldValues = fetchFieldValuesFromMongo();
            if (fieldValues == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No field values found in MongoDB".getBytes());
            }

            // Edit the PDF without altering its properties
            byte[] editedPdf = modifyPdfFieldsWithoutFlattening(pdfFile, fieldValues);

            // Return the edited PDF for download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=edited.pdf");
            return new ResponseEntity<>(editedPdf, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error editing PDF: " + e.getMessage()).getBytes());
        }
    }

    private Map<String, String> fetchFieldValuesFromMongo() {
        /*// Assuming a collection named 'pdfFields' with a single document containing field values
        return mongoTemplate.findById("fieldValues", Map.class, "pdfFields");*/
        Map<String, String> fieldValues = mongoTemplate.findById("fieldValues", Map.class, "pdfFields");
        if (fieldValues == null) {
            System.err.println("No document found with _id 'fieldValues' in collection 'pdfFields'");
        } else {
            System.out.println("Fetched field values from MongoDB: " + fieldValues);
        }
        return fieldValues;
    }

    private byte[] modifyPdfFieldsWithoutFlattening(MultipartFile pdfFile, Map<String, String> fieldValues) throws IOException, DocumentException {
        // Read the PDF
        PdfReader reader = new PdfReader(pdfFile.getInputStream());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        // Access the PDF form fields
        AcroFields fields = stamper.getAcroFields();

        // Update fields with values from MongoDB
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            fields.setField(entry.getKey(), entry.getValue());
        }

        // Ensure form fields remain editable
        stamper.setFormFlattening(false);

        // Close stamper and reader
        stamper.close();
        reader.close();

        return outputStream.toByteArray();
    }
}
