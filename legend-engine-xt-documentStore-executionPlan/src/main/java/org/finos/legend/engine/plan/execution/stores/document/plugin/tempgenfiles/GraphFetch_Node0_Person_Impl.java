package org.finos.legend.engine.plan.execution.stores.document.plugin.tempgenfiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.finos.legend.engine.plan.dependencies.domain.date.PureDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphFetch_Node0_Person_Impl implements Person, org.finos.legend.engine.plan.dependencies.domain.dataQuality.Constrained<Person>, java.io.Serializable
{
    private String firstName;
    private String lastName;
    private Object pk$_0;
    private static final ObjectMapper objectMapper$ = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new SimpleModule().addSerializer(PureDate.class, new JsonSerializer<PureDate>() { @Override public void serialize(PureDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException { gen.writeRawValue("\"" + value.toString() + "\""); } }));
    private String setId$;
    public static String databaseConnection$;
    private String alloyStoreObjectReference$;
    private static final long serialVersionUID = 298373131L;

    public String getFirstName()
    {
        return this.firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void addFirstName(String object)
    {
        if ((Object) this.firstName != null)
        {
            throw new IllegalStateException("Found multiple objects for property 'firstName' of multiplicity with bound 1");
        }
        this.firstName = object;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void addLastName(String object)
    {
        if ((Object) this.lastName != null)
        {
            throw new IllegalStateException("Found multiple objects for property 'lastName' of multiplicity with bound 1");
        }
        this.lastName = object;
    }

    public List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> allConstraints()
    {
        return this.allConstraints(new org.finos.legend.engine.plan.dependencies.domain.dataQuality.GraphContext());
    }

    public Person withConstraintsApplied()
    {
        List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> defects = allConstraints();
        if (!defects.isEmpty())
        {
            throw new IllegalStateException(defects.stream().map(org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect::getMessage).collect(Collectors.joining("\n")));
        }
        return this;
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<Person> toChecked()
    {
        return this.toChecked(null, true);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<Person> toChecked(boolean applyConstraints)
    {
        return this.toChecked(null, applyConstraints);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<Person> toChecked(Object source)
    {
        return this.toChecked(source, true);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<Person> toChecked(Object source,
                                                                                                   boolean applyConstraints)
    {
        List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> defects = applyConstraints ? allConstraints() : Collections.emptyList();
        return new org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<Person>() {
            public List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> getDefects() { return defects; }
            public Object getSource() { return source; }
            public Person getValue() { return GraphFetch_Node0_Person_Impl.this; }
        };
    }

    public List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> allConstraints(org.finos.legend.engine.plan.dependencies.domain.dataQuality.GraphContext context)
    {
        List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> result = new ArrayList<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect>();
        if (!context.visited.contains(this))
        {
            context.visited.add(this);
        }
        return result;
    }

    public Object getPk$_0()
    {
        return this.pk$_0;
    }

    public void setPk$_0(Object pk$_0)
    {
        this.pk$_0 = pk$_0;
    }

    public String getSetId$()
    {
        return this.setId$;
    }

    public void setSetId$(String setId)
    {
        this.setId$ = setId;
    }

    public String getAlloyStoreObjectReference$()
    {
        if (this.alloyStoreObjectReference$ == null)
        {
            try
            {
                StringBuilder referenceBuilder = new StringBuilder();
                referenceBuilder.append("001:");
                referenceBuilder.append("010:");

                referenceBuilder.append("0000000010:");
                referenceBuilder.append("Relational:");

                referenceBuilder.append("0000000071:");
                referenceBuilder.append("meta::external::store::document::tests::simple::simpleRelationalMapping:");

                referenceBuilder.append("0000000048:");
                referenceBuilder.append("meta_external_store_document_tests_simple_Person:");

                String setId = this.getSetId$();
                referenceBuilder.append(String.format("%010d", setId.length()));
                referenceBuilder.append(":");
                referenceBuilder.append(setId);
                referenceBuilder.append(":");

                String databaseConnectionString = GraphFetch_Node0_Person_Impl.databaseConnection$;
                referenceBuilder.append(String.format("%010d", databaseConnectionString.length()));
                referenceBuilder.append(":");
                referenceBuilder.append(databaseConnectionString);
                referenceBuilder.append(":");

                Map<String, Object> pkMap = new HashMap<>();

                pkMap.put("pk$_0", this.getPk$_0());
                String pkMapString = objectMapper$.writeValueAsString(pkMap);
                referenceBuilder.append(String.format("%010d", pkMapString.length()));
                referenceBuilder.append(":");
                referenceBuilder.append(pkMapString);

                this.alloyStoreObjectReference$ = "ASOR:" + org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(referenceBuilder.toString().getBytes());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        return this.alloyStoreObjectReference$;
    }

    public void setAlloyStoreObjectReference$(String reference)
    {
        this.alloyStoreObjectReference$ = reference;
    }

    private static long getClassSize$()
    {
        return 132L;
    }

    public long getInstanceSize$()
    {
        long size = GraphFetch_Node0_Person_Impl.getClassSize$();
        if (this.firstName != null)
        {
            size = size + this.firstName.length();
        }
        if (this.lastName != null)
        {
            size = size + this.lastName.length();
        }
        if (this.setId$ != null)
        {
            size = size + this.setId$.length();
        }
        if (this.alloyStoreObjectReference$ != null)
        {
            size = size + this.alloyStoreObjectReference$.length();
        }
        return size;
    }
}
