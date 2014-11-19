package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.support.Time
import org.junit.Test

class VersionInfoTest {

    @Test
    void 'Release'() {
        VersionInfo info = new VersionInfo(
                Time.now(),
                'release-2.0-abc1234',
                'release-2.0',
                'abc1234',
                'abc12347276321bef16abd7dcf19a5533ab8bd97',
                'release/2.0',
                'release'
        )
        assert info.marketingVersion == '2.0'
        assert !info.release
        assert info.label == "RC"
    }

    @Test
    void 'Feature'() {
        VersionInfo info = new VersionInfo(
                Time.now(),
                'feature-240-my-feature-abc1234',
                'feature-240-my-feature',
                'abc1234',
                'abc12347276321bef16abd7dcf19a5533ab8bd97',
                'feature/240-my-feature',
                'feature'
        )
        assert info.marketingVersion == '240-my-feature'
        assert !info.release
        assert info.label == "Feature"
    }

    @Test
    void 'Development'() {
        VersionInfo info = new VersionInfo(
                Time.now(),
                'develop-abc1234',
                'develop',
                'abc1234',
                'abc12347276321bef16abd7dcf19a5533ab8bd97',
                'develop',
                'develop'
        )
        assert info.marketingVersion == 'develop'
        assert !info.release
        assert info.label == "Dev"
    }

    @Test
    void 'Tag'() {
        VersionInfo info = new VersionInfo(
                Time.now(),
                '2.0-rc',
                '2.0-rc',
                'abc1234',
                'abc12347276321bef16abd7dcf19a5533ab8bd97',
                '2.0-rc',
                'tag'
        )
        assert info.marketingVersion == '2.0-rc'
        assert info.release
        assert info.label == "Release"
    }

}