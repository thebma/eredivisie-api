package frl.hacklab.hw3.logging;

import frl.hacklab.hw3.repositories.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtendedLogger
{
    private Logger internalLogger;

    public void init(Class<?> clazz)
    {
        this.internalLogger = LoggerFactory.getLogger(clazz);
    }

    public void log(String message, Object... object)
    {
        this.internalLogger.error(message, object);
    }

    public void warn(String message, Object... object)
    {
        this.internalLogger.error(message, object);
    }

    public void error(String message, Object... object)
    {
        this.internalLogger.error(message, object);
    }

    public void logResult(RepositoryResult result, String method, String what, String where, Object... additional)
    {
        switch(result)
        {
            case NotFound:
                this.internalLogger.error("" +
                    "Could not find a {} for method {} in {}." +
                    "\n\tEntity information: {}",
                    what, method, where, additional
                );
                break;
            case CreationNotAllowed:
                this.internalLogger.error(
                    "Not allowed to create a {} in method {} at {}." +
                    "\n\tMethod is not allowed to create new entities." +
                    "\n\tEntity information: {}",
                    what, method, where, additional
                );
                break;
            case UpdatePartialNotAllowed:
                this.internalLogger.error(
                    "Not allowed to update a {} with empty fields in method {} at {}." +
                    "\n\tEntity information: {}",
                    what, method, where, additional
                );
                break;
            case CreatePartial:
                this.internalLogger.error(
                    "Creating a {} with empty fields is erroneous - in method {} at {}." +
                    "\n\tEntity information: {}",
                    what, method, where, additional
                );
                break;
            case NotImplemented:
                this.internalLogger.error(
                    "Method was not implented for entity {} with method {} at {}." +
                    "\n\tEntity information: {}",
                    what, method, where, additional
                );
                break;
            default:
                break;
        }
    }
}
