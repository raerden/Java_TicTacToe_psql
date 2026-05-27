plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
}

group = "ru.tictactoe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-web") // Главная зависимость для веб-приложения
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Для работы с базой данных (JPA/Hibernate)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // JDBC-драйвер для PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}