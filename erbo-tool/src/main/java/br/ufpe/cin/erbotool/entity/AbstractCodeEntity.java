package br.ufpe.cin.erbotool.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode
public abstract class AbstractCodeEntity {

	/**
	 * resource could be a Class, XML, Enum, Config.
	 * Could be anything that can have a smell
	 */	
	private String resource;	
	private ProjectEntity project;
	
}
