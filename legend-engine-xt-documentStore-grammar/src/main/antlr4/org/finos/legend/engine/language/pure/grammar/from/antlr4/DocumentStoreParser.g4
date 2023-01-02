parser grammar DocumentStoreParser;

import CoreParserGrammar;

options
{
    tokenVocab = DocumentStoreLexer;
}

// IDENTIFIER

unquotedIdentifier:                         VALID_STRING
                                            | DOCUMENTSTORE
                                            | INCLUDE | JOIN | COLLECTION | COLLECTIONFRAGMENT
                                            | FILTER | ENUMERATION_MAPPING
                                            | OBJECT | ARRAY
                                            | AND | OR | CONSTRAINT | PROCESSING_MILESTONING
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
        collectionFragment
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

collectionFragment:
    COLLECTIONFRAGMENT collectionFragmentIdentifier
    PAREN_OPEN
        propertyDefinitions
    PAREN_CLOSE
;

propertyDefinitions: propertyDefinition (COMMA propertyDefinition)*
;

propertyDefinition:
    propertyIdentifier
    (typeReferenceDefinition (PRIMARY_KEY | NOT_NULL)? | arrayDefinition)
;

collectionIdentifier: identifier
;

collectionFragmentIdentifier: identifier
;

propertyIdentifier: identifier
;

typeReferenceDefinition: type | listType
;
type: ( primitiveType | complexType)
;
primitiveType: identifier
;
complexType: (collectionFragment | collectionFragmentPointer )
;
listType: ( BRACKET_OPEN type BRACKET_CLOSE )
;

elementsArray:  PAREN_OPEN
                    (type)?
                PAREN_CLOSE
;

arrayDefinition: ARRAY elementsArray
;

collectionFragmentPointer: COLLECTIONFRAGMENT qualifiedName
;

//qualifiedName: (packagePath PATH_SEPARATOR)? identifier
//;

//packagePath:                                    identifier (PATH_SEPARATOR identifier)*
//;

//// -------------------------------------- DOCUMENT STORE MAPPING --------------------------------------
//
//mapping:                                    mappingPrimaryKey?
//                                            mappingMainCollection?
//;
//
//mappingPrimaryKey:                          PRIMARY_KEY_CMD
//                                                PAREN_OPEN
//                                                    (operation (COMMA operation)*)?
//                                                PAREN_CLOSE
//;
//
//mappingMainCollection:                     MAINCOLLECTION_CMD databasePointer mappingScopeInfo
//;
//
//// -------------------------------------- BUILDING BLOCK --------------------------------------
//
//databasePointer:                            BRACKET_OPEN qualifiedName BRACKET_CLOSE
//;