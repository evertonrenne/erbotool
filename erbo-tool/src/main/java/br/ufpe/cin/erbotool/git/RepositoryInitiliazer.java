package br.ufpe.cin.erbotool.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;

public class RepositoryInitiliazer {

	@Getter @Setter
	private List<ProjectEntity> projectList;
	private final static Logger LOGGER = LogManager.getLogger();
	
	private final int EXCEPTION_LIMIT = 50;
	
	public RepositoryInitiliazer(List<ProjectEntity> projectList) {
		this.projectList = projectList;
		
	}
	
	public void init() {
		int catchCount = 0;
		String repoDIR = PropertiesUtil.getRepositoryDir();
		
		for(ProjectEntity proj : projectList.stream().filter(p -> p.isValid()).collect(Collectors.toList())) {
			// evaluate put every project in parallel
			try {
				// create dir
				Path path = FileUtil.createDirRandom(repoDIR);								
				
				// update entity
				proj.setPath(path);
				
				// checkout from git
				GitWorker git = new GitWorker();
				boolean cloneWorks = git.clone(proj);
				proj.setValid(cloneWorks);
								
				// fetch tag list
				git.fetch(proj);
				git.fetchTags(proj);
				List<String> tags = git.tag(proj);
				proj.setTags(tags);				
			} catch (IOException|SmellException e) {
				LOGGER.error(e.getMessage(), e);
				catchCount++;
				if ( catchCount >= EXCEPTION_LIMIT ) {
					throw new RuntimeException("Aborting process due successive errors!");
				}
			}			
		}
	}
}
