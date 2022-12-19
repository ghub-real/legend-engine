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
                                                        localMongoDSPTestDataSetupCSV |
                                                        localMongoDSPTestDataSetupNoSQLS
                                                    )*
                                                BRACE_CLOSE
;
localMongoDSPTestDataSetupCSV:              LOCAL_MONGO_DSP_TEST_DATA_SETUP_CSV COLON STRING SEMI_COLON
;

localMongoDSPTestDataSetupNoSQLS:           LOCAL_MONGO_DSP_TEST_DATA_SETUP_SQLS COLON noSqlsArray SEMI_COLON
;

noSqlsArray:                                BRACKET_OPEN ( STRING (COMMA STRING)* )? BRACKET_CLOSE
;

dbPort:                                     PORT COLON INTEGER SEMI_COLON
;

dbHost:                                     HOST COLON STRING SEMI_COLON
;

dbName:                                     NAME COLON STRING SEMI_COLON
;

cloudType:                                  CLOUDTYPE COLON STRING SEMI_COLON
;

clusterID:                                  CLUSTER_ID COLON STRING SEMI_COLON
;