package nl.kpmg.lcm.server;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.kpmg.lcm.server.data.MetaData;
import nl.kpmg.lcm.server.data.dao.DaoException;
import nl.kpmg.lcm.server.data.dao.file.MetaDataDaoImpl;
import nl.kpmg.lcm.server.data.dao.file.TaskDescriptionDaoImpl;
import nl.kpmg.lcm.server.data.dao.file.TaskScheduleDaoImpl;
import nl.kpmg.lcm.server.data.service.MetaDataService;
import nl.kpmg.lcm.server.task.TaskManager;
import nl.kpmg.lcm.server.task.TaskManagerException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 *
 * @author mhoekstra
 */
public class Server {
    private final String baseUri;
    private final String dataPath;

    private HttpServer restServer;
    private TaskManager taskManager;

    public Server(String[] arguments) {
        /** @TODO Parse arguments, probably abstract argument parsing */
        baseUri = "http://localhost:8080/";
        dataPath = ".";

        try {
            Resources.setMetaDataDao(new MetaDataDaoImpl(String.format("%s/%s", dataPath, "metadata/")));
            Resources.setTaskDescriptionDao(new TaskDescriptionDaoImpl(String.format("%s/%s", dataPath, "taskdescription/")));
            Resources.setTaskScheduleDao(new TaskScheduleDaoImpl(String.format("%s/%s", dataPath, "taskschedule/")));

            Resources.setMetaDataService(new MetaDataService());

        } catch (DaoException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                    "Construction of MetaDataDaoImpl failed. The storage path doesn't exist", ex);
        }
    }


    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public HttpServer startRestInterface() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig()
                .packages("nl.kpmg.lcm.server.rest")
                .registerClasses(JacksonFeature.class)
                .registerClasses(JacksonJsonProvider.class)
                .registerClasses(MetaData.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), rc);
    }

    public TaskManager startTaskManager() throws TaskManagerException {
        TaskManager taskManager = TaskManager.getInstance();
        taskManager.initialize();
        return taskManager;
    }

    /**
     * Main method.
     * @throws ServerException
     */
    public void start() throws ServerException  {
        try {
            restServer = startRestInterface();
            taskManager = startTaskManager();
        } catch (TaskManagerException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Failed starting the LocalCatalogManager due to the TaskManager", ex);
            throw new ServerException(ex);
        }
    }

    public void stop() {
        restServer.stop();
        taskManager.stop();
    }

    public String getBaseUri() {
        return baseUri;
    }
}
