# Matrix Font Assets

This directory contains font files for the Matrix digital rain effect.

## Font Files

### matrix_code_nfi.ttf
- **Description**: Matrix Code NFI font - a fan-made recreation of the authentic Matrix movie font
- **Characters**: Half-width Katakana, Latin characters, numerals, and custom Matrix glyphs
- **Usage**: Primary font for authentic Matrix visual effect
- **License**: Free for non-commercial use

### NotoSansJP-Regular.ttf (Optional)
- **Description**: Google Noto Sans Japanese font for Katakana characters
- **Usage**: Fallback font for Japanese characters if Matrix Code NFI is not available
- **License**: Open Font License

## Installation Instructions

1. Download the Matrix Code NFI font from a reputable source
2. Place the `matrix_code_nfi.ttf` file in this directory
3. The app will automatically detect and use the font
4. If the font is not available, the app will fall back to a hybrid system using system fonts

## Font Features

- **Half-width Katakana**: ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン
- **Latin Characters**: A-Z, 0-9
- **Custom Glyphs**: Matrix-specific symbols and glitch characters
- **Monospace Design**: Consistent character width for authentic terminal look

## Troubleshooting

If the Matrix font is not loading:
1. Ensure the font file is in the correct location: `app/src/main/assets/fonts/matrix_code_nfi.ttf`
2. Check that the font file is not corrupted
3. The app will automatically fall back to system fonts if the Matrix font is unavailable
4. Check the logcat for font loading messages from `MatrixFontManager`
