package ru.nti.dtps.cimconverter.rdf.schema

object Namespace {

    object cim : RdfNamespace {
        override fun getAlias() = "cim"
        override fun getUri() = "http://iec.ch/TC57/2016/CIM-schema-cim#"
    }

    object cim302 : RdfNamespace {
        override fun getAlias() = "cim302"
        override fun getUri() = "http://iec.ch/TC57/2018/CIM-schema-cim#"
    }

    object dtps : RdfNamespace {
        override fun getAlias() = "dtps"
        override fun getUri() = "http://dtps.cloud/2023/schema-cim01#"
    }

    fun getRdfRootElementNamespaceAttributes(): String = Namespace::class.nestedClasses
        .joinToString(separator = " ") { nestedObjectClass ->
            val instance = nestedObjectClass.objectInstance

            if (instance != null && instance is RdfNamespace) {
                "xmlns:${instance.getAlias()}=\"${instance.getUri()}\""
            } else {
                ""
            }
        }
}
