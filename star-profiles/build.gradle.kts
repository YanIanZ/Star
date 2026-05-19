dependencies {
    compileOnly(project(":star-common"))
    compileOnly(project(":star-database"))
    compileOnly("org.mongodb:mongodb-driver-sync:5.1.0")
    testImplementation(project(":star-common"))
    testImplementation(project(":star-database"))
    testImplementation("org.mongodb:mongodb-driver-sync:5.1.0")
}
