const {PHASE_PRODUCTION_BUILD} = require('next/constants')

module.exports = (phase) => {
    const config = {
        reactStrictMode: false,
        output: 'standalone',
        images: {
            unoptimized: true,
        },
    }
    if (phase === PHASE_PRODUCTION_BUILD) {
        config.basePath = '/ui'
    }

    return config
}
