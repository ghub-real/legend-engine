// Copyright 2020 Goldman Sachs
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

package org.finos.legend.engine.language.pure.grammar.integration.util;

import org.finos.legend.engine.protocol.mongodb.schema.metamodel.pure.RootMongoDBClassMapping;

public class MongoDBMappingComposer
{
    public static String renderRootMongoDBClassMapping(RootMongoDBClassMapping rootMongoDBClassMapping)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (rootMongoDBClassMapping.mainCollectionName != null && rootMongoDBClassMapping.storePath != null)
        {
            stringBuilder.append("    ~mainCollection [" + rootMongoDBClassMapping.storePath + "] " + rootMongoDBClassMapping.mainCollectionName + "\n");
        }

        if (rootMongoDBClassMapping.bindingPath != null)
        {
            stringBuilder.append("    ~binding " + rootMongoDBClassMapping.bindingPath + "\n");
        }

        return stringBuilder.toString();
    }
}