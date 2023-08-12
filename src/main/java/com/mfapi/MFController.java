package com.mfapi;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MFController {

//Use http://localhost:8080/exportCSV/Large Cap/21-03-2022/20-02-2023 to launch api

    @GetMapping(path = "/exportCSV/{name}/{startDate}/{endDate}", produces = "text/csv")
    public ResponseEntity<Resource> exportCSV(@PathVariable String name, @PathVariable String startDate, @PathVariable String endDate) throws Exception {

        //GenerateCSV needs to be provided the path where csv needs to be stored
        GenerateCSV gen1 = new GenerateCSV();
        //SchemeCode needs to be provided the category within which mutual funds need to be compared
        SchemeCode category = new SchemeCode(name);
        HashMap<String,Long> schemes = category.schemes();
        String[] csvHeader = new String[schemes.size() + 1];
        ArrayList<ArrayList<String>> mahaList = new ArrayList<>();
        int l = 0;
        boolean addDate = true;
        // Below code is used to fetch json data from url link for each mutual fund and store it in -
        // - ArrayList<ArrayList<String>> mahaList
        for(Map.Entry<String,Long> mapElement : schemes.entrySet())
        {
            ArrayList<Map<String,String>> schemedata2 = category.MFData(mapElement.getValue());
            ArrayList<String> navData = gen1.addNavToList(schemedata2);
            if(navData.size() > 0) {
                System.out.println(mapElement.getValue());
                if(addDate)
                {
                    //Start and end dates can be provided as input to inputDates method
                    //gen1.inputDates(gen1.addDateToList(schemedata2),"21-03-2022","16-02-2023");
                    gen1.inputDates(gen1.addDateToList(schemedata2),startDate,endDate);
                    mahaList.add(gen1.addDateToList(schemedata2));
                    csvHeader[l] = "Date";
                    l += 1;
                    addDate = false;
                }
                csvHeader[l] = mapElement.getKey();
                ArrayList<String> normalizedNav = gen1.normalizeNavData(gen1.addNavToList(schemedata2));
                mahaList.add(normalizedNav);
                l += 1;
            }
        }
        //Below code converts Arraylist<Arraylist<String>> i.e mahaList to Arraylist<String[]> i.e. csvData
        //This is done because writeAll function of CSVWriter takes ArrayList<String[]> as input
        //mahaList = [[date1,date2,....],
        //            [MF1_nav1,MF1_nav2,...],
        //            [MF2_nav1,MF2_nav2,...],
        //            .......................
        //            [MFn_nav1,MFn_nav2,...]]
        //csvData = [[date1,MF1_nav1,MF2_nav1,..MFn_nav1],
        //           [date2,MF1_nav2,MF2_nav2,..MFn_nav2],
        //           ........................
        //           [daten,MF1_navn,MF2_navn,..MFn_navn]]
        //ArrayList<String[]> csvData = new ArrayList<>();
        ArrayList<ArrayList<String>> csvData = new ArrayList<>();
        int len = mahaList.get(0).size();
        for(int i = 0;i < len;i++) {
            ArrayList<String> arr = new ArrayList<>();
            for (ArrayList<String> k : mahaList) {
                arr.add(k.get(i));
            }
            csvData.add(arr);
        }
        ByteArrayInputStream byteArrayOutputStream;
        int length;

        // closing resources by using a try with resources
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(csvHeader)
                )
        ) {
            // populating the CSV content
            for (List<String> record : csvData)
                csvPrinter.printRecord(record);

            // writing the underlying stream
            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
            length = out.size();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);
        String csvFileName = name + "_mutualfund_comparison.csv";
        System.out.println("Size is " + length);
        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        String lengthInString = String.valueOf(length);
        headers.set(HttpHeaders.CONTENT_LENGTH,lengthInString);

        return new ResponseEntity<>(
                fileInputStream,
                headers,
                HttpStatus.OK
        );
    }
}
