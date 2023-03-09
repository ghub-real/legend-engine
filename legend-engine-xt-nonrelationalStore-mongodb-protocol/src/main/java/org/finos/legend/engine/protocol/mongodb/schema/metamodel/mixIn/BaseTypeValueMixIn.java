package org.finos.legend.engine.protocol.mongodb.schema.metamodel.mixIn;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({@JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ArrayTypeValue.class, name = "ArrayTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.BoolTypeValue.class, name = "BoolTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.DecimalTypeValue.class, name = "DecimalTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.IntTypeValue.class, name = "IntTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.LongTypeValue.class, name = "LongTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.NullTypeValue.class, name = "NullTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ObjectTypeValue.class, name = "ObjectTypeValue"), @JsonSubTypes.Type(value = org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.StringTypeValue.class, name = "StringTypeValue")})
public class BaseTypeValueMixIn
{
}
