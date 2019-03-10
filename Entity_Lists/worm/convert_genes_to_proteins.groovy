
def input = "WORM_GENES_FINAL_CLEAN.tsv"
def output = "WORM_GENES_AS_PROTEINS_FINAL.tsv"
def timeStamp = new Date()

File ourInputFile = new File(input)
File ourOutputFile = new File(output)

if (ourOutputFile.exists()) {
	ourOutputFile.delete();
}


String gene = ""
publicName = ""
cgcName = ""
otherName = ""
sequenceName = ""
species = ""
liveOrDead = ""
junk = ""
junk1 = ""
junk2 = ""
junk3 = ""


ourOutputFile << "# input[" << input << "] script ingest_wormbase_genes.groovy" << "\n"
ourOutputFile<< "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"

int runCounter = 0 
geneSet = new HashSet<>()

ourInputFile.eachLine {
    if (!it.startsWith('#')){
		++runCounter ;
        entries = it.split('\t')
		gene = entries[1].toUpperCase()
//        println "input gene: ${gene}"
//        if(gene!=entries[1] && !geneSet.contains(gene)){
		if(gene && !geneSet.contains(gene)){
			ourOutputFile << entries[0] << "\t" << gene 
			for(i = 2 ; i < entries.length ; i++){
			  ourOutputFile << "\t" << entries[i]
			}
			ourOutputFile << "\n" 
			geneSet.add(gene)
		}
//        else{
//            println "[${gene}] not added because: ${!geneSet.contains(gene)} or [${entries[1]}] same"
//        }
    }
}
println("considered: "+runCounter + " final: " + geneSet.size() )
