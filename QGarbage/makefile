all: compile run

compile:
	@echo ''
	@echo "Compiling java files in order of dependency"
	@nxjc *.java

run:
	@echo ''
	@echo "Running the Program"
	@nxj -u -r GarbageAgentNXT

clean:
	@echo "Removing binary files"
	@rm *.class
