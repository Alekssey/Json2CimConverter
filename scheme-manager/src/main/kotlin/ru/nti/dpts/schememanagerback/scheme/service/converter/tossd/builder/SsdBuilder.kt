package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.SCL
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.THeader

class SsdBuilder {

    fun build(
        scheme: SchemeDomain,
        substation: Substation,
        substationEquipment: List<ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain>
    ) = createEmptyScl().apply {
        this.substation.add(SubstationBuilder().build(scheme, substation, substationEquipment))
    }

    private fun createEmptyScl() = SCL().apply {
        this.version = "2007"
        this.revision = "B"
        this.release = 4
        this.header = THeader().apply {
            this.id = "dtps"
            this.version = "1"
            this.revision = "1"
            this.toolID = "dtps"
            this.nameStructure = "IEDName"
        }
    }
}
