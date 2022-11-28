package org.finos.legend.engine.plan.execution.stores.relational.plugin.tempgenfiles;


import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.engine.plan.dependencies.domain.graphFetch.IGraphInstance;
import org.finos.legend.engine.plan.dependencies.store.relational.graphFetch.IRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics;
import org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject;

public class Execute implements IRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics
{
    private Specifics specifics;

    public Execute()
    {
        this.specifics = new Specifics();
    }

    public void prepare(ResultSet resultSet, String databaseTimeZone, String databaseConnection)
    {
        this.specifics.prepare(resultSet, databaseTimeZone, databaseConnection);
    }

    public IGraphInstance<? extends IReferencedObject> nextGraphInstance()
    {
        return this.specifics.nextGraphInstance();
    }

    public List<Method> primaryKeyGetters()
    {
        return this.specifics.primaryKeyGetters();
    }

    public List<Pair<String, String>> allInstanceSetImplementations()
    {
        return this.specifics.allInstanceSetImplementations();
    }

    public List<String> primaryKeyColumns(int setIndex)
    {
        return this.specifics.primaryKeyColumns(setIndex);
    }

    public boolean supportsCaching()
    {
        return true;
    }
}
