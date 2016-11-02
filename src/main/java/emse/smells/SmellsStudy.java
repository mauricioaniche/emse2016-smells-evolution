package emse.smells;

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
	private String pmdPath;
	private String linterPath;

	public SmellsStudy(String projectPath, String csvPath, String pmdPath, String linterPath) {
		this.projectPath = projectPath;
		this.csvPath = csvPath;
		this.pmdPath = pmdPath;
		this.linterPath = linterPath;
	}

	@Override
	public void execute() {
	
		new RepositoryMining()
			.in(GitRepository.singleProject(projectPath))
			.through(Commits.all())
//			.through(Commits.since(new GregorianCalendar(2016, Calendar.JANUARY, 1)))
			.filters(new OnlyInMainBranch(), new OnlyNoMerge())
			.process(new SmellElimination(new PMD(pmdPath), new SpringLint(linterPath)), new CSVFile(csvPath))
			.mine();
	}

}
