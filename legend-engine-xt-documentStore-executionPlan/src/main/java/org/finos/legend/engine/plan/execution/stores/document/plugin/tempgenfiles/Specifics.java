
package org.finos.legend.engine.plan.execution.stores.document.plugin.tempgenfiles;


import org.bson.Document;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.engine.plan.dependencies.domain.date.PureDate;
import org.finos.legend.engine.plan.dependencies.domain.graphFetch.IGraphInstance;
import org.finos.legend.engine.plan.dependencies.store.document.DocumentResultSet;
import org.finos.legend.engine.plan.dependencies.store.document.graphFetch.IDocumentQueryResultField;
import org.finos.legend.engine.plan.dependencies.store.document.graphFetch.INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;

public class Specifics implements INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics
{
    private static final List<Integer> STRING_TYPES = Arrays.asList(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.LONGNVARCHAR, Types.OTHER, Types.NULL);
    private static final List<Integer> INT_TYPES = Arrays.asList(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.NULL);
    private static final List<Integer> FLOAT_TYPES = Arrays.asList(Types.REAL, Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.NULL);
    private static final List<Integer> BOOL_TYPES = Arrays.asList(Types.BIT, Types.BOOLEAN, Types.NULL);
    private static final List<Integer> STRICT_DATE_TYPES = Arrays.asList(Types.DATE, Types.NULL);
    private static final List<Integer> DATE_TIME_TYPES = Arrays.asList(Types.TIMESTAMP, Types.NULL);
    private ResultSet resultSet;

//    List<? extends IDocumentQueryResultField> resultFields;
    private DocumentResultSet documentResultSet;
    private String databaseTimeZone;
    private String databaseConnection;
    private List<Integer> columnTypes;
    private List<List<Integer>> propertyIndices;
    private List<List<Supplier<Object>>> propertyGetters;
    private Calendar calendar;
    private Method parentPropertyAdder;
    private Method parentEdgePointPropertyAdder;

