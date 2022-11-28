package org.finos.legend.engine.plan.execution.stores.relational.plugin.tempgenfiles;

public interface Person extends EntityWithAddress, org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject
{
    String getFirstName();
    String getLastName();
    String getAlloyStoreObjectReference$();
}