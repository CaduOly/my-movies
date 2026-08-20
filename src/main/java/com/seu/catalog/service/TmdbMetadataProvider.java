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
        return searchByTitle(term, "pt-BR");
    }

    @Override
    public List<MediaItem> searchByTitle(String term, String language) {
        List<MediaItem> items = new ArrayList<>();
        if (API_KEY == null || API_KEY.trim().isEmpty()) return items;

        String lang = normalizeLanguage(language);

        try {
            String encodedTerm = URLEncoder.encode(term, "UTF-8");
            String urlStr = BASE_URL + "/search/multi?query=" + encodedTerm + "&language=" + URLEncoder.encode(lang, "UTF-8");
            
            if (!API_KEY.startsWith("eyJ")) {
                urlStr += "&api_key=" + API_KEY;
            }
            
            JSONObject response = fetchJson(urlStr);
            
            if (response != null && response.has("results")) {
                JSONArray results = response.getJSONArray("results");
                int count = Math.min(results.length(), 6);
                for (int i = 0; i < count; i++) {
                    JSONObject res = results.getJSONObject(i);
                    String mediaTypeStr = res.optString("media_type");
                    int tmdbId = res.getInt("id");
                    MediaItem item = null;
                    if ("tv".equalsIgnoreCase(mediaTypeStr)) {
                        item = findTvById(String.valueOf(tmdbId), lang);
                    } else if ("movie".equalsIgnoreCase(mediaTypeStr)) {
                        item = findMovieById(String.valueOf(tmdbId), lang);
                    }
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
        return findById(externalId, "pt-BR");
    }

    @Override
    public MediaItem findById(String externalId, String language) {
        MediaItem item = findMovieById(externalId, language);
        if (item == null) {
            item = findTvById(externalId, language);
        }
        return item;
    }

    public MediaItem findMovieById(String externalId, String language) {
        if (API_KEY == null || API_KEY.trim().isEmpty()) return null;

        String lang = normalizeLanguage(language);

        try {
            String urlStr = BASE_URL + "/movie/" + externalId + "?language=" + URLEncoder.encode(lang, "UTF-8") + "&append_to_response=credits";
            
            if (!API_KEY.startsWith("eyJ")) {
                urlStr += "&api_key=" + API_KEY;
            }

            JSONObject response = fetchJson(urlStr);
            
            if (response != null && response.has("id")) {
                MediaItem item = new MediaItem();
                item.setTitle(response.optString("title"));
                item.setMediaType(MediaType.MOVIE);
                
                String releaseDate = response.optString("release_date", "");
                if (releaseDate.length() >= 4) {
                    try {
                        item.setReleaseYear(Integer.parseInt(releaseDate.substring(0, 4)));
                    } catch (NumberFormatException ignored) {}
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

    public MediaItem findTvById(String externalId, String language) {
        if (API_KEY == null || API_KEY.trim().isEmpty()) return null;

        String lang = normalizeLanguage(language);

        try {
            String urlStr = BASE_URL + "/tv/" + externalId + "?language=" + URLEncoder.encode(lang, "UTF-8") + "&append_to_response=credits";
            
            if (!API_KEY.startsWith("eyJ")) {
                urlStr += "&api_key=" + API_KEY;
            }

            JSONObject response = fetchJson(urlStr);
            
            if (response != null && response.has("id")) {
                MediaItem item = new MediaItem();
                item.setTitle(response.optString("name"));
                item.setMediaType(MediaType.SERIES);
                
                String firstAirDate = response.optString("first_air_date", "");
                if (firstAirDate.length() >= 4) {
                    try {
                        item.setReleaseYear(Integer.parseInt(firstAirDate.substring(0, 4)));
                    } catch (NumberFormatException ignored) {}
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
                
                JSONArray createdBy = response.optJSONArray("created_by");
                if (createdBy != null && createdBy.length() > 0) {
                    item.setAuthorDirector(createdBy.getJSONObject(0).optString("name"));
                } else {
                    JSONObject credits = response.optJSONObject("credits");
                    if (credits != null) {
                        JSONArray crew = credits.optJSONArray("crew");
                        if (crew != null) {
                            for (int i = 0; i < crew.length(); i++) {
                                JSONObject person = crew.getJSONObject(i);
                                String job = person.optString("job");
                                if ("Executive Producer".equals(job) || "Director".equals(job) || "Creator".equals(job)) {
                                    item.setAuthorDirector(person.optString("name"));
                                    break;
                                }
                            }
                        }
                    }
                }
                
                return item;
            }
        } catch (Exception e) {
            LOG.warning("Erro ao buscar série por id em TMDB: " + e.getMessage());
        }

        return null;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            return "pt-BR";
        }
        String lang = language.trim();
        if ("en".equalsIgnoreCase(lang) || "en_US".equalsIgnoreCase(lang) || "en-US".equalsIgnoreCase(lang)) {
            return "en-US";
        }
        if ("pt".equalsIgnoreCase(lang) || "pt_BR".equalsIgnoreCase(lang) || "pt-BR".equalsIgnoreCase(lang)) {
            return "pt-BR";
        }
        return lang.replace('_', '-');
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
