plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
    id("java")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

repositories {
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
    mavenCentral()
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create("flerovium") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    
    // 添加 Fabric Loader（提供 FabricLoader API）
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}") {
        isTransitive = false // 防止重复依赖
    }
    
    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
    
    // Mixin（显式添加，确保编译时可用）
    compileOnly("org.spongepowered:mixin:0.8.5") {
        isTransitive = false
    }
    
    // 其他依赖
    compileOnly("maven.modrinth:sodium:2Yom1N68")
    compileOnly("maven.modrinth:iris:oaD6KQls")
}

tasks.processResources {
    val version = project.version.toString()
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to version))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25) // 26.2 需要 Java 25
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.name}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}