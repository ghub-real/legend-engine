parser grammar DataSourceSpecificationParserGrammar;

import CoreParserGrammar;

options
{
    tokenVocab = DataSourceSpecificationLexerGrammar;
}

identifier:                      VALID_STRING
;

// ----------------------------- DOCUMENT STORE CONNECTION DATASOURCE SPEC -----------------------------

staticDatasourceSpecification:              STATIC_DSP
                                                BRACE_OPEN
                                                    (
                                                        dbName
                                                        | dbHost
                                                        | dbPort
                                                    )*
                                                BRACE_CLOSE
;

localMongoDatasourceSpecification:             LOCAL_MONGO_DSP
                                                BRACE_OPEN
                                                    (
                                                        dbName
                                                        | dbHost
                                                        | dbPort
                                                    )*
                                                BRACE_CLOSE
;

dbPort:                                     PORT COLON INTEGER SEMI_COLON
;

dbHost:                                     HOST COLON STRING SEMI_COLON
;

dbName:                                     NAME COLON STRING SEMI_COLON
;

clusterID:                                  CLUSTER_ID COLON STRING SEMI_COLON
;