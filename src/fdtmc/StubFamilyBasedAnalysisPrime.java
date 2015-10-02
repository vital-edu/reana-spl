package fdtmc;

public class StubFamilyBasedAnalysisPrime {

	private static String fdtmcSqlite = "sSqlite=0(init) --- persist / 0.999 ---> sSqlite=3" + '\n' +
			"sSqlite=0(init) --- persist / 0.001 ---> sSqlite=2(fail)" + '\n' + 
			"sSqlite=1(success) ---  / 1.0 ---> sSqlite=1(success)" + '\n' + 
			"sSqlite=2(fail) ---  / 1.0 ---> sSqlite=2(fail)" + '\n' + 
			"sSqlite=3 --- persist_return / 0.999 ---> sSqlite=1(success)" + '\n' + 
			"sSqlite=3 --- persist_return / 0.001 ---> sSqlite=2(fail)" + '\n';
	
	private static String fdtmcMemory = "sMemory=0(init) --- persist / 0.999 ---> sMemory=3" + '\n' + 
			"sMemory=0(init) --- persist / 0.001 ---> sMemory=1(error_mem)" + '\n' + 
			"sMemory=1(error_mem) ---  / 1.0 ---> sMemory=1(error_mem)" + '\n' + 
			"sMemory=2(success) ---  / 1.0 ---> sMemory=2(success)" + '\n' + 
			"sMemory=3 --- persistReturn / 0.999 ---> sMemory=2(success)" + '\n' + 
			"sMemory=3 --- persistReturn / 0.001 ---> sMemory=1(error_mem)" + '\n';
;
	
	private static String fdtmcFile = "sFile=0(init) --- persist / 0.999 ---> sFile=3" + '\n' +
			"sFile=0(init) --- persist / 0.001 ---> sFile=2(fail)" + '\n' + 
			"sFile=1(success) ---  / 1.0 ---> sFile=1(success)" + '\n' + 
			"sFile=2(fail) ---  / 1.0 ---> sFile=2(fail)" + '\n' + 
			"sFile=3 --- write / 0.999 ---> sFile=4" + '\n' +
			"sFile=3 --- write / 0.001 ---> sFile=2(fail)" + '\n' +
			"sFile=4 --- persistReturn / 0.999 ---> sFile=1(success)" + '\n' +
			"sFile=4 --- persistReturn / 0.001 ---> sFile=2(fail)" + '\n';
	
	private static String fdtmcOxygenation = "sOxygenation=0(init) --- register / 0.999 ---> sOxygenation=3" + '\n' +
			"sOxygenation=0(init) --- register / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=1(success) ---  / 1.0 ---> sOxygenation=1(success)" + '\n' + 
			"sOxygenation=2(fail) ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=3 --- register_return / 0.999 ---> sOxygenation=4" + '\n' + 
			"sOxygenation=3 --- register_return / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=4 --- sendSituation_SPO2 / 0.999 ---> sOxygenation=5" + '\n' + 
			"sOxygenation=4 --- sendSituation_SPO2 / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=5 --- persist / 0.999 ---> sOxygenation=6" + '\n' + 
			"sOxygenation=5 --- persist / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=6 --- sqliteSelection / fSqlite ---> sOxygenation=7" + '\n' + 
			"sOxygenation=6 --- sqliteSelection / 1-fSqlite ---> sOxygenation=8" + '\n' + 
			"sOxygenation=7 ---  / rSqlite ---> sOxygenation=9" + '\n' + 
			"sOxygenation=7 ---  / 1-rSqlite ---> sOxygenation=10" + '\n' + 
			"sOxygenation=8 --- memorySelection / fMemory ---> sOxygenation=11" + '\n' + 
			"sOxygenation=8 --- memorySelection / 1-fMemory ---> sOxygenation=12" + '\n' + 
			"sOxygenation=9 ---  / 1.0 ---> sOxygenation=8" + '\n' + 
			"sOxygenation=10 ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=11 ---  / rMemory ---> sOxygenation=13" + '\n' + 
			"sOxygenation=11 ---  / 1-rMemory ---> sOxygenation=14" + '\n' + 
			"sOxygenation=12 --- persistReturn / 0.999 ---> sOxygenation=15" + '\n' + 
			"sOxygenation=12 --- persistReturn / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=13 ---  / 1.0 ---> sOxygenation=12" + '\n' + 
			"sOxygenation=14 ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=15 --- sendSituation_Oxygenation / 0.999 ---> sOxygenation=1(success)" + '\n' + 
			"sOxygenation=15 --- sendSituation_Oxygenation / 0.001 ---> sOxygenation=2(fail)" + '\n'; 

	private static String fdtmcPosition = "sPosition=0(init) --- register / 0.999 ---> sPosition=3" + '\n' + 
			"sPosition=0(init) --- register / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=1(success) ---  / 1.0 ---> sPosition=1(success)" + '\n' + 
			"sPosition=2(fail) ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=3 --- register_return / 0.999 ---> sPosition=4" + '\n' + 
			"sPosition=3 --- register_return / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=4 --- sendSituation_POS / 0.999 ---> sPosition=5" + '\n' + 
			"sPosition=4 --- sendSituation_POS / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=5 --- persist / 0.999 ---> sPosition=6" + '\n' + 
			"sPosition=5 --- persist / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=6 --- sqliteSelection / fSqlite ---> sPosition=7" + '\n' + 
			"sPosition=6 --- sqliteSelection / 1-fSqlite ---> sPosition=8" + '\n' + 
			"sPosition=7 ---  / rSqlite ---> sPosition=9" + '\n' + 
			"sPosition=7 ---  / 1-rSqlite ---> sPosition=10" + '\n' + 
			"sPosition=8 --- memorySelection / fMemory ---> sPosition=11" + '\n' + 
			"sPosition=8 --- memorySelection / 1-fMemory ---> sPosition=12" + '\n' + 
			"sPosition=9 ---  / 1.0 ---> sPosition=8" + '\n' + 
			"sPosition=10 ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=11 ---  / rMemory ---> sPosition=13" + '\n' + 
			"sPosition=11 ---  / 1-rMemory ---> sPosition=14" + '\n' + 
			"sPosition=12 --- persistReturn / 0.999 ---> sPosition=15" + '\n' + 
			"sPosition=12 --- persistReturn / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=13 ---  / 1.0 ---> sPosition=12" + '\n' + 
			"sPosition=14 ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=15 --- sendSituation_Position / 0.999 ---> sPosition=1(success)" + '\n' + 
			"sPosition=15 --- sendSituation_Position / 0.001 ---> sPosition=2(fail)" + '\n';
	
	
	public static String evaluateFDTMC(FDTMC fdtmc) {
		if (fdtmc.toString().equals(fdtmcSqlite))
			return "998001/1000000";
		else if (fdtmc.toString().equals(fdtmcMemory))
			return "998001/1000000";
		else if (fdtmc.toString().equals(fdtmcFile))
			return "997002999/1000000000";
		else if (fdtmc.toString().equals(fdtmcOxygenation))
			return "(3968112769300741146947010999*rMemory*rSqlite -1985048909104923034991001000000*rMemory -1985048909104923034991001000000*rSqlite +993020965034979006999000000000000) / (1000000000000000000000000000000000)";
		else if (fdtmc.toString().equals(fdtmcPosition))
			return "(3968112769300741146947010999*rMemory*rSqlite -1985048909104923034991001000000*rMemory -1985048909104923034991001000000*rSqlite +993020965034979006999000000000000) / (1000000000000000000000000000000000)";
		else System.out.println("entrei no else");
		return null;
//		else return null;
	}

}
