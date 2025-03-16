/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
      "./src/**/*.{html,ts}",
    ],
    theme: {
      extend: {
        colors: {
          primary: {
            DEFAULT: '#3f51b5',
            light: '#757de8',
            dark: '#002984',
          },
          secondary: {
            DEFAULT: '#2196f3',
            light: '#6ec6ff',
            dark: '#0069c0',
          },
          accent: {
            DEFAULT: '#ff9800',
            light: '#ffc947',
            dark: '#c66900',
          },
          success: {
            DEFAULT: '#4caf50',
            light: '#80e27e',
            dark: '#087f23',
          },
          warning: {
            DEFAULT: '#ff9800',
            light: '#ffc947',
            dark: '#c66900',
          },
          error: {
            DEFAULT: '#f44336',
            light: '#ff7961',
            dark: '#ba000d',
          },
          info: {
            DEFAULT: '#2196f3',
            light: '#6ec6ff',
            dark: '#0069c0',
          },
        },
      },
    },
    plugins: [],
  }