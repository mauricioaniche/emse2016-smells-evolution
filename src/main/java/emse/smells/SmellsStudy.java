package emse.smells;

import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.commit.OnlyInMainBranch;
import org.repodriller.filter.commit.OnlyNoMerge;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRepository;

import emse.smells.linter.PMD;
import emse.smells.linter.SpringLint;

public class SmellsStudy implements Study {

	private String projectPath;
	private String csvPath;

	public SmellsStudy(String projectPath, String csvPath) {
		this.projectPath = projectPath;
		this.csvPath = csvPath;
	}

	public static void main(String[] args) {
		new RepoDriller().start(new SmellsStudy(args[0], args[1]));
	}
	
	@Override
	public void execute() {
	
		String pmdPath = System.getenv("EMSE_PMD_PATH");
		String linterPath = System.getenv("EMSE_SPRINGLINT_PATH");
		
		new RepositoryMining()
			.in(GitRepository.allProjectsIn(projectPath))
			.through(Commits.all())
			.filters(new OnlyInMainBranch(), new OnlyNoMerge())
			.process(new SmellElimination(new PMD(pmdPath), new SpringLint(linterPath)), new CSVFile(csvPath))
			.mine();
	}

}
