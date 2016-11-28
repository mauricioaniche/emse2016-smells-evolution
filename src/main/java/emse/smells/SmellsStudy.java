package emse.smells;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.domain.ChangeSet;
import org.repodriller.filter.range.CommitRange;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.SCM;

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
	
	private static Logger log = Logger.getLogger(SmellsStudy.class);
	private String projectName;

	public SmellsStudy(String projectPath, String csvPath, String pmdPath, String linterPath) {
		this.projectName = extractProjectName(projectPath);
		this.projectPath = projectPath;
		this.csvPath = csvPath;
		this.pmdPath = pmdPath;
		this.linterPath = linterPath;
	}

	@Override
	public void execute() {
	
		ClassRepository clazzRepo = new ClassRepository();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(projectPath, true))
			.through(new CommitRange() {
				@Override
				public List<ChangeSet> get(SCM scm) {
					List<ChangeSet> cs = scm.getChangeSets();
					List<ChangeSet> subList = cs.subList(0, cs.size()-500);
					return subList;
				}
			})
//			.through(Commits.since(new GregorianCalendar(2016, Calendar.OCTOBER, 20)))
			.process(new SmellsVisitor(new PMD(pmdPath), new SpringLint(linterPath), clazzRepo))
			.mine();
		
		outputFileList(clazzRepo);
		output(clazzRepo, "pmd");
		output(clazzRepo, "mvc");
		
		serialize(clazzRepo);
	}

	private void outputFileList(ClassRepository clazzRepo) {
		CSVFile writer = new CSVFile(csvPath, projectName + "-files.csv");
		writer.write("project","file","first_seen","first_seen_hash","first_seen_number", "deleted","deleted_hash", "deleted_number");
		for(ClassInfo ci : clazzRepo.getAllClassInfo()) {
			writer.write(
				projectName,
				ci.getFile(),
				ci.getFirstSeen().getTimeInMillis(),
				ci.getFirstSeenHash(),
				ci.getFirstSeenNumber(),
				(ci.getDeletedDate()!=null ? ci.getDeletedDate().getTimeInMillis() : "null"),
				ci.getDeletedHash(),
				ci.getDeletedNumber()
			);
		}
		writer.close();
	}

	private void output(ClassRepository clazzRepo, String type) {
		CSVFile writer = new CSVFile(csvPath, projectName + "-" + type + ".csv");
		writer.write("project","file","smell","started","started_hash","started_number", "lastseen","lastseen_hash","lastseen_number", "alive");
		for(ClassInfo ci : clazzRepo.getAllClassInfo()) {
			for(LiveSmell ls : ci.getAllSmells(type)) {
				writer.write(
					projectName,
					ci.getFile(),
					ls.getName(),
					(ls.getDayStarted()!=null ? ls.getDayStarted().getTimeInMillis() : "null"),
					ls.getFirstSeenHash(),
					ls.getFirstSeenNumber(),
					(ls.getLastDaySeen()!=null ? ls.getLastDaySeen().getTimeInMillis() : "null"),
					ls.getLastHashSeen(),
					ls.getLastSeenNumber(),
					(ls.isAlive() ? "1" : "0")
				);
			}
		}
		writer.close();		
	}

	private void serialize(ClassRepository clazzRepo) {
		try {
	         FileOutputStream fileOut =
	         new FileOutputStream(csvPath + (csvPath.endsWith(File.separator) ? "" : File.separator) + projectName + "-repo.out");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(clazzRepo);
	         out.close();
	         fileOut.close();
	      }catch(IOException ex) {
	    	  log.error(ex);
	      }		
	}
	
	private String extractProjectName(String path) {
		String[] div = path.split(File.separator);
		return div[div.length-1];
	}

}
