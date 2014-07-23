package de.hfu.studiportal.data;

import java.io.Serializable;

import de.hfu.funfpunktnull.R;

import android.content.Context;

public class Exam implements Serializable {

	public enum Kind {
		PL, VL, P, G, KO, UNDEFINED
	}

	public enum State {
		AN, BE, NB, EN, UNDEFINED
	}

	public enum Note {
		GR, K, SA, U, VF, UNDEFINED
	}

	private static final long serialVersionUID = -4473350889205404637L;
	private int id;
	private String examNo;
	private String name = "-";
	private String bonus = "-";
	private String malus = "-";
	private String ects = "-";
	private String sws = "-";
	private String semester = "-";
	private String kind = "-";
	private String tryCount = "-";
	private String grade = "-";
	private String state = "-";
	private String comment = "-";
	private String resignation = "-";
	private String note = "-";

	public Exam(String examNo) {
		this.setExamNo(examNo);

	}

	public boolean isResignated() {
		try {
			return this.getResignation().equals("RT");
			
		} catch(Exception e) {
			return false;

		}
	}

	public Kind getKindEnum() {
		try {
			return Kind.valueOf(this.getKind());

		} catch(Exception e) {
			return Kind.UNDEFINED;

		}
	}

	public State getStateEnum() {
		try {
			return State.valueOf(this.getState());

		} catch(Exception e) {
			return State.UNDEFINED;

		}
	}

	public String getStateName(Context c) {
		switch(this.getStateEnum()) {
		case BE: return this.getStringResource(c, R.string.text_be);
		case AN: return this.getStringResource(c, R.string.text_an);
		case NB: return this.getStringResource(c, R.string.text_nb);
		case EN: return this.getStringResource(c, R.string.text_en);
		case UNDEFINED: return "Undefined";
		}

		return null;
	}
	
	private String getStringResource(Context c, int id) {
		return c.getResources().getString(id);
		
	}

	public Note getNoteEnum() {
		try {
			return Note.valueOf(this.getNote());

		} catch(Exception e) {
			return Note.UNDEFINED;

		}
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
		this.examNo = examNo.replaceAll(" +", " ");
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
