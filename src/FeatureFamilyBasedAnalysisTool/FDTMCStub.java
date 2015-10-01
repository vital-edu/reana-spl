package FeatureFamilyBasedAnalysisTool;

public class FDTMCStub {

	public static FDTMC createFDTMC(Feature feature) {
		switch (feature.getName()) {
		case "Sqlite":
			FDTMC sqlite = createSqliteFDTMC();
			return sqlite;
		
		case "Memory":
			return createMemoryFDTMC();
			
		case "File":
			FDTMC file = createFileFDTMC();
			return file;
			
		case "Oxygenation":
			FDTMC oxygenation = createOxygenationFDTMC(); 
			return oxygenation;
			
		case "PulseRate":
			return createPulseRateFDTMC();
			
		case "Position":
			return createPositionFDTMC();
			
		case "Situation":
			return createSituationFDTMC();
			
		default:
			System.out.println("Entrei no default");
			return null;
		} 
	}
	
	
	private static FDTMC createOxygenationFDTMC() {
		FDTMC fdtmcOxygenation = new FDTMC(); 
		fdtmcOxygenation.setVariableName("sOxygenation");
		
		//States Creation
		State	init = fdtmcOxygenation.createState("init"), 
				success = fdtmcOxygenation.createState("success"),
				fail = fdtmcOxygenation.createState("fail"),
				source, target, target2;
		
		source = init; 
		target = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "register", "0.999");
		fdtmcOxygenation.createTransition(source, fail, "register", "0.001");
		
