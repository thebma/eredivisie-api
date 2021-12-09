package frl.hacklab.hw3.api;

import java.sql.Timestamp;

public class GenericApiResponse
{
    public String message;
    public Timestamp at;

    public GenericApiResponse()
    {
        this.message = "OK";
        this.at = new Timestamp(System.currentTimeMillis());
    }

    public GenericApiResponse(String message)
    {
        this.message = message;
        this.at = new Timestamp(System.currentTimeMillis());
    }
}
