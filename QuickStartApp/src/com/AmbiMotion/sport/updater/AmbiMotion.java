package com.AmbiMotion.sport.updater;

import android.app.Application;

public class AmbiMotion extends Application{

	private String team;
	private String competition;
	
	
	
	public String getCompetition() {
		return competition;
	}
	public void setCompetition(String competition) {
		this.competition = competition;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	
	
}
