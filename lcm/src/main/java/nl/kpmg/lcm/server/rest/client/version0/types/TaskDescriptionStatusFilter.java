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
package nl.kpmg.lcm.server.rest.client.version0.types;

/**
 *
 * @author mhoekstra
 */
public enum TaskDescriptionStatusFilter {
    ALL,

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
