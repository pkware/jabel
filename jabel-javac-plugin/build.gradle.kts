plugins {
    java
    `maven-publish`
    signing
}

val jabelVersion = project.property("jabelVersion") as String
version = jabelVersion

dependencies {
    implementation(platform("net.bytebuddy:byte-buddy-parent:1.18.10"))
    implementation("net.bytebuddy:byte-buddy")
    implementation("net.bytebuddy:byte-buddy-agent")
    implementation("net.java.dev.jna:jna:5.19.1")
}

// <editor-fold desc="Publishing and Signing">

tasks.withType<Javadoc>{
    options.source = "8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = pomArtifactId
            from(components["java"])
            pom {
                name.set(pomName)
                packaging = pomPackaging
                description.set(pomDescription)
                url.set("https://github.com/pkware/jabel")
                setPkwareOrganization()

                developers {
                    developer {
                        id.set("bsideup")
                        name.set("Sergei Egorov")
                        email.set("bsideup@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/pkware/jabel.git")
                    developerConnection.set("scm:git:ssh://github.com/pkware/jabel.git")
                    url.set("https://github.com/pkware/jabel")
                }

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        distribution.set("repo")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(if (version.toString().isReleaseBuild) releaseRepositoryUrl else snapshotRepositoryUrl)
            credentials {
                username = repositoryUsername
                password = repositoryPassword
            }
        }
    }
}

signing {
    // Signing credentials are stored as secrets in GitHub.
    // See https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials for more information.
    useInMemoryPgpKeys(
        signingKeyId,
        signingKey,
        signingPassword,
    )

    sign(publishing.publications["mavenJava"])
}

val String.isReleaseBuild
    get() = !contains("SNAPSHOT")

val Project.releaseRepositoryUrl: String
    get() = (findProperty("RELEASE_REPOSITORY_URL")
        ?: "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2").toString()

val Project.snapshotRepositoryUrl: String
    get() = (findProperty("SNAPSHOT_REPOSITORY_URL")
        ?: "https://central.sonatype.com/repository/maven-snapshots/").toString()

val Project.repositoryUsername: String
    get() = (findProperty("NEXUS_USERNAME") ?: "").toString()

val Project.repositoryPassword: String
    get() = (findProperty("NEXUS_PASSWORD") ?: "").toString()

val Project.signingKeyId: String
    get() = (findProperty("SIGNING_KEY_ID") ?: "").toString()

val Project.signingKey: String
    get() = (findProperty("SIGNING_KEY") ?: "").toString()

val Project.signingPassword: String
    get() = (findProperty("SIGNING_PASSWORD") ?: "").toString()

val Project.pomPackaging: String
    get() = (findProperty("POM_PACKAGING") ?: "jar").toString()

val Project.pomName: String?
    get() = findProperty("POM_NAME")?.toString()

val Project.pomDescription: String?
    get() = findProperty("POM_DESCRIPTION")?.toString()

val Project.pomArtifactId
    get() = (findProperty("POM_ARTIFACT_ID") ?: name).toString()

fun MavenPom.setPkwareOrganization() {
    organization {
        name.set("PKWARE, Inc.")
        url.set("https://www.pkware.com")
    }
}
// </editor-fold>

