Feature: Autenticacao de usuarios

  Scenario: Registro de novo usuario com sucesso
    Given que o usuario "Ana" com email "ana@test.com" e senha "senha123" nao existe
    When o usuario realiza o registro com esses dados
    Then o sistema retorna um token JWT valido

  Scenario: Login com credenciais validas
    Given que o usuario "Ana" com email "ana@test.com" ja esta cadastrado com senha "senha123"
    When o usuario realiza o login com email "ana@test.com" e senha "senha123"
    Then o sistema retorna um token JWT valido

  Scenario: Registro com email duplicado
    Given que o usuario com email "duplicado@test.com" ja esta cadastrado
    When o usuario tenta se registrar com o mesmo email "duplicado@test.com"
    Then o sistema retorna um erro de conflito
