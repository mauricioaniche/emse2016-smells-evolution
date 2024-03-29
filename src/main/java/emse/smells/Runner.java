package emse.smells;

import org.repodriller.RepoDriller;

public class Runner {

	public static void main(String[] args) {
		
		if(args.length != 2) {
			System.out.println("You should provide <project path> <output path>");
			System.exit(-1);
		}
		
		String projectPath = args[0];
		String csvPath = args[1];
		String pmdPath = System.getenv("EMSE_PMD_PATH");
		String linterPath = System.getenv("EMSE_SPRINGLINT_PATH");

//		String projectPath = "/Users/mauricioaniche/workspace/SSP/";
//		String csvPath = "/Users/mauricioaniche/Desktop/";
//		String pmdPath = "/Users/mauricioaniche/ferramentas/pmd-bin-5.4.1/bin";
//		String linterPath = "/Users/mauricioaniche/ferramentas/springlint/";
		
		new RepoDriller().start(new SmellsStudy(projectPath, csvPath, pmdPath, linterPath));
	}
	
}
