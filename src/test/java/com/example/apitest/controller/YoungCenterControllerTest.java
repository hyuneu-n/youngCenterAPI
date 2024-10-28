package com.example.apitest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest  // 전체 애플리케이션 컨텍스트 로드
@AutoConfigureMockMvc
public class YoungCenterControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("청년정책 Open API 통신 테스트")
    public void callOpenApi() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("display", "10");        // 출력 건수
        params.add("pageIndex", "1");       // 조회할 페이지
        params.add("query", "취업");          // 정책명, 정책 소개 등 키워드 검색 (예시로 "취업" 사용)
        params.add("bizTycdSel", "023010"); // 정책분야: 예시로 '일자리 분야' (코드 023010)
        params.add("srchPolyBizSecd", "003002001"); // 지역코드: 예시로 '서울' (코드 003002001)
        params.add("keyword", "지원");        // 키워드 검색: "지원"을 예시로 사용

        this.mvc.perform(get("/api/youngcenter").params(params))
                .andExpect(status().isOk())
                .andDo(print());
    }
}