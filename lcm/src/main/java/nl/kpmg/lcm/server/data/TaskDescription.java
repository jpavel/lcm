/*
 * Copyright 2015 KPMG N.V. (unless otherwise stated).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.kpmg.lcm.server.data;

import java.util.Date;

/**
 * Description of tasks in the system.
 *
 * This Model object describes Tasks. TaskDescriptions are used in two way's
 * first to log the result of scheduled tasks and secondly to schedule ad-hoc
 * tasks. Only in the second case the life cycle will apply.
 *
 * A Tasks lifecycle follows the following steps:
 *
 *   1. PENDING             : A task is created
 *   2. SCHEDULED           : The scheduler has picked the task up
 *   3. RUNNING             : The task is being executed
 *   4. FAILED | SUCCESS    : The task is done and either failed or successful
 *
 * @author mhoekstra
 */
public class TaskDescription {

    /**
     * The unique task identifier.
     */
    private Integer id;

    /**
     * The name of the task.
     */
    private String name;

    /**
     * The name of the class which contains the executable code.
     */
    private String job;

    /**
     * The target expression describing on what the task should run.
     */
    private String target;

    /**
     * Contains the output of the job if there is any.
     */
    private String output;

    /**
     * The time this job was started.
     */
    private Date startTime;

    /**
     * The time this job finished.
     */
    private Date endTime;

    /**
     * The current status of this job.
     */
    private TaskStatus status;

    /**
     * An inner enumeration describing the states the task can be in.
     */
    public enum TaskStatus {
        /**
         * A task is created.
         */
        PENDING,

        /**
         * The scheduler has picked the task up.
         */
        SCHEDULED,

        /**
         * The task is being executed.
         */
        RUNNING,

        /**
         * The task end of life if the task is failed.
         */
        FAILED,

        /**
         * The task end of life if the task is successful.
         */
        SUCCESS;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
