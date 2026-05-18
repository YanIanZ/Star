plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0"
}

group = "dev.yanianz"
version = "1.0.0"

allprojects {
    group = "dev.yanianz"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io/")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.isIncremental = true
        options.isFork = true
        options.forkOptions.memoryMaximumSize = "512M"
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        failOnNoDiscoveredTests = false
        testLogging {
            events("passed", "skipped", "failed")
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "dev.yanianz"
            version = rootProject.version.toString()
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/yanianz/star")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

// Dependencies shared by all modules
subprojects {
    dependencies {
        "compileOnly"("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        // Test dependencies need paper-api and jsr305 available at test compile time
        "testCompileOnly"("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
        "testCompileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        "testImplementation"("org.junit.jupiter:junit-jupiter:5.10.3")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        "testImplementation"("org.mockito:mockito-core:5.14.1")
        "testImplementation"("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.41.1")
    }
}
