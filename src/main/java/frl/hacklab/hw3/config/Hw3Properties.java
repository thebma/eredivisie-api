package frl.hacklab.hw3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(
    prefix = "frl.hacklab.hw3"
)
public class Hw3Properties
{
    public boolean repositoryPersistAfterRuntime = false;

    public void setRepositoryPersistAfterRuntime(boolean repositoryPersistAfterRuntime)
    {
        this.repositoryPersistAfterRuntime = repositoryPersistAfterRuntime;
    }
}
