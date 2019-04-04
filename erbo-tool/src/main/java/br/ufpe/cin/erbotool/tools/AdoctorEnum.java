package br.ufpe.cin.erbotool.tools;

public enum AdoctorEnum {
	
	DATA_TRANSMISSION_WITHOUT_COMPRESSION("100000000000000"),
	DEBUGGABLE_RELEASE("010000000000000"),
	DURABLE_WAKELOCK("001000000000000"),
	INEFFICIENT_DATA_FORMAT_AND_PARSER("000100000000000"),
	INEFFICIENT_DATA_STRUCTURE("000010000000000"),
	INEFFICIENT_SQL_QUERY("000001000000000"),
	INTERNAL_GETTER_AND_SETTER("000000100000000"),
	LEAKING_INNER_CLASS("000000010000000"),
	LEAKING_THREAD("000000001000000"),
	MEMBER_IGNORING_METHOD("000000000100000"),
	NO_LOW_MEMORY_RESOLVER("000000000010000"),
	PUBLIC_DATA("000000000001000"),
	RIGID_ALARM_MANAGER("000000000000100"),
	SLOW_LOOP("000000000000010"),
	UNCLOSED_CLOSABLE("000000000000001");

	private String smell;
	
	private AdoctorEnum(String smellName) {
		this.smell = smellName;
	}
	
	public String getSmellCode() {
		return this.smell;
	}
	
/*
	//000000000000001: Unclosed Closable;
	aDoctor.execute(project, "000000000000001");
	//000000000000010: Slow Loop;
	aDoctor.execute(project, "000000000000010");
	//000000000000100: Rigid Alarm Manager;
	aDoctor.execute(project, "000000000000100");
	//000000000001000: Public Data;
	aDoctor.execute(project, "000000000001000");
	//000000000010000: No Low Memory Resolver;
	aDoctor.execute(project, "000000000010000");
	//000000000100000: Member Ignoring Method;
	aDoctor.execute(project, "000000000100000");
	//000000001000000: Leaking Thread;
	aDoctor.execute(project, "000000001000000");
	//000000010000000: Leaking Inner Class;
	aDoctor.execute(project, "000000010000000");
	//000000100000000: Internal Getter and Setter;
	aDoctor.execute(project, "000000100000000");
	//000001000000000: Inefficient SQL Query;
	aDoctor.execute(project, "000001000000000");
	//000010000000000: Inefficient Data Structure;
	aDoctor.execute(project, "000010000000000");
	//000100000000000: Inefficient Data Format and Parser;
	aDoctor.execute(project, "000100000000000");
	//001000000000000: Durable Wakelock;
	aDoctor.execute(project, "001000000000000");
	//010000000000000: Debuggable Release;
	aDoctor.execute(project, "010000000000000");
	//100000000000000: Data Transmission Without Compression;
	aDoctor.execute(project, "100000000000000");
 */
}
