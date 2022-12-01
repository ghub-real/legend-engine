package org.finos.legend.engine.plan.execution.stores.relational.plugin.tempgenfiles;

import org.finos.legend.engine.plan.dependencies.store.platform.IGraphSerializer;
import org.finos.legend.engine.plan.dependencies.store.platform.ISerializationWriter;
import org.finos.legend.engine.plan.dependencies.store.shared.IExecutionNodeContext;
import org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject;

public class Serializer implements IGraphSerializer<Person>
{
    private ISerializationWriter writer;
    private IExecutionNodeContext context;

    Serializer(ISerializationWriter writer, IExecutionNodeContext context)
    {
        this.writer = writer;
        this.context = context;
    }

    public void serialize(Person value)
    {
        if (value instanceof IReferencedObject)
        {
            this.writer
                    .startObject("meta::external::store::document::tests::simple::Person",
                            ((IReferencedObject) value).getAlloyStoreObjectReference$());
        }
        else
        {
            this.writer.startObject("meta::external::store::document::tests::simple::Person");
        }
        this.writer.writeStringProperty("firstName", value.getFirstName());
        this.writer.writeStringProperty("lastName", value.getLastName());
        this.writer.endObject();
    }
}