HOME := $(shell pwd)

JAVAC := $(shell which javac)
JAVA := $(shell which java)
JAR := $(shell which jar)

# JAVAC_FLAGS = NOT PROVIDED YET
JAVAC_TEST_FLAGS = $(JAVAC_FLAGS) " -verbose -Xlint "
JAVA_DEPS = -jar lib/checkstyle-8.12-all.jar -c conf/checkstyle.xml

TARGET_PATH = build
SRC_PATH = src

# TODO: since the commit structure will be fixed, it's possible to derive this path
TASK_PATH = ru/ifmo/task1
TASK_PATH_JAVA = ru.ifmo.task1
MAIN_SRC_NAME = Walker
JAVA_SUFFIX = .java
CLASS_SUFFIX = .class

COMPILED_CLASS = $(shell $(TASK_PATH).$(MAIN_SRC_NAME) | sed s/\\./\\//g)

# TODO: add an output file here
TRASH_LIST = ""

all: ./src build
	$(JAVA) $(JAVA_DEPS) $(SRC_PATH)/$(TASK_PATH) && \
	    $(JAVA) -cp $(TARGET_PATH) $(TASK_PATH_JAVA).$(MAIN_SRC_NAME) $(filter-out $@,$(MAKECMDGOALS))

build: create_target
	$(JAVAC) -cp $(SRC_PATH) $(SRC_PATH)/$(TASK_PATH)/$(MAIN_SRC_NAME)$(JAVA_SUFFIX) -d $(TARGET_PATH)

test_build: create_target
	$(JAVAC) $(JAVAC_TEST_FLAGS) -cp $(SRC_PATH) \
	    $(SRC_PATH)/$(TASK_PATH)/$(MAIN_SRC_NAME)$(JAVA_SUFFIX) -d $(TARGET_PATH)

test: test_build
	echo "No tests provided. STUB"

create_target:
	mkdir $(TARGET_PATH) 2>/dev/null |:

clean:
	rm -rf $(TARGET_PATH) $(TRASH_LIST)