package org.finos.legend.engine.protocol.mongodb.schema.metamodel.mixIn;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="_type")
@JsonSubTypes({@JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.AggregationPipeline.class, name="AggregationPipeline"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.AndOperatorExpression.class, name="AndOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ArgumentExpression.class, name="ArgumentExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ComparisonOperatorExpression.class, name="ComparisonOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ComputedFieldValue.class, name="ComputedFieldValue"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.DatabaseCommand.class, name="DatabaseCommand"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.EqOperatorExpression.class, name="EqOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ExprQueryExpression.class, name="ExprQueryExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.FieldPathExpression.class, name="FieldPathExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.GTEOperatorExpression.class, name="GTEOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.GTOperatorExpression.class, name="GTOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.InOperatorExpression.class, name="InOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.JsonSchemaExpression.class, name="JsonSchemaExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.LTEOperatorExpression.class, name="LTEOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.LTOperatorExpression.class, name="LTOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.LiteralValue.class, name="LiteralValue"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.LogicalOperatorExpression.class, name="LogicalOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.MatchStage.class, name="MatchStage"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.NEOperatorExpression.class, name="NEOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.NinOperatorExpression.class, name="NinOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.NorOperatorExpression.class, name="NorOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.NotOperatorExpression.class, name="NotOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ObjectExpression.class, name="ObjectExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ObjectQueryExpression.class, name="ObjectQueryExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.OrOperatorExpression.class, name="OrOperatorExpression"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ProjectStage.class, name="ProjectStage"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.QueryExprKeyValue.class, name="QueryExprKeyValue"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.Stage.class, name="Stage"), @JsonSubTypes.Type(value=org.finos.legend.engine.protocol.mongodb.schema.metamodel.aggregation.ViewPipeline.class, name="ViewPipeline")})

public class MongoDBOperationElementMixIn
{
}
