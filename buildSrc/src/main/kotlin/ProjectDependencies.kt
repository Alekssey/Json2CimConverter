object LibVersion {
    const val spring_boot = "3.1.0"
    const val java_dev_kit = "2.2.3"
    const val arrow = "1.1.5"
    const val rd4j = "4.2.3"
    const val log4j = "2.20.0"
    const val prometheus = "1.10.2"
    const val jaxb = "2.3.1"
}

object Libs {
    object Kotlin {
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Global.kotlin_version}"
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Global.kotlin_version}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Global.kotlin_version}"
        const val arrow = "io.arrow-kt:arrow-core:${LibVersion.arrow}"
    }

    object Dtps {
        const val java_dev_kit = "ru.nti.dtps:dtps-java-dev-kit:${LibVersion.java_dev_kit}"
    }

    object Jackson {
        const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
        const val databind = "com.fasterxml.jackson.core:jackson-databind"
    }

    object Rdf4j {
        const val rio_rdfxml = "org.eclipse.rdf4j:rdf4j-rio-rdfxml:${LibVersion.rd4j}"
        const val repository_api = "org.eclipse.rdf4j:rdf4j-repository-api:${LibVersion.rd4j}"
        const val sail_memory = "org.eclipse.rdf4j:rdf4j-sail-memory:${LibVersion.rd4j}"
        const val repository_sail = "org.eclipse.rdf4j:rdf4j-repository-sail:${LibVersion.rd4j}"
    }

    object Log4j {
        const val api = "org.apache.logging.log4j:log4j-api:${LibVersion.log4j}"
        const val core = "org.apache.logging.log4j:log4j-core:${LibVersion.log4j}"
        const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${LibVersion.log4j}"
    }


    object Spring {
        const val starter_web = "org.springframework.boot:spring-boot-starter-web:${LibVersion.spring_boot}"
    }

    object Actuator {
        const val spring_starter_actuator =
            "org.springframework.boot:spring-boot-starter-actuator:${LibVersion.spring_boot}"
    }

    object Javax {
        const val validation = "javax.validation:validation-api:2.0.1.Final"
        const val activation = "javax.activation:activation:1.1.1"
    }


    object Jaxb {
        const val runtime = "org.glassfish.jaxb:jaxb-runtime:${LibVersion.jaxb}"
        const val api = "javax.xml.bind:jaxb-api:${LibVersion.jaxb}"
    }

    object Caffeine {
        const val caffeine = "com.github.ben-manes.caffeine:caffeine:3.1.8"
    }

}
