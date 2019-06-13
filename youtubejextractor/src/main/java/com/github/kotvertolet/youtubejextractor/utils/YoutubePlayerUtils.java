package com.github.kotvertolet.youtubejextractor.utils;

import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeNetworkCallException;
import com.github.kotvertolet.youtubejextractor.network.YoutubeSiteNetwork;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class YoutubePlayerUtils {

    /**
     * Extracts url of js player from embedded youtube page
     *
     * @param embeddedVideoPageHtml embedded youtube page
     * @return returns player ulr (like this one - 'https://www.youtube.com/yts/jsbin/player-vflkwPKV5/en_US/base.js')
     */
    public static String getJsPlayerUrl(String embeddedVideoPageHtml) throws ExtractionException {
        Pattern pattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
        Matcher matcher = pattern.matcher(embeddedVideoPageHtml);

        if (matcher.find()) {
            String match = matcher.group(1);
            // Removing backslashes
            match = match.replaceAll("\\\\", "");
            // Removing leading and trailing quotes
            return match.replaceAll("^\"|\"$", "");
        } else
            throw new ExtractionException("No encrypted signature found");
    }

    /**
     * Downloads JS youtube video player
     *
     * @param playerUrl player url
     * @return js code of the player
     */
    public static String downloadJsPlayer(String playerUrl) throws YoutubeNetworkCallException {
        try {
            Response<ResponseBody> responseBody = YoutubeSiteNetwork.getInstance().downloadWebpage(playerUrl);
            return responseBody.body().string();
        } catch (IOException | NullPointerException e) {
            throw new YoutubeNetworkCallException("Error while downloading youtube js video player", e);
        }
    }
}
