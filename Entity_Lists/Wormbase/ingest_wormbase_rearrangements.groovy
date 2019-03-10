evaluate(new File("Utilities.groovy"))

def input = "ftp://ftp.sanger.ac.uk/pub/consortia/wormbase/STAFF/mh6/nightly_geneace/rearrangements.ace.gz"
def output = "rearrangements.ace.gz"
def unzipped_input = "rearrangements.ace"
def final_output = "WORMBASE_REARRANGEMENTS_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)
Utilities.gunzip(output,unzipped_input)

File ourInputFile = new File(unzipped_input)
File ourOutputFile = new File(final_output)

ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_rearrangements.groovy" << "\n"
ourOutputFile << "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"

rearrangement = ''
otherName = ''
strain = ''
junk = ''
junk1 = ''
junk2 = ''

ourInputFile.eachLine {
    if (it.startsWith('Rearrangement')){
        junk = it.split('\"')
        rearrangement = junk[1]
    }
    else if (it.startsWith('Other_name')){
        junk1 = it.split('\"')
        otherName = junk1[1]
    }
    else if (it.startsWith('Strain')){
        junk2 = it.split('\"')
        if (strain == '') {
            strain = junk2[1]
        }
        else {
            strain += ("|" + junk2[1])
        }
    }
    else if ((it.isEmpty()) && (rearrangement != '')) {
        ourOutputFile << rearrangement << "\t" << rearrangement << "\t" << otherName << "\t\t" << strain << "\n"
        rearrangement = ''
        otherName = ''
        strain = ''
        junk = ''
        junk1 = ''
        junk2 = ''
    }

}
println("done!")
