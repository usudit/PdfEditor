package com.example.demo;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Value;
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
public class PdfEditorController {

    @Value("${pdf.fields.fullName}")
    private String fullName;

    @Value("${pdf.fields.address}")
    private String address;

    @Value("${pdf.fields.phoneNumber}")
    private String phoneNumber;

    @Value("${pdf.fields.recommend}")
    private String recommend;

    //@PostMapping(value = "/edit", produces = MediaType.APPLICATION_PDF_VALUE)
    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(@RequestParam("file") MultipartFile file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Load the existing PDF from MultipartFile input stream
            //PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(file));
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            AcroFields form = pdfStamper.getAcroFields();

            // Debugging: Log all field names and current values
            Map<String, AcroFields.Item> fields = form.getFields();
            for (String fieldName : fields.keySet()) {
                System.out.println("Field: " + fieldName + ", Current Value: " + form.getField(fieldName));
            }

            // Fill the form fields with explicit checks
            if (fields.containsKey("fullNme11")) {
                boolean success = form.setField("fullNme11", fullName);
                System.out.println("Setting 'fullNme11' to '" + fullName + "': " + (success ? "Success" : "Failed"));
            }
            if (fields.containsKey("address12")) {
                boolean success = form.setField("address12", address);
                System.out.println("Setting 'address12' to '" + address + "': " + (success ? "Success" : "Failed"));
            }
            if (fields.containsKey("phoneNumber14")) {
                boolean success = form.setField("phoneNumber14", phoneNumber);
                System.out.println("Setting 'phoneNumber14' to '" + phoneNumber + "': " + (success ? "Success" : "Failed"));
            }
            if (fields.containsKey("willYou6")) {
                boolean success = form.setField("willYou6", recommend.equalsIgnoreCase("Yes") ? "Yes" : "Off");
                System.out.println("Setting 'willYou6' to '" + recommend + "': " + (success ? "Success" : "Failed"));
            }

            // Debugging: Log all field names and current values after setting
            for (String fieldName : fields.keySet()) {
                System.out.println("After Setting - Field: " + fieldName + ", Value: " + form.getField(fieldName));
            }

            // Flatten the form to prevent further editing
            //pdfStamper.setFormFlattening(true);
            pdfStamper.close();
            pdfReader.close();

            // Return the edited PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }


    /*@PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(@RequestParam("file") MultipartFile file) throws DocumentException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Load the existing PDF
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            AcroFields form = pdfStamper.getAcroFields();

            // Fill the form fields
            Map<String, AcroFields.Item> fields = form.getFields();
            for (String fieldName : fields.keySet()) {
                System.out.println("Field: " + fieldName + ", Value: " + form.getField(fieldName));
            }
            if (fields.containsKey("fullNme11")) {
                form.setField("fullNme11", fullName);
            } else {
                System.out.println("Field 'fullNme11' not found.");
            }
            if (fields.containsKey("address12")) {
                form.setField("address12", address);
            } else {
                System.out.println("Field 'address12' not found.");
            }
            if (fields.containsKey("phoneNumber14")) {
                form.setField("phoneNumber14", phoneNumber);
            } else {
                System.out.println("Field 'phoneNumber14' not found.");
            }
            *//*if (fields.containsKey("willYou6")) {
                form.setField("willYou6", recommend);
            }*//*
            if (fields.containsKey("willYou6")) {
                // Set to 'Yes', 'No', or 'Maybe' as needed
                // Handle checkbox field
                form.setField("willYou6", recommend.equalsIgnoreCase("Yes") ? "Yes" : "Off");
            } else {
                System.out.println("Field 'willYou6' not found.");
            }

            *//*for (String fieldName : fields.keySet()) {
                System.out.println("Field: " + fieldName + ", Value: " + form.getField(fieldName));
            }*//*

            // Flatten the form to prevent further editing
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();
            pdfReader.close();

            // Return the edited PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }*/


    /*@PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(@RequestParam("file") MultipartFile file) throws DocumentException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Load the existing PDF
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            AcroFields form = pdfStamper.getAcroFields();

            // Fill the form fields
            Map<String, AcroFields.Item> fields = form.getFields();
            if (fields.containsKey("fullNme11")) {
                form.setField("fullNme11", fullName);
            }
            if (fields.containsKey("address12")) {
                form.setField("address12", address);
            }
            if (fields.containsKey("phoneNumber14")) {
                form.setField("phoneNumber14", phoneNumber);
            }
            if (fields.containsKey("willYou6")) {
                form.setField("willYou6", recommend);
            }

            // Flatten the form to prevent further editing
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();
            pdfReader.close();

            // Return the edited PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }*/


    /*@PostMapping(value = "/edit", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> editPdf(@RequestParam("file") byte[] file) throws DocumentException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Load the existing PDF
            PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(file));
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            AcroFields form = pdfStamper.getAcroFields();

            // Fill the form fields
            Map<String, AcroFields.Item> fields = form.getFields();
            if (fields.containsKey("fullNme11")) {
                form.setField("fullNme11", fullName);
            }
            if (fields.containsKey("address12")) {
                form.setField("address12", address);
            }
            if (fields.containsKey("phoneNumber14")) {
                form.setField("phoneNumber14", phoneNumber);
            }
            if (fields.containsKey("willYou6")) {
                form.setField("willYou6", recommend);
            }

            // Flatten the form to prevent further editing
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();
            pdfReader.close();

            // Return the edited PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }*/
}
