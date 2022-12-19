// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.language.pure.grammar.from;

import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.DocumentStoreParser;
import org.finos.legend.engine.protocol.pure.v1.model.context.EngineErrorType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.DefaultCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.Collection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.Field;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.BooleanTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DateTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DecimalTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DoubleTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.IntegerTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.LongTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.ObjectTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.StringTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.TypeReference;
import org.finos.legend.engine.shared.core.operational.errorManagement.EngineException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DocumentStoreParseTreeWalker
{
    private final ParseTreeWalkerSourceInformation walkerSourceInformation;
    private final Consumer<PackageableElement> elementConsumer;
    private final DefaultCodeSection section;

    public DocumentStoreParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation)
    {
        this(walkerSourceInformation, null, null);
    }

    public DocumentStoreParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation, Consumer<PackageableElement> elementConsumer, DefaultCodeSection section)
    {
        this.walkerSourceInformation = walkerSourceInformation;
        this.elementConsumer = elementConsumer;
        this.section = section;
    }

    public void visit(DocumentStoreParser.DefinitionContext ctx)
    {
        ctx.documentStore().stream().map(this::visitDocumentStore).peek(e -> this.section.elements.add(e.getPath())).forEach(this.elementConsumer);
    }

    private DocumentStore visitDocumentStore(DocumentStoreParser.DocumentStoreContext ctx)
    {
        DocumentStore documentStore = new DocumentStore();
        documentStore.name = PureGrammarParserUtility.fromIdentifier(ctx.qualifiedName().identifier());
        documentStore._package = ctx.qualifiedName().packagePath() == null ? "" : PureGrammarParserUtility.fromPath(ctx.qualifiedName().packagePath().identifier());
        documentStore.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        documentStore.includedStores = ListIterate.collect(ctx.include(), includeContext -> PureGrammarParserUtility.fromQualifiedName(includeContext.qualifiedName().packagePath() == null ? Collections.emptyList() : includeContext.qualifiedName().packagePath().identifier(), includeContext.qualifiedName().identifier()));
        // No need for schemas here
        List<Collection> tables = ListIterate.collect(ctx.collection(), this::visitCollection);
        return documentStore;
    }

    private Collection visitCollection(DocumentStoreParser.CollectionContext ctx)
    {
        Collection collection = new Collection();
        collection.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        collection.name = ctx.collectionIdentifier().identifier().getText();
        List<String> primaryKeys = new ArrayList<>();
        collection.fields = ListIterate.collect(ctx.propertyDefinitions().propertyDefinition(), fieldDefinitionContext -> this.visitFieldDefinition(fieldDefinitionContext, primaryKeys));
        collection.primaryKey = primaryKeys;
        return collection;
    }

    private Field visitFieldDefinition(DocumentStoreParser.PropertyDefinitionContext ctx, List<String> primaryKeys)
    {
        Field field = new Field();
        field.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        field.name = ctx.propertyIdentifier().identifier().getText();
        boolean nullable = true;
        if (ctx.PRIMARY_KEY() != null)
        {
            nullable = false;
            primaryKeys.add(field.name);
        }
        else
        {
            if (ctx.NOT_NULL() != null)
            {
                nullable = false;
            }
        }
        field.nullable = nullable;
        field.type = this.visitTypeReference(ctx.typeReferenceDefinition());

        return field;
    }

    private TypeReference visitTypeReference(DocumentStoreParser.TypeReferenceDefinitionContext ctx)
    {
        TypeReference typeReference;
        DocumentStoreParser.TypeContext typeCtx = ctx.type() != null ? ctx.type() : ctx.listType().type();

        if (typeCtx.complexType() != null)
        {
            ObjectTypeReference objTypeReference = new ObjectTypeReference();
            objTypeReference.type = PureGrammarParserUtility.fromQualifiedName(typeCtx.complexType().collectionFragmentPointer().qualifiedName().packagePath() == null ? Collections.emptyList() :
                            typeCtx.complexType().collectionFragmentPointer().qualifiedName().packagePath().identifier(),
                    typeCtx.complexType().collectionFragmentPointer().qualifiedName().identifier());

            typeReference = objTypeReference;
        }
        else
        {
            String type = PureGrammarParserUtility.fromIdentifier(typeCtx.primitiveType().identifier());
            switch (type)
            {
                case "Boolean":
                {
                    typeReference = new BooleanTypeReference();
                    break;
                }
                case "Date":
                {
                    typeReference = new DateTypeReference();
                    break;
                }
                case "Decimal":
                {
                    typeReference = new DecimalTypeReference();
                    break;
                }
                case "Double":
                {
                    typeReference = new DoubleTypeReference();
                    break;
                }
                case "Integer":
                {
                    typeReference = new IntegerTypeReference();
                    break;
                }
                case "Long":
                {
                    typeReference = new LongTypeReference();
                    break;
                }
                case "String":
                {
                    typeReference = new StringTypeReference();
                    break;
                }
                case "ObjectId":
                {
                    typeReference = new ObjectTypeReference();
                    break;
                }
                default:
                {
                    throw new EngineException("Unsupported Parameter Value Type - " + type + ". Supported types are - Boolean, Date, Decimal, Double, Integer, Long, ObjectType, String", this.walkerSourceInformation.getSourceInformation(typeCtx), EngineErrorType.PARSER);
                }
            }
        }

        typeReference.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        typeReference.list = ctx.listType() != null;

        return typeReference;
    }
}
