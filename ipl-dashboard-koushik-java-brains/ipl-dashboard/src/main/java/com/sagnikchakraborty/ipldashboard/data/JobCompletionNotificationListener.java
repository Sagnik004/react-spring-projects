package com.sagnikchakraborty.ipldashboard.data;

import com.sagnikchakraborty.ipldashboard.model.Team;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final EntityManager entityManager;

    @Autowired
    public JobCompletionNotificationListener(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED. Time to verify the results");

            // Prepare team data with count of matches they played and their count of wins
            Map<String, Team> teamData = new HashMap<>();

            // Collect total count of matches each team played...
            entityManager
                    .createQuery("SELECT m.team1, COUNT(*) FROM Match m GROUP BY m.team1", Object[].class)
                    .getResultList()
                    .stream()
                    .map(e -> new Team((String) e[0], (long) e[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));

            entityManager
                    .createQuery("SELECT m.team2, COUNT(*) FROM Match m GROUP BY m.team2", Object[].class)
                    .getResultList()
                    .forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        if (team == null) { // If a team-2 never played first innings
                            String teamName = (String) e[0];
                            long totalMatches = (long) e[1];
                            team = new Team(teamName, totalMatches);
                            teamData.put(teamName, team);
                        } else {
                            long totalMatches = (long) e[1];
                            team.setTotalMatches(team.getTotalMatches() + totalMatches);
                        }
                    });

            // Collect total count of match wins each team have...
            entityManager
                    .createQuery("SELECT m.matchWinner, COUNT(*) FROM Match m GROUP BY m.matchWinner", Object[].class)
                    .getResultList()
                    .forEach(e -> {
                        String teamName = (String) e[0];
                        Team team = teamData.get(teamName);
                        if (team != null) {
                            long totalWins = (long) e[1];
                            team.setTotalWins(totalWins);
                        }
                    });

            // Persist the team data (model = Team)
            teamData.values().forEach(entityManager::persist);
        }
    }
}
