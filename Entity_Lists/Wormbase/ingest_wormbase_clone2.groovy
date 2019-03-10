evaluate(new File("Utilities.groovy"))

def input = "ftp://ftp.sanger.ac.uk/pub/consortia/wormbase/STAFF/mh6/nightly_geneace/clones2.ace.gz"
def output = "clones2.ace.gz"
def unzipped_input = "clones2.ace"
def final_output = "WORMBASE_CLONES2_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)
Utilities.gunzip(output,unzipped_input)

File ourInputFile = new File(unzipped_input)
File ourOutputFile = new File(final_output)


symbol = ""
species = ""
sequence = ""
junk = ""
junk1 = ""
junk2 = ""

ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_clone2.groovy" << "\n"
ourOutputFile<< "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"


ourInputFile.eachLine {
    if (it.startsWith('Clone ')){
        junk = it.split('\"')
        symbol = junk[1]
        //println(symbol)
    }
    else if (it.startsWith('Species')){
        junk1 = it.split('\"')
        species = junk1[1]
        //println(species)
    }
    else if (it.startsWith('Sequence')){
        junk2 = it.split('\"')
        sequence = junk2[1]
        if (sequence != null) {
            sequence = sequence + "|"
        }
        //println(sequence)
    }
    else if (it.startsWith('Other')){
        ourOutputFile << symbol << "\t" << symbol << "\t\t" << sequence  << species  << '\n'
        symbol = ''
        species = ''
        sequence = ''
        junk = ''
        junk1 = ''
        junk2 = ''
    }
}
println("done!")
