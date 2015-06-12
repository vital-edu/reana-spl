package FeatureFamilyBasedAnalysisTool;

public class FDTMCStub {

	public FDTMC createFDTMC(String feature) {
		switch (feature) {
		case "Sqlite":
			return createSqliteFDTMC();
		
		case "Mem": 
			return createMemFDTMC();
			
		case "File":
			return createFileFDTMC();
			
		case "PulseRate":
			return createPulseRateFDTMC();
			
		case "Situation":
			return createSituationFDTMC();
			
		default:
			return null;
		} 
	}
	
	
	private FDTMC createSituationFDTMC() {
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
	


	private FDTMC createSqliteFDTMC() {
		FDTMC fdtmcSqlite = new FDTMC(); 
		fdtmcSqlite.setVariableName("sSqlite");
		
		//States Creation
		State s0, s1, s2, s3;
		s0 = fdtmcSqlite.createState("error_sqlite");
		s1 = fdtmcSqlite.createState("init"); 
		s2 = fdtmcSqlite.createState(); 
		s3 = fdtmcSqlite.createState("success");
		
		//Transitions Creation
		//Transition t0, t1, t2, t3; 
		fdtmcSqlite.createTransition(s1, s2, "persistSqlite", "0.999"); 
		fdtmcSqlite.createTransition(s1, s0, "persistSqlite", "0.001");
		fdtmcSqlite.createTransition(s2, s3, "persistSqliteReturn", "0.999");
		fdtmcSqlite.createTransition(s2, s0, "persistSqliteReturn", "0.001");
		
		return fdtmcSqlite; 
	}
	
	private FDTMC createMemFDTMC() {
		FDTMC fdtmcMem = new FDTMC(); 
		fdtmcMem.setVariableName("sMem");
		
		//States Creation
		State s0, s1, s2, s3; 
		s0 = fdtmcMem.createState("error_mem"); 
		s1 = fdtmcMem.createState("init");
		s2 = fdtmcMem.createState(); 
		s3 = fdtmcMem.createState("success");
		
		//Transitions creation
		fdtmcMem.createTransition(s1, s2, "persist", "0.999");
		fdtmcMem.createTransition(s1, s0, "persist", "0.001"); 
		fdtmcMem.createTransition(s2, s3, "persistReturn", "0.999"); 
		fdtmcMem.createTransition(s2, s0, "persistReturn", "0.001");
		
		return fdtmcMem; 	
	}
	
	private FDTMC createFileFDTMC() {
		FDTMC fdtmcFile = new FDTMC(); 
		fdtmcFile.setVariableName("sFile");
		
		//States Creation
		State s0, s1, s2, s3, s4; 
		s0 = fdtmcFile.createState("error_file");
		s1 = fdtmcFile.createState("init");
		s2 = fdtmcFile.createState(); 
		s3 = fdtmcFile.createState(); 
		s4 = fdtmcFile.createState("success");
		
		//Transitions creation
		fdtmcFile.createTransition(s1, s2, "persist", "0.999");
		fdtmcFile.createTransition(s1, s0, "persist", "0.001");
		fdtmcFile.createTransition(s2, s3, "write", "0.999");
		fdtmcFile.createTransition(s2, s0, "write", "0.001");
		fdtmcFile.createTransition(s3, s4, "persistReturn", "0.999");
		fdtmcFile.createTransition(s3, s0, "persistReturn", "0.001");
		
		return fdtmcFile;		
	}
	
	
	private FDTMC createPulseRateFDTMC() {
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
		fdtmcPulseRate.createTransition(s9, s10, "fMemSelection", "fMem");
		fdtmcPulseRate.createTransition(s9, s13, "fMemSelection", "1-fMem");
		fdtmcPulseRate.createTransition(s10, s11, "", "rMem");
		fdtmcPulseRate.createTransition(s10, s12, "", "1-rMem");
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
		FDTMCStub f = new FDTMCStub();
		System.out.println(f.createFDTMC("Sqlite"));
		
		System.out.println(f.createFDTMC("Mem")); 
		
		System.out.println(f.createFDTMC("File"));
		
		System.out.println(f.createFDTMC("PulseRate"));
		
		System.out.println(f.createFDTMC("Situation"));
	}
}
