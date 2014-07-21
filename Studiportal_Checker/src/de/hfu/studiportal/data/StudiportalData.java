package de.hfu.studiportal.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudiportalData implements Serializable {

	private static final long serialVersionUID = 5635830112259256921L;
	private List<ExamCategory> categoryList = new ArrayList<>();
	
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
