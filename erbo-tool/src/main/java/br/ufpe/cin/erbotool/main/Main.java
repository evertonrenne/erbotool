package br.ufpe.cin.erbotool.main;


import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.checker.SystemChecker;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.ResultEntity;
import br.ufpe.cin.erbotool.git.RepositoryInitiliazer;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.HttpUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;
import br.ufpe.cin.erbotool.util.Util;


public final class Main {

	private static Logger LOGGER = LogManager.getLogger();
	private static Vector<ProjectEntity> projectsRunning = new Vector<>();
	private static Set<ResultEntity> resultMatchMessageList = new LinkedHashSet<ResultEntity>();
	
	public static synchronized void addProj(ProjectEntity proj) {
		projectsRunning.add(proj);
	}
	
	public static synchronized void delProj(ProjectEntity proj) {
		projectsRunning.remove(proj);
	}
	
	public static synchronized void delProj(ProjectEntity proj, Set<ResultEntity> threadResultMatchMessageList) {
		delProj(proj);
		synchronized (resultMatchMessageList) {
			resultMatchMessageList.addAll(threadResultMatchMessageList);
		}
	}
	
	public static void main(String[] args) throws IOException {		
		LOGGER.trace("checking args...");
		if (args.length!=1) {
			throw new RuntimeException("You should pass at least one argument!");
		} 
		LOGGER.trace("path: " + args[0]);
		
		PropertiesUtil.load(args[0]);
		
		LOGGER.trace("Git is available? " + SystemChecker.checkGit());
		LOGGER.trace("Java application is available? " + SystemChecker.checkJar());
		LOGGER.trace("Repository dir is created? " + SystemChecker.checkRepoDir());
		LOGGER.trace("Is there available space in disk? " + SystemChecker.checkFreeSpaceInDisk());
				
		// 	1.	Escrever rotina que leia uma lista de repositórios/projetos e confirme a disponibilidade no git 
		List<String> urlList = FileUtil.readLines(PropertiesUtil.getProjectList());
		List<ProjectEntity> projectList = HttpUtil.urlIsAvailable(urlList);
		
		// 	2.	Escrever rotina que construa a lista de tags/releases de cada repositório/projeto e faça o download do release + antigo (primeiro release/tag - BASE)
		RepositoryInitiliazer repositoryInitiliazer = new RepositoryInitiliazer(projectList);		
		repositoryInitiliazer.init();
		
		List<ProjectEntity> projects = projectList.stream().filter(p -> p.isValid()).collect(Collectors.toList());		
		try {
			// Finding Number of Cores
			int cores = Runtime.getRuntime().availableProcessors();
			for(ProjectEntity proj : projects) {								
				int count = 0;
				LOGGER.info("There is " + projectsRunning.size() + " threads running right now. Free cpus: " + (cores - projectsRunning.size()) );
				while(projectsRunning.size() >= cores) {
					Thread.sleep(200);
					count+=200;
					if ( count > 60000 ) {
						LOGGER.trace("All cores ("+cores+") are busy at moment! Waiting for a spot...");
						count = 0;
					}
				}
				LOGGER.info("Starting thread for project " + proj.getName() + "("+proj.getUrl()+")");				
				ProjectThread pt = new ProjectThread(proj);
				pt.start();
				Main.addProj(proj);
			} // for			
		} catch (Exception e) {
			LOGGER.error("ERROR: " + e.getMessage(), e);
		} finally {
			while(projectsRunning.size()>0) {
				try {
					Thread.sleep(60000);
					LOGGER.trace(projectsRunning.size() + " threads is still running!");
				} catch (InterruptedException e) {
					LOGGER.error("ERROR: " + e.getMessage(), e);
				}
			}
			if ( resultMatchMessageList.size() > 0 ) {
				// write csv or json
				String resultFilename = Util.getFormatedDateYYYYMMDDHHmmss(new Date()) + "result.json";
				FileUtil.persistJson(resultMatchMessageList, resultFilename);
				LOGGER.info("Execution complete!!! Results in " + resultFilename);
			}
		}
	}

}
