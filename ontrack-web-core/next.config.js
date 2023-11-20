const {PHASE_DEVELOPMENT_SERVER} = require('next/constants')

const nextConfig = {
    reactStrictMode: false,
    output: "export",
    basePath: '/ui',
    images: {
        unoptimized: true,
    },
}

module.exports = (phase, {defaultConfig}) => {
    if (phase === PHASE_DEVELOPMENT_SERVER) {
        nextConfig.basePath = ''
    }

    return nextConfig
}
