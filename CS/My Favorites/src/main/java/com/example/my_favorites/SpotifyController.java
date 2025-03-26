package com.example.my_favorites;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Controller
public class SpotifyController {
    @Value("${spotify.client.id}")
    private String clientId;
    @Value("${spotify.redirect.uri}")
    private String redirectUri;
    @Value("${spotify.auth.url")
    private String authUrl;
    @Value("${spotify.scope}")
    private String scope;

    protected static final String clientSecret  = System.getenv("SPOTIFY_CLIENT_SECRET");

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException, IOException {
        String state = generateRandomString(16);
        response.addCookie(new Cookie("spotify_auth_state", state)); //
        String url = "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)+
              "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        response.sendRedirect(url);
    }
    private String exchangeForAccessToken(String code) {
        String accessToken = "";
        RestTemplate restTemplate = new RestTemplate();
        String body = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + redirectUri +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://accounts.spotify.com/api/token",
                    HttpMethod.POST,
                    request,
                    Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                accessToken = response.getBody().get("access_token").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }
    String accessToken = "";
    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code", required = false)
                               String code,
                           @RequestParam(value = "state", required = false)
                           String state,
                           @RequestParam(value = "error", required = false)
                               String error, Model model) {
        System.out.println("Received code: " + code);
        accessToken = exchangeForAccessToken(code);
        model.addAttribute("accessToken", accessToken);

        return "/display";
    }
    @GetMapping("/access_token")
    @ResponseBody
    public String getAccessToken() {
        return "{\"access_token\": \"" + accessToken + "\"}";
    }
    @GetMapping("/top_tracks")
    public ResponseEntity<String> getTopTracks() {
        String apiUrl = "https://api.spotify.com/v1/me/top/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, String.class);
            return ResponseEntity.ok(response.getBody());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    private static String generateRandomString(int length) {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

}
