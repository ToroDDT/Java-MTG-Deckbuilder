import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { minify } from "terser";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const rootDir = path.resolve(__dirname, "..");
const staticDir = path.join(rootDir, "src/main/resources/static");
const nodeModulesDir = path.join(rootDir, "node_modules");

const jsBundles = {
  "js/decks.min.js": [
    "js/decks.js",
  ],
  "js/library.min.js": [
    "js/decks.js",
    "js/combos.js",
    "js/personal-library.js",
    "js/bulk-image-scan.js",
  ],
  "js/deck-builder.min.js": [
    "js/deck-builder.js",
  ],
  "js/chart.min.js": [
    "js/chart.js",
  ],
};

const vendorAssets = {
  "js/htmx.min.js": "htmx.org/dist/htmx.min.js",
};

async function readStaticFile(file) {
  return fs.readFile(path.join(staticDir, file), "utf8");
}

async function writeStaticFile(file, contents) {
  const outputPath = path.join(staticDir, file);
  await fs.mkdir(path.dirname(outputPath), { recursive: true });
  await fs.writeFile(outputPath, contents);
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

async function copyVendorAsset(output, input) {
  const source = await fs.readFile(path.join(nodeModulesDir, input), "utf8");
  await writeStaticFile(output, source);
  return source.length;
}

async function main() {
  for (const [output, input] of Object.entries(vendorAssets)) {
    const bytes = await copyVendorAsset(output, input);
    console.log(`js  ${output} ${bytes} bytes`);
  }

  for (const [output, inputs] of Object.entries(jsBundles)) {
    const bytes = await buildJsBundle(output, inputs);
    console.log(`js  ${output} ${bytes} bytes`);
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
