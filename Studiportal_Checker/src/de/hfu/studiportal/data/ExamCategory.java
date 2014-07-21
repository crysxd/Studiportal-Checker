package de.hfu.studiportal.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamCategory implements Serializable {
	
	private static final long serialVersionUID = -5178814560848378523L;
	private List<Exam> examList;
	private String categoryName;
	
	public ExamCategory(String categoryName) {
		this.categoryName = categoryName;
		this.examList = new ArrayList<>();
		
	}
	
	public String getCategoryName() {
		return this.categoryName;
		
	}
	
	public void setCategoryName(String newName) {
		this.categoryName = newName;
		
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
}
