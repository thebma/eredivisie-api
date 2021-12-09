package frl.hacklab.hw3.repositories;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import frl.hacklab.hw3.config.Hw3Properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository<T>
{
    private final static String DATA_ROOT =  "./src/main/java/frl/hacklab/hw3/data/";

    protected List<T> values;

    private final Hw3Properties properties;
    private final String source;
    private String temporarySource;

    public JsonRepository(Hw3Properties prop, String sourceFile)
    {
        properties = prop;
        source = sourceFile;
    }

    protected int nextId()
    {
        return values.size() + 1;
    }

    protected void load(Class<T> type)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try
        {
            values = mapper.readValue(
                    new File(Path.of(DATA_ROOT, source + ".json").toString()),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, type)
            );

            temporarySource = String.format("%s%s%s", source, System.currentTimeMillis() / 1000, ".json");
            Path temporaryFilePath = Path.of(DATA_ROOT, temporarySource);
            File temporaryFile = new File(temporaryFilePath.toString());

            if(!properties.repositoryPersistAfterRuntime)
            {
                temporaryFile.deleteOnExit();
            }

            mapper.writeValue(temporaryFile, values);
        }
        catch (JsonMappingException e)
        {
            //TODO: log
            System.out.printf("Failed to map %s.json to a data structure, error: %s", source, e.getMessage());
            e.printStackTrace();
        }
        catch (JsonParseException e)
        {
            //TODO: log
            System.out.printf("Failed to parse %s.json due to a JSON syntax error: %s", source, e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            //TODO: log
            System.out.printf("Failed to load %s.json, error: %s", source, e.getMessage());
            e.printStackTrace();
        }
    }

    protected void save()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            System.out.println("Writing " + temporarySource);

            Path path = Path.of(DATA_ROOT, temporarySource);
            File file = new File(path.toString());
            mapper.writeValue(file, this.values);
        }
        catch (Exception e)
        {
            //TODO: log
            System.out.println("Failed saving the data.");
            e.printStackTrace();
        }
    }
}
