import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

import autoprefixer from "autoprefixer";
import cssnano from "cssnano";
import postcss from "postcss";
import postcssImport from "postcss-import";
import { minify } from "terser";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const rootDir = path.resolve(__dirname, "..");
const staticDir = path.join(rootDir, "src/main/resources/static");

const args = new Set(process.argv.slice(2));
const cssOnly = args.has("--css");
const jsOnly = args.has("--js");

const cssBundles = {
  "css/site.min.css": [
    "css/main.css",
    "css/style.css",
  ],
  "css/library.min.css": [
    "css/main.css",
    "css/personal-library.css",
    "css/combos.css",
  ],
  "css/decks.min.css": [
    "css/style.css",
    "css/decks.css",
  ],
  "css/builder.min.css": [
    "css/builder.css",
    "css/personal-library.css",
  ],
  "css/combos.min.css": [
    "css/combos.css",
  ],
  "css/login.min.css": [
    "css/login.css",
  ],
  "css/dashboard.min.css": [
    "css/style.css",
    "mtg-dashboard.css",
  ],
};

const jsBundles = {
  "js/decks.min.js": [
    "js/decks.js",
  ],
  "js/library.min.js": [
    "js/decks.js",
    "js/combos.js",
    "js/personal-library.js",
  ],
  "js/builder-tailwind-config.min.js": [
    "js/builder-tailwind-config.js",
  ],
  "js/deck-builder.min.js": [
    "js/deck-builder.js",
  ],
  "js/chart.min.js": [
    "js/chart.js",
  ],
};

async function readStaticFile(file) {
  return fs.readFile(path.join(staticDir, file), "utf8");
}

async function writeStaticFile(file, contents) {
  const outputPath = path.join(staticDir, file);
  await fs.mkdir(path.dirname(outputPath), { recursive: true });
  await fs.writeFile(outputPath, contents);
}

async function buildCssBundle(output, inputs) {
  const source = (await Promise.all(inputs.map(async (input) => {
    const css = await readStaticFile(input);
    return `/* ${input} */\n${css}`;
  }))).join("\n\n");

  const result = await postcss([
    postcssImport(),
    autoprefixer(),
    cssnano({ preset: "default" }),
  ]).process(source, {
    from: path.join(staticDir, output),
    to: path.join(staticDir, output),
    map: false,
  });

  await writeStaticFile(output, result.css);
  return result.css.length;
}

async function buildJsBundle(output, inputs) {
  const source = (await Promise.all(inputs.map(async (input) => {
    const js = await readStaticFile(input);
    return `/* ${input} */\n${js}`;
  }))).join("\n;\n");

  const result = await minify(source, {
    compress: true,
    mangle: true,
    format: {
      comments: false,
    },
  });

  if (!result.code) {
    throw new Error(`Terser produced no output for ${output}`);
  }

  await writeStaticFile(output, result.code);
  return result.code.length;
}

async function main() {
  if (!jsOnly) {
    for (const [output, inputs] of Object.entries(cssBundles)) {
      const bytes = await buildCssBundle(output, inputs);
      console.log(`css ${output} ${bytes} bytes`);
    }
  }

  if (!cssOnly) {
    for (const [output, inputs] of Object.entries(jsBundles)) {
      const bytes = await buildJsBundle(output, inputs);
      console.log(`js  ${output} ${bytes} bytes`);
    }
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
