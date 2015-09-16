package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class AutoPromotionPropertyTest {

    private Branch branch

    @Before
    void 'Setup'() {
        branch = Branch.of(
                Project.of(nd('P', '')).withId(ID.of(1)),
                nd('B', '')
        ).withId(ID.of(1))
    }

    @Test
    void 'Checks that a validation stamp is contained'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('VS1', '')
        ).withId(ID.of(1))

        def vs2 = ValidationStamp.of(
                branch,
                nd('VS2', '')
        ).withId(ID.of(2))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [
                        vs1,
                        vs2,
                ], "", ""
        )

        assert property.contains(vs1)
    }

    @Test
    void 'Checks that a validation stamp is not contained'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('VS1', '')
        ).withId(ID.of(1))

        def vs2 = ValidationStamp.of(
                branch,
                nd('VS2', '')
        ).withId(ID.of(2))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [
                        vs2,
                ], "", ""
        )

        assert !property.contains(vs1)
    }

    @Test
    void 'Inclusion based on pattern'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('CI.1', '')
        ).withId(ID.of(1))
        def vs2 = ValidationStamp.of(
                branch,
                nd('CI.2', '')
        ).withId(ID.of(2))
        def qa = ValidationStamp.of(
                branch,
                nd('QA', '')
        ).withId(ID.of(3))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [], 'CI.*', ''
        )

        assert property.contains(vs1)
        assert property.contains(vs2)
        assert !property.contains(qa)
    }

    @Test
    void 'Inclusion based on pattern and list'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('CI.1', '')
        ).withId(ID.of(1))
        def vs2 = ValidationStamp.of(
                branch,
                nd('CI.2', '')
        ).withId(ID.of(2))
        def qa = ValidationStamp.of(
                branch,
                nd('QA', '')
        ).withId(ID.of(3))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [qa], 'CI.*', ''
        )

        assert property.contains(vs1)
        assert property.contains(vs2)
        assert property.contains(qa)
    }

    @Test
    void 'Inclusion and exclusion based on pattern'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('CI.1', '')
        ).withId(ID.of(1))
        def vs2 = ValidationStamp.of(
                branch,
                nd('CI.NIGHT', '')
        ).withId(ID.of(2))
        def qa = ValidationStamp.of(
                branch,
                nd('QA', '')
        ).withId(ID.of(3))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [], 'CI.*', '.*NIGHT.*'
        )

        assert property.contains(vs1)
        assert !property.contains(vs2)
        assert !property.contains(qa)
    }

    @Test
    void 'Inclusion and exclusion based on pattern and list'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('CI.1', '')
        ).withId(ID.of(1))
        def vs2 = ValidationStamp.of(
                branch,
                nd('CI.NIGHT', '')
        ).withId(ID.of(2))
        def qa = ValidationStamp.of(
                branch,
                nd('QA', '')
        ).withId(ID.of(3))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [qa], 'CI.*', '.*NIGHT.*'
        )

        assert property.contains(vs1)
        assert !property.contains(vs2)
        assert property.contains(qa)
    }

    @Test
    void 'List has priority on inclusion and exclusion patterns'() {
        def vs1 = ValidationStamp.of(
                branch,
                nd('CI.1', '')
        ).withId(ID.of(1))
        def vs2 = ValidationStamp.of(
                branch,
                nd('CI.NIGHT', '')
        ).withId(ID.of(2))

        AutoPromotionProperty property = new AutoPromotionProperty(
                [vs2], 'CI.*', '.*NIGHT.*'
        )

        assert property.contains(vs1)
        assert property.contains(vs2)
    }

}
