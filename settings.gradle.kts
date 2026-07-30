pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "Mojang"
			url = uri("https://libraries.minecraft.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "flerovium"
