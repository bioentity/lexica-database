evaluate(new File("Utilities.groovy"))

def input = "ftp://ftp.sanger.ac.uk/pub/consortia/wormbase/STAFF/mh6/nightly_geneace/genes.ace.gz"
def output = "genes.ace.gz"
def unzipped_input = "genes.ace"
def final_output = "WORMBASE_SEQUENCE_FINAL.txt"
def timeStamp = new Date()

Utilities.download(input, output)
Utilities.gunzip(output,unzipped_input)

File ourInputFile = new File(unzipped_input)
File ourOutputFile = new File(final_output)

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


ourOutputFile.bytes = []
ourOutputFile << "# input[" << input << "] script ingest_wormbase_sequence.groovy" << "\n"
ourOutputFile<< "#" << timeStamp << "\n"
ourOutputFile << "# ID|SYMBOL|NAME|SYNONYMS|NOTES" << "\n"


ourInputFile.eachLine {
    if (it.startsWith('Gene ')){
        junk = it.split('\"')
        gene = junk[1]
        //println(symbol)
    }
    else if (it.startsWith('Public_name')){
        junk1 = it.split('\"')
        publicName = junk1[1]
        //println(species)
    }
    else if (it.startsWith('CGC_name')){
        junk2 = it.split('\"')
        cgcName = junk2[1]
        //println(sequence)
    }
    else if (it.startsWith('otherName')){
        junk3 = it.split('\"')
        otherName = junk3[1]
    }
    else if(it.startsWith('Sequence_name')){
        junk4 = it.split('\"')
        sequenceName = junk4[1]
    }
    else if (it.startsWith("Species")){
        junk5 = it.split('\"')
        species = junk5[1]
    }

    else if ((it.startsWith("Dead"))||(it.startsWith("Live"))){
        liveOrDead = it
        if (gene.contains('WBGene') && publicName != '') {
            ourOutputFile << gene << "\t" << sequenceName << "\t" << publicName + "|" + cgcName + "|" + otherName << "\t\t" << species + "|" + liveOrDead << "\n"
        }
        gene = ''
        publicName = ''
        cgcName = ''
        otherName = ''
        sequenceName = ''
        species = ''
        liveOrDead = ''
        junk = ''
        junk1 = ''
        junk2 = ''
        junk3 = ''
        junk4 = ''
        junk5 = ''
    }
}
println("done!")
