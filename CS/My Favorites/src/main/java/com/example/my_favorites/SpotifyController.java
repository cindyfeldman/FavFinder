package com.example.my_favorites;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Controller
public class SpotifyController {
    private static final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private static final String redirectUri = "http://localhost:8080/callback"; // Your callback URL
    private static final String stateKey = generateRandomString(16);
    private static final String scope = "user-read-private user-read-email user-top-read"; // Adjust scopes as needed

    /**
     * Redirects user to Spotify authorization endpoint.
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException, IOException {
        String state = generateRandomString(16); // Generate a random state
        response.addCookie(new Cookie("spotify_auth_state", state)); //
        String url = "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)+
              "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        response.sendRedirect(url);
    }

    private static String generateRandomString(int length) {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

}
