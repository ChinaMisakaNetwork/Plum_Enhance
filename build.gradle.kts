plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.4"
}

group = "com.sakurawald"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.8.6")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.13.1")
    // https://mvnrepository.com/artifact/net.sourceforge.htmlunit/htmlunit
    implementation("net.sourceforge.htmlunit:htmlunit:2.50.0")
    implementation("com.alibaba:fastjson:1.2.76")
    implementation("mysql:mysql-connector-java:8.0.11")
    implementation(fileTree(mapOf("dir" to "libs","include" to listOf("*.jar"))))
}
