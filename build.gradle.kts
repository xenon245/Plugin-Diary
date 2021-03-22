plugins {
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    `maven-publish`
}

group = "com.github.monulo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io/")
    maven("https://papermc.io/repo/repository/maven-public")
    maven("https://maven.enginehub.org/repo")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation("com.github.monulo:tap:3.3.3")
    implementation("com.github.monulo:kommand:0.7.+")
}
tasks {
    create<Copy>("copyToServer") {
        from(shadowJar)
        var dest = File(rootDir, ".server/plugins")
        if(File(rootDir, shadowJar.get().archiveFileName.get()).exists()) dest = File(dest, "update")
        into(dest)
    }
    create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }
}
try {
    publishing {
            publications {
                create<MavenPublication>("Plugin-Diary") {
                    artifactId = project.name
                    from(components["java"])
                    artifact(tasks["sourcesJar"])
                    pom {
                        name.set("Plugin-Diary")
                        description.set("A Diary for My Plugin")
                        url.set("https://github.com/monulo/Plugin-Diary")
                        licenses {
                            license {
                                name.set("GNU General Public License v2.0")
                                url.set("https://opensource.org/licenses/gpl-2.0.php")
                            }
                        }
                        developers {
                            developer {
                                id.set("monulo")
                                name.set("monulo")
                                email.set("monulo10@gmail.com")
                                url.set("https://github.com/monulo10")
                                roles.addAll("developer")
                                timezone.set("Asia/Seoul")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/monulo/Plugin-Diary.git")
                            developerConnection.set("scm:git:ssh://github.com:monulo/Plugin-Diary.git")
                            url.set("https://github.com/monulo/Plugin-Diary")
                        }
                    }
                }
            }
        }
} catch(e: groovy.lang.MissingPropertyException) {}