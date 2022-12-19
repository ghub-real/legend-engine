parser grammar AuthenticationStrategyParserGrammar;

import CoreParserGrammar;

options
{
    tokenVocab = AuthenticationStrategyLexerGrammar;
}

identifier:                      VALID_STRING
;

// ----------------------------- DOCUMENT DATABASE CONNECTION AUTH STRATEGY -----------------------------

defaultMongoAuth:                       MONGO_DEFAULT_AUTH
;
testDBAuth:                             TEST_DB_AUTH
;
userNamePasswordAuth:                   USERNAME_PASSWORD_AUTH
                                            BRACE_OPEN
                                                (
                                                    userNamePasswordAuthBaseVaultRef
                                                    | userNamePasswordAuthUserNameVaultRef
                                                    | userNamePasswordAuthPasswordVaultRef
                                                )*
                                            BRACE_CLOSE
;
userNamePasswordAuthBaseVaultRef:       USERNAME_PASSWORD_AUTH_BASE_VAULT_REF COLON STRING SEMI_COLON
;

userNamePasswordAuthUserNameVaultRef:   USERNAME_PASSWORD_AUTH_USERNAME_VAULT_REF COLON STRING SEMI_COLON
;

userNamePasswordAuthPasswordVaultRef:   USERNAME_PASSWORD_AUTH_PASSWORD_VAULT_REF COLON STRING SEMI_COLON
;
