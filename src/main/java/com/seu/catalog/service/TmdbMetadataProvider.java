package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Provider que busca metadados em TMDB (The Movie Database).
 * Endpoints: /search/movie, /movie/{id}, /genre/movie/list
 * Timeout: 5s. Fallback: null (não quebra a app).
 */
public class TmdbMetadataProvider implements MovieMetadataProvider {
    private static final Logger LOG = Logger.getLogger(TmdbMetadataProvider.class.getName());
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private String API_KEY = System.getenv("TMDB_API_KEY");
    private static final int TIMEOUT_MS = 5000;
    private static final String POSTER_BASE = "https://image.tmdb.org/t/p/w500";

    /**
     * Construtor padrão. Inicializa o provider verificando a presença da chave da API.
     */
    public TmdbMetadataProvider() {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            LOG.warning("TMDB_API_KEY não configurada; provider em modo fallback (retornará null)");
        }
    }

    @Override
    public List<MediaItem> searchByTitle(String term) {
        List<MediaItem> items = new ArrayList<>();
        if (API_KEY == null || API_KEY.trim().isEmpty()) return items;

        try {
            String encodedTerm = URLEncoder.encode(term, "UTF-8");
            String urlStr = BASE_URL + "/search/movie?query=" + encodedTerm + "&language=pt-BR";
            
            if (!API_KEY.startsWith("eyJ")) {
                urlStr += "&api_key=" + API_KEY;
            }
            
            JSONObject response = fetchJson(urlStr);
            
            if (response != null && response.has("results")) {
                JSONArray results = response.getJSONArray("results");
                int count = Math.min(results.length(), 6);
                for (int i = 0; i < count; i++) {
                    JSONObject res = results.getJSONObject(i);
                    int tmdbId = res.getInt("id");
                    MediaItem item = findById(String.valueOf(tmdbId));
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (Exception e) {
            LOG.warning("Erro ao buscar em TMDB: " + e.getMessage());
        }

        return items;
    }

    @Override
    public MediaItem findById(String externalId) {
        if (API_KEY == null || API_KEY.trim().isEmpty()) return null;

        try {
            String urlStr = BASE_URL + "/movie/" + externalId + "?language=pt-BR&append_to_response=credits";
            
            if (!API_KEY.startsWith("eyJ")) {
                urlStr += "&api_key=" + API_KEY;
            }

            JSONObject response = fetchJson(urlStr);
            
            if (response != null) {
                MediaItem item = new MediaItem();
                item.setTitle(response.optString("title"));
                item.setMediaType(MediaType.MOVIE);
                
                String releaseDate = response.optString("release_date", "");
                if (releaseDate.length() >= 4) {
                    item.setReleaseYear(Integer.parseInt(releaseDate.substring(0, 4)));
                }
                
                item.setSynopsis(response.optString("overview"));
                
                String posterPath = response.optString("poster_path", null);
                if (posterPath != null && !posterPath.isEmpty() && !"null".equals(posterPath)) {
                    item.setPosterUrl(POSTER_BASE + posterPath);
                }
                
                JSONArray genres = response.optJSONArray("genres");
                if (genres != null && genres.length() > 0) {
                    item.setGenre(genres.getJSONObject(0).optString("name"));
                }
                
                JSONObject credits = response.optJSONObject("credits");
                if (credits != null) {
                    JSONArray crew = credits.optJSONArray("crew");
                    if (crew != null) {
                        for (int i = 0; i < crew.length(); i++) {
                            JSONObject person = crew.getJSONObject(i);
                            if ("Director".equals(person.optString("job"))) {
                                item.setAuthorDirector(person.optString("name"));
                                break;
                            }
                        }
                    }
                }
                
                return item;
            }
        } catch (Exception e) {
            LOG.warning("Erro ao buscar filme por id em TMDB: " + e.getMessage());
        }

        return null;
    }
    
    private JSONObject fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/json");
        
        if (API_KEY != null && API_KEY.startsWith("eyJ")) {
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return new JSONObject(response.toString());
            }
        } else {
            LOG.warning("TMDB API HTTP error: " + responseCode + " for URL: " + urlStr);
            return null;
        }
    }
}
