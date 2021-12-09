package frl.hacklab.hw3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

public class Match
{
    @JsonProperty("team_home")
    public int HomeTeam;

    @JsonProperty("team_out")
    public int OutTeam;

    public HashSet<Goal> Goals;
}
