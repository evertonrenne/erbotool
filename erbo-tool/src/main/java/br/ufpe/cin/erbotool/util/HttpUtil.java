package br.ufpe.cin.erbotool.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;

public class HttpUtil {

	private final static Logger LOGGER = LogManager.getLogger();
	
	public static List<ProjectEntity> urlIsAvailable(List<String> urlList) throws IOException {
		List<ProjectEntity> projectList = new ArrayList<>();
		for(String url : urlList) {
			if ( urlIsAvailable(url) ) {
				ProjectEntity proj = new ProjectEntity();
				proj.setUrl(url);
				proj.setName(getLastNameInURL(url));
				proj.setValid(true);
				projectList.add(proj);				
			}
		}
		
		return projectList;
	}
	
	public static boolean urlIsAvailable(String URL) throws IOException {				
		URL u = new URL(URL); 
	    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection(); 
	    HttpURLConnection.setFollowRedirects(true);
	    huc.setRequestMethod("HEAD"); 
	    huc.connect();
	    boolean retorno = (huc.getResponseCode() == HttpURLConnection.HTTP_OK);
	    LOGGER.trace("URL: " + URL + " is " + (retorno?"OK":huc.getResponseCode() + " error"));
	    return retorno;
	}
	
	public static String getLastNameInURL(String URL) {
		int lastIndex = URL.lastIndexOf("/");
		String lastName = URL.substring(lastIndex+1, URL.length());
		return lastName;
	}
}
