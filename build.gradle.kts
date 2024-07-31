import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.qi"
version = "1.0.1"

repositories {
    maven(  "https://maven.aliyun.com/repository/central")
    maven(  "https://maven.aliyun.com/repository/public")
    maven(  "https://maven.aliyun.com/repository/google")
    maven(  "https://maven.aliyun.com/repository/jcenter")
    maven(  "https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.6")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("242.*")
    }

    signPlugin {
        val properties = Properties()
        properties.load(file("local.properties").inputStream())
        certificateChainFile.set(file("certificate/chain.crt"))
        privateKeyFile.set(file("certificate/private.pem"))
        password.set(properties["privateKeyPassword"] as String)
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    implementation("org.freemarker:freemarker:2.3.31")
}
