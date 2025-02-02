package com.example.demo.service.impl;

import com.example.demo.repository.MongoDbRepository;
import com.example.demo.service.MultiCitiPdfService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class MultiCitiPdfServiceImpl implements MultiCitiPdfService {

    @Autowired
    private MongoDbRepository mongoDbRepository;

    @Override
    public void printAllFieldNames(MultipartFile pdfFile) throws IOException {
        PdfReader reader = new PdfReader(pdfFile.getInputStream());
        AcroFields fields = reader.getAcroFields();

        // Check Box1 is the name of a check box field
        String[] values = fields.getAppearanceStates("Branch Staff");

        for(String value: values) {
            System.out.println("Possible value for check box Branch Staff: "+ value);
        }

        // Check Box1 is the name of a check box field
        /*String[] values = fields.getAppearanceStates("Check Box1");

        for(String value: values) {
            System.out.println("Possible value for check box is "+ value);
        }*/



        System.out.println("List of all form fields:");
        Map<String, AcroFields.Item> fieldMap = fields.getFields();
        for (String key : fieldMap.keySet()) {
            System.out.println(key + ": " + fields.getField(key));
        }
        reader.close();
    }


    @Override
    public Map<String, String> fetchFieldValuesFromMongo(String formId) {
        return mongoDbRepository.findById(formId, Map.class, "pdfFields");
    }

    @Override
    public byte[] modifyPdfFieldsWithoutFlattening(MultipartFile pdfFile, Map<String, String> fieldValues) throws IOException, DocumentException {
        // Read the PDF
        PdfReader reader = new PdfReader(pdfFile.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        // Access the PDF form fields
        AcroFields fields = stamper.getAcroFields();

        /*// Update fields with values from MongoDB
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            fields.setField(entry.getKey(), entry.getValue());
        }*/
        //Set<String> fieldNames = fields.getFields().keySet();

        // Update all fields with values from MongoDB
        /*for (String fieldName : fieldNames) {
            String value = fieldValues.get(fieldName); // Get value from MongoDB
            if (value != null) {
                fields.setField(fieldName, value);

                //new
                //fields.setFieldProperty(fieldName, "setfflags", AcroFields.FIELD_READ_ONLY, null); // Keep field editable
            }
        }*/

        // Update fields with values from MongoDB
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            fields.setField(entry.getKey(), entry.getValue());
        }

        // 🔹 Force appearance update (ensures field values are displayed properly)
        //stamper.getAcroFields().setGenerateAppearances(true);


        // Ensure form fields remain editable
        // Force appearance update to ensure visibility of changes
        stamper.setFormFlattening(false); // Prevent flattening


        // Force appearance update (iText 5 workaround for visibility)
        stamper.partialFormFlattening(null);  // Keep all fields editable

        // Close stamper and reader
        stamper.close();
        reader.close();

        return outputStream.toByteArray();
    }

}
