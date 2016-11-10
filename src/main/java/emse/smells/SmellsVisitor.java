package emse.smells;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;
import org.repodriller.util.FileUtils;

import emse.smells.db.ClassInfo;
import emse.smells.db.ClassRepository;
import emse.smells.db.LiveSmell;
import emse.smells.linter.PMD;
import emse.smells.linter.SpringLint;

public class SmellsVisitor implements CommitVisitor {

	private static Logger log = Logger.getLogger(SmellsVisitor.class);
	
	private PMD pmd;
	private SpringLint linter;

	private ClassRepository clazzRepo;


	public SmellsVisitor(PMD pmd, SpringLint linter, ClassRepository clazzRepo) {
		this.pmd = pmd;
		this.linter = linter;
		this.clazzRepo = clazzRepo;
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		for(Modification m : commit.getModifications()) {
			if(m.getType().equals(ModificationType.RENAME)) {
				log.info("Rename: " + m.getOldPath() + " -> " + m.getNewPath());
				clazzRepo.rename(m.getOldPath(), m.getNewPath());
			}
			else if(m.getType().equals(ModificationType.DELETE)) {
				log.info("Deleted: " + m.getOldPath());
				clazzRepo.delete(m.getOldPath(), commit.getDate(), commit.getHash());
			}
		}
		
		try {

			repo.getScm().checkout(commit.getHash());
			try {
				List<File> allFiles = onlyJava(FileUtils.getAllFilesInPath(repo.getPath()));
				log.info("Identified " + allFiles.size() + " java files");
				
				Report pmdReport = pmd.run(repo.getPath());
				log.info("PMD smells: " + pmdReport.getSmells());
				Report linterReport = linter.run(repo.getPath());
				log.info("Linter smells: " + linterReport.getSmells());
				
				for(File file : allFiles) {
					String filePath = file.getCanonicalPath();
					
					if(!clazzRepo.contains(filePath)) {
						clazzRepo.add(new ClassInfo(filePath, commit.getDate(), commit.getHash()));
					}
					
					updateSmells(commit, pmdReport, filePath, "pmd");
					updateSmells(commit, linterReport, filePath, "mvc");
					
				}
				
			} catch(Exception e) {
				log.error("error in:" + commit.getHash(), e);
			} 
		}catch(Exception e) {
			log.error("not able to check out " + commit.getHash(), e);
		} finally {
			repo.getScm().reset();
		}
	}

	private void updateSmells(Commit commit, Report report, String canonicalFilePath, String type) throws IOException {
		
		
		ClassInfo clazzInfo = clazzRepo.get(canonicalFilePath);
		boolean classIsCompletelyClean = !report.contains(canonicalFilePath);
		
		if(classIsCompletelyClean) {
			clazzInfo.clean(commit.getDate(), commit.getHash(), type);
		}
		else {
			List<LiveSmell> smellsSoFar = clazzInfo.getAliveSmells(type);
			List<Smell> smellsInThisVersion = report.smellsFor(canonicalFilePath);
			
			// remove smells that were removed in this version.
			for(LiveSmell ls : smellsSoFar) {
				boolean smellStillExist = smellsInThisVersion.stream().anyMatch(x -> x.getSmell().equals(ls.getName()));
				if(!smellStillExist) {
					log.info("Smell " + ls.getName() + "(" + type + ") still exist in " + canonicalFilePath);
					clazzInfo.remove(commit.getDate(), commit.getHash(), type, ls.getName());
				}
			}
			
			// update list of current/new smells
			smellsInThisVersion.stream().forEach(x -> {
				log.info("Updating " + x.getSmell() + "(" + type + ") in " + canonicalFilePath);
				clazzInfo.current(commit.getDate(), commit.getHash(), type, x.getSmell());
			});
		}
	}

	private List<File> onlyJava(List<File> allFilesInPath) {
		return allFilesInPath.stream()
				.filter(x -> {
					try {
						return x.getCanonicalPath().endsWith(".java");
					} catch (IOException e) {
						return false;
					}
				})
				.collect(Collectors.toList());
	}

	@Override
	public String name() {
		return "smells";
	}

}
