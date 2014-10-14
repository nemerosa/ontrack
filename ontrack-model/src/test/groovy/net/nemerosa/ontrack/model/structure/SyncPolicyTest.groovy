package net.nemerosa.ontrack.model.structure

import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class SyncPolicyTest {

    @Test
    void 'Copy sync'() {
        def sources = ['a', 'b', 'c', 'd', 'e'].collectEntries { [it, nd(it, it.toUpperCase())] }
        def targets = ['b', 'c', 'd', 'f'].collectEntries { [it, nd(it, it.toLowerCase())] }

        def result = SyncPolicy.COPY.sync(config(sources, targets))
        assert result.created == 2
        assert result.presentTargetIgnored == 3
        assert result.presentTargetReplaced == 0
        assert result.unknownTargetIgnored == 1
        assert result.unknownTargetDeleted == 0


        assert targets.values() as List == [
                nd('b', 'b'),
                nd('c', 'c'),
                nd('d', 'd'),
                nd('f', 'f'),
                nd('a', 'A'),
                nd('e', 'E'),
        ]
    }

    @Test
    void 'Sync'() {
        def sources = ['a', 'b', 'c', 'd', 'e'].collectEntries { [it, nd(it, it.toUpperCase())] }
        def targets = ['b', 'c', 'd', 'f'].collectEntries { [it, nd(it, it.toLowerCase())] }

        def result = SyncPolicy.SYNC.sync(config(sources, targets))
        assert result.created == 2
        assert result.presentTargetIgnored == 0
        assert result.presentTargetReplaced == 3
        assert result.unknownTargetIgnored == 0
        assert result.unknownTargetDeleted == 1

        assert targets.values() as List == [
                nd('b', 'B'),
                nd('c', 'C'),
                nd('d', 'D'),
                nd('a', 'A'),
                nd('e', 'E'),
        ]
    }

    private static SyncConfig<NameDescription, String> config(
            Map<String, NameDescription> sources,
            Map<String, NameDescription> targets) {
        SyncConfig.builder()
                .itemType('Name description')
                .sourceItems({ sources.values() })
                .targetItems({ targets.values() })
                .itemId({ it -> it.name })
                .targetItem({ String it -> Optional.ofNullable(targets[it]) })
                .createTargetItem(
                {
                    NameDescription source ->
                        targets.put(
                                source.name,
                                nd(source.name, source.description)
                        )
                })
                .replaceTargetItem(
                {
                    NameDescription source, NameDescription target ->
                        targets.put(
                                target.name,
                                nd(target.name, source.description)
                        )
                })
                .deleteTargetItem({ NameDescription t -> targets.remove(t.name) })
                .build()
    }

}