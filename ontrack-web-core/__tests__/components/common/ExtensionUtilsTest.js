import {getExtensionShortName} from "@components/common/ExtensionUtils";

describe('ExtensionUtils', () => {
    it('returns an extension short name based on a long class name', () => {
        const typeName = 'net.nemerosa.ontrack.extensions.environments.ui.BuildEnvironmentsDecorations'
        const shortName = getExtensionShortName(typeName)
        expect(shortName).toEqual('environments.ui.BuildEnvironmentsDecorations')
    })
    it('returns an extension short name based on a short class name', () => {
        const typeName = 'net.nemerosa.ontrack.extension.general.ReleaseDecorationExtension'
        const shortName = getExtensionShortName(typeName)
        expect(shortName).toEqual('general.ReleaseDecorationExtension')
    })
})
