plugins {
    plugSpringBoot()
    plugSpringKotlin()
    plugSpringDependencyManagement()
    application
}

group = "ru.nti.dpts"
version = "0.0.1"
description = "Json to CIM converter"

dependencies {
    implementation(Libs.Spring.starter_web)
    implementation(project(":cim-converter"))
}

springBoot {
    mainClass.set("ru.nti.dpts.schememanagerback.SchemeManagerBackAppKt")
}
