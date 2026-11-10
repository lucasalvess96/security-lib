# Login com Google

A segurança é fornecida pela biblioteca `com.lib:security-lib:0.0.1-SNAPSHOT`.
Ela registra automaticamente `SecurityConfig`, `SessionController` e `CsrfController`.
Não é necessário usar `@ComponentScan` ou `@Import` na aplicação consumidora.

Antes de compilar a API em uma máquina nova, instale a biblioteca:

```powershell
.\mvnw.cmd -f ..\..\security-lib\pom.xml clean install
```

As credenciais Google e `spring.security.oauth2.redirect-url` continuam na aplicação.
As propriedades opcionais `alga.security.public-paths` (lista separada por vírgulas,
padrão `/api/public,/error`) e `alga.security.api-pattern` (padrão `/api/**`)
permitem reutilizar a biblioteca em outras APIs. As demais rotas exigem autenticação.
Uma aplicação pode fornecer seu próprio bean `SecurityFilterChain` para substituir
as regras padrão da biblioteca. Os endpoints `/api/session` e `/api/csrf` são fixos
e devem continuar protegidos nas configurações personalizadas.

A aplicação usa Spring Security OAuth2 Client com OpenID Connect e sessão HTTP.
O Google autentica o usuário e o Spring mantém a autenticação no servidor,
identificada pelo cookie `JSESSIONID`. A API não emite JWT próprio.

## Configuração

Defina `CLIENT_ID` e `CLIENT_SECRET` com as credenciais OAuth de uma aplicação
Web no Google Cloud. Cadastre como URI de redirecionamento autorizada:

```text
http://localhost:8080/login/oauth2/code/google
```

Defina também `URL_DATABASE` e `PASSWORD_DATABASE`. As variáveis podem vir do
ambiente ou de `.env` na raiz do projeto (diretório de trabalho da execução).
Não versione credenciais. `JWT_SECRET_VALUE` não é mais utilizado.

`OAUTH2_REDIRECT_URL` define a página de destino quando não existe uma requisição
salva antes do login; o padrão local é `http://localhost:4200/login`.

## Uso no navegador

1. Navegue para `http://localhost:8080/oauth2/authorization/google` para iniciar o login.
2. Após o retorno, o navegador envia o cookie de sessão nas requisições seguintes.
   O redirecionamento não contém token. Remova do frontend a leitura de `?token=`
   e o envio de um Bearer token próprio.
3. Com a sessão autenticada, faça `GET /api/csrf`. Use o campo `token` no cabeçalho
   indicado por `headerName` para POST, PUT, PATCH e DELETE.
4. Para sair, envie `POST /logout` com o token CSRF. A sessão será invalidada e
   a resposta será 204, sem redirecionamento.
   Obtenha um novo token CSRF depois de um novo login.

`GET /api/session` responde 204 para uma sessão autenticada e 401 para um cliente
sem sessão. `/api/**` retorna 401 em vez de
redirecionar chamadas não autenticadas para uma página HTML.

O projeto `angular-app-v18` usa um proxy na porta 4200: `/backend/*` é encaminhado
à API sem esse prefixo. O callback `/login/oauth2/code/*` também é encaminhado.
Nesse fluxo, cadastre no Google Cloud a URI autorizada
`http://localhost:4200/login/oauth2/code/google` e inicie o login pelo Angular.
O Host é preservado para o Spring gerar o callback na origem do frontend.

Se o frontend fizer chamadas
diretas entre origens diferentes, será necessário configurar CORS para a origem
exata e habilitar o envio de cookies no cliente. Essa configuração não está
incluída neste projeto. Em produção, configure o reverse proxy para os caminhos
acima, publique por HTTPS e configure o cookie de
sessão como Secure (`server.servlet.session.cookie.secure=true`).

## Referências

- [OAuth2 Login / OpenID Connect](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html)
- [Configuração do Google](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html)
- [CSRF para aplicações JavaScript](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
- [Guia Spring Boot e OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
