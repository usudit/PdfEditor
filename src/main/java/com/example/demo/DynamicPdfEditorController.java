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
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class DynamicPdfEditorController {

    @PostMapping(value = "/editDynamic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(@RequestParam("file") MultipartFile pdfFile) throws DocumentException {
        try {
            // Load the JSON file from the codebase
            File jsonFile = new File("src/main/resources/pdf-field-values.json");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, String>> pdfData = objectMapper.readValue(jsonFile, Map.class);

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

            /*// Fill the form fields
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                if (form.getFields().containsKey(fieldName)) {
                    form.setField(fieldName, fieldValue);
                }
            }*/

            // Fill the form fields with special handling for radio buttons and checkboxes
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (!form.getFields().containsKey(fieldName)) {
                    continue;
                }

                int fieldType = form.getFieldType(fieldName);
                switch (fieldType) {
                    case AcroFields.FIELD_TYPE_RADIOBUTTON:
                        /*// Ensure the value matches an option in the radio button group
                        form.setField(fieldName, fieldValue);
                        System.out.println("Radio Button - Field: " + fieldName + ", Value: " + fieldValue);*/
                    String[] radioOptions = form.getAppearanceStates(fieldName);
                        System.out.println("Radio Button Options for Field: " + fieldName);
                        for (String option : radioOptions) {
                            System.out.println("  Option: " + option);
                        }
                    boolean isSet = false;
                    for (String option : radioOptions) {
                        if (option.equalsIgnoreCase(fieldValue)) {
                            form.setField(fieldName, option);
                            isSet = true;
                            System.out.println("Radio Button - Field: " + fieldName + ", Set Value: " + option);
                            break;
                        }
                    }
                    if (!isSet) {
                        System.out.println("Invalid value for Radio Button - Field: " + fieldName + ", Value: " + fieldValue);
                    }
                        break;

                    case AcroFields.FIELD_TYPE_CHECKBOX:
                        // Set checkbox to "Yes" or "Off"
                        // Checkboxes require "Yes" for selected and "Off" for unselected
                        form.setField(fieldName, fieldValue.equalsIgnoreCase("Yes") ? "Yes" : "Off");
                        //System.out.println("Checkbox - Field: " + fieldName + ", Value: " + fieldValue);
                        System.out.println("Checkbox - Field: " + fieldName + ", Set Value: " + (fieldValue.equalsIgnoreCase("Yes") ? "Yes" : "Off"));
                        break;

                    default:
                        // Handle regular text fields
                        form.setField(fieldName, fieldValue);
                        //System.out.println("Text Field - Field: " + fieldName + ", Value: " + fieldValue);
                        System.out.println("Text Field - Field: " + fieldName + ", Set Value: " + fieldValue);
                        break;
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

