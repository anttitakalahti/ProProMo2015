#! /bin/sh

cat data1.txt| sed -E 's/([1-9][0-9])/x/g'|sed -E 's/[1-9]/x/g'|sort|uniq -c|sort > patterns_asc.txt

