package br.ufpe.cin.erbotool.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(exclude={"smellList", "refactoringList"})
public class TagIntervalEntity {

	private String tagBeforeFix;
	private String tagFixed;
	private Set<CodeSmellEntity> smellList = new LinkedHashSet<CodeSmellEntity>();
	private List<RefactoringEntity> refactoringList = new ArrayList<>();
}
