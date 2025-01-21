package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class DynamicCitiPdfController {

    @PostMapping("/editCitiDynamic")
    public ResponseEntity<byte[]> editPdf(@RequestParam("file") MultipartFile pdfFile) {
        try {
            // Load JSON from local file
            File jsonFile = new ClassPathResource("fieldValues.json").getFile();
            Map<String, String> fieldValues = parseJson(jsonFile);

            //check pdf fields
            printAllFieldNames(pdfFile);

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

    private void printAllFieldNames(MultipartFile pdfFile) throws IOException {
        PdfReader reader = new PdfReader(pdfFile.getInputStream());
        AcroFields fields = reader.getAcroFields();

        // Check Box1 is the name of a check box field
        String[] values = fields.getAppearanceStates("Check Box1");

        for(String value: values) {
            System.out.println("Possible value for check box is "+ value);
        }



        System.out.println("List of all form fields:");
        Map<String, AcroFields.Item> fieldMap = fields.getFields();
        for (String key : fieldMap.keySet()) {
            System.out.println(key + ": " + fields.getField(key));
        }
        reader.close();
    }


    private Map<String, String> parseJson(File jsonFile) throws IOException {
        // Use Jackson to parse the JSON file
        return new ObjectMapper().readValue(jsonFile, Map.class);
    }

    private byte[] modifyPdfFieldsWithoutFlattening(MultipartFile pdfFile, Map<String, String> fieldValues) throws IOException, DocumentException {
        // Read the PDF
        PdfReader reader = new PdfReader(pdfFile.getInputStream());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        // Access the PDF form fields
        AcroFields fields = stamper.getAcroFields();

        // Update fields with values from the JSON
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
