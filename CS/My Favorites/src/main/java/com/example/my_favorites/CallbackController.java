package com.example.my_favorites;

import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.json.XMLTokener.entity;


@Controller
public class CallbackController {
    private static final String stateKey = "spotify_auth_state";
    String authCode = "";
    private static final String url = "https://api.spotify.com/v1/users/me";
    private static final String clientId =System.getenv("SPOTIFY_CLIENT_ID");
    protected static final String clientSecret  = System.getenv("SPOTIFY_CLIENT_SECRET");

//    @RequestMapping("/callback")
//    public String callback(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "state", required = false) String state, @RequestParam(value = "error", required = false) String error, HttpServletRequest request, Model model) {
//        authCode = code;
//
//        request.getSession().setAttribute("authCode", authCode);
//        ArrayList<String> songs =  getUserSongs();
//        return "index";
//    }


//public ArrayList<String> getUserSongs(){
//        RestTemplate restTemplate = new RestTemplate();
//        ArrayList<String> songs = new ArrayList<>();
//        HttpHeaders headers = new HttpHeaders();
//        String currToken = exchangeForRefreshToken();
//
//        headers.set("Authorization", "Bearer " + currToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange("https://api.spotify.com/v1/me/top/tracks?limit=5", HttpMethod.GET, entity, String.class);
//
//        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.getBody()));
//        JSONArray items = jsonObject.getJSONArray("items");
//        if(response.getStatusCode() == HttpStatus.OK){
//            for(int i = 0; i< items.length(); i++){
//                JSONObject item = items.getJSONObject(i);
//                JSONObject album = item.getJSONObject("album");
//                String currSong = album.getString("name");
//                songs.add(currSong);
//                System.out.println(currSong);
//            }
//        }else{
//            System.out.println("Error: " + response.getStatusCode());
//        }
//   return songs;
//}

}