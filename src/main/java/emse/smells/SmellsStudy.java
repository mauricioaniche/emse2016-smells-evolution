package emse.smells;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.commit.OnlyInMainBranch;
import org.repodriller.filter.commit.OnlyNoMerge;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRepository;

import emse.smells.db.ClassInfo;
import emse.smells.db.ClassRepository;
import emse.smells.db.LiveSmell;
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
	
		ClassRepository clazzRepo = new ClassRepository();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(projectPath))
//			.through(Commits.all())
			.through(Commits.since(new GregorianCalendar(2016, Calendar.JUNE, 1)))
			.filters(new OnlyInMainBranch(), new OnlyNoMerge())
			.process(new SmellsVisitor(new PMD(pmdPath), new SpringLint(linterPath), clazzRepo))
			.mine();
		
		CSVFile writer = new CSVFile(csvPath + "files.csv");
		for(ClassInfo ci : clazzRepo.getAllClassInfo()) {
			writer.write(
				projectPath,
				ci.getFile(),
				ci.getFirstSeen().getTimeInMillis(),
				ci.getFirstSeenHash(),
				(ci.getDeletedDate()!=null ? ci.getDeletedDate().getTimeInMillis() : "null"),
				ci.getDeletedHash()
			);
		}
		writer.close();

		writer = new CSVFile(csvPath + "pmd.csv");
		for(ClassInfo ci : clazzRepo.getAllClassInfo()) {
			for(LiveSmell ls : ci.getAllSmells("pmd")) {
				writer.write(
					projectPath,
					ci.getFile(),
					ls.getName(),
					(ls.getDayStarted()!=null ? ls.getDayStarted().getTimeInMillis() : "null"),
					ls.getFirstSeenHash(),
					(ls.getLastDaySeen()!=null ? ls.getLastDaySeen().getTimeInMillis() : "null"),
					ls.isAlive()
				);
			}
		}
		writer.close();

		writer = new CSVFile(csvPath + "mvc.csv");
		for(ClassInfo ci : clazzRepo.getAllClassInfo()) {
			for(LiveSmell ls : ci.getAllSmells("mvc")) {
				writer.write(
						projectPath,
						ci.getFile(),
						ls.getName(),
						(ls.getDayStarted()!=null ? ls.getDayStarted().getTimeInMillis() : "null"),
						ls.getFirstSeenHash(),
						(ls.getLastDaySeen()!=null ? ls.getLastDaySeen().getTimeInMillis() : "null"),
						ls.isAlive()
						);
			}
		}
		writer.close();
	}

}
