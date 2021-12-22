package frl.hacklab.hw3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import frl.hacklab.hw3.util.Mergeable;
import lombok.Data;

@Data
public class Player extends Mergeable<Player>
{
    protected int ID = Integer.MIN_VALUE;
    protected String Name = "";

    @JsonProperty("dob")
    protected String DateOfBirth = "00.00.0000";
    protected String Height = "0 cm";
    protected int Quality = Integer.MIN_VALUE;

    @JsonProperty("position")
    protected Position FieldPosition = Position.Unknown;
}
