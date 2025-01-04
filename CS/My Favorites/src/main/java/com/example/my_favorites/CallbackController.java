package com.example.my_favorites;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;



@Controller
public class CallbackController {
    private static final String stateKey = "spotify_auth_state";
    String authCode = "";
    private static final String url = "https://api.spotify.com/v1/users/me";
    private static final String clientId =System.getenv("SPOTIFY_CLIENT_ID");
    private static final String clientSecret  = System.getenv("SPOTIFY_CLIENT_SECRET");
    @GetMapping("/callback")
    public String callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpServletRequest request) {

        if (error != null) {

            return "redirect:/error?error=" + error;
        }

        Cookie[] cookies = request.getCookies();
        String storedState = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (stateKey.equals(cookie.getName())) {
                    storedState = cookie.getValue();
                }
            }
        }

        if (state == null || !state.equals(storedState)) {
            return "redirect:/error?error=state_mismatch";
        }
        authCode = code;

        getUserSongs();
        return "redirect:/success?code=" + code;
    }
protected String exchangeForRefreshToken(){
    RestTemplate restTemplate = new RestTemplate();
    String refreshToken ="";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String body = "grant_type=authorization_code" +
            "&code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8) +
            "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/callback", StandardCharsets.UTF_8) +
            "&client_id=" + clientId +
            "&client_secret=" + clientSecret;

    HttpEntity<String> request = new HttpEntity<>(body, headers);

    try {
        ResponseEntity<Map> response = restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            refreshToken = (String) responseBody.get("access_token");

        } else {
            System.out.println("Failed to fetch tokens. Response: " + response);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
        return refreshToken;
}

    public void getUserSongs(){
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
           String currToken = exchangeForRefreshToken();
            headers.set("Authorization", "Bearer " + currToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            System.out.println(headers);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            ResponseEntity<String> response = restTemplate.exchange("https://api.spotify.com/v1/me/top/tracks?limit=5", HttpMethod.GET, entity, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                System.out.println("User albums: " + response.getBody());
            }else{
                System.out.println("Error: " + response.getStatusCode());
            }

    }
}
