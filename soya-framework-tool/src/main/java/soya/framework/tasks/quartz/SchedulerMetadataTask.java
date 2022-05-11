package soya.framework.tasks.quartz;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.quartz.SchedulerMetaData;
import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-metadata", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class SchedulerMetadataTask extends QuartzSchedulerTask {

    @Override
    public String execute() throws Exception {
        JsonObject json = new JsonObject();
        SchedulerMetaData metaData = scheduler.getMetaData();

        json.addProperty("version", metaData.getVersion());
        json.addProperty("schedulerName", metaData.getSchedulerName());
        json.addProperty("schedulerInstanceId", metaData.getSchedulerInstanceId());
        json.addProperty("schedulerClass", metaData.getSchedulerClass().getName());

        json.addProperty("startTime", metaData.getRunningSince().toString());
        json.addProperty("jobsExecuted", metaData.getNumberOfJobsExecuted());
        json.addProperty("remote", metaData.isSchedulerRemote());
        json.addProperty("started", metaData.isStarted());
        json.addProperty("stopped", metaData.isShutdown());
        json.addProperty("standby", metaData.isInStandbyMode());

        json.addProperty("threadPoolClass", metaData.getThreadPoolClass().getName());
        json.addProperty("threadPoolSize", metaData.getThreadPoolSize());

        json.addProperty("jobStoreClass", metaData.getJobStoreClass().getName());
        json.addProperty("jobStoreClustered", metaData.isJobStoreClustered());
        json.addProperty("jobStoreSupportsPersistence", metaData.isJobStoreSupportsPersistence());

        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }

}
