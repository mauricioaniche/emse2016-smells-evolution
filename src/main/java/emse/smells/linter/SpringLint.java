package emse.smells.linter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import emse.smells.Report;
import emse.smells.Smell;
import emse.smells.util.Cmd;

public class SpringLint {

	private String linterPath;
	private static Logger log = Logger.getLogger(SpringLint.class);

	public SpringLint(String linterPath) {
		this.linterPath = linterPath;
	}

	public Report run(String path) {
		try {
			Files.deleteIfExists(new File("smells.csv").toPath());
			Cmd cmd = new Cmd(linterPath);
			cmd.execute("java -jar springlint.jar -p " + path + " -otype csv -o .");
			String response = new String(Files.readAllBytes(Paths.get("smells.csv")), StandardCharsets.UTF_8);
			CSVParser parsedCsv = CSVParser.parse(response, CSVFormat.DEFAULT);
			
			Report report = new Report();
			
			int count = 0;
			for (CSVRecord csvRecord : parsedCsv) {
				if(count++ == 0) continue;
				String file = csvRecord.get(0);
				String smell = csvRecord.get(3);
				
				report.add(new Smell(file, smell));
			}
			
			return report;
		} catch (IOException e) {
			log.error("error in pmd", e);
			return new Report();
		}
	}

}
