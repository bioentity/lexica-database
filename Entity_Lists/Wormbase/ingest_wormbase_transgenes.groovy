evaluate(new File("Utilities.groovy"))
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.CharUtils

@Grab(group='org.apache.commons', module='commons-lang3', version='3.4')


def input = "http://tazendra.caltech.edu/~acedb/karen/hyperlink_dumps/trp_data.txt"
def output = "trp_data.txt"
def final_output = "WORMBASE_TRANSGENES_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)

File ourOutputFile = new File(final_output)
ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_transgenes.groovy" << "\n"
ourOutputFile<< "#" << timeStamp << "\n"


new File(output).readLines().eachWithIndex { line, index ->
    //println line
    try {
        def publicName
        def synonyms
        def tgFullName
        def lines = line.split("\t")
        if (lines.size() < 1) {
            println "this is a blank line in the input file."
        }
        else {
            if (lines[0] == "name") {
                println "skip first line"
            } else {
                def tgID = lines[0]
                if (lines.size() > 1) {
                    publicName = lines[1]
                    if (lines.size() > 2) {
                        synonym = lines[2]
                        synonyms = synonym.replace(',', '|')
                        tgFullName = publicName

                    } else {
                        synonyms = ''
                    }

                } else {
                    publicName = ''
                    synonyms = ''
                    tgFullName = ''
                }

                ourOutputFile << tgID << "\t" << publicName << "\t" << tgFullName << "\t" << synonyms << "\t" << "\n"
            }
        }
    } catch (e) {
        println "error filtering for line[${line}], ${e}"
    }
}

private String htmlEncode(final String string) {
    final StringBuffer stringBuffer = new StringBuffer()
    for (int i = 0; i < string.length(); i++) {
        final Character character = string.charAt(i)
        if (CharUtils.isAscii(character)) {
            // Encode common HTML equivalent characters
            stringBuffer.append(
                    StringEscapeUtils.escapeHtml4(character.toString()))
        } else {
            // Why isn't this done in escapeHtml4()?
            stringBuffer.append(
                    String.format("&#x%x;",
                            Character.codePointAt(string, i)))
        }
    }
    return stringBuffer.toString()
}
println "done"





