plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":star-common"))
    implementation(project(":star-reflection"))
    implementation(project(":star-config"))
    implementation(project(":star-chat"))
    implementation(project(":star-data"))
    implementation(project(":star-skins"))
    implementation(project(":star-items"))
    implementation(project(":star-inventories"))
    // star-protection excluded from shadow JAR — external plugin APIs not available on Maven
    // compileOnly(project(":star-protection"))
    implementation(project(":star-recipes"))
    implementation(project(":star-updater"))
    implementation(project(":star-scheduling"))
    implementation(project(":star-swm"))

    implementation(project(":star-gui"))
    implementation(project(":star-vfx"))
    implementation(project(":star-economy"))
    implementation(project(":star-commands"))
    implementation(project(":star-world"))
    compileOnly("com.mojang:authlib:6.0.52")
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set("star-api")

    // Relocate all Star packages into shaded JAR
    relocate("dev.yanianz.star", "dev.yanianz.star")

    // Exclude unnecessary files
    exclude("META-INF/maven/**")
    exclude("META-INF/LICENSE*")
    exclude("META-INF/NOTICE*")
    exclude("module-info.class")
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
