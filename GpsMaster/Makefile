
all:
	./basecompile.sh
	./setclasss.sh      >Makefile-2
	cat Makefile.part1 >>Makefile-2
	./findJava.sh      >>Makefile-2
	cat Makefile.part3 >>Makefile-2
	make -f Makefile-2
	./createJAR.sh

clean:
	rm -rf GpsMaster_jar
	rm -f Makefile-2
	find src -name "*.class" | xargs rm -v

