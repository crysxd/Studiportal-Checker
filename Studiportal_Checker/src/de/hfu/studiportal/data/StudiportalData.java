package de.hfu.studiportal.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.SharedPreferences;
import android.util.Base64;

public class StudiportalData implements Serializable {

	private static final long serialVersionUID = 5635830112259256921L;
	private List<ExamCategory> categoryList = new ArrayList<>();

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
		return this.findChangedExams(StudiportalData.loadFromSharedPreferences(sp, key));
		
	}
	
	public List<Exam> findChangedExams(StudiportalData otherInstance) {
		//create empty list
		List<Exam> changed = new ArrayList<>();
		
		//Get all my exams
		List<Exam> myExams = this.getAllExams();
		
		//Iterate over myexasm and compare to same ids
		for(Exam e : myExams) {
			//If the Exam is nota seperator
			if(!(e instanceof Seperator)) {
				//Find the equivalent exam
				Exam other = otherInstance.findExam(e.getId());
				
				//If there is a other one and it was changed, add it
				if(other != null && other.getGrade() != null && !other.getGrade().equals(e.getGrade())) {
					changed.add(e);
				}
			}
		}
		
		//return all changed
		return changed;
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

	private Exam createExam(Element row, ExamCategory category) {
		//Get all columns
		Elements cols = row.getElementsByTag("td");
		
		//Create Exam
		Exam e = new Exam(cols.get(0).text());
		
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
			
			//Swicth case for every col
			switch(i+offset) {
			case 1: e.setName(col.text()); break;
			case 2: e.setBonus(col.text()); break;
			case 3: e.setMalus(col.text()); break;
			case 4: e.setECTS(col.text()); break;
			case 5: e.setSWS(col.text()); break;
			case 6: e.setSemester(col.text()); break;
			case 7: e.setKind(col.text()); break;
			case 8: e.setTryCount(col.text()); break;
			case 9: e.setGrade(col.text()); break;
			case 10: e.setState(col.text()); break;
			case 11: e.setComment(col.text()); break;
			case 12: e.setResignation(col.text()); break;
			case 13: e.setNote(col.text()); break;
			}
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
		List<Exam> exams = new ArrayList<>();
		
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
