dependencies {
    compileOnly(project(":star-common"))
    compileOnly(project(":star-cache"))
    compileOnly("redis.clients:jedis:5.1.3")
    testImplementation(project(":star-common"))
    testImplementation("redis.clients:jedis:5.1.3")
}
