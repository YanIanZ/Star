dependencies {
    compileOnly(project(":star-common"))
    compileOnly(project(":star-database"))
    compileOnly("org.mongodb:mongodb-driver-sync:5.1.0")
    compileOnly(project(":star-redis"))
    compileOnly(project(":star-cache"))
    compileOnly("com.google.code.gson:gson:2.11.0")
    testImplementation(project(":star-common"))
    testImplementation(project(":star-database"))
    testImplementation("org.mongodb:mongodb-driver-sync:5.1.0")
    testImplementation(project(":star-redis"))
    testImplementation(project(":star-cache"))
}
