# Frontend UI Structure

UI styling is split by ownership so a specific visual change has an obvious place to start.

- `tokens.css`: theme variables, colors, gradients, shadows, light/dark values.
- `base.css`: browser reset, body background, links, native form focus states.
- `motion.css`: global animations and reduced-motion handling.
- `components.css`: reusable UI components in `src/components/ui`.
- `shell.css`: app shell/navigation layout, including the top navbar.
- Page-specific layout remains inside each `src/pages/*Page.jsx` file for now.

Rule of thumb:

- Change app-wide color, dark mode, shadow, or gradient: edit `tokens.css`.
- Change button/card/modal/badge/tabs/header appearance: edit `components.css`.
- Change navigation layout or theme toggle: edit `shell.css` and `components/Navbar.jsx`.
- Change only one page's layout: edit that page file's local `styles`/`s` object.
