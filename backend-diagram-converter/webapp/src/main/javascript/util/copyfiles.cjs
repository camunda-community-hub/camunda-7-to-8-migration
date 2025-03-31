const fs = require('node:fs');
const { glob } = require('glob');
const path = require('path');

const copyFilesRecursively = (source, target) => {
  const stat = fs.statSync(source);

  if (stat.isDirectory()) {
    if (!fs.existsSync(target)) {
      fs.mkdirSync(target);
    }
    fs.readdirSync(source).forEach((file) => {
      const srcFile = path.join(source, file);
      const tgtFile = path.join(target, file);
      copyFilesRecursively(srcFile, tgtFile);
    });
  } else {
    console.log(`Copying ${source} to ${target}`);
    fs.copyFileSync(source, target);
  }
}
// Use glob to support patterns
glob(path.join('dist', '*')).then((files) => {
    files.forEach(file => {
    const targetPath = path.join('..', 'resources', 'static', path.basename(file));
    copyFilesRecursively(file, targetPath);
  });
});
