package org.finos.legend.engine.plan.dependencies.store.document;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class DocumentResultSetImpl implements DocumentResultSet
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private final List<String> results;
    private final int size;
    private int currentRow;

    public DocumentResultSetImpl(List<String> docs)
    {
        this.results = docs;
        this.size = results.size();
        this.currentRow = 0;
    }

    @Override
    public JsonNode getValue(String jsonPath)
    {
        try
        {
            return mapper.readTree(results.get(currentRow)).get(jsonPath);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean next()
    {
        if (currentRow < size-1)
        {
            currentRow++;
            return true;
        }
        return false;
    }

    @Override
    public void close()
    {

    }

    @Override
    public boolean first()
    {
        if (size == 0)
        {
            return false;
        }
        else
        {
            currentRow = 0;
            return true;
        }
    }

    @Override
    public boolean last()
    {
        if (size == 0)
        {
            return false;
        }
        else
        {
            currentRow = size - 1;
            return true;
        }
    }
}