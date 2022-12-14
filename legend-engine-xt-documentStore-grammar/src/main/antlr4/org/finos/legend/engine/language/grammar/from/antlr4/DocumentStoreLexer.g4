lexer grammar DocumentStoreLexer;

import CoreLexerGrammar;

AND : 'and' ;
OR : 'or' ;
CONSTRAINT : 'constraint' ;
PROCESSING_MILESTONING : 'processing';
DOCUMENTSTORE : 'DocumentStore';

INCLUDE : 'include' ;
DOC_EQUAL: 'eq';
DOC_NOTEQUAL : 'neq';
DOC_GT : 'gt';
DOC_GTE : 'gte';
DOC_IN : 'in';
DOC_NOTIN : 'nin';
DOC_LT : 'lt';
DOC_LTE : 'lte';

JOIN : 'Join' ;
MAINCOLLECTIONCMD : '~mainCollection';
COLLECTION : 'Collection' ;
COLLECTIONFRAGMENT : 'CollectionFragment';

PRIMARY_KEY : 'PRIMARY KEY';
NOT_NULL: 'NOT NULL';
PRIMARY_KEY_CMD : '~primaryKey';
PARTIAL_KEY : 'PARTIAL KEY';
PARTIAL_KEY_CMD : '~partialKey';
SCOPE: 'scope';
ENUMERATION_MAPPING: 'EnumerationMapping';

//QUOTED_STRING:   ('"' ( EscSeq | ~["\r\n] )*  '"' ) ;
//STRING:   ('\'' ( EscSeq | ~['\r\n] )*  '\'' ) ;
INTEGER: (Digit)+;
FLOAT : ('+' | '-')? (Digit)* '.' (Digit)+ ( ('e' | 'E') ('+' | '-')? (Digit)+)? ('f' | 'F')?;
VALID_STRING: (Letter | Digit | '_' ) (Letter | Digit | '_' | '$')*;
//fragment Digit:                         [0-9];
//fragment Letter:                        [A-Za-z];
