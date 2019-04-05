package br.ufpe.cin.erbotool.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(exclude={"smellList"})
public class TagIntervalEntity {

	private String tag;
	private String tagFixed;
	private Set<CodeSmellEntity> smellList = new LinkedHashSet<CodeSmellEntity>();
}
