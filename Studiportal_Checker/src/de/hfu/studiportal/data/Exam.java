package de.hfu.studiportal.data;

import java.io.Serializable;

public class Exam implements Serializable {

	private static final long serialVersionUID = -4473350889205404637L;
	private int id;
	private String examNo;
	private String name;
	private String bonus;
	private String malus;
	private String ects;
	private String sws;
	private String semester;
	private String kind;
	private String tryCount;
	private String grade;
	private String state;
	private String comment;
	private String resignation;
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
	
	public String getECTS() {
		return ects;

	}
	
	public String getSWS() {
		return sws;

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
	
	public String getTryCount() {
		return tryCount;

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
	
	public Exam setECTS(String ects) {
		this.ects = ects;
		return this;

	}
	
	public Exam setSWS(String sws) {
		this.sws = sws;
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
	
	public Exam setTryCount(String tryCount) {
		this.tryCount = tryCount;
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
