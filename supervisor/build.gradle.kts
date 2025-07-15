import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "eu.deyanix"
version = "0.0.1-SNAPSHOT"

tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.register<Exec>("buildFrontend") {
	workingDir = file("src/vue")
	commandLine("yarn.cmd")
	commandLine("yarn.cmd", "run", "build")
}

tasks.register<Copy>("copyFrontend") {
	dependsOn("buildFrontend")
	from("src/vue/dist/spa")
	into("src/main/resources/static")
}

tasks.getByName("processResources") {
	dependsOn("copyFrontend")
}

tasks.named<BootJar>("bootJar") {
	exclude("application.properties")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fazecast:jSerialComm:2.11.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
