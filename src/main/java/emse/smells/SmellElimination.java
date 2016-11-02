package emse.smells;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;
import org.repodriller.util.FileUtils;

import emse.smells.linter.PMD;
import emse.smells.linter.SpringLint;

public class SmellElimination implements CommitVisitor {

	private static Logger log = Logger.getLogger(SmellElimination.class);
	
	private PMD pmd;
	private SpringLint linter;

	private int commitCount;

	public SmellElimination(PMD pmd, SpringLint linter) {
		this.pmd = pmd;
		this.linter = linter;
		this.commitCount = 1;
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		try {

			repo.getScm().checkout(commit.getHash());
			List<File> allFiles = FileUtils.getAllFilesInPath(repo.getPath());
			Report pmdReport = pmd.run(repo.getPath());
			Report linterReport = linter.run(repo.getPath());

			for(File file : allFiles) {
				String filePath = file.getCanonicalPath();
				boolean classIsNotSmelly = !pmdReport.contains(filePath) && linterReport.contains(filePath);
				
				if(classIsNotSmelly) {
					print(commit, writer, filePath, "no");
				} else {
					List<Smell> allSmells = new ArrayList<>(pmdReport.smellsFor(filePath));
					allSmells.addAll(linterReport.smellsFor(filePath));
					
					allSmells.stream().forEach(x -> print(commit, writer, filePath, x.getSmell()));
				}
			}
	
		} catch(Exception e) {
			log.error("error in " + commit.getHash(), e);
		} finally {
			commitCount++;
			repo.getScm().reset();
		}
	}

	private void print(Commit commit, PersistenceMechanism writer, String filePath, String toPrint) {
		writer.write(
			commit.getHash(),
			commitCount,
			new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(commit.getDate().getTime()),
			commit.getDate().getTimeInMillis(),
			filePath,
			toPrint
		);
	}
	
	@Override
	public String name() {
		return "smells-elimination";
	}

}
