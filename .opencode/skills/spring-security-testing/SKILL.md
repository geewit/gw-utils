---
name: spring-webflux-security-testing
description: "Use this skill when the user asks to test authentication or authorization rules in a Spring WebFlux application, test a secured reactive endpoint, test role-based access control with WebTestClient, set up @WithMockUser or @WithUserDetails for reactive security, test CSRF in WebFlux, test JWT resource server endpoints with mockJwt(), test opaque token endpoints with mockOpaqueToken(), test OAuth2 or OIDC login in WebFlux, test custom reactive principals, or test @PreAuthorize method security in reactive services. Also triggers on: @WebFluxTest, WebTestClient, SecurityMockServerConfigurers, mockUser(), mockJwt(), mockOpaqueToken(), mockOAuth2Login(), mockOidcLogin(), csrf(), @EnableWebFluxSecurity, @EnableReactiveMethodSecurity, ReactiveSecurityContextHolder, JwtAuthenticationToken, SCOPE_."
---

# Spring WebFlux Security Testing

**Signals**: `@WebFluxTest`, `WebTestClient`, `SecurityMockServerConfigurers`, `mockUser()`, `mockJwt()`, `mockOpaqueToken()`, `mockOAuth2Login()`, `mockOidcLogin()`, `csrf()`, `@WithMockUser`, `@WithUserDetails`, `@EnableReactiveMethodSecurity`, `ReactiveSecurityContextHolder`, `SCOPE_`

## Tested With

- Spring Boot 4.x
- Spring Security 7.x
- Spring Framework 7.x
- JUnit 6 / Jupiter

## Core Mental Model

- Spring WebFlux security runs through a **`WebFilter` chain**, not the Servlet `Filter` chain.
- HTTP endpoint tests should prefer **`WebTestClient`**, not `MockMvc`.
- `@WebFluxTest` is the slice test for WebFlux controllers and auto-configures `WebTestClient`.
- Reactive method security relies on **Reactor `Context`** via `ReactiveSecurityContextHolder`, so secured methods must return reactive types like `Mono` or `Flux`.

## Do NOT Use This Skill When

- Testing Servlet MVC security with `MockMvc` → use an MVC/Servlet security testing skill
- Writing pure unit tests with no Spring Security context involved
- Testing non-reactive service-layer method security in a Servlet stack

## When to Read References

| Situation                                                                      | Read                                      |
|--------------------------------------------------------------------------------|-------------------------------------------|
| `@WebFluxTest` + `WebTestClient` setup for secured endpoints                   | `references/security-testing-patterns.md` |
| `@WithMockUser` / `@WithUserDetails` in reactive tests                         | `references/security-testing-patterns.md` |
| `roles` vs `authorities` and the `ROLE_` prefix trap                           | `references/security-testing-patterns.md` |
| Per-request auth using `mockUser()` / `mockAuthentication()`                   | `references/security-testing-patterns.md` |
| CSRF on POST/PUT/PATCH/DELETE in WebFlux tests                                 | `references/security-testing-patterns.md` |
| JWT resource server testing with `mockJwt()`                                   | `references/security-testing-patterns.md` |
| Opaque token testing with `mockOpaqueToken()`                                  | `references/security-testing-patterns.md` |
| OAuth2 login / OIDC login testing with `mockOAuth2Login()` / `mockOidcLogin()` | `references/security-testing-patterns.md` |
| Custom reactive principals or custom authentication tokens                     | `references/security-testing-patterns.md` |
| Reactive `@PreAuthorize` / method security testing with `Mono` / `Flux`        | `references/security-testing-patterns.md` |