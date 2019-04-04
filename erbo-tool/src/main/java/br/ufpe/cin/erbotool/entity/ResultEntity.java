package br.ufpe.cin.erbotool.entity;

import java.util.List;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude={"smellList","occurrenceList","refactoringList"})
public class ResultEntity {

	public ProjectEntity project;
	public CodeSmellEntity smell;
	public List<CodeSmellEntity> smellList;
	public List<CodeSmellEntity> occurrenceList;
	public List<RefactoringEntity> refactoringList;
}
