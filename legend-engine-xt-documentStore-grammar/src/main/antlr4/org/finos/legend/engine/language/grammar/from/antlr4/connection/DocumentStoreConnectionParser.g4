parser grammar DocumentStoreConnectionParser;

import CoreParserGrammar;

options
{
    tokenVocab = DocumentStoreConnectionLexer;
}


// -------------------------------------- IDENTIFIER -------------------------------------

identifier:                             VALID_STRING | STRING
                                        | STORE
                                        | TYPE | NONRELATIONAL_DATASOURCE_SPEC | NONRELATIONAL_AUTH_STRATEGY
                                        | DB_TIMEZONE | QUOTE_IDENTIFIERS | BASE_URL
;

// -------------------------------------- DEFINITION -------------------------------------

definition:                                 (
                                                     connectionStore
                                                     | dbType
                                                     | dbConnectionTimezone
                                                     | dbQuoteIdentifiers
                                                     | nonRelationalDBAuth
                                                     | nonRelationalDBDatasourceSpec
                                                     | baseUrl
                                                 )*
;
connectionStore:                        STORE COLON qualifiedName SEMI_COLON
;
dbConnectionTimezone:                   DB_TIMEZONE COLON TIMEZONE SEMI_COLON
;
dbQuoteIdentifiers:                     QUOTE_IDENTIFIERS COLON BOOLEAN SEMI_COLON
;
dbType:                                 TYPE COLON identifier SEMI_COLON
;

nonRelationalDBAuth:                       NONRELATIONAL_AUTH_STRATEGY COLON specification SEMI_COLON
;

nonRelationalDBDatasourceSpec:             NONRELATIONAL_DATASOURCE_SPEC COLON specification SEMI_COLON
;


specification:                specificationType (specificationValueBody)?
;

specificationType:            VALID_STRING
;

specificationValueBody:       BRACE_OPEN (specificationValue)*
;

specificationValue:           SPECIFICATION_BRACE_OPEN | SPECIFICATION_CONTENT | SPECIFICATION_BRACE_CLOSE
;

baseUrl:                       BASE_URL COLON identifier SEMI_COLON
;