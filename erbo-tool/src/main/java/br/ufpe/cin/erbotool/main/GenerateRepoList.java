package br.ufpe.cin.erbotool.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpe.cin.erbotool.entity.github.ApiResult;
import br.ufpe.cin.erbotool.entity.github.Item;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.Util;

public class GenerateRepoList {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		int page = 1;
		String path = "https://api.github.com/search/repositories?q=android+language:java+stars:>=1000&sort=stars&order=desc&page="+page;
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(path);
		Response response = target.request().get();
		String result = response.readEntity(String.class);
		ObjectMapper mapper = new ObjectMapper();
		ApiResult apiResult =  mapper.readValue(result, ApiResult.class);
		//FileUtil.persistJson(apiResult, Util.getFormatedDateYYYYMMDDHHmmss(new Date()) + "android_repolist_github.json");
		
		// creating project list
		List<String> projectList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for(Item rep : apiResult.items) {
			if ( rep.fork == false ) {
				projectList.add(rep.htmlUrl);
				sb.append(rep.htmlUrl + System.getProperty("line.separator"));
			}
		}		
		
		// iterate
		int total = apiResult.totalCount / 30;		
		for(int i=1; i<total; i++) {
			path = "https://api.github.com/search/repositories?q=android+language:java+stars:>=1000&sort=stars&order=desc&page="+(page+i);
			target = client.target(path);
			response = target.request().get();
			result = response.readEntity(String.class);
			apiResult =  mapper.readValue(result, ApiResult.class);
			for(Item rep : apiResult.items) {
				if ( rep.fork == false ) {
					projectList.add(rep.htmlUrl);
					sb.append(rep.htmlUrl + System.getProperty("line.separator"));
				}
			}
			Thread.sleep(10000);
		}
		
			
		FileUtil.writeFile(Util.getFormatedDateYYYYMMDDHHmmss(new Date()) + "_projects.txt", sb.toString());
	}

}
