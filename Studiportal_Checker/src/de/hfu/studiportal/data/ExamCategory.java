package de.hfu.studiportal.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamCategory implements Serializable {
	
	private static final long serialVersionUID = -5178814560848378523L;
	private List<Exam> mensaList;
	private String categoryName;
	
	public ExamCategory(String categoryName) {
		this.categoryName = categoryName;
		this.mensaList = new ArrayList<>();
		
	}
	
	public String getCategoryName() {
		return this.categoryName;
		
	}
	
	public void setCategoryName(String newName) {
		this.categoryName = newName;
		
	}
	
	public void addExam(Exam e) {
		this.mensaList.add(e);
		
	}
	
	public void removeExam(Exam e) {
		this.mensaList.remove(e);
		
	}
	
	public void removeExam(int index) {
		this.mensaList.remove(index);
		
	}
	
	public int getExamCount() {
		return this.mensaList.size();
		
	}
	
	public Exam getExam(int index) {
		return this.mensaList.get(index);
		
	}
}
