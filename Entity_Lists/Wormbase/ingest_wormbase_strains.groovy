evaluate(new File("Utilities.groovy"))

def input = "ftp://ftp.sanger.ac.uk/pub/consortia/wormbase/STAFF/mh6/nightly_geneace/strains.ace.gz"
def output = "strains.ace.gz"
def unzipped_input = "strains.ace"
def final_output = "WORMBASE_STRAINS_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)
Utilities.gunzip(output,unzipped_input)

File ourInputFile = new File(unzipped_input)
File ourOutputFile = new File(final_output)

ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_strains.groovy" << "\n"
ourOutputFile << "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"

strain = ''
genotype = ''
species = ''
location = ''
junk = ''
junk1 = ''
junk2 = ''
junk3 = ''


ourInputFile.eachLine {
    if (it.startsWith('Strain')){
        junk = it.split('\"')
        strain = junk[1]
    }
    else if (it.startsWith('Genotype')){
        junk1 = it.split('\"')
        genotype = junk1[1]
    }
    else if (it.startsWith('Location')){
        junk2 = it.split('\"')
        if (junk2.size() > 1) {
            location = junk2[1]
        }
    }
    else if (it.startsWith("Species")) {
        junk3 = it.split('\"')
        species = junk3[1]
        ourOutputFile << strain << "\t" << strain << "\t\t\t" << genotype + "|" + species + "|" + location << "\n"
        strain = ''
        genotype = ''
        species = ''
        location = ''
        junk = ''
        junk1 = ''
        junk2 = ''
        junk3 = ''
    }

}
println("done!")
