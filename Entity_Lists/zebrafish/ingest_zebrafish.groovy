import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.CharUtils

@Grab(group='org.apache.commons', module='commons-lang3', version='3.4')
def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download " + input
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download" + output
}

def input = "https://zfin.org/downloads/genetic_markers.txt"
def output = "ZEBRAFISH_OBJECTS.txt"
def aliasInput = "http://zfin.org/downloads/aliases.txt"
def aliasOutput = "ZEBRAFISH_ALIASES.txt"
def featureInput = "https://zfin.org/downloads/features.txt"
def featureOutput = "ZEBRAFISH_VARIANT.txt"


//all zebrafish objects and aliases

def filteredOutput = "ZEBRAFISH_OBJECTS_FINAL.txt"
def filteredFeatureOutput = "ZEBRAFISH_VARIANT_FINAL.txt"
def filteredATBOutput = "ZEBRAFISH_ATB_FINAL.txt"
def filteredSTROutput = "ZEBRAFISH_VARIANT_FINAL.txt"
def filteredGeneOutput = "ZEBRAFISH_GENE_FINAL.txt"

def objectMap = [:]

File featureOutputFile = new File(featureOutput)
if (!featureOutputFile.exists()) {
    download(featureInput, featureOutput)
}

File aliasOutputFile = new File(aliasOutput)
if (!aliasOutputFile.exists()) {
    download(aliasInput, aliasOutput)
}
File outputFile = new File(output)
if (!outputFile.exists()) {
    download(input, output)
}
File filteredOutputFile = new File(filteredOutput)
File filteredFeatureOutputFile = new File(filteredFeatureOutput)
File filteredATBOutputFile = new File(filteredATBOutput)
File filteredSTROutputFile = new File(filteredSTROutput)
File filteredGeneOutputFile = new File(filteredGeneOutput)

new File(aliasOutput).readLines().eachWithIndex { line, index ->
    //println line
    def lines = line.split("\t")
    def objectId = lines[0]
    def parsedSynonym = lines[3]

    if (objectMap.containsKey(objectId)) {
        def currentSynonym = objectMap.getAt(objectId)
        def synonym = currentSynonym + "|" + parsedSynonym  + "|" + (htmlEncode(parsedSynonym))

        objectMap[(objectId)] = synonym
    }
    else {
        objectMap[(objectId)] = parsedSynonym
    }
}


println "filtering file ${output} to ${filteredOutput}"
filteredOutputFile.write("")
new File(output).readLines().eachWithIndex { line, index ->
    try {
        def lines = line.split("\t")
        def objectID = lines[0]
        def symbol = lines[1]
        def fullName = lines[2]
        fullName = fullName == symbol ? "" : fullName

        //def synonyms = lines[3]
        //synonyms = synonyms + "|" + fullName
       // synonyms = synonyms + "|" + (htmlEncode(fullName))
        //objectMap.objectID = synonyms
        def synonyms = objectMap.getAt(objectID)
        if (synonyms == null) {
            synonyms = fullName
        }
        def notes = lines[4]
        //println synonyms
        if (objectID.substring(0,8)=="ZDB-GENE") {
            filteredGeneOutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
        }
        else if (objectID.substring(0,7)=="ZDB-ATB"){
            filteredATBOutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
        }
        else if (objectID.substring(0,7)=="ZDB-ALT"){
            filteredFeatureOutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
        }
        else if (objectID.substring(0,8)=="ZDB-CRIS" || objectID.substring(0,8)=="ZDB-MRPH" || objectID.substring(0,8)=="ZDB-TALE"){
            filteredSTROutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
        }
        else {
            filteredOutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
        }
    } catch (e) {
        println "error filtering for line[${line}], ${e}"
    }
}

new File(featureOutput).readLines().eachWithIndex { line, index ->
    try {
        def lines = line.split("\t")
        def objectID = lines[0]
        def symbol = lines[2]
        def fullName = lines[3]
        fullName = fullName == symbol ? "" : fullName

        //def synonyms = lines[3]
        //synonyms = synonyms + "|" + fullName
        // synonyms = synonyms + "|" + (htmlEncode(fullName))
        //objectMap.objectID = synonyms
        def synonyms = objectMap.getAt(objectID)
        if (synonyms == null) {
            synonyms = fullName
        }
        def notes = lines[4]
        //println synonyms
        filteredFeatureOutputFile << objectID << "\t" << symbol << "\t" << fullName << "\t" << synonyms << "\t" << notes << "\n"
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





