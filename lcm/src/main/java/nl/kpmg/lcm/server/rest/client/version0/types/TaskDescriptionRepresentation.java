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

import java.util.List;
import javax.ws.rs.core.Link;
import nl.kpmg.lcm.server.data.TaskDescription;
import nl.kpmg.lcm.server.rest.client.version0.TaskDescriptionController;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * A wrapper class for a TaskDescription and its links.
 *
 * @author mhoekstra
 */
public class TaskDescriptionRepresentation {

    /**
     * The actual TaskDescription.
     */
    private final TaskDescription item;

    /**
     * The links the of a TaskDescription.
     */
    @InjectLinks({
        @InjectLink(
                resource = TaskDescriptionController.class,
                style = InjectLink.Style.ABSOLUTE,
                rel = "self",
                method = "getTask",
                bindings = {@Binding(name = "tasks_id", value = "${instance.item.id}") }
        )
    })
    private List<Link> links;

    /**
     * @param item TaskDescription to be wrapped
     */
    public TaskDescriptionRepresentation(final TaskDescription item) {
        this.item = item;
    }

    /**
     * @return the TaskDescription
     */
    public final TaskDescription getItem() {
        return item;
    }

    /**
     * @return the list of Links
     */
    public final List<Link> getLinks() {
        return links;
    }
}
