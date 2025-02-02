package com.example.demo;

import com.example.demo.service.MultiCitiPdfService;
import com.itextpdf.text.DocumentException;
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
import java.util.Set;

@RestController
@RequestMapping("/api/pdf")
public class DynamicMultiCitiPdfControllerDB {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MultiCitiPdfService multiCitiPdfService;

    @PostMapping("/editMultiCitiDynamicDB")
    public ResponseEntity<byte[]> editPdf(@RequestParam("file") MultipartFile pdfFile) {
        String pdfName = pdfFile.getOriginalFilename();
        try {
            // Retrieve field values for Personal_Form.pdf from MongoDB
            // Retrieve field values for the PDF from MongoDB using the filename
            //Map<String, String> fieldValues = fetchFieldValuesFromMongo("Personal_Form");
            Map<String, String> fieldValues = multiCitiPdfService.fetchFieldValuesFromMongo(pdfName);

            //check pdf fields
            // Debug: Print all PDF field names
            multiCitiPdfService.printAllFieldNames(pdfFile);

            if (fieldValues == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No field values found in MongoDB for the specified form.".getBytes());
            }

            // Edit the PDF with the retrieved values
            // Edit the PDF while keeping it editable
            byte[] editedPdf = multiCitiPdfService.modifyPdfFieldsWithoutFlattening(pdfFile, fieldValues);

            // Return the edited PDF for download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Personal_Form_Edited.pdf");
            return new ResponseEntity<>(editedPdf, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error editing PDF: " + e.getMessage()).getBytes());
        }
    }
}
