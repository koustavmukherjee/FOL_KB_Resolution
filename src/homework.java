

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fol.kb.KnowledgeBase;

public class homework {
	public static int QUERY_SIZE;
	public static int KB_SIZE;
	public static List<String> QUERIES = new ArrayList<>();
	public static List<String> KB_CLAUSES = new ArrayList<>();
	public static List<String> RESULTS = new ArrayList<>();
	
	public static void main(String[] args) {
		readInput();
		KnowledgeBase kb = new KnowledgeBase();
		for(String clause : KB_CLAUSES)
			kb.tell(clause);
		for(String query : QUERIES)
			RESULTS.add(String.valueOf(kb.ask(query)).toUpperCase());
		writeOutput(RESULTS);
	}
	
	public static void writeOutput(List<String> results) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter("./output.txt");
			bw = new BufferedWriter(fw);
			for (int i = 0; i < results.size(); i++) {
				bw.write(results.get(i));
				if (i < results.size() - 1)
					bw.write(System.getProperty("line.separator"));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void readInput() {
		try {
			File f = new File("./input.txt");
			BufferedReader br = new BufferedReader(new FileReader(f));
			QUERY_SIZE = Integer.parseInt(br.readLine().trim());
			for (int i = 0; i < QUERY_SIZE; i++) {
				QUERIES.add(br.readLine().trim());
			}
			KB_SIZE = Integer.parseInt(br.readLine().trim());
			for (int i = 0; i < KB_SIZE; i++) {
				KB_CLAUSES.add(br.readLine().trim());
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}