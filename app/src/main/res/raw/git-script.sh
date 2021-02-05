#!/bin/bash

git add $1;
echo $1;
git commit -m "CSV " + $1 + " added";
git push;
