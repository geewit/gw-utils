---
name: java-architecture
description: Java 25 + Spring Boot 4 + Gradle 9 multi-module architecture skill
---

# Java Architecture Skill

## Goal

Implement code that fits a Java 25 / Spring Boot 4 / Gradle 9 multi-module architecture.

## Rules

- Prefer explicit architecture over convenience hacks
- Respect module boundaries
- Prefer small, reviewable changes
- Use import statements instead of fully-qualified class names unless required by naming conflict
- Avoid introducing unnecessary frameworks

## Multi-module expectations

- app: JavaFX desktop client
- embedded: WebFlux + SQLite + JDBC
- server/edge: WebFlux + MySQL 8 + R2DBC
- server/cloud: WebFlux + MySQL 8 + R2DBC
- core: shared kernel/capabilities
- persistence/spec: persistence contracts and shared mapping abstractions

## Do not

- Do not introduce JPA/Hibernate
- Do not replace WebFlux with MVC
- Do not break build/deploy scripts
- Do not move classes across modules unless necessary