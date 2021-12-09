package frl.hacklab.hw3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Goal
{
    public int Time;

    @JsonProperty("team_id")
    public int ScoringTeam;

    @JsonProperty("player_id")
    public int ScoringPlayer;
}
