def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download"
}

//https://bitbucket.org/lussierlab/faime-opensource.git with modifications to handle multiple synonyms and is_a lines, downloading, output formatting, etc..

if (args == null || args.length == 0) {
    println "Proper usage is: groovy ingest_ontology.groovy url ontology_name"
} else {
    def input = args[0]
    def name = args[1]
    def output = name + ".obo"

    File oboFile = new File(output)
    File parsedFile = new File(name+"_ontology_terms.txt")
    
    removedObo = oboFile.delete()
    removedParsedFile = parsedFile.delete()
    
    if (!oboFile.exists()) {
        download(input, output)
    }

    def termInfoRegExp = /(.+?): (.+)/
    boolean isSearchingForTerm = true
    String oId = null
    keyStore = [:]

    // Parse obo file input
    new File(output)?.eachLine { line ->
        // Wait until we find '[Term]' token before processing term info
        if (isSearchingForTerm) {
            isSearchingForTerm = !line.contains("[Term]")
        } else {
            def match = (line =~ termInfoRegExp)
            if (match) {
                // Extract (key, value) pairs from term line
                String key = match[0][1].trim()
                String val = match[0][2].trim()
                // If key is 'id', then we need to create a new term mapping
                if (key == 'id') {
                    oId = val
                    keyStore[oId] = [:]
                    synonyms = ""
                    isas = ""
                } else {
                    // Else, store (key, value) pair for this GO identifier
                    assert oId
                    if (key == 'synonym') {
                        synonyms = synonyms + "|" + val
                        synonyms.toString().startsWith("|")
                        val = synonyms.substring(1)
                        //println synonyms
                    } else if (key == 'is_a') {
                        isas = isas + "|" + val
                        isas.toString().startsWith("|")
                        val = isas.substring(1)
                    }
                    keyStore[oId][key] = val
                }

            } else {
                // We didn't match a term line, start searching for next term
                isSearchingForTerm = true
                oId = null
            }
        }
    }

// generate the output file 

    keyStore.each { key, value ->
        parsedFile << key + "\t"
        value.each { key2, value2 ->
            if (key2 == 'name') {
                parsedFile << value2 + "\t"
         	name = value2
	    }
            if (key2 == 'synonym') {
                parsedFile << value2 + "\t"
            }
        }
        parsedFile << name + "\t"
        value.each { key2, value2 ->
            if (key2 == 'is_a') {
                parsedFile << value2 + "\t"
            }
        }
        parsedFile << "\n"
    }
}



