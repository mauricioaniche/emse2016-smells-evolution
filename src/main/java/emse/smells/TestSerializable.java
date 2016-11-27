package emse.smells;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import emse.smells.db.ClassRepository;

public class TestSerializable {

	public static void main(String[] args) {
		
		ClassRepository repo = null;
		
		try {
	         FileInputStream fileIn = new FileInputStream("/Users/mauricioaniche/Desktop/emse-data/a.out");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         repo = (ClassRepository) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i) {
	         i.printStackTrace();
	         return;
	      }catch(ClassNotFoundException c) {
	         System.out.println("Employee class not found");
	         c.printStackTrace();
	         return;
	      }
		
		System.out.println(repo.getAllClassInfo());
	}
}
