package br.ufpe.cin.erbotool.git;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.util.ProcessUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;

public class GitWorker {

	private final static Logger LOGGER = LogManager.getLogger();
	
	public boolean clone(ProjectEntity proj) throws SmellException {	
		boolean retorno = false;
		String[] cmdArray = new String[4];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "clone";
		cmdArray[2] = proj.getUrl();
		cmdArray[3] = proj.getPath().toAbsolutePath().toString();
		
		LOGGER.trace("Cloning " + proj.getUrl());
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			p.waitFor();
			int exitValue = p.exitValue();
			retorno = exitValue==0;
			LOGGER.trace(PropertiesUtil.getGitApp() + " finished clone with exitcode " + exitValue);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			p.destroy();
		}*/
		retorno = ProcessUtil.executeBoolean(cmdArray);
		return retorno;
	}
	
	public List<String> tag(ProjectEntity proj) throws SmellException {				
		List<String> tagList = new ArrayList<>();
		
		String[] cmdArray = new String[2];
		cmdArray[0] = PropertiesUtil.getGitTagScript();
		cmdArray[1] = proj.getPath().toAbsolutePath().toString();
		
		
		LOGGER.trace("getting tag list " + proj.getUrl());
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			p.waitFor();
			int exitValue = p.exitValue();
			if ( exitValue != 0 ) {
				return tagList;
			} 
			// reading standard output
			tagList = StreamUtil.readLines(p.getInputStream());
			p.destroy();
			
			LOGGER.trace(PropertiesUtil.getGitApp() + " finished retrieve "+proj.getName()+"'s tags. Total: " + tagList.size() + " tags.");
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		
		tagList = ProcessUtil.executeList(cmdArray);
		
		LOGGER.trace(PropertiesUtil.getGitApp() + " finished retrieve "+proj.getName()+"'s tags. Total: " + tagList.size() + " tags.");
		
		return tagList;
	}
	
	public String log(ProjectEntity proj, String commitSHA1) throws SmellException {				
		String message = null;
		
		String[] cmdArray = new String[7];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "-C";
		cmdArray[2] = proj.getPath().toAbsolutePath().toString();
		cmdArray[3] = "log";
		cmdArray[4] = "-1";
		cmdArray[5] = "--format=%B";
		cmdArray[6] = commitSHA1;
		
		
		LOGGER.trace("getting commit "+commitSHA1+" log message in " + proj.getUrl());
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			p.waitFor();
			int exitValue = p.exitValue();
			if ( exitValue != 0 ) {
				return message;
			}
			
			// reading standard output
			message = StreamUtil.readLine(p.getInputStream());
			p.destroy();
			
			LOGGER.trace(PropertiesUtil.getGitApp() + " finished retrieve "+proj.getName()+"'s commit message.");
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		message = ProcessUtil.execute(cmdArray);
		
		return message;
	}
	
	public void fetch(ProjectEntity proj) throws SmellException {
		// git fetch && git fetch --tags

		String[] cmdArray = new String[4];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "-C";
		cmdArray[2] = proj.getPath().toAbsolutePath().toString();
		cmdArray[3] = "fetch";
		
		LOGGER.trace("fetching repo " + proj.getUrl());
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.error(Arrays.toString(erroList.toArray()));
			}
			p.destroy();
			LOGGER.trace(PropertiesUtil.getGitApp() + " finished fetch repo with exitcode " + exitValue);		
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		ProcessUtil.executeBoolean(cmdArray);
	}
	
	public void fetchTags(ProjectEntity proj) throws SmellException {
		// git fetch && git fetch --tags

		String[] cmdArray = new String[5];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "-C";
		cmdArray[2] = proj.getPath().toAbsolutePath().toString();
		cmdArray[3] = "fetch";
		cmdArray[4] = "--tags";
		
		LOGGER.trace("fetching tags " + proj.getUrl());
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			p.waitFor();
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.error(Arrays.toString(erroList.toArray()));
			}
			p.destroy();
			
			LOGGER.trace(PropertiesUtil.getGitApp() + " finished fetch tags with exitcode " + exitValue);		
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		
		ProcessUtil.executeBoolean(cmdArray);
	}
	
	public void checkout(ProjectEntity proj, String tag) throws SmellException {
		String[] cmdArray = new String[5];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "-C";
		cmdArray[2] = proj.getPath().toAbsolutePath().toString();
		cmdArray[3] = "checkout";
		cmdArray[4] = tag;
		
		LOGGER.trace(proj.getUrl() + " checkout tag " + tag);
		
		/*Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
			p.waitFor();
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.error(Arrays.toString(erroList.toArray()));
			}
			p.destroy();
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);	
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		ProcessUtil.executeBoolean(cmdArray);
	}
}
