package de.hfu.studiportal.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamCategory implements Serializable {
	
	private static final long serialVersionUID = -5178814560848378523L;
	private List<Exam> examList;
	private String categoryName;
	
	public ExamCategory(String categoryName) {
		this.setCategoryName(categoryName);
		this.examList = new ArrayList<Exam>();
		
	}
	
	public String getCategoryName() {
		return this.categoryName;
		
	}
	
	public void setCategoryName(String newName) {
		//Replace long terms with short ones to keep the titles short.
		//Replacing only parts of the title will reserve the meaning (even with unknown titles)
		this.categoryName = newName.replace(":", "").replace("*", "")
				.replace("Module/Teilmodule", "Module").replace("(ECTS) ", "")
				.replace("Bestandene Module", "Bestanden").trim();
		
	}
	
	public void addExam(Exam e) {
		this.examList.add(e);
		
	}
	
	public void removeExam(Exam e) {
		this.examList.remove(e);
		
	}
	
	public void removeExam(int index) {
		this.examList.remove(index);
		
	}
	
	public int getExamCount() {
		return this.examList.size();
		
	}
	
	public Exam getExam(int index) {
		return this.examList.get(index);
		
	}
	
	public List<Exam> getAllExams() {
		return new ArrayList<Exam>(this.examList);
		
	}
}
