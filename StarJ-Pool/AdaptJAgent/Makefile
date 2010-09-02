CC=gcc
#JAVA_HOME=/usr/localcc/pkgs/j2sdk1.4.0
CFLAGS=-Wall -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -O2
LIBS=-lz
TARGET=AdaptJ
SOURCES=${shell find . -maxdepth 1 -name "*.c" -print}
TARGET_DIR=ldlib

all: install

compile: ${SOURCES}
	${CC} ${CFLAGS} -shared ${SOURCES} -o lib${TARGET}.so ${LIBS}

install: compile
	mkdir -p ${TARGET_DIR}
	cp lib${TARGET}.so ${TARGET_DIR}

clean:
	rm -f lib${TARGET}.so
