// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.datatype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.SourceInformation;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringTypeReference.class, name = "String"),
        @JsonSubTypes.Type(value = BooleanTypeReference.class, name = "Boolean"),
        @JsonSubTypes.Type(value = IntegerTypeReference.class, name = "Integer"),
        @JsonSubTypes.Type(value = LongTypeReference.class, name = "Long"),
        @JsonSubTypes.Type(value = DateTypeReference.class, name = "Date"),
        @JsonSubTypes.Type(value = DoubleTypeReference.class, name = "Double"),
        @JsonSubTypes.Type(value = DecimalTypeReference.class, name = "Decimal"),
        @JsonSubTypes.Type(value = ObjectTypeReference.class, name = "Object"),
        @JsonSubTypes.Type(value = ObjectIdTypeReference.class, name = "ObjectId"),

})
public abstract class TypeReference
{
    public boolean list;

    public SourceInformation sourceInformation;
}
