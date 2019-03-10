def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download"
}

def input = "ftp://ftp.rgd.mcw.edu/pub/data_release/GENES_RAT.txt"
def output = "RAT_GENES.txt"
def filteredOutput = "RAT_GENES_FINAL.txt"
File filteredOutputFile = new File(filteredOutput)
filteredOutputFile.write("")
File outputFile = new File(output)
if(!outputFile.exists()){
    download(input,output)
}

filteredOutputFile << "# input[" << input << "] script ingest_rat.groovy" << "\n"

println "filtering file ${output} to ${filteredOutput}"
new File(output).readLines().eachWithIndex { line, index ->
    if (line.startsWith("#")) {
        filteredOutputFile << line << "\n"
    } else if (line.startsWith("GENE_RGD_ID")) {
        filteredOutputFile << "#" << line << "\n"
    } else {
        def lines = line.split("\t")
        def geneID = lines[0]
        def symbol = lines[1]
        def geneFullName = lines[2]
        def synonyms = lines[29]
        synonyms += (synonyms ? "|" : "") +lines[30]
        synonyms = synonyms ? synonyms.replaceAll(";","|") : synonyms
        def notes = lines[33]
        notes += (notes ? "|" : "") + lines[36]

        filteredOutputFile << geneID << "\t" << symbol << "\t" << geneFullName << "\t" << synonyms << "\t" << notes << "\n"
    }
}
println "done"





