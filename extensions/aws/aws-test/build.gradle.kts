/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

val awsVersion: String by project

dependencies {
    api(project(":spi"))

    testFixturesApi(platform("com.amazonaws:aws-java-sdk-bom:1.11.1018"))
    testFixturesApi("com.amazonaws:aws-java-sdk-s3")

    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testFixturesRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

}

publishing {
    publications {
        create<MavenPublication>("aws.test") {
            artifactId = "aws.test"
            from(components["java"])
        }
    }
}