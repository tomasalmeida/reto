@Grab(group = 'org.spockframework', module = 'spock-core', version = '1.1-groovy-2.4-rc-4')
@Grab('org.springframework.boot:spring-boot:1.2.1.RELEASE')

import spock.lang.*
import org.springframework.boot.test.OutputCapture

class BasicFormatterSpec extends Specification {

    final String lineSeparator = System.getProperty('line.separator')

    @org.junit.Rule
    OutputCapture capture = new OutputCapture()

    def "correct BASIC code indentation"() {
        given:
        def input = '''12
····
VAR I
·FOR I=1 TO 31
»»»»IF !(I MOD 3) THEN
··PRINT "FIZZ"
··»»ENDIF
»»»»····IF !(I MOD 5) THEN
»»»»··PRINT "BUZZ"
··»»»»»»ENDIF
»»»»IF (I MOD 3) && (I MOD 5) THEN
······PRINT "FIZZBUZZ"
··»»ENDIF
»»»»·NEXT
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        capture.toString() == """VAR I
FOR I=1 TO 31
····IF !(I MOD 3) THEN
········PRINT "FIZZ"
····ENDIF
····IF !(I MOD 5) THEN
········PRINT "BUZZ"
····ENDIF
····IF (I MOD 3) && (I MOD 5) THEN
········PRINT "FIZZBUZZ"
····ENDIF
NEXT
"""
    }

    def "BASIC indentation fails because an ENDIF is missing"() {
        given:
        def input = '''4
....
FOR I=0 TO 10
····IF I MOD 2 THEN
········PRINT I
NEXT
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        lineSeparator + capture.toString() == '''
FOR I=0 TO 10
....IF I MOD 2 THEN
........PRINT I
....NEXT
Error: ENDIF missed, identation 1
'''
    }

    def "BASIC indentation fails because an ENDIF and NEXT are missing"() {
        given:
        def input = '''3
....
FOR I=0 TO 10
····IF I MOD 2 THEN
········PRINT I
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        lineSeparator + capture.toString() == '''
FOR I=0 TO 10
....IF I MOD 2 THEN
........PRINT I
Error: NEXT missed, identation 1
Error: ENDIF missed, identation 1
'''

    }


    def "BASIC indentation fails because an IF and NEXT are missing"() {
        given:
        def input = '''3
.....
FOR I=0 TO 10
····PRINT I
ENDIF
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        lineSeparator + capture.toString() == '''
FOR I=0 TO 10
.....PRINT I
ENDIF
Error: NEXT missed, identation 1
Error: IF missed, identation 1
'''
    }

    def "BASIC indentation fails because there is an extra ENDIF"() {
        given:
        def input = '''4
....
FOR I=0 TO 10
····PRINT I
NEXT
ENDIF
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        lineSeparator + capture.toString() == '''
FOR I=0 TO 10
....PRINT I
NEXT
ENDIF
Error: IF missed, identation 1
'''
    }

    def "BASIC indentation fails because block not properly closed"() {
        given:
        def input = '''6
....
VAR I
·FOR I=1 TO 31
»»»»IF !(I MOD 3) THEN
··PRINT "FIZZ"
NEXT
··»»ENDIF
'''

        def consoleMock = GroovyMock(MockConsole)
        input.eachLine {
            1 * consoleMock.readLine(_, null) >> it
        }

        when:
        def formatter = new BasicFormatter(consoleMock)
        formatter.setNumberLines(System.err)
        formatter.setIdentationStr()
        formatter.autoIndent(System.out)
        formatter.validateSourceCode(System.err)

        then:
        lineSeparator + capture.toString() == '''
VAR I
FOR I=1 TO 31
....IF !(I MOD 3) THEN
........PRINT "FIZZ"
....NEXT
ENDIF
Error: block FOR not properly closed
Error: block IF not properly closed
'''
    }
}


interface MockConsole {
    String readLine(String fmt, Object... args)

    String readLine()

    char[] readPassword(String fmt, Object... args)

    char[] readPassword()
}