    private Object getAlloyNativeValueFromResultSet(ResultSet resultSet,
                                                    int columnIndex,
                                                    int columnType)
    {
        try
        {
            Object result = null;
            switch (columnType)
            {
                case Types.DATE:
                {
                    java.sql.Date date = resultSet.getDate(columnIndex);
                    if (date != null)
                    {
                        result = PureDate.fromSQLDate(date);
                    }
                    break;
                }
                case Types.TIMESTAMP:
                {
                    java.sql.Timestamp timestamp = resultSet.getTimestamp(columnIndex, this.calendar);
                    if (timestamp != null)
                    {
                        result = PureDate.fromSQLTimestamp(timestamp);
                    }
                    break;
                }
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                {
                    long num = resultSet.getLong(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        result = Long.valueOf(num);
                    }
                    break;
                }
                case Types.REAL:
                case Types.FLOAT:
                case Types.DOUBLE:
                {
                    double num = resultSet.getDouble(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        result = Double.valueOf(num);
                    }
                    break;
                }
                case Types.DECIMAL:
                case Types.NUMERIC:
                {
                    result = resultSet.getBigDecimal(columnIndex);
                    break;
                }
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.NCHAR:
                case Types.NVARCHAR:
                case Types.LONGNVARCHAR:
                case Types.OTHER:
                {
                    result = resultSet.getString(columnIndex);
                    break;
                }
                case Types.BIT:
                case Types.BOOLEAN:
                {
                    boolean bool = resultSet.getBoolean(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        result = Boolean.valueOf(bool);
                    }
                }
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                {
                    byte[] bytes = resultSet.getBytes(columnIndex);
                    if (bytes != null)
                    {
                        result = this.encodeHex(bytes);
                    }
                    break;
                }
                case Types.NULL:
                {
                    // do nothing: value is already assigned to null
                    break;
                }
                default:
                {
                    result = resultSet.getObject(columnIndex);
                }
            }
            return result;}
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String encodeHex(byte[] data)
    {
        final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++)
        {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    private Supplier<Object> getResultSetPropertyGetterForStringProperty(ResultSet resultSet,
                                                                         int columnIndex,
                                                                         int columnType,
                                                                         String propertyName)
    {
        if (STRING_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    return resultSet.getString(columnIndex);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        else
        {
            throw new RuntimeException("Error reading in property '" + propertyName + "' of type String from SQL column of type '" + columnType + "'.");
        }
    }

    private Supplier<Object> getResultSetPropertyGetterForIntegerProperty(ResultSet resultSet,
                                                                          int columnIndex,
                                                                          int columnType,
                                                                          String propertyName)
    {
        if (INT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Long res = null;
                    long r = resultSet.getLong(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Long.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type Integer from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForFloatProperty(ResultSet resultSet,
                                                                        int columnIndex,
                                                                        int columnType,
                                                                        String propertyName)
    {
        if (FLOAT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Double res = null;
                    double r = resultSet.getDouble(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Double.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (INT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Double res = null;
                    long r = resultSet.getLong(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Double.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type Float from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForDecimalProperty(ResultSet resultSet,
                                                                          int columnIndex,
                                                                          int columnType,
                                                                          String propertyName)
    {
        if (FLOAT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    java.math.BigDecimal res = null;
                    double r = resultSet.getDouble(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = java.math.BigDecimal.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (INT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    java.math.BigDecimal res = null;
                    long r = resultSet.getLong(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = java.math.BigDecimal.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type Decimal from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForBooleanProperty(ResultSet resultSet,
                                                                          int columnIndex,
                                                                          int columnType,
                                                                          String propertyName)
    {
        if (BOOL_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Boolean res = null;
                    boolean r = resultSet.getBoolean(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Boolean.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (STRING_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Boolean res = null;
                    String r = resultSet.getString(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Boolean.valueOf(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (INT_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    Boolean res = null;
                    long r = resultSet.getLong(columnIndex);
                    if (!resultSet.wasNull())
                    {
                        res = Boolean.valueOf(r == 1);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type Boolean from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForStrictDateProperty(ResultSet resultSet,
                                                                             int columnIndex,
                                                                             int columnType,
                                                                             String propertyName)
    {
        if (STRICT_DATE_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    java.sql.Date r = resultSet.getDate(columnIndex);
                    if (r != null)
                    {
                        res = PureDate.fromSQLDate(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (STRING_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    String r = resultSet.getString(columnIndex);
                    if (r != null)
                    {
                        try
                        {
                            res = PureDate.parsePureDate(r);
                        }
                        catch (IllegalArgumentException dateTimeParseException)
                        {
                            res = PureDate.fromSQLDate(java.sql.Date.valueOf(r));
                        }
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type StrictDate from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForDateTimeProperty(ResultSet resultSet,
                                                                           int columnIndex,
                                                                           int columnType,
                                                                           String propertyName)
    {
        if (DATE_TIME_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    java.sql.Timestamp r = resultSet.getTimestamp(columnIndex, this.calendar);
                    if (r != null)
                    {
                        res = PureDate.fromSQLTimestamp(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (STRING_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    String r = resultSet.getString(columnIndex);
                    if (r != null)
                    {
                        try
                        {
                            res = PureDate.parsePureDate(r);
                        }
                        catch (IllegalArgumentException dateTimeParseException)
                        {
                            res = PureDate.fromSQLTimestamp(java.sql.Timestamp.valueOf(r));
                        }
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type DateTime from SQL column of type '" + columnType + "'.");
    }

    private Supplier<Object> getResultSetPropertyGetterForDateProperty(ResultSet resultSet,
                                                                       int columnIndex,
                                                                       int columnType,
                                                                       String propertyName)
    {
        if (STRICT_DATE_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    java.sql.Date r = resultSet.getDate(columnIndex);
                    if (r != null)
                    {
                        res = PureDate.fromSQLDate(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (DATE_TIME_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    java.sql.Timestamp r = resultSet.getTimestamp(columnIndex, this.calendar);
                    if (r != null)
                    {
                        res = PureDate.fromSQLTimestamp(r);
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        if (STRING_TYPES.contains(columnType))
        {
            return () -> {
                try
                {
                    PureDate res = null;
                    String r = resultSet.getString(columnIndex);
                    if (r != null)
                    {
                        try
                        {
                            res = PureDate.parsePureDate(r);
                        }
                        catch (IllegalArgumentException dateTimeParseException1)
                        {
                            try
                            {
                                res = PureDate.fromSQLTimestamp(java.sql.Timestamp.valueOf(r));
                            }
                            catch (java.time.format.DateTimeParseException dateTimeParseException2)
                            {
                                res = PureDate.fromSQLDate(java.sql.Date.valueOf(r));
                            }
                        }
                    }
                    return res;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            };
        }
        throw new RuntimeException("Error reading in property '" + propertyName + "' of type Date from SQL column of type '" + columnType + "'.");
    }

    public IGraphInstance<? extends org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject> nextGraphInstance()
    {
        try
        {
//            if (!this.documentResultSet.next()) {
//                return null;
//            }
            final GraphFetch_Node0_Person_Impl object = new GraphFetch_Node0_Person_Impl();



            object.setFirstName(this.documentResultSet.getValue("firstName").asText());
            object.setLastName(this.documentResultSet.getValue("lastName").asText());

//            object.setFirstName(doc.getString("firstName"));
//            object.setLastName(doc.getString("lastName"));



            object.setSetId$("firm_id");
            Object pk$_0 = "someuniquepk567";
            object.setPk$_0(pk$_0);

            return new IGraphInstance<GraphFetch_Node0_Person_Impl>()
            {
                public GraphFetch_Node0_Person_Impl getValue()
                {
                    return object;
                }
                public long instanceSize()
                {
                    return object.getInstanceSize$();
                }
            };
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean supportsCaching()
    {
        return false;
    }

    @Override
    public Object deepCopy(Object object)
    {
        return INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics.super.deepCopy(object);
    }

    public List<Method> primaryKeyGetters()
    {
        try
        {
            return Arrays.asList(GraphFetch_Node0_Person_Impl.class.getMethod("getPk$_0"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void prepare(DocumentResultSet resultDocuments, String databaseTimeZone, String databaseConnection)
    {
        this.documentResultSet = resultDocuments;
    }

    public List<Pair<String, String>> allInstanceSetImplementations()
    {
        return Arrays.asList(Tuples.pair("meta::external::store::document::tests::simple::simpleRelationalMapping",
                "meta_external_store_document_tests_simple_Person"));
    }

   public List<String> primaryKeyColumns(int setIndex)
    {
        if (setIndex == 0)
        {
            return Arrays.asList("pk_0");
        }
        return null;
    }
}