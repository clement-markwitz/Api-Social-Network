plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.2.0.6526"
    id("jacoco")
}

group = "fr.univartois.butinfo.s5"
version = "0.0.1-SNAPSHOT"
description = "Une api-rest"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

sonar {
    properties {
        property("sonar.projectKey", "api-rest-groupe-5")
        property("sonar.host.url", "https://sonarqube.univ-artois.fr")
    }
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    testImplementation("io.rest-assured:rest-assured:5.4.0")

	// Authentification
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	implementation("io.jsonwebtoken:jjwt-impl:0.13.0")
	implementation("io.jsonwebtoken:jjwt-jackson:0.13.0")

	// WebSocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")

}

tasks.withType<Test> {
	useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
