package com.mfapi;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchemeCode {
    private final String name;
    //Website used for fetching NAV data in the form of JSON : https://api.mfapi.in/mf
    private final static String LINK_URL = "https://api.mfapi.in/mf";

    public SchemeCode(String name)
    {
        this.name = name;
    }

    public HashMap<String,Long> schemes() throws Exception
    {
        ClassPathResource staticDataResource = new ClassPathResource("mf_list.json");
        String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);
        Object parser = new JSONParser().parse(staticDataString);
        JSONObject obj = (JSONObject) parser;
        return (HashMap<String,Long>) (JSONObject) obj.get(this.name);
    }

    //Provides JSON data for the particular mutual fund which includes date and NAV
    //Requires scheme code as input
    public ArrayList<Map<String,String>> MFData(long schemecode)
    {
        ArrayList<Map<String,String>> data = new ArrayList<>();
        try{
            URL schemeurl = new URL(LINK_URL + "/" + schemecode);
            String json = IOUtils.toString(schemeurl, Charset.forName("UTF-8"));
            Object obj = new JSONParser().parse(String.valueOf(json));
            JSONObject dataJson = (JSONObject) obj;
            data = (JSONArray) dataJson.get("data");
        }
        catch(Exception e)
        {
            System.out.println("Invalid protocol");
        }
        return data;
    }
}


