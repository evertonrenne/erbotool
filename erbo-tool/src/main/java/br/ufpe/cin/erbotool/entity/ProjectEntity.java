package br.ufpe.cin.erbotool.entity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(of={"url", "name"})
public class ProjectEntity {

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
	private static Logger LOGGER = LogManager.getLogger();
	
	private String name;
	private String url;
	private Path path;
	private List<String> tags = new ArrayList<>();
	private Set<String[]> tagsWithErrors = new LinkedHashSet<>();
	private String currentTag;
	private boolean valid;
	private String currentBranch;
	
	public String getPreviously(String tag) {
		String previouslyTag = tag;
		
		try {
			int ix = tags.indexOf(tag);
			if ( ix != -1 && ix > 0 )
				previouslyTag = tags.get(ix - 1);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return previouslyTag;
	}
}
