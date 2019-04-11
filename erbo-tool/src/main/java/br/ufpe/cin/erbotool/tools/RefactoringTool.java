package br.ufpe.cin.erbotool.tools;

import java.io.IOException;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.TagIntervalEntity;
import br.ufpe.cin.erbotool.exception.SmellException;

public interface RefactoringTool extends Tools {

	public String execute(CodeSmellEntity smell, ProjectEntity proj, boolean invertTags) throws IOException, SmellException;

	public String execute(TagIntervalEntity interval, ProjectEntity proj, boolean invertTags) throws IOException, SmellException;
}
