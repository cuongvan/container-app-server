#! /usr/bin/env python3.6

import shutil, os, subprocess
from pathlib import Path

script_dir = Path(__file__).parent

build_dir = Path('/tmp/app-build')

tar_file = '/home/cuongvt4/luan-van/appserver/build/distributions/appserver.tar'

build_dir.mkdir(exist_ok=True)

# make hardlink to tar file
if (build_dir / 'appserver.tar').is_file():
    (build_dir / 'appserver.tar').unlink()

os.link(tar_file, build_dir / 'appserver.tar')


if (build_dir / 'Dockerfile').is_file():
    (build_dir / 'Dockerfile').unlink()
os.link(script_dir / 'Dockerfile', build_dir / 'Dockerfile')


subprocess.run(['docker', 'build', '.', '-t', 'appserver'], cwd=build_dir)