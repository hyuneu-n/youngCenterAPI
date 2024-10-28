package com.example.apitest.controller;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Slf4j
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
        String result = null;

        try {
            // 파라미터를 URL 인코딩 처리
            String urlStr = callBackUrl +
                    "?openApiVlak=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8) +
                    "&display=" + URLEncoder.encode(String.valueOf(display), StandardCharsets.UTF_8) +
                    "&pageIndex=" + URLEncoder.encode(String.valueOf(pageIndex), StandardCharsets.UTF_8) +
                    (srchPolicyId != null ? "&srchPolicyId=" + URLEncoder.encode(srchPolicyId, StandardCharsets.UTF_8) : "") +
                    (query != null ? "&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) : "") +
                    (bizTycdSel != null ? "&bizTycdSel=" + URLEncoder.encode(bizTycdSel, StandardCharsets.UTF_8) : "") +
                    (srchPolyBizSecd != null ? "&srchPolyBizSecd=" + URLEncoder.encode(srchPolyBizSecd, StandardCharsets.UTF_8) : "") +
                    (keyword != null ? "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) : "");

            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            stream = getNetworkConnection(urlConnection);
            result = readStreamToString(stream);

            if (stream != null) stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private InputStream getNetworkConnection(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(3000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);

        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code : " + urlConnection.getResponseCode());
        }

        return urlConnection.getInputStream();
    }

    private String readStreamToString(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        String readLine;
        while((readLine = br.readLine()) != null) {
            result.append(readLine).append("\n");
        }

        br.close();
        return result.toString();
    }
}