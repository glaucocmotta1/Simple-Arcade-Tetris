import subprocess

# Generate a 512x512 SVG of a vibrant arcade Tetris composition
svg_content = """<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 512 512">
  <defs>
    <!-- Gradients for glossy arcade bevel effect -->
    <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#140f2d"/>
      <stop offset="50%" stop-color="#0a0718"/>
      <stop offset="100%" stop-color="#030208"/>
    </linearGradient>

    <!-- Cyan I Block -->
    <linearGradient id="cyanGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#67e8f9"/>
      <stop offset="100%" stop-color="#06b6d4"/>
    </linearGradient>

    <!-- Yellow O Block -->
    <linearGradient id="yellowGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#fef08a"/>
      <stop offset="100%" stop-color="#eab308"/>
    </linearGradient>

    <!-- Purple T Block -->
    <linearGradient id="purpleGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#d8b4fe"/>
      <stop offset="100%" stop-color="#9333ea"/>
    </linearGradient>

    <!-- Orange L Block -->
    <linearGradient id="orangeGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#fdba74"/>
      <stop offset="100%" stop-color="#ea580c"/>
    </linearGradient>

    <!-- Green S Block -->
    <linearGradient id="greenGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#86efac"/>
      <stop offset="100%" stop-color="#16a34a"/>
    </linearGradient>

    <!-- Red Z Block -->
    <linearGradient id="redGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#fca5a5"/>
      <stop offset="100%" stop-color="#dc2626"/>
    </linearGradient>

    <!-- Blue J Block -->
    <linearGradient id="blueGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#93c5fd"/>
      <stop offset="100%" stop-color="#2563eb"/>
    </linearGradient>
  </defs>

  <!-- Background -->
  <rect width="512" height="512" fill="url(#bg)"/>

  <!-- Subtle Arcade Matrix Grid -->
  <g stroke="#ffffff" stroke-opacity="0.07" stroke-width="2">
    <line x1="88" y1="0" x2="88" y2="512"/>
    <line x1="144" y1="0" x2="144" y2="512"/>
    <line x1="200" y1="0" x2="200" y2="512"/>
    <line x1="256" y1="0" x2="256" y2="512"/>
    <line x1="312" y1="0" x2="312" y2="512"/>
    <line x1="368" y1="0" x2="368" y2="512"/>
    <line x1="424" y1="0" x2="424" y2="512"/>

    <line x1="0" y1="88" x2="512" y2="88"/>
    <line x1="0" y1="144" x2="512" y2="144"/>
    <line x1="0" y1="200" x2="512" y2="200"/>
    <line x1="0" y1="256" x2="512" y2="256"/>
    <line x1="0" y1="312" x2="512" y2="312"/>
    <line x1="0" y1="368" x2="512" y2="368"/>
    <line x1="0" y1="424" x2="512" y2="424"/>
  </g>

  <!-- Arcade Glow -->
  <circle cx="256" cy="256" r="180" fill="#a855f7" opacity="0.15"/>

  <!-- Macro block helper definition with arcade bevel -->
  <!-- We'll draw 56x56 square blocks with 4px gap -->
  <!-- T-piece centered at top: Purple -->
  <!-- Block (200, 144) -->
  <g transform="translate(200, 144)">
    <rect width="54" height="54" rx="7" fill="url(#purpleGrad)" stroke="#f3e8ff" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.4"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.25"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <!-- Block (144, 200) -->
  <g transform="translate(144, 200)">
    <rect width="54" height="54" rx="7" fill="url(#purpleGrad)" stroke="#f3e8ff" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.4"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.25"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <!-- Block (200, 200) - Center of T -->
  <g transform="translate(200, 200)">
    <rect width="54" height="54" rx="7" fill="url(#purpleGrad)" stroke="#f3e8ff" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.4"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.25"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <!-- Block (256, 200) - Right of T -->
  <g transform="translate(256, 200)">
    <rect width="54" height="54" rx="7" fill="url(#purpleGrad)" stroke="#f3e8ff" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.4"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.25"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>

  <!-- Cyan I-piece column on the right: (312, 144) to (312, 312) -->
  <g transform="translate(312, 144)">
    <rect width="54" height="54" rx="7" fill="url(#cyanGrad)" stroke="#cffafe" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(312, 200)">
    <rect width="54" height="54" rx="7" fill="url(#cyanGrad)" stroke="#cffafe" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(312, 256)">
    <rect width="54" height="54" rx="7" fill="url(#cyanGrad)" stroke="#cffafe" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(312, 312)">
    <rect width="54" height="54" rx="7" fill="url(#cyanGrad)" stroke="#cffafe" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>

  <!-- Yellow O-piece (Square 2x2) at bottom-left: (144, 256), (200, 256), (144, 312), (200, 312) -->
  <g transform="translate(144, 256)">
    <rect width="54" height="54" rx="7" fill="url(#yellowGrad)" stroke="#fef9c3" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(200, 256)">
    <rect width="54" height="54" rx="7" fill="url(#yellowGrad)" stroke="#fef9c3" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(144, 312)">
    <rect width="54" height="54" rx="7" fill="url(#yellowGrad)" stroke="#fef9c3" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(200, 312)">
    <rect width="54" height="54" rx="7" fill="url(#yellowGrad)" stroke="#fef9c3" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>

  <!-- Orange L-piece corner at (256, 256) and (256, 312) -->
  <g transform="translate(256, 256)">
    <rect width="54" height="54" rx="7" fill="url(#orangeGrad)" stroke="#ffedd5" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>
  <g transform="translate(256, 312)">
    <rect width="54" height="54" rx="7" fill="url(#orangeGrad)" stroke="#ffedd5" stroke-width="2"/>
    <polygon points="4,4 50,4 44,10 10,10" fill="#ffffff" opacity="0.45"/>
    <polygon points="4,4 10,10 10,44 4,50" fill="#ffffff" opacity="0.3"/>
    <polygon points="50,4 50,50 44,44 44,10" fill="#000000" opacity="0.3"/>
    <polygon points="4,50 50,50 44,44 10,44" fill="#000000" opacity="0.4"/>
  </g>

  <!-- Retro Arcade Star Accents -->
  <polygon points="100,110 105,125 120,130 105,135 100,150 95,135 80,130 95,125" fill="#facc15" opacity="0.8"/>
  <polygon points="400,100 404,112 416,116 404,120 400,132 396,120 384,116 396,112" fill="#38bdf8" opacity="0.8"/>
  <polygon points="390,370 393,380 403,383 393,386 390,396 387,386 377,383 387,380" fill="#a855f7" opacity="0.7"/>
</svg>"""

with open("/tmp/icon.svg", "w") as f:
    f.write(svg_content)

print("SVG written successfully")
