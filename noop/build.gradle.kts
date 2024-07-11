plugins {
    java
    `maven-publish`
}

group = "org.redsxi.mc.mvapi"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}

dependencies {
}

publishing {
    repositories {
        maven {
            name = "LocalRepo"
            url = uri("file://D:/MavenRepo")
        }
    }

    publications {
        create<MavenPublication>("api") {
            groupId = "org.redsxi.mc.mvapi"
            artifactId = "api"
            version = project.version as String

            from(components["java"])
        }
    }
}