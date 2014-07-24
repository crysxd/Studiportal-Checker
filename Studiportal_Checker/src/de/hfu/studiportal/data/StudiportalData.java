package de.hfu.studiportal.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.SharedPreferences;
import android.util.Base64;

public class StudiportalData implements Serializable {

	private static final long serialVersionUID = 5635830112259256921L;
	private List<ExamCategory> categoryList = new ArrayList<ExamCategory>();

	public static StudiportalData loadFromSharedPreferences(SharedPreferences sp, String key) throws Exception {
		byte[] data = Base64.decode(sp.getString(key, "").getBytes(), Base64.DEFAULT);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		StudiportalData sd = (StudiportalData) ois.readObject();
		ois.close();
		
		return sd; 
	}
	
	public StudiportalData() {
		//Empty constructor needed to deserialize

	}

	public StudiportalData(String htmlTable) {
		//Create Jsoup document
		Document table = Jsoup.parse(htmlTable);

		//Parse the table
		this.parseTable(table);

	}
	
	public List<Exam> findChangedExams(SharedPreferences sp, String key) throws Exception {
		try {
			return this.findChangedExams(StudiportalData.loadFromSharedPreferences(sp, key));

		} catch(Exception e) {
			return new ArrayList<Exam>();
			
		}
	}
	
	public List<Exam> findChangedExams(StudiportalData otherInstance) {
		//create empty list
		List<Exam> changed = new ArrayList<Exam>();
		
		//Get all my exams
		List<Exam> myExams = this.getAllExams();
		
		//Iterate over myexasm and compare to same ids
		for(Exam e : myExams) {
			//If the Exam is nota seperator
			if(!(e instanceof Seperator)) {
				//Find the equivalent exam
				Exam other = otherInstance.findExam(e.getId());
				
				//If there is a other one and it was changed, add it
				if(other != null && other.getGrade() != null && 
						!other.getGrade().equals(e.getGrade()) && !doesListContainSubject(e.getName(), changed)) {
					changed.add(e);
				}
			}
		}
		
		//return all changed
		return changed;
	}
	
	public ExamCategory searchExams(String query) {
		List<Exam> all = this.getAllExams();
		ExamCategory result = new ExamCategory("Result");
		
		if(query.length() == 0)
			return result;
		
		query = query.toLowerCase(Locale.getDefault());
		
		for(Exam e : all) {
			if(e.getName().toLowerCase(Locale.getDefault()).contains(query))
				result.addExam(e);
			
		}
		
		return result;
		
	}
	
	private boolean doesListContainSubject(String examName, List<Exam> list) {
		for(Exam e : list) {
			if(e.getName().equals(examName)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public void save(SharedPreferences sp, String key) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.close();
		
		sp.edit().putString(key, Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)).apply();
	}

	private void parseTable(Document table) {
		//Get all rows
		Elements rows = table.getElementsByTag("tr");

		//var for current category
		ExamCategory current = new ExamCategory("Unmatched");

		//Iterate over rows
		for (Element row : rows) {
			try {
				//If the rows contains a th element -> New category
				int thCount = row.getElementsByTag("th").size();
				if(thCount > 0) {
					//More than 1x th? Must be the real header row -> skip
					if(thCount > 1) 
						continue;
					
					//Just one -> create new category
					current = this.createExamCategory(row, current);
					this.addCategory(current);

				//Normal row, extract exam
				} else {
					current.addExam(this.createExam(row, current));

				}
			} catch(Exception e) {
				//Do nothing

			}
		}
	}
	
	private String trimString(String s) {
		return s.replace(String.valueOf((char) 160), " ").trim();
	}

	private Exam createExam(Element row, ExamCategory category) {
		//Get all columns
		Elements cols = row.getElementsByTag("td");
		
		//Create Exam
		Exam e = new Exam(this.trimString(cols.get(0).text()));
		
		//Extract data
		int offset = 0;
		for(int i=0; i<cols.size(); i++) {
			//Save current col
			Element col = cols.get(i);
			
			//If the col contains colspan attribute, skip this and all spanned columns
			String colspan = col.attr("colspan");
			if(colspan != null && colspan.length() > 0) {
				int colspanInt = Integer.valueOf(col.attr("colspan"));
				//Add the colspan to i (substract -1 because i++ on next iteration)
				offset += colspanInt - 1;	
				continue;
				
			}
			
			//Get text and remove leading and trailing whitespaces and espacially the #160
			String text = this.trimString(col.text());
			
			//Swicth case for every col
			switch(i+offset) {
			case 1: e.setName(text); break;
			case 2: e.setBonus(text); break;
			case 3: e.setMalus(text); break;
			case 4: e.setECTS(text); break;
			case 5: e.setSWS(text); break;
			case 6: e.setSemester(text); break;
			case 7: e.setKind(text); break;
			case 8: e.setTryCount(text); break;
			case 9: e.setGrade(text); break;
			case 10: e.setState(text); break;
			case 11: e.setComment(text); break;
			case 12: e.setResignation(text); break;
			case 13: e.setNote(text); break;
			}
		}
		
		if(e.getName().equals("Formale Sprachen")) {
			e.setGrade("5.0");
			e.setState("NB");
		}

		return e;
	}

	private ExamCategory createExamCategory(Element row, ExamCategory current) throws Exception {
		//Extract name
		String name = row.text();
		
		//If no name is set, skip this category, it's just a gap row (represented by Seperator)
		if(name.length() == 0) {
			current.addExam(new Seperator());
			throw new RuntimeException();
			
		}
		
		//Create ExamCategory
		return new ExamCategory(name);
		
	}
	
	public Exam findExam(int id) {
		for(ExamCategory c : this.categoryList) {
			for(Exam e : c.getAllExams()) {
				if(e.getId() == id)
					return e;
				
			}
		}
		
		return null;
	}
	
	public List<Exam> getAllExams() {
		///create empty list
		List<Exam> exams = new ArrayList<Exam>();
		
		//Iterate over all categories
		for(ExamCategory c : this.categoryList) {
			exams.addAll(c.getAllExams());
			
		}
		
		return exams;
	}

	public void addCategory(ExamCategory e) {
		this.categoryList.add(e);

	}

	public void removeCategory(ExamCategory e) {
		this.categoryList.remove(e);

	}

	public void removeCategory(int index) {
		this.categoryList.remove(index);

	}

	public int getCategoryCount() {
		return this.categoryList.size();

	}

	public ExamCategory getCategory(int index) {
		return this.categoryList.get(index);

	}
}
