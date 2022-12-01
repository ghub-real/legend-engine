package org.finos.legend.engine.plan.execution.stores.relational.plugin.tempgenfiles;


import org.finos.legend.engine.plan.dependencies.store.platform.IGraphSerializer;
import org.finos.legend.engine.plan.dependencies.store.platform.IPlatformPureExpressionExecutionNodeSerializeSpecifics;
import org.finos.legend.engine.plan.dependencies.store.platform.ISerializationWriter;
import org.finos.legend.engine.plan.dependencies.store.shared.IExecutionNodeContext;

public class Serialize implements IPlatformPureExpressionExecutionNodeSerializeSpecifics
{
    public IGraphSerializer<?> serializer(ISerializationWriter writer,
                                          IExecutionNodeContext context)
    {
        return new Serializer(writer, context);
    }
}
