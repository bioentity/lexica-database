#!/bin/env groovy


def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download"
}

def input = "ftp://ftp.rgd.mcw.edu/pub/data_release/GENES_MOUSE.txt"
def output = "MOUSE_GENES.txt"
def filteredOutput = "MOUSE_GENES_FINAL.txt"
File ouputFile = new File(output)
if (!ouputFile.exists()) {
    download(input, output)
    // gunzip(output)
}
File filteredOutputFile = new File(filteredOutput)

filteredOutputFile << "# input[" << input << "] script ingest_mouse.groovy" << "\n"

println "filtering file ${output} to ${filteredOutput}"
filteredOutputFile.write("")
new File(output).readLines().eachWithIndex { line, index ->
    if (line.startsWith("#")) {
        filteredOutputFile << line << "\n"
    } else if (line.startsWith("GENE_RGD_ID")) {
        filteredOutputFile << "#" << line << "\n"
    } else if (!line.startsWith("FBtr") && !line.startsWith("FBpp") && !line.startsWith("FBba")) {
        try {
            def lines = line.split("\t")
            def geneID = lines[38]
			geneID = geneID.contains(":") ? geneID.split(":")[1] : geneID // we split this for the case of MGI, which already includes its prefix
            def symbol = lines[1]
            def geneFullName = lines[2]
            def synonyms = lines[29]
            synonyms += (synonyms ? "|" : "") + lines[30]
            def notes = lines[33]
            notes += (notes ? "|" : "") + lines[36]

            filteredOutputFile << geneID << "\t" << symbol << "\t" << geneFullName << "\t" << synonyms << "\t" << notes << "\n"
        } catch (e) {
            println "error filtering for line[${line}], ${e}"
        }
    }
}
println "done"





