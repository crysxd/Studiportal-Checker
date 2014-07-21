package de.hfu.studiportal.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Exam {

	@Attribute(name="id")
	private int id;
	@Element
	private String examNo;
	@Element
	private String name;
	@Element
	private String bonus;
	@Element
	private String malus;
	@Element
	private String semester;
	@Element
	private String kind;
	@Element
	private String grade;
	@Element
	private String state;
	@Element
	private String comment;
	@Element
	private String resignation;
	@Element
	private String note;
	
	public Exam(String examNo) {
		this.setExamNo(examNo);
		
	}

	public int getId() {
		return id;

	}

	public String getExamNo() {
		return examNo;

	}

	public String getName() {
		return name;

	}

	public String getBonus() {
		return bonus;

	}

	public String getMalus() {
		return malus;

	}

	public String getSemester() {
		return semester;

	}

	public String getKind() {
		return kind;

	}

	public String getGrade() {
		return grade;

	}

	public String getState() {
		return state;

	}

	public String getComment() {
		return comment;

	}

	public String getResignation() {
		return resignation;

	}

	public String getNote() {
		return note;

	}
	
	public Exam setExamNo(String examNo) {
		this.examNo = examNo;
		this.id = examNo.hashCode();
		return this;
		
	}

	public Exam setName(String name) {
		this.name = name;
		return this;
		
	}

	public Exam setBonus(String bonus) {
		this.bonus = bonus;
		return this;
		
	}

	public Exam setMalus(String malus) {
		this.malus = malus;
		return this;
		
	}

	public Exam setSemester(String semester) {
		this.semester = semester;
		return this;
		
	}

	public Exam setKind(String kind) {
		this.kind = kind;
		return this;
		
	}

	public Exam setGrade(String grade) {
		this.grade = grade;
		return this;
		
	}

	public Exam setState(String state) {
		this.state = state;
		return this;
		
	}
	public Exam setComment(String comment) {
		this.comment = comment;
		return this;
		
	}

	public Exam setResignation(String resignation) {
		this.resignation = resignation;
		return this;
		
	}

	public Exam setNote(String note) {
		this.note = note;
		return this;
		
	}
}
