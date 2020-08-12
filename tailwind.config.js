// tailwind.config.js
module.exports = {
    purge: [
        'src/main/resources/templates/**/*.mustache',
        'src/main/webapp/WEB-INF/static/js/**/*.vue'
    ],
    theme: {
        extend: {
            colors: {
                'semi-75': 'rgba(0, 0, 0, 0.75)'
            }
        }
    },
    variants: {
        cursor: ['responsive', 'hover', 'disabled'],
        opacity: ['disabled'],
    }
}