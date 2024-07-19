/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,
  swcMinify: true,
  images: {
    domains: ["countryflagsapi.com"],
  },
};

module.exports = nextConfig;
