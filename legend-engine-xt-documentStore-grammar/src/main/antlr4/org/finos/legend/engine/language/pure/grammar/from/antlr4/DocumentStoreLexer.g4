lexer grammar DocumentStoreLexer;

import CoreLexerGrammar;


DOCUMENTSTORE : 'DocumentStore';
INCLUDE : 'include' ;
JOIN : 'Join' ;
COLLECTION : 'Collection' ;
COLLECTIONFRAGMENT: 'CollectionFragment';
FILTER: 'Filter';
ENUMERATION_MAPPING: 'EnumerationMapping';
OBJECT: 'Object';
ARRAY: 'Array';
AND : 'and' ;
OR : 'or' ;
CONSTRAINT : 'constraint' ;
PROCESSING_MILESTONING : 'processing';


MAINCOLLECTION_CMD: '~mainCollection';
FILTER_CMD: '~filter';
PRIMARY_KEY_CMD : '~primaryKey';
PRIMARY_KEY: 'PRIMARY KEY';
NOT_NULL: 'NOT NULL';

NOT_EQUAL:                                  '<>';
QUOTED_STRING:                              ('"' ( EscSeq | ~["\r\n] )*  '"');
