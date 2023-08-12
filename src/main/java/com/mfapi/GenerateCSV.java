package com.mfapi;

import java.util.ArrayList;
import java.util.Map;

public class GenerateCSV {
    private int startDate = 0;
    private int endDate = 1600;
    //Provides list of NAV data extracted from input map
    //Requires Map of mutual fund Date and NAV data
    public ArrayList<String> addNavToList(ArrayList<Map<String,String>> data)
    {
        ArrayList<String> navData = new ArrayList<>();
        if(data.size() >= 1600) {
            for (int i = startDate; i < endDate; i ++)
            {
                navData.add(0,(String) data.get(i).get("nav"));
            }
        }
        return navData;
    }
    //Provides list of date data extracted from input map
    //Requires Map of mutual fund Date and NAV data
    public ArrayList<String> addDateToList(ArrayList<Map<String,String>> data)
    {
        ArrayList<String> navData = new ArrayList<>();
        for (int i = startDate; i < endDate; i ++)
        {
            navData.add(0,(String) data.get(i).get("date"));
        }
        return navData;
    }
    //Sets start and end date between which mutual fund comparison needs to take place
    //Requires start and end date as input
    public void inputDates(ArrayList<String> dates,String startDate,String endDate)
    {
        if(dates.contains(startDate) && dates.contains(endDate))
        {
            this.endDate = 1600 - dates.indexOf(startDate);
            this.startDate = 1600 - dates.indexOf(endDate) - 1;
        }
        else{
            System.out.println("Invalid Date. Default date range will be applied");
        }
    }
    //Provides normalized data as output
    public ArrayList<String> normalizeNavData(ArrayList<String> navData)
    {
        ArrayList<String> normNAV = new ArrayList<>();
        double initialNav = Double.parseDouble (navData.get(0));
        for(String nav: navData)
        {
            double navValue = Double.parseDouble(nav);
            double normValue = (navValue - initialNav) * 100 / (initialNav);
            normNAV.add(String.valueOf(normValue));
        }
        return normNAV;
    }
}




