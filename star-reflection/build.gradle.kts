dependencies {
    compileOnly(project(":star-common"))
    compileOnly("io.papermc:paperlib:1.0.8")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation(project(":star-common"))
    testCompileOnly("com.google.code.findbugs:jsr305:3.0.2")
}
