package mutsa.yewon.talksparkbe.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class JWTUtilTest {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext web;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    public void token_validation(){
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IiQyYSQxMCRJWEo3cHlFY2pQLmEzUldEWFlZaFgubnk5V2JPaGdMbVpJcURHSjVrOUlLZXplTmxiNU5wYSIsIm5hbWUiOiLrsJXsirnrspQiLCJrYWthb0lkIjoiMzc3Njg4NTE5MiIsInJvbGVOYW1lcyI6WyJVU0VSIl0sImlhdCI6MTczMDg5Njg5NiwiZXhwIjoxNzMwODk3NDk2fQ.0qSUBzb6Mg4LEKuApmoJLyohdKYaTzNHr-IAkbZ_6zE";

        CustomTalkSparkException ex = assertThrows(CustomTalkSparkException.class, () -> {
            jwtUtil.validateToken(accessToken);
        });

        String expectedMessage = "만료된 토큰 입니다.";
        String message = ex.getErrorCode().getMessage();

        assertEquals(message, expectedMessage);
    }

    @Test
    public void new_refresh_token() throws Exception {

        final String refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IiQyYSQxMCRJWEo3cHlFY2pQLmEzUldEWFlZaFgubnk5V2JPaGdMbVpJcURHSjVrOUlLZXplTmxiNU5wYSIsIm5hbWUiOiLrsJXsirnrspQiLCJrYWthb0lkIjoiMzc3Njg4NTE5MiIsInJvbGVOYW1lcyI6WyJVU0VSIl0sImlhdCI6MTczMDg5Njg5NiwiZXhwIjoxNzMwOTgzMjk2fQ.vl9GJMvvnUuYQ1ZrAN8j8n11l_g1DMuHsVcO42N9Zio";
        final String url = "/api/member/refresh?" + refreshToken;

        ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON_VALUE));




    }
}