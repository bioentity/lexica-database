def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download"
}

def input = "http://www.informatics.jax.org/downloads/reports/MRK_List1.rpt"
def output = "MOUSE_2_GENES.txt"
def filteredOutput = "MOUSE_2_GENES_FINAL.txt"
File ouputFile = new File(output)
if (!ouputFile.exists()) {
    download(input, output)
    // gunzip(output)
}
File filteredOutputFile = new File(filteredOutput)

filteredOutputFile << "# input[" << input << "] script ingest_mouse_2.groovy" << "\n"

println "filtering file ${output} to ${filteredOutput}"
filteredOutputFile.write("")
new File(output).readLines().eachWithIndex { line, index ->
    if (line.startsWith("MGI A")) {
        filteredOutputFile << "#" + line << "\n"
    } else if (line.startsWith("NULL")) {
        filteredOutputFile << ""
    } else  if( line.startsWith("MGI:") ) {
        try {
            def lines = line.split("\t")
            def geneID = lines[0]
            def symbol = lines[6]
            def geneFullName = lines[8]
//            geneFullName = geneFullName==symbol ? "" : geneFullName
            def synonyms = lines[11]

//            synonyms += (synonyms ? "|" : "") + lines[3]
//            def notes = lines[4]
//            notes += (notes ? "|" : "") + lines[36]
/*            if (synonyms == "GENE") {
                notes = synonyms + '|' + notes
                synonyms = '' */
            def status = lines[7]
            if (status == 'O') {
                filteredOutputFile << geneID << "\t" << symbol << "\t" << geneFullName << "\t" << synonyms << "\n"
                //<< notes << "\n"
            }
        }
        catch (e) {
            println "error filtering for line[${line}], ${e}"
        }
    }
}
println "done"





