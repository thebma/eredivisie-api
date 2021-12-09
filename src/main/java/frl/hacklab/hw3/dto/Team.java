package frl.hacklab.hw3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import frl.hacklab.hw3.util.Mergeable;
import lombok.Data;

@Data
public class Team extends Mergeable<Team>
{
    protected int ID = Integer.MIN_VALUE;
    protected String Name = "";
    protected String City = "";
    protected String Stadium = "";

    @JsonProperty("surface")
    protected Surface FieldSurface = Surface.Onbekend;
    protected int Since = Integer.MIN_VALUE;

    public Team()
    {
        super(Team.class);
    }

    public Team(int id)
    {
        this.ID = id;
        this.Name = "Unknown";
        this.City = "Unknown";
        this.Stadium = "Unknown";
        this.FieldSurface = Surface.Hybride;
    }
    public Team(int id, String name, String city, String stadium)
    {
        this.ID = id;
        this.Name = name;
        this.City = city;
        this.Stadium = stadium;
        this.FieldSurface = Surface.Hybride;
    }
}
