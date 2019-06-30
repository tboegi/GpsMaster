#!/bin/sh
LIBCLASSFILESLIST=libclasses.list

createlibclasseslist()
{
	cmd=$(echo find . -name *.class | sort) &&
	echo cmd="$cmd >$LIBCLASSFILESLIST" &&
	eval $cmd >$LIBCLASSFILESLIST
}

rm -f gpsmasterLib.jar &&

createlibclasseslist
if ! test -s $LIBCLASSFILESLIST; then
	./compile.sh
fi
createlibclasseslist &&
cmd=$(echo jar --create --file  gpsmasterLib.jar @$LIBCLASSFILESLIST) &&
echo cmd=$cmd &&
eval $cmd
