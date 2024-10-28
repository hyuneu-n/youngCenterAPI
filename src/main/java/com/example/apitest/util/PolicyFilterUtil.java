package com.example.apitest.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PolicyFilterUtil {

    public static JSONArray filterCurrentPolicies(String xmlResult) {
        //XML->JSON
        JSONObject jsonObject = XML.toJSONObject(xmlResult);

        //current Date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //정책 데이터 get
        JSONArray youthPolicyArray = jsonObject.getJSONObject("youthPolicyList").getJSONArray("youthPolicy");
        JSONArray currentPolicies = new JSONArray(); //진행중인 정책만 저장할 array

        for (int i = 0; i < youthPolicyArray.length(); i++) {
            JSONObject policy = youthPolicyArray.getJSONObject(i);

            //정책 신청기간 (rqutPrdCn=신청기간)
            if (policy.has("rqutPrdCn")) {
                String rqutPrdCn = policy.getString("rqutPrdCn");

                //구분 '-' 인가 '~'인가 확인 필요
                String[] dates = rqutPrdCn.split("~");
                if (dates.length == 2) {
                    LocalDate startDate = LocalDate.parse(dates[0].trim(), formatter);
                    LocalDate endDate = LocalDate.parse(dates[1].trim(), formatter);

                    if ((currentDate.isAfter(startDate) || currentDate.isEqual(startDate)) &&
                            (currentDate.isBefore(endDate) || currentDate.isEqual(endDate))) {
                        currentPolicies.put(policy);
                    }
                }
            }
        }
        return currentPolicies;
    }
}