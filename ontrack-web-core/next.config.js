/** @type {import('next').NextConfig} */
const nextConfig = {
    reactStrictMode: false,
    output: "export",
    basePath: "/ui",
    images: {
        unoptimized: true,
    },
}

module.exports = nextConfig
