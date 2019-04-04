package br.ufpe.cin.erbotool.tools;

import java.io.IOException;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;

public interface SmellTools extends Tools {
	
	/**
	 * 
	 * @param proj
	 * @param parameters
	 * @return a String with filepath for csv generated as output
	 * @throws IOException
	 * @throws SmellException 
	 */
	String execute(ProjectEntity proj, String parameters) throws IOException, SmellException;
	
	/**
	 * 
	 * @param proj
	 * @return a String with filepath for csv generated as output
	 * @throws IOException
	 * @throws IOException
	 */
	String execute(ProjectEntity proj) throws IOException, SmellException;
}
