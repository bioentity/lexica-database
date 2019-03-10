def download(String input, String output) {
    def outputStream = new File(output).newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()
    println "finish download download"
}

def input = "http://flybase.org/static_pages/downloads/FB2017_01/genes/gene_snapshots_fb_2017_01.tsv.gz"
def output = "FLY_GENES.txt"
def filteredOutput = "FLY_GENES_FINAL.txt"
File filteredOutputFile = new File(filteredOutput)
File ouputFile = new File(output)
if (!ouputFile.exists()) {
    download(input, output)
    // gunzip(output)
}

filteredOutputFile << "# input[" << input << "] script ingest_fly.groovy" << "\n"

println "filtering file ${output} to ${filteredOutput}"
filteredOutputFile.write("")
new File(output).readLines().eachWithIndex { line, index ->
    if (line.startsWith("#")) {
        filteredOutputFile << line << "\n"
    } else if (line.startsWith("GENE_RGD_ID")) {
        filteredOutputFile << "#" << line << "\n"
    } else
    if (!line.startsWith("FBtr") && !line.startsWith("FBpp") && !line.startsWith("FBba")) {
        try {
            def lines = line.split("\t")
            def geneID = lines[0]
            def symbol = lines[1]
            def geneFullName = lines[2]
            def synonyms = ""
//        synonyms += (synonyms ? "|" : "") +lines[30]
            def notes = lines[4]
//        notes += (notes ? "|" : "") + lines[36]

            filteredOutputFile << geneID << "\t" << symbol << "\t" << geneFullName << "\t" << synonyms << "\t" << notes << "\n"
        } catch (e) {
            println "error filtering for line[${line}]"
        }
    }
}
println "done"





