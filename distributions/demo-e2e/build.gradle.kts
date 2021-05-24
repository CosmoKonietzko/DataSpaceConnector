/*
 * Copyright (c) Microsoft Corporation.
 * All rights reserved.
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.bmuschko.docker-remote-api") version "6.7.0"
}


dependencies {
    implementation(project(":runtime"))
    implementation(project(":extensions:protocol:web"))
    implementation(project(":extensions:control-http"))

    implementation(project(":extensions:metadata:metadata-memory"))
    implementation(project(":extensions:transfer:transfer-core"))
    implementation(project(":extensions:transfer:transfer-store-memory"))
    implementation(project(":extensions:transfer:transfer-provision-aws"))
    implementation(project(":extensions:transfer:transfer-provision-azure"))
    implementation(project(":extensions:transfer:transfer-nifi"))

    implementation(project(":extensions:ids"))

    // todo: replace with atlas - but we need this for the time being to provide catalog entries
    implementation(project(":extensions:demo:demo-nifi"))

    implementation(project(":extensions:security:security-fs"))
    implementation(project(":extensions:policy:policy-registry-memory"))
    implementation(project(":extensions:iam:iam-mock"))
    implementation(project(":extensions:ids:ids-policy-mock"))
    implementation(project(":extensions:configuration:configuration-fs"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

}

application {
    @Suppress("DEPRECATION")
    mainClassName = "com.microsoft.dagx.runtime.DagxRuntime"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("dagx-demo-e2e.jar")
}