parser grammar DocumentStoreParser;

import CoreParserGrammar;

options
{
    tokenVocab = DocumentStoreLexer;
}

// IDENTIFIER

unquotedIdentifier:                         VALID_STRING
                                            | DOCUMENTSTORE | INCLUDE
                                            | COLLECTION | DOCUMENTFRAGMENT
                                            | SCOPE | ENUMERATION_MAPPING | BINDING
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
        fieldDefinitions
    PAREN_CLOSE
;

collectionfragment:
    DOCUMENTFRAGMENT collectionIdentifier
    PAREN_OPEN
        fieldDefinitions
    PAREN_CLOSE
;

fieldDefinitions: fieldDefinition (COMMA fieldDefinition)*
;

fieldDefinition:
    fieldIdentifier
    typeReferenceDefinition (PRIMARY_KEY | NOT_NULL)?
;

collectionIdentifier: identifier
;

fieldIdentifier: identifier
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

collectionFragmentPointer: DOCUMENTFRAGMENT qualifiedName
;

qualifiedName: packagePath? identifier
;

