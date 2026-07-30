plugins {
    id 'net.fabricmc.fabric-loom' version "${loom_version}"
    id 'maven-publish'
}

version = project.mod_version
group = project.maven_group

repositories {
    mavenCentral()

    // Modrinth Maven (Sodium / Iris)
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

loom {
    mods {
        "flerovium" {
            sourceSet sourceSets.main
        }
    }
}

dependencies {
    // Minecraft
    minecraft "com.mojang:minecraft:${project.minecraft_version}"

    // Fabric Loader
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

    // Fabric API
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_api_version}"

    // MixinExtras（打包进 jar + 注解处理）
    include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:${project.mixin_extras_version}")))

    // Sodium（仅编译引用，不打包）
    modCompileOnly "maven.modrinth:sodium:2Yom1N68"

    // Iris（仅编译引用，不打包）
    modCompileOnly "maven.modrinth:iris:oaD6KQls"
}

processResources {
    def version = project.version
    inputs.property "version", version

    filesMatching("fabric.mod.json") {
        expand "version": version
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.release = 25
    it.options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

jar {
    def projectName = project.name
    inputs.property "projectName", projectName

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}

publishing {
    publications {
        create("mavenJava", MavenPublication) {
            from components.java
        }
    }
    repositories {}
}