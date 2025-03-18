/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"], // ✅ Make sure it scans all component files
  theme: {
    extend: {
      fontFamily: {
        sans: ["IBM Plex Sans", "sans-serif"], // ✅ Set IBM Plex Sans as the default sans-serif font
      },
    },
  },
  plugins: [],
};
