#!/bin/sh
git ls-files "*.java" | sort | sed  -e 's/\(.*\).java$/ \1.class \\/'
