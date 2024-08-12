import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("io.vertx.vertx-plugin") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

fun getVersionName():Any{
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine = listOf("git","describe","--tags","--abbrev=0")
            standardOutput = stdout
        }
        stdout.toString(Charset.defaultCharset()).trim()
    }catch (exception:Exception){
        "1.0.0-SNAPSHOT"
    }
}

group = "com.rose.blog"
version = getVersionName()

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-core:4.5.9"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-mongo-client")
    implementation("io.vertx:vertx-mail-client")
    implementation("io.vertx:vertx-health-check")
    implementation("com.auth0:java-jwt:4.2.2")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("io.vertx:vertx-auth-oauth2:4.4.4")
    implementation("commons-logging:commons-logging:1.2")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.springframework.security:spring-security-crypto:5.6.4")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("org.json:json:20231013")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testImplementation("io.vertx:vertx-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    testImplementation(kotlin("test"))
}

val mainVerticleName = "com.rose.blog.BlogService"
val watchForChange = "src/**/"
val doChange = "$projectDir/gradlew classes"

vertx{
    mainVerticle = mainVerticleName
}

application{
    executableDir = "jarFile"
    val mainClassName = "io.vertx.core.Launcher"
    mainClass.set(mainClassName)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
