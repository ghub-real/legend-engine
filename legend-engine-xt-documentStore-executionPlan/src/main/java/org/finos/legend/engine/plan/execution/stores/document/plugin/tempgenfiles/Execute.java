package org.finos.legend.engine.plan.execution.stores.document.plugin.tempgenfiles;


import org.bson.Document;
import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.engine.plan.dependencies.domain.graphFetch.IGraphInstance;
import org.finos.legend.engine.plan.dependencies.store.document.DocumentResultSet;
import org.finos.legend.engine.plan.dependencies.store.document.graphFetch.IDocumentQueryResultField;
import org.finos.legend.engine.plan.dependencies.store.document.graphFetch.INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics;
import org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class Execute implements INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics
{
    private Specifics specifics;

    public Execute()
    {
        this.specifics = new Specifics();
    }


    public IGraphInstance<? extends IReferencedObject> nextGraphInstance()
    {
        return this.specifics.nextGraphInstance();
    }

    public List<Method> primaryKeyGetters()
    {
        return this.specifics.primaryKeyGetters();
    }

    @Override
    public void prepare(DocumentResultSet documentResultSet, String databaseTimeZone, String databaseConnection)
    {
        this.specifics.prepare(documentResultSet, databaseTimeZone, databaseConnection);
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

    @Override
    public Object deepCopy(Object object)
    {
        return INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics.super.deepCopy(object);
    }
}
