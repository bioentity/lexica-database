def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download"
}

def input = "http://downloads.yeastgenome.org/curation/chromosomal_feature/SGD_features.tab"
def output = "YEAST_GENES.txt"
def filteredOutput = "YEAST_GENES_FINAL.txt"
File outputFile = new File(output)
if (!outputFile.exists()) {
    download(input, output)
    // gunzip(output)
}
File filteredOutputFile = new File(filteredOutput)

filteredOutputFile << "# input[" << input << "] script ingest_yeast.groovy" << "\n"

println "filtering file ${output} to ${filteredOutput}"
filteredOutputFile.write("")
new File(output).readLines().eachWithIndex { line, index ->
    if (line.startsWith("S")) {
        try {
            def lines = line.split("\t")
            def geneID = lines[0]
            def symbol = lines[4]
//            def geneFullName = lines[4]
            def synonyms = lines[3]
            if (lines[5] != "") {
                synonyms += (synonyms ? "|" : "") + lines[5]
            }
//            def notes = lines[15]
//            notes += (notes ? "|" : "") + lines[2]
            def isitanorf = lines[1]
            if(isitanorf != "CDS") {
                filteredOutputFile << geneID << "\t" << symbol << "\t" /* << geneFullName << "\t"  */ << synonyms << /*"\t" << notes << */"\n"
            }
        }
        catch (e) {
            println "error filtering for line[${line}]"
        }
    }
    }
println "done"





