plugins {
    id("fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.name)
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

loom {
    runs {
        named("client") {
            // 如果需要指定 sourceSet
        }
        named("server") {
            // 如果需要指定 sourceSet
        }
    }
}

dependencies {
    // Minecraft
    "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
    "mappings"("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

    // ⚠️ 不要手动添加 fabric-loader，Loom 会自动管理！
    // modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")  // ❌ 已注释

    // Fabric API
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")

    // 第三方模组依赖（编译时可选）
    "modCompileOnly"("maven.modrinth:sodium:2Yom1N68")
    "modCompileOnly"("maven.modrinth:iris:oaD6KQls")
}

tasks.processResources {
    val version = project.version.toString()
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to version))
    }
}

// 如果有其他自定义任务处理 projectName，可以类似这样写：
// tasks.register("yourCustomTask") {
//     val projectName = project.name
//     inputs.property("projectName", projectName)
// }

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        // 如果需要发布到仓库，在这里添加
    }
}