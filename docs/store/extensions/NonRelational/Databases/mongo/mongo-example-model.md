# Example Setup


## Domain Model

```
###Pure

Class demo::domain::Person
{
    firstName: String[1];
    lastName: String[1];
    otherNames: String[*];
    govtIdentities: GovernmentIdentity[*];
}

Class demo::domain::Firm
{
    EntityId: Integer[1];
    LegalName: String[1];
    address: demo::domain::Address[0..1];
    employees: foo::domain::Employee[*];
}

Class demo::domain::Address
{   
    city: String[1];
    country: String[1];
}

Class demo::domain::Employee extends demo::domain::Person
{   
    department: String[1];
    email: String[1];
}

Class demo::domain::GovernmentIdentity
{
    type: String[1];
    value: String[1];
    issuerCountry: String[1];
}
```

## Store Model
Document Store models define the physical document layout.  The collectionfragment construct allows for reuse of same node in different parts of the document.
Below is defintion of a single collection, with object nodes defined as collectionfragments.
```
###DocumentStore

DocumentStore demo::database::Mongo 
(
    Collection Firm
    (
        _id ObjectId,
        firmId Long,
        legalName String,
        location String,
        address Collectionfragment AddressNode,
        employees Array( Collectionfragment EmployeeNode )
    )
    
    Collectionfragment AddressNode
    (
        city String,
        country String
    )
    
    Collectionfragment EmployeeNode
    (
        _id ObjectId,
        dept String,
        email String
        fName String,
        lName String,
        oNames Array( String ),
        govtIds Array( Collectionfragment GovtIdentifier )
    )
    
    Collectionfragment GovtIdentifier
    (
        type String, 
        value String,
        country String
    )
)
```