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
DOCUMENTFRAGMENT : 'DocumentFragment';

PRIMARY_KEY : 'PRIMARY KEY';
NOT_NULL: 'NOT NULL';
PRIMARY_KEY_CMD : '~primaryKey';
PARTIAL_KEY : 'PARTIAL KEY';
PARTIAL_KEY_CMD : '~partialKey';
BINDING: 'Binding';
SCOPE: 'scope';
ENUMERATION_MAPPING: 'EnumerationMapping';

INTEGER: ('+' | '-')? (Digit)+;
FLOAT: ('+' | '-')? (Float)+;
QUOTED_STRING:   ('"' ( EscSeq | ~["\r\n] )*  '"' ) ;



//mode ISLAND_BLOCK;
//INNER_CURLY_BRACKET_OPEN : '{' -> pushMode (ISLAND_BLOCK);
//CONTENT: (~[{}])+;
//INNER_CURLY_BRACKET_CLOSE: '}' -> popMode;

