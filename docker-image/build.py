#! /usr/bin/env python3.6

import shutil, os, subprocess
from pathlib import Path

build_dir = Path(__file__).parent

tar_file = '/home/cuongvt4/luan-van/appserver/build/distributions/appserver.tar'

# make hardlink to tar file
(build_dir / 'appserver.tar').unlink()
os.link(tar_file, build_dir / 'appserver.tar')

subprocess.run(['docker', 'build', '.', '-t', 'appserver'], cwd=build_dir)