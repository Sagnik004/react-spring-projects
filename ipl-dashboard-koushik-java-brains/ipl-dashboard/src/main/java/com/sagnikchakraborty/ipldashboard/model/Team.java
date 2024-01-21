package com.sagnikchakraborty.ipldashboard.model;

import jakarta.persistence.*;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "team_name")
    private String teamName;
    @Column(name = "total_matches")
    private long totalMatches;
    @Column(name = "total_wins")
    private long totalWins;

    public Team() {}

    public Team(String teamName, long totalMatches) {
        this.teamName = teamName;
        this.totalMatches = totalMatches;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getTotalMatches() {
        return totalMatches;
    }
    public void setTotalMatches(long totalMatches) {
        this.totalMatches = totalMatches;
    }

    public long getTotalWins() {
        return totalWins;
    }
    public void setTotalWins(long totalWins) {
        this.totalWins = totalWins;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", totalMatches=" + totalMatches +
                ", totalWins=" + totalWins +
                '}';
    }
}
