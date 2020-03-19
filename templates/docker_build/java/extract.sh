tar xvf *.tar
rm *.tar
mv `ls` /dist

cd /dist/bin/
rm *.bat
mv `ls` run.sh
