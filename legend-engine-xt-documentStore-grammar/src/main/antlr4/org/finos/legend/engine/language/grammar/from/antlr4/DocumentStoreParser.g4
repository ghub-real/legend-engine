parser grammar DocumentStoreParser;

import CoreParserGrammar;

options
{
    tokenVocab = DocumentStoreLexer;
}

// IDENTIFIER

unquotedIdentifier:                         VALID_STRING
                                            | DOCUMENTSTORE | INCLUDE
                                            | COLLECTION | COLLECTIONFRAGMENT
                                            | SCOPE | ENUMERATION_MAPPING
;

identifier:                                 unquotedIdentifier | STRING
;

// DEFINITIONS
definition:
    (documentStore)*
    EOF
;

documentStore:
    DOCUMENTSTORE qualifiedName
    PAREN_OPEN
        include*
        (
        collection |
        collectionfragment
        )*
    PAREN_CLOSE
;


include: INCLUDE qualifiedName
;

collection:
    COLLECTION collectionIdentifier
    PAREN_OPEN
        propertyDefinitions
    PAREN_CLOSE
;

collectionfragment:
    COLLECTIONFRAGMENT collectionIdentifier
    PAREN_OPEN
        propertyDefinitions
    PAREN_CLOSE
;

propertyDefinitions: propertyDefinition (COMMA propertyDefinition)*
;

propertyDefinition:
    propertyIdentifier
    typeReferenceDefinition (PRIMARY_KEY | NOT_NULL)?
;

collectionIdentifier: identifier
;

propertyIdentifier: identifier
;

typeReferenceDefinition: type | listType
;
type: ( primitiveType | complexType)
;
primitiveType: identifier
;
complexType: (collectionfragment | collectionFragmentPointer)
;
listType: ( BRACKET_OPEN type BRACKET_CLOSE )
;

collectionFragmentPointer: COLLECTIONFRAGMENT qualifiedName
;

//qualifiedName: (packagePath PATH_SEPARATOR)? identifier
//;

//packagePath:                                    identifier (PATH_SEPARATOR identifier)*
//;

