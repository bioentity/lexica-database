evaluate(new File("Utilities.groovy"))

def input = "ftp://ftp.sanger.ac.uk/pub/consortia/wormbase/STAFF/mh6/nightly_geneace/variations.ace.gz"
def output = "variations.ace.gz"
def unzipped_input = "variations.ace"
def final_output = "WORMBASE_VARIATIONS_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)
Utilities.gunzip(output,unzipped_input)

File ourInputFile = new File(unzipped_input)
File ourOutputFile = new File(final_output)

ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_variations.groovy" << "\n"
ourOutputFile<< "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"

variation = ''
publicName = ''
species = ''
gene = ''
liveOrDead = ''
junk = ''
junk1 = ''
junk2 = ''
junk3 = ''

ourInputFile.eachLine {
    if (it.startsWith('Variation')){
        junk = it.split('\"')
        variation = junk[1]
    }
    else if (it.startsWith('Public_name')){
        junk1 = it.split('\"')
        publicName = junk1[1]
    }
    else if (it.startsWith('Gene ')){
        junk2 = it.split('\"')
        gene = junk2[1]
    }

    else if (it.startsWith("Species")){
        junk3 = it.split('\"')
        species = junk3[1]
    }

    else if ((it.startsWith("Dead"))||(it.startsWith("Live"))) {
        liveOrDead = it
    }
    else if (it.startsWith("Method")) {
        ourOutputFile << variation << "\t" << publicName << "\t\t\t" << gene + "|" + liveOrDead + "|" + species << "\n"
        variation = ''
        publicName = ''
        species = ''
        gene = ''
        liveOrDead = ''
        junk = ''
        junk1 = ''
        junk2 = ''
        junk3 = ''
    }

}
println("done!")
