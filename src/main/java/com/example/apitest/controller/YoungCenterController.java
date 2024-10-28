package com.example.apitest.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.apitest.util.PolicyFilterUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class YoungCenterController {

    @Value("${openApi.serviceKey}")
    private String serviceKey;

    @Value("${openApi.callBackUrl}")
    private String callBackUrl;

    @GetMapping("/youngcenter")
    public ResponseEntity<String> callYoungCenterApi(
            @RequestParam(value="display", defaultValue="10") int display,
            @RequestParam(value="pageIndex", defaultValue="1") int pageIndex,
            @RequestParam(value="srchPolicyId", required = false) String srchPolicyId,
            @RequestParam(value="query", required = false) String query,
            @RequestParam(value="bizTycdSel", required = false) String bizTycdSel,
            @RequestParam(value="srchPolyBizSecd", required = false) String srchPolyBizSecd,
            @RequestParam(value="keyword", required = false) String keyword
    ) {
        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String jsonResult = null;
//        String xmlResult;

        try {
            //파라미터 URL에 추가 (없어도 되나?)
            String urlStr = callBackUrl +
                    "?openApiVlak=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8) +
                    "&display=" + URLEncoder.encode(String.valueOf(display), StandardCharsets.UTF_8) +
                    "&pageIndex=" + URLEncoder.encode(String.valueOf(pageIndex), StandardCharsets.UTF_8) +
                    (srchPolicyId != null ? "&srchPolicyId=" + URLEncoder.encode(srchPolicyId, StandardCharsets.UTF_8) : "") +
                    (query != null ? "&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) : "") +
                    (bizTycdSel != null ? "&bizTycdSel=" + URLEncoder.encode(bizTycdSel, StandardCharsets.UTF_8) : "") +
                    (srchPolyBizSecd != null ? "&srchPolyBizSecd=" + URLEncoder.encode(srchPolyBizSecd, StandardCharsets.UTF_8) : "") +
                    (keyword != null ? "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) : "");

//            URL url = new URL(urlStr);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            stream = getNetworkConnection(urlConnection);
//            xmlResult = readStreamToString(stream);
//
//            //진행 중인 정책만 필터링
//            JSONArray currentPolicies = PolicyFilterUtil.filterCurrentPolicies(xmlResult);
//            jsonResult = currentPolicies.toString(2);

            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            stream = getNetworkConnection(urlConnection);
            String xmlResult = readStreamToString(stream);  //XML->String
            JSONObject jsonObject = XML.toJSONObject(xmlResult); //XML->Json

            //youthPolicyList -> youthPolicy array (이 데이터 맞는지 다시 확인 필요)
            JSONArray youthPolicyArray = jsonObject.getJSONObject("youthPolicyList")
                    .getJSONArray("youthPolicy");

            jsonResult = youthPolicyArray.toString(2);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }

    private InputStream getNetworkConnection(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);

        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code : " + urlConnection.getResponseCode());
        }

        return urlConnection.getInputStream();
    }

    private String readStreamToString(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                result.append(readLine).append("\n");
            }
        }
        return result.toString();
    }
}
