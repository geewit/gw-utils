# Spring WebFlux Security Testing Patterns

Quick-reference for testing secured endpoints and secured reactive services in Spring Boot WebFlux applications.

`@WebFluxTest` auto-configures `WebTestClient`, which is the primary tool for HTTP security testing in WebFlux applications. Do not use `MockMvc` for WebFlux endpoint tests.

---

## `@WebFluxTest` + `WebTestClient`

Use `@WebFluxTest` for controller-slice tests in reactive applications.

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(OrderController.class)
class OrderControllerSecurityTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    OrderService orderService;

    @Test
    void getOrder_noAuth_returns401() {
        webTestClient.get()
            .uri("/orders/1")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
```

Testing all three paths for every secured endpoint:

authenticated + authorized → 200 / expected success status
authenticated + unauthorized → 403
unauthenticated → 401

@WithMockUser

@WithMockUser still works in Spring Security tests and is especially useful for reactive method security tests, and can also be used in HTTP tests when the Spring Security test context is active.

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(AdminController.class)
class AdminControllerSecurityTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    AdminService adminService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_asAdmin_returns200() {
        webTestClient.get()
            .uri("/admin")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoint_asUser_returns403() {
        webTestClient.get()
            .uri("/admin")
            .exchange()
            .expectStatus().isForbidden();
    }
}
```
roles vs authorities — The ROLE_ Prefix
// roles = "ADMIN"       -> authority = "ROLE_ADMIN"   (prefix added automatically)
// authorities = "ADMIN" -> authority = "ADMIN"        (no prefix added)

// Use `roles` when config uses hasRole("ADMIN")
// Use `authorities` when config uses hasAuthority("products:read")

Common mistake:

@WithMockUser(roles = "ROLE_ADMIN")

This becomes ROLE_ROLE_ADMIN, which does not match hasRole("ADMIN").

@WithUserDetails

Use @WithUserDetails when the application needs a real principal loaded from a real ReactiveUserDetailsService / user-details-backed authentication setup rather than a synthetic mock user.

In practice, this is more useful when your code inspects principal fields beyond username/roles.

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;

@Import(ProfileControllerTest.TestSecurityConfig.class)
class ProfileControllerTest {

    @Test
    @WithUserDetails("alice@example.com")
    void profile_usesRealUserDetails() {
        // invoke controller or service and assert principal-dependent behavior
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        MapReactiveUserDetailsService userDetailsService() {
            return new MapReactiveUserDetailsService(
                User.withUsername("alice@example.com")
                    .password("{noop}password")
                    .roles("USER")
                    .build()
            );
        }
    }
}
```
SecurityMockServerConfigurers — Per-request Authentication

For WebFlux HTTP tests, prefer request/client-level mutation with WebTestClient.

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

webTestClient
.mutateWith(mockUser("alice").roles("USER"))
.get()
.uri("/orders")
.exchange()
.expectStatus().isOk();
```

This is often more explicit than using class/method annotations when you need different authentication setups within the same test class.

Custom Authentication Token
```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

webTestClient
.mutateWith(mockAuthentication(authentication))
.get()
.uri("/orders")
.exchange()
.expectStatus().isOk();
```
Use this when you need full control over the Authentication object.

CSRF — Required for Mutating Requests

For session-based security, mutating requests usually require a CSRF token.

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

webTestClient
.mutateWith(mockUser("alice").roles("USER"))
.mutateWith(csrf())
.post()
.uri("/orders")
.exchange()
.expectStatus().isCreated();
```
Without csrf(), the response is commonly 403 Forbidden.

Disabling CSRF for Stateless APIs

For JWT-based stateless APIs, disable CSRF in the test security configuration if that matches production behavior.

```java
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
class DisableCsrfConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .anyExchange().authenticated()
            )
            .build();
    }
}
```

JWT Resource Server Testing with mockJwt()

For reactive resource-server tests, mockJwt() is the canonical way to test JWT-secured endpoints without a real authorization server.

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

webTestClient
.mutateWith(mockJwt())
.get()
.uri("/messages")
.exchange()
.expectStatus().isOk();
```
Customizing Claims
```java
webTestClient
.mutateWith(mockJwt().jwt(jwt -> jwt
.subject("alice")
.claim("scope", "message:read")
.claim("iss", "https://idp.example.org")))
.get()
.uri("/messages")
.exchange()
.expectStatus().isOk();
```

Explicit Authorities

```java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
webTestClient
.mutateWith(mockJwt().authorities(
new SimpleGrantedAuthority("SCOPE_message:read")))
.get()
.uri("/messages")
.exchange()
.expectStatus().isOk();
```
Missing Scope → 403
```java
webTestClient
.mutateWith(mockJwt())
.get()
.uri("/admin/messages")
.exchange()
.expectStatus().isForbidden();
```
Opaque Token Testing with mockOpaqueToken()

If the resource server uses opaque bearer tokens rather than JWTs, use mockOpaqueToken().

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;

webTestClient
.mutateWith(mockOpaqueToken())
.get()
.uri("/introspected-endpoint")
.exchange()
.expectStatus().isOk();
```
OAuth2 Login / OIDC Login Testing

For WebFlux OAuth2 login flows, use the reactive mock configurers rather than servlet-side request post-processors.

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

webTestClient
.mutateWith(mockOAuth2Login())
.get()
.uri("/profile")
.exchange()
.expectStatus().isOk();

webTestClient
.mutateWith(mockOidcLogin().idToken(token -> token
.subject("user-123")
.claim("email", "alice@example.com")))
.get()
.uri("/profile")
.exchange()
.expectStatus().isOk();
```

Use these for login-based applications, not for resource-server bearer token tests.

Reactive Method Security — @PreAuthorize

Reactive method security relies on Reactor Context. The secured method should return a reactive type like Mono or Flux.

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.test.StepVerifier;

@SpringBootTest(classes = {MessageService.class, MethodSecurityConfig.class})
class MessageServiceMethodSecurityTest {

    @Autowired
    MessageService messageService;

    @Test
    void message_whenUnauthenticated_thenDenied() {
        StepVerifier.create(messageService.findSecretMessage())
            .expectError(AccessDeniedException.class)
            .verify();
    }

    @Test
    @WithMockUser(roles = "USER")
    void message_whenUser_thenDenied() {
        StepVerifier.create(messageService.findSecretMessage())
            .expectError(AccessDeniedException.class)
            .verify();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void message_whenAdmin_thenAllowed() {
        StepVerifier.create(messageService.findSecretMessage())
            .expectNext("secret")
            .verifyComplete();
    }
}
```
Method Security Configuration

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@Configuration
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
class MethodSecurityConfig {
}
```


Reactive Principal Access

When your controller or service reads the authenticated principal from Reactor context or via method injection, prefer tests that verify the actual principal shape and values.

Examples:
```java
Mono<Principal>
@AuthenticationPrincipal
ReactiveSecurityContextHolder.getContext()
```
If your logic depends on a custom principal type, prefer mockAuthentication(...) with a real token/principal object over mockUser().