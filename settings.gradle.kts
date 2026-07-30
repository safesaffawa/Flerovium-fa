pluginManagement {
	repositories {
		maven {
			name = 'Fabric'
			url = 'https://maven.fabricmc.net/'
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "flerovium"

gradle.includedBuild("sodium") {
    dir = file("sodium")
    name = "sodium"
}

gradle.includedBuild("iris") {
    dir = file("iris")
    name = "iris"
}
