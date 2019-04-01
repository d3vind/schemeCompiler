JAVAFILES=ca/dal/cs/schememm/common/CharRanges.java \
          ca/dal/cs/schememm/common/CharStream.java \
          ca/dal/cs/schememm/common/Pair.java \
          ca/dal/cs/schememm/common/Stream.java \
          ca/dal/cs/schememm/scanner/LexicalError.java \
          ca/dal/cs/schememm/scanner/Scanner.java \
          ca/dal/cs/schememm/scanner/Token.java \
          ca/dal/cs/schememm/scanner/fa/DFA.java \
          ca/dal/cs/schememm/scanner/fa/DFABuilder.java \
          ca/dal/cs/schememm/scanner/fa/DFAMinimizer.java \
          ca/dal/cs/schememm/scanner/fa/FA.java \
          ca/dal/cs/schememm/scanner/fa/NFA.java \
          ca/dal/cs/schememm/scanner/fa/State.java \
          ca/dal/cs/schememm/scanner/regex/AlternativeRegex.java \
          ca/dal/cs/schememm/scanner/regex/CharRangeRegex.java \
          ca/dal/cs/schememm/scanner/regex/EpsilonRegex.java \
          ca/dal/cs/schememm/scanner/regex/Regex.java \
          ca/dal/cs/schememm/scanner/regex/RegexError.java \
          ca/dal/cs/schememm/scanner/regex/RegexParser.java \
          ca/dal/cs/schememm/scanner/regex/RegexRepeater.java \
          ca/dal/cs/schememm/scanner/regex/RepeatRegex.java \
          ca/dal/cs/schememm/scanner/regex/SequenceRegex.java
CLASSFILES=$(JAVAFILES:.java=.class) ca/dal/cs/schememm/scanner/Token$$Name.class

all: scanner.jar

scanner.jar: $(CLASSFILES)
	jar cfe $@ ca.dal.cs.schememm.scanner.Scanner $(subst $$,\$$,$^)

%.class: %.java
	javac $<