		source = target; 
		target = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "register_return", "0.999");
		fdtmcOxygenation.createTransition(source, fail, "register_return", "0.001");
		
		source = target; 
		target = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "sendSituation_SPO2", "0.999");
		fdtmcOxygenation.createTransition(source, fail, "sendSituation_SPO2", "0.001");
		
		source = target; 
		target = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "persist", "0.999"); 
		fdtmcOxygenation.createTransition(source, fail, "persist", "0.001");
		
		//SQlite selection / interface
		source = target; 
		target = fdtmcOxygenation.createState();
		target2 = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "sqliteSelection", "fSqlite");
		fdtmcOxygenation.createTransition(source, target2, "sqliteSelection", "1-fSqlite");
		State sucessSqlite = fdtmcOxygenation.createState(); 
		State failSqlite = fdtmcOxygenation.createState();
		source = target; 
		fdtmcOxygenation.createTransition(source, sucessSqlite, "", "rSqlite");
		fdtmcOxygenation.createTransition(sucessSqlite, target2, "", "1.0");
		fdtmcOxygenation.createTransition(source, failSqlite, "", "1-rSqlite");
		fdtmcOxygenation.createTransition(failSqlite, fail, "", "1.0");
		
		//Memory selection / interface
		source = target2; 
		target = fdtmcOxygenation.createState(); 
		target2 = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "memorySelection", "fMemory");
		fdtmcOxygenation.createTransition(source, target2, "memorySelection", "1-fMemory");
		State sucessMemory = fdtmcOxygenation.createState(); 
		State failMemory = fdtmcOxygenation.createState(); 
		source = target; 
		fdtmcOxygenation.createTransition(source, sucessMemory, "", "rMemory");
		fdtmcOxygenation.createTransition(sucessMemory, target2, "", "1.0");
		fdtmcOxygenation.createTransition(source, failMemory, "", "1-rMemory");
		fdtmcOxygenation.createTransition(failMemory, fail, "", "1.0");
		
		source = target2; 
		target = fdtmcOxygenation.createState(); 
		fdtmcOxygenation.createTransition(source, target, "persistReturn", "0.999");
		fdtmcOxygenation.createTransition(source, fail, "persistReturn", "0.001");
		
		source = target; 
		target = success; 
		fdtmcOxygenation.createTransition(source, target, "sendSituation_Oxygenation", "0.999");
		fdtmcOxygenation.createTransition(source, fail, "sendSituation_Oxygenation", "0.001");
		
		fdtmcOxygenation.createTransition(success, success, "", "1.0");
		fdtmcOxygenation.createTransition(fail, fail, "", "1.0");

		return fdtmcOxygenation;
	}


	
	private static FDTMC createPositionFDTMC() {
		FDTMC fdtmcPosition = new FDTMC(); 
		fdtmcPosition.setVariableName("sPosition");
		
		//States Creation
		State	init = fdtmcPosition.createState("init"), 
				success = fdtmcPosition.createState("success"),
				fail = fdtmcPosition.createState("fail"),
				source, target, target2;
		
		source = init; 
		target = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "register", "0.999");
		fdtmcPosition.createTransition(source, fail, "register", "0.001");
		
		source = target; 
		target = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "register_return", "0.999");
		fdtmcPosition.createTransition(source, fail, "register_return", "0.001");
		
		source = target; 
		target = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "sendSituation_POS", "0.999");
		fdtmcPosition.createTransition(source, fail, "sendSituation_POS", "0.001");
		
		source = target; 
		target = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "persist", "0.999"); 
		fdtmcPosition.createTransition(source, fail, "persist", "0.001");
		
		//SQlite selection / interface
		source = target; 
		target = fdtmcPosition.createState();
		target2 = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "sqliteSelection", "fSqlite");
		fdtmcPosition.createTransition(source, target2, "sqliteSelection", "1-fSqlite");
		State sucessSqlite = fdtmcPosition.createState(); 
		State failSqlite = fdtmcPosition.createState();
		source = target; 
		fdtmcPosition.createTransition(source, sucessSqlite, "", "rSqlite");
		fdtmcPosition.createTransition(sucessSqlite, target2, "", "1.0");
		fdtmcPosition.createTransition(source, failSqlite, "", "1-rSqlite");
		fdtmcPosition.createTransition(failSqlite, fail, "", "1.0");
		
		//Memory selection / interface
		source = target2; 
		target = fdtmcPosition.createState(); 
		target2 = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "memorySelection", "fMemory");
		fdtmcPosition.createTransition(source, target2, "memorySelection", "1-fMemory");
		State sucessMemory = fdtmcPosition.createState(); 
		State failMemory = fdtmcPosition.createState(); 
		source = target; 
		fdtmcPosition.createTransition(source, sucessMemory, "", "rMemory");
		fdtmcPosition.createTransition(sucessMemory, target2, "", "1.0");
		fdtmcPosition.createTransition(source, failMemory, "", "1-rMemory");
		fdtmcPosition.createTransition(failMemory, fail, "", "1.0");
		
		source = target2; 
		target = fdtmcPosition.createState(); 
		fdtmcPosition.createTransition(source, target, "persistReturn", "0.999");
		fdtmcPosition.createTransition(source, fail, "persistReturn", "0.001");
		
		source = target; 
		target = success; 
		fdtmcPosition.createTransition(source, target, "sendSituation_Position", "0.999");
		fdtmcPosition.createTransition(source, fail, "sendSituation_Position", "0.001");
		
		fdtmcPosition.createTransition(success, success, "", "1.0");
		fdtmcPosition.createTransition(fail, fail, "", "1.0");

		return fdtmcPosition;
	}
	
	
	
	private static FDTMC createSituationFDTMC() {
		FDTMC fdtmcSituation = new FDTMC(); 
		fdtmcSituation.setVariableName("sSituation");
		
		//States Creation
		State s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, 
			  s10, s11, s12, s13, s14, s15, s16, s17;
		s0 = fdtmcSituation.createState("error_situation");
		s1 = fdtmcSituation.createState("init");
		s2 = fdtmcSituation.createState();
		s3 = fdtmcSituation.createState("plsRte_success");
		s4 = fdtmcSituation.createState("plsRte_error");
		s5 = fdtmcSituation.createState();
		s6 = fdtmcSituation.createState();
		s7 = fdtmcSituation.createState("oxy_success");
		s8 = fdtmcSituation.createState("oxy_error");
		s9 = fdtmcSituation.createState();
		s10 = fdtmcSituation.createState();
		s11 = fdtmcSituation.createState("temp_success");
		s12 = fdtmcSituation.createState("temp_error");
		s13 = fdtmcSituation.createState();
		s14 = fdtmcSituation.createState();
		s15 = fdtmcSituation.createState("pos_success");
		s16 = fdtmcSituation.createState("pos_error");
		s17 = fdtmcSituation.createState("success");
		
		//Transitions Creation
		fdtmcSituation.createTransition(s1, s2, "pulseRateSelection", "fPlsRte"); 
		fdtmcSituation.createTransition(s1, s5, "pulseRateSelection", "1-fPlsRte");
		fdtmcSituation.createTransition(s2, s3, "", "rPlsRte");
		fdtmcSituation.createTransition(s2, s4, "", "1-rPlsRte");
		fdtmcSituation.createTransition(s3, s5, "", "1.0");
		fdtmcSituation.createTransition(s4, s0, "", "1.0");
		fdtmcSituation.createTransition(s5, s6, "oxySelection", "fOxy");
		fdtmcSituation.createTransition(s5, s9, "oxySelection", "1-fOxy");
		fdtmcSituation.createTransition(s6, s7, "", "rOxy");
		fdtmcSituation.createTransition(s6, s8, "", "1-rOxy");
		fdtmcSituation.createTransition(s7, s9, "", "1.0");
		fdtmcSituation.createTransition(s8, s0, "", "1.0");
		fdtmcSituation.createTransition(s9, s10, "tempSelection", "fTemp");
		fdtmcSituation.createTransition(s9, s13, "tempSelection", "1-fTemp");
		fdtmcSituation.createTransition(s10, s11, "", "rTemp");
		fdtmcSituation.createTransition(s10, s12, "", "1-rTemp");
		fdtmcSituation.createTransition(s11, s13, "", "1.0");
		fdtmcSituation.createTransition(s12, s0, "", "1.0");
		fdtmcSituation.createTransition(s13, s14, "posSelection", "fPos");
		fdtmcSituation.createTransition(s13, s17, "posSelection", "1-fPos");
		fdtmcSituation.createTransition(s14, s15, "", "rPos");
		fdtmcSituation.createTransition(s14, s16, "", "1-rPos");
		fdtmcSituation.createTransition(s15, s17, "", "1.0");
		fdtmcSituation.createTransition(s16, s0, "", "1.0");
		
		
		return fdtmcSituation;
	}
	


	private static FDTMC createSqliteFDTMC() {
		FDTMC fdtmcSqlite = new FDTMC(); 
		fdtmcSqlite.setVariableName("sSqlite");
		
		State   init =fdtmcSqlite.createState("init"), 
				success = fdtmcSqlite.createState("success"),
				fail = fdtmcSqlite.createState("fail"), 
				source, target;
		source = init; 
		target = fdtmcSqlite.createState(); 
		fdtmcSqlite.createTransition(source, target, "persist", "0.999"); 
		fdtmcSqlite.createTransition(source, fail, "persist", "0.001");
		
		source = target; 
		target = success; 
		fdtmcSqlite.createTransition(source, target, "persist_return", "0.999"); 
		fdtmcSqlite.createTransition(source, fail, "persist_return", "0.001");
		
		fdtmcSqlite.createTransition(success, success, "", "1.0");
		fdtmcSqlite.createTransition(fail, fail, "", "1.0");
		
		return fdtmcSqlite; 
	}
	
	private static FDTMC createMemoryFDTMC() {
		FDTMC fdtmcMemory = new FDTMC(); 
		fdtmcMemory.setVariableName("sMemory");
		
		//States Creation
		State init = fdtmcMemory.createState("init"), 
				fail = fdtmcMemory.createState("error_mem"),
				success = fdtmcMemory.createState("success"), 
				source, target; 
		
		source = init; 
		target = fdtmcMemory.createState(); 
		fdtmcMemory.createTransition(source, target, "persist", "0.999");
		fdtmcMemory.createTransition(source, fail, "persist", "0.001");
		
		source = target; 
		fdtmcMemory.createTransition(source, success, "persistReturn", "0.999");
		fdtmcMemory.createTransition(source, fail, "persistReturn", "0.001");
		
		fdtmcMemory.createTransition(success, success, "", "1.0");
		fdtmcMemory.createTransition(fail, fail, "", "1.0");
		//Transitions creation 
//		fdtmcMem.createTransition(s2, success, "persistReturn", "0.999"); 
//		fdtmcMem.createTransition(s2, fail, "persistReturn", "0.001");
		
		return fdtmcMemory; 	
	}
	
	private static FDTMC createFileFDTMC() {
		FDTMC fdtmcFile = new FDTMC(); 
		fdtmcFile.setVariableName("sFile");

		//States Creation
		State 	init = fdtmcFile.createState("init"), 
				success = fdtmcFile.createState("success"),
				fail = fdtmcFile.createState("fail"), 
				source, target; 
		
		source = init; 
		target = fdtmcFile.createState(); 
		fdtmcFile.createTransition(source, target, "persist", "0.999"); 
		fdtmcFile.createTransition(source, fail, "persist", "0.001"); 
		
		source = target; 
		target = fdtmcFile.createState(); 
		fdtmcFile.createTransition(source, target, "write", "0.999"); 
		fdtmcFile.createTransition(source, fail, "write", "0.001"); 

		source = target; 
		target = success;
		fdtmcFile.createTransition(source, target, "persistReturn", "0.999"); 
		fdtmcFile.createTransition(source, fail, "persistReturn", "0.001");
		fdtmcFile.createTransition(success, success, "", "1.0");
		fdtmcFile.createTransition(fail, fail, "", "1.0");

		
		return fdtmcFile;		
	}
	
	
	private static FDTMC createPulseRateFDTMC() {
		FDTMC fdtmcPulseRate = new FDTMC(); 
		fdtmcPulseRate.setVariableName("sPulseRate");
		
		//States creation
		State s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
			  s10, s11, s12, s13, s14, s15, s16;
		s0 = fdtmcPulseRate.createState("error");
		s1 = fdtmcPulseRate.createState("init"); 
		s2 = fdtmcPulseRate.createState();
		s3 = fdtmcPulseRate.createState();
		s4 = fdtmcPulseRate.createState();
		s5 = fdtmcPulseRate.createState();
		s6 = fdtmcPulseRate.createState();
		s7 = fdtmcPulseRate.createState();
		s8 = fdtmcPulseRate.createState("error_sqlite");
		s9 = fdtmcPulseRate.createState();
		s10 = fdtmcPulseRate.createState();
		s11 = fdtmcPulseRate.createState();
		s12 = fdtmcPulseRate.createState("error_mem");
		s13 = fdtmcPulseRate.createState();
		s14 = fdtmcPulseRate.createState();
		s15 = fdtmcPulseRate.createState();
		s16 = fdtmcPulseRate.createState("success");
		
		//Transition
		fdtmcPulseRate.createTransition(s1, s2, "register", "0.999");
		fdtmcPulseRate.createTransition(s1, s0, "register", "0.001");
		fdtmcPulseRate.createTransition(s2, s3, "registerReturn", "0.999");
		fdtmcPulseRate.createTransition(s2, s0, "registerReturn", "0.001");
		fdtmcPulseRate.createTransition(s3, s4, "sendSituation", "0.999");
		fdtmcPulseRate.createTransition(s3, s0, "sendSituation", "0.001");
		fdtmcPulseRate.createTransition(s4, s5, "persist", "0.999");
		fdtmcPulseRate.createTransition(s4, s0, "persist", "0.001");
		fdtmcPulseRate.createTransition(s5, s6, "fSqliteSelection", "fSqlite");
		fdtmcPulseRate.createTransition(s5, s9, "fSqliteSelection", "1-fSqlite");
		fdtmcPulseRate.createTransition(s6, s7, "", "rSqlite");
		fdtmcPulseRate.createTransition(s6, s8, "", "1-rSqlite");
		fdtmcPulseRate.createTransition(s7, s9, "", "1.0");
		fdtmcPulseRate.createTransition(s8, s0, "", "1.0");
		fdtmcPulseRate.createTransition(s9, s10, "fMemorySelection", "fMemory");
		fdtmcPulseRate.createTransition(s9, s13, "fMemorySelection", "1-fMemory");
		fdtmcPulseRate.createTransition(s10, s11, "", "rMemory");
		fdtmcPulseRate.createTransition(s10, s12, "", "1-rMemory");
		fdtmcPulseRate.createTransition(s11, s13, "", "1.0");
		fdtmcPulseRate.createTransition(s12, s0, "", "1.0");
		fdtmcPulseRate.createTransition(s13, s14, "persistReturn", "0.999");
		fdtmcPulseRate.createTransition(s13, s0, "persistReturn", "0.001");
		fdtmcPulseRate.createTransition(s14, s15, "sendSituation", "0.999");
		fdtmcPulseRate.createTransition(s14, s0, "sendSituation", "0.001");
		fdtmcPulseRate.createTransition(s15, s16, "sendSituationReturn", "0.999");
		fdtmcPulseRate.createTransition(s15, s0, "sendSituationReturn", "0.001");
		
		return fdtmcPulseRate;
	}
	
	
	
	public static void main (String[] args) {
//		FDTMCStub f = new FDTMCStub();
//		System.out.println(f.createFDTMC(Feature.getFeatureByName("File")));
//		System.out.println(f.createFDTMC("Sqlite"));
//		
//		System.out.println(f.createFDTMC("Mem")); 
//		
//		System.out.println(f.createFDTMC("File"));
//		
//		System.out.println(f.createFDTMC("PulseRate"));
//		
//		System.out.println(f.createFDTMC("Situation"));
	}
}
