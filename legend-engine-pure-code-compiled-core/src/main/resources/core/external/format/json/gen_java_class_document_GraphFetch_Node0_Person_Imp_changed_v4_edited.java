package _pure.plan.root.n1.localGraph;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.finos.legend.engine.plan.dependencies.domain.date.DayOfWeek;
import org.finos.legend.engine.plan.dependencies.domain.date.DurationUnit;
import org.finos.legend.engine.plan.dependencies.domain.date.PureDate;
import org.finos.legend.engine.plan.dependencies.util.Library;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;

public class GraphFetch_Node0_Person_Impl implements _pure.app.meta.external.store.document.tests.simple.Person, org.finos.legend.engine.plan.dependencies.domain.dataQuality.Constrained<_pure.app.meta.external.store.document.tests.simple.Person>, java.io.Serializable
{
    private String firstName;
    private String lastName;
    private Object pk$_0;
    private static final ObjectMapper objectMapper$ = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new SimpleModule().addSerializer(PureDate.class, new JsonSerializer<PureDate>() { @Override public void serialize(PureDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException { gen.writeRawValue("\"" + value.toString() + "\""); } }));
    private String setId$;
    public static String databaseConnection$;
    private String alloyStoreObjectReference$;
    private static final long serialVersionUID = 624778034L;

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

    public _pure.app.meta.external.store.document.tests.simple.Person withConstraintsApplied()
    {
        java.util.List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> defects = allConstraints();
        if (!defects.isEmpty())
        {
            throw new IllegalStateException(defects.stream().map(org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect::getMessage).collect(java.util.stream.Collectors.joining("\n")));
        }
        return this;
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<_pure.app.meta.external.store.document.tests.simple.Person> toChecked()
    {
        return this.toChecked(null, true);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<_pure.app.meta.external.store.document.tests.simple.Person> toChecked(boolean applyConstraints)
    {
        return this.toChecked(null, applyConstraints);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<_pure.app.meta.external.store.document.tests.simple.Person> toChecked(Object source)
    {
        return this.toChecked(source, true);
    }

    public org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<_pure.app.meta.external.store.document.tests.simple.Person> toChecked(Object source,
                                                                                                                                                       boolean applyConstraints)
    {
        java.util.List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> defects = applyConstraints ? allConstraints() : java.util.Collections.emptyList();
        return new org.finos.legend.engine.plan.dependencies.domain.dataQuality.IChecked<_pure.app.meta.external.store.document.tests.simple.Person>() {
            public java.util.List<org.finos.legend.engine.plan.dependencies.domain.dataQuality.IDefect> getDefects() { return defects; }
            public Object getSource() { return source; }
            public _pure.app.meta.external.store.document.tests.simple.Person getValue() { return GraphFetch_Node0_Person_Impl.this; }
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
        return "abcdef456";
    }

    public void setAlloyStoreObjectReference$(String reference)
    {
        this.alloyStoreObjectReference$ = reference;
    }

    private static long getClassSize$()
    {
        return 108L;
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