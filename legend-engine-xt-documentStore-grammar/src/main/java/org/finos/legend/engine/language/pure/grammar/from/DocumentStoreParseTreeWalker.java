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
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.CollectionFragment;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.Field;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.BooleanTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.CollectionFragmentReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DateTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DecimalTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.DoubleTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.IntegerTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.LongTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.ObjectIdTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.ObjectTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.StringTypeReference;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype.TypeReference;
import org.finos.legend.engine.shared.core.operational.errorManagement.EngineException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<String, CollectionFragment> fragments = ctx.collectionFragment().stream().map(frag -> this.visitCollectionfragment(frag, Collections.EMPTY_MAP)).collect(Collectors.toMap(CollectionFragment::getName, Function.identity()));

        List<Collection> collections = ctx.collection().stream().map(col -> this.visitCollection(col, fragments)).collect(Collectors.toList());
        return documentStore;
    }

    private Collection visitCollection(DocumentStoreParser.CollectionContext ctx, Map<String, CollectionFragment> fragments)
    {
        Collection collection = new Collection();
        collection.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        collection.name = ctx.collectionIdentifier().identifier().getText();
        List<String> primaryKeys = new ArrayList<>();
        collection.fields = ListIterate.collect(ctx.propertyDefinitions().propertyDefinition(), fieldDefinitionContext -> this.visitPropertyDefinition(fieldDefinitionContext, primaryKeys, fragments));
        collection.primaryKey = primaryKeys;
        System.out.println(collection);
        return collection;
    }

    private CollectionFragment visitCollectionfragment(DocumentStoreParser.CollectionFragmentContext ctx, Map<String, CollectionFragment> fragments)
    {
        CollectionFragment collectionfragment = new CollectionFragment();
        collectionfragment.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);
        collectionfragment.name = ctx.collectionFragmentIdentifier().identifier().getText();
        List<String> primaryKeys = new ArrayList<>();
        collectionfragment.fields = ListIterate.collect(ctx.propertyDefinitions().propertyDefinition(), fieldDefinitionContext -> this.visitPropertyDefinition(fieldDefinitionContext, primaryKeys, fragments));
        System.out.println(collectionfragment);
        return collectionfragment;
    }

    private Field visitPropertyDefinition(DocumentStoreParser.PropertyDefinitionContext ctx, List<String> primaryKeys, Map<String, CollectionFragment> fragments)
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
        if (ctx.arrayDefinition() != null)
        {
            field.type = this.visitArray(ctx.arrayDefinition(), fragments);
        }
        else if (ctx.typeReferenceDefinition() != null)
        {
            field.type = this.visitTypeDefinitionReference(ctx.typeReferenceDefinition(), fragments);
        }
        return field;
    }

    private TypeReference visitArray(DocumentStoreParser.ArrayDefinitionContext ctx, Map<String, CollectionFragment> fragments)
    {
        TypeReference typeReference;
        DocumentStoreParser.TypeContext typeCtx = ctx.elementsArray().type();
        typeReference = visitTypeReference(typeCtx, fragments);
        typeReference.list = true;
        return typeReference;
    }

    private TypeReference visitTypeDefinitionReference(DocumentStoreParser.TypeReferenceDefinitionContext ctx, Map<String, CollectionFragment> fragments)
    {
        TypeReference typeReference;
        DocumentStoreParser.TypeContext typeCtx = ctx.type() != null ? ctx.type() : ctx.listType().type();
        typeReference = visitTypeReference(typeCtx, fragments);
        typeReference.list = ctx.listType() != null;
        return typeReference;
    }

    private TypeReference visitTypeReference(DocumentStoreParser.TypeContext typeCtx, Map<String, CollectionFragment> fragments)
    {
        TypeReference typeReference;
        if (typeCtx.primitiveType() != null)
        {
            typeReference = this.createTypeReferenceFromType(typeCtx.primitiveType());
        }
        else
        {
            typeReference = this.visitComplexTypeReference(typeCtx.complexType(), fragments);
        }

        typeReference.sourceInformation = this.walkerSourceInformation.getSourceInformation(typeCtx);
        return typeReference;
    }

    private TypeReference visitComplexTypeReference(DocumentStoreParser.ComplexTypeContext cplxTypeCtx, Map<String, CollectionFragment> fragments)
    {
        if (cplxTypeCtx.collectionFragment() != null)
        {
            CollectionFragmentReference cfReference = new CollectionFragmentReference();
            cfReference.fragment = this.visitCollectionfragment(cplxTypeCtx.collectionFragment(), fragments);
            cfReference.type = CollectionFragmentReference.COMPLEXTYPE.COLLECTION_FRAGMENT;
            return cfReference;
        }
        else
        {
            // Pointer to Fragment
            CollectionFragmentReference cfReference = new CollectionFragmentReference();
            String cfName = PureGrammarParserUtility.fromIdentifier(cplxTypeCtx.collectionFragmentPointer().qualifiedName().identifier());
            String cfPackage = cplxTypeCtx.collectionFragmentPointer().qualifiedName().packagePath() == null ? "" : PureGrammarParserUtility.fromPath(cplxTypeCtx.collectionFragmentPointer().qualifiedName().packagePath().identifier());
            String pathToFragment = cfPackage + "::" + cfName;
            cfReference.fragment = fragments.computeIfAbsent(pathToFragment, p ->
            {
                throw new EngineException("Collection fragment referenced: " + p + " does not exist in scope", this.walkerSourceInformation.getSourceInformation(cplxTypeCtx), EngineErrorType.PARSER);
            });
            cfReference.type = CollectionFragmentReference.COMPLEXTYPE.COLLECTION_FRAGMENT;
            return cfReference;
        }
    }

    private TypeReference createTypeReferenceFromType(DocumentStoreParser.PrimitiveTypeContext typeCtx)
    {
        String type = PureGrammarParserUtility.fromIdentifier(typeCtx.identifier());
        switch (type)
        {
            case "Boolean":
            {
                return new BooleanTypeReference();
            }
            case "Date":
            {
                return new DateTypeReference();
            }
            case "Decimal":
            {
                return new DecimalTypeReference();
            }
            case "Double":
            {
                return new DoubleTypeReference();
            }
            case "Integer":
            {
                return new IntegerTypeReference();
            }
            case "Long":
            {
                return new LongTypeReference();
            }
            case "String":
            {
                return new StringTypeReference();
            }
            case "ObjectId":
            {
                return new ObjectIdTypeReference();
            }
            case "Object":
            {
                return new ObjectTypeReference();
            }
            default:
            {
                throw new EngineException("Unsupported Parameter Value Type - " + type + ". Supported types are - Boolean, Date, Decimal, Double, Integer, Long, String, ObjectId, Object", this.walkerSourceInformation.getSourceInformation(typeCtx), EngineErrorType.PARSER);
            }
        }
    }

}
